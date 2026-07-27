package com.example.my_weather_forecast.presentation.theme

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import kotlin.math.PI
import kotlin.math.sin
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_weather_precipitation_cloud
import myweatherforecast.composeapp.generated.resources.ic_weather_thunderstorm_bolt
import myweatherforecast.composeapp.generated.resources.ic_weather_thunderstorm_cloud
import org.jetbrains.compose.resources.painterResource

/**
 * A weather icon tinted with its condition's accent color.
 *
 * Static by default: every compact instance in a scrolling list renders the plain condition
 * drawable and never animates. Only the Detail hero passes [WeatherIconMotion.HERO], and even then
 * the loop is dropped whenever the platform asks for reduced motion, the app is not resumed, or the
 * icon has scrolled off-screen.
 *
 * Motion is limited to property transforms and Canvas draws, so there is no path morphing or bitmap
 * work on either platform.
 */
@Composable
fun WeatherConditionIcon(
    icon: WeatherIcon,
    isDaytime: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    motion: WeatherIconMotion = WeatherIconMotion.STATIC,
) {
    val tint = icon.accentColor(isDaytime = isDaytime, darkTheme = WeatherTheme.darkTheme)
    val gesture = WeatherIconMotionPolicy.conditionMotion(icon = icon, isDaytime = isDaytime)

    // A static icon has no motion to suppress, so it must not depend on the platform adapter or a
    // lifecycle owner. That keeps the many list instances usable in plain contexts and previews.
    if (motion == WeatherIconMotion.STATIC || gesture == ConditionMotion.NONE) {
        StaticWeatherIcon(
            icon = icon,
            tint = tint,
            contentDescription = contentDescription,
            modifier = modifier,
        )
        return
    }

    HeroWeatherIcon(
        icon = icon,
        tint = tint,
        contentDescription = contentDescription,
        modifier = modifier,
        gesture = gesture,
    )
}

@Composable
private fun HeroWeatherIcon(
    icon: WeatherIcon,
    tint: Color,
    contentDescription: String?,
    modifier: Modifier,
    gesture: ConditionMotion,
) {
    val reducedMotion = LocalWeatherPlatformBehavior.current.reducedMotion
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    var onScreen by remember { mutableStateOf(true) }

    // Stays attached even while paused, so scrolling the hero back into view resumes the gesture.
    val iconModifier = modifier.onGloballyPositioned { coordinates ->
        val bounds = coordinates.boundsInWindow()
        onScreen = bounds.width > 0f && bounds.height > 0f
    }

    val resolved = WeatherIconMotionPolicy.resolve(
        requested = WeatherIconMotion.HERO,
        reducedMotion = reducedMotion,
        active = lifecycleState.isAtLeast(Lifecycle.State.RESUMED) && onScreen,
    )
    if (resolved == WeatherIconMotion.STATIC) {
        StaticWeatherIcon(
            icon = icon,
            tint = tint,
            contentDescription = contentDescription,
            modifier = iconModifier,
        )
        return
    }

    val timing = WeatherIconMotionPolicy.timing(gesture)
    val progress = rememberAmbientProgress(gesture = gesture, timing = timing)

    when (gesture) {
        ConditionMotion.TURN -> TransformedWeatherIcon(
            icon = icon,
            tint = tint,
            contentDescription = contentDescription,
            modifier = iconModifier,
        ) {
            rotationZ = wave(progress) * WeatherMotion.HeroMotionRotationDegrees
        }

        ConditionMotion.BREATHE -> TransformedWeatherIcon(
            icon = icon,
            tint = tint,
            contentDescription = contentDescription,
            modifier = iconModifier,
        ) {
            val scale = 1f + wave(progress) * (WeatherMotion.HeroMotionScale - 1f)
            scaleX = scale
            scaleY = scale
        }

        ConditionMotion.DRIFT, ConditionMotion.HAZE_DRIFT -> {
            val driftPx = with(LocalDensity.current) {
                WeatherMotion.HeroMotionTranslationDp.dp.toPx()
            }
            TransformedWeatherIcon(
                icon = icon,
                tint = tint,
                contentDescription = contentDescription,
                modifier = iconModifier,
            ) {
                translationX = wave(progress) * driftPx
            }
        }

        ConditionMotion.RAIN_FALL -> FallingPrecipitation(
            tint = tint,
            contentDescription = contentDescription,
            modifier = iconModifier,
            progress = progress,
            repeats = timing.repeatsPerCycle,
            dropXFractions = listOf(0.32f, 0.5f, 0.68f),
            dropLengthFraction = 0.16f,
            strokeWidthFraction = 0.09f,
        )

        ConditionMotion.DRIZZLE_FALL -> FallingPrecipitation(
            tint = tint,
            contentDescription = contentDescription,
            modifier = iconModifier,
            progress = progress,
            repeats = timing.repeatsPerCycle,
            dropXFractions = listOf(0.38f, 0.62f),
            dropLengthFraction = 0.1f,
            strokeWidthFraction = 0.06f,
        )

        ConditionMotion.SNOW_FALL -> FallingPrecipitation(
            tint = tint,
            contentDescription = contentDescription,
            modifier = iconModifier,
            progress = progress,
            repeats = timing.repeatsPerCycle,
            dropXFractions = listOf(0.38f, 0.62f),
            dropLengthFraction = 0.07f,
            strokeWidthFraction = 0.09f,
            swayFraction = 0.06f,
        )

        ConditionMotion.THUNDER_PULSE -> ThunderIcon(
            tint = tint,
            contentDescription = contentDescription,
            modifier = iconModifier,
            progress = progress,
        )

        ConditionMotion.NONE -> Unit
    }
}

/**
 * Drives one gesture cycle followed by its quiet rest, so the value returns to 0 and holds there
 * before the next cycle begins.
 */
@Composable
private fun rememberAmbientProgress(
    gesture: ConditionMotion,
    timing: ConditionMotionTiming,
): Float {
    val transition = rememberInfiniteTransition(label = "weatherIconMotion")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = timing.totalMillis
                0f at 0
                1f at timing.cycleMillis
                1f at timing.totalMillis
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "weatherIconProgress-${gesture.name}",
    )
    return progress
}

/** One smooth there-and-back excursion that starts and ends at rest. */
private fun wave(progress: Float): Float = sin(progress * 2f * PI.toFloat())

@Composable
private fun StaticWeatherIcon(
    icon: WeatherIcon,
    tint: Color,
    contentDescription: String?,
    modifier: Modifier,
) {
    Icon(
        painter = painterResource(icon.toDrawableResource()),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier,
    )
}

@Composable
private fun TransformedWeatherIcon(
    icon: WeatherIcon,
    tint: Color,
    contentDescription: String?,
    modifier: Modifier,
    transform: androidx.compose.ui.graphics.GraphicsLayerScope.() -> Unit,
) {
    Icon(
        painter = painterResource(icon.toDrawableResource()),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.graphicsLayer(transform),
    )
}

/**
 * Cloud body only, with the drops drawn on the Canvas. The whole burst fades in and out across the
 * cycle so the quiet rest arrives without drops freezing in mid-air.
 */
@Composable
private fun FallingPrecipitation(
    tint: Color,
    contentDescription: String?,
    modifier: Modifier,
    progress: Float,
    repeats: Int,
    dropXFractions: List<Float>,
    dropLengthFraction: Float,
    strokeWidthFraction: Float,
    swayFraction: Float = 0f,
) {
    Box(modifier = modifier) {
        Icon(
            painter = painterResource(Res.drawable.ic_weather_precipitation_cloud),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.matchParentSize(),
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            val burstAlpha = sin(progress * PI.toFloat()).coerceIn(0f, 1f)
            if (burstAlpha <= 0f) return@Canvas

            val startY = size.height * 0.5f
            val endY = size.height * 0.95f
            val dropLength = size.height * dropLengthFraction
            val strokeWidthPx = size.minDimension * strokeWidthFraction
            dropXFractions.forEachIndexed { index, xFraction ->
                val staggered = progress * repeats + index / dropXFractions.size.toFloat()
                val dropProgress = staggered % 1f
                val alpha = sin(dropProgress * PI.toFloat()).coerceIn(0f, 1f) * burstAlpha
                val sway = if (swayFraction > 0f) {
                    sin(dropProgress * 2f * PI.toFloat()) * size.width * swayFraction
                } else {
                    0f
                }
                val centerX = size.width * xFraction + sway
                val centerY = startY + (endY - startY) * dropProgress
                drawLine(
                    color = tint,
                    start = Offset(centerX, centerY - dropLength / 2f),
                    end = Offset(centerX, centerY + dropLength / 2f),
                    strokeWidth = strokeWidthPx,
                    cap = StrokeCap.Round,
                    alpha = alpha,
                )
            }
        }
    }
}

/**
 * One restrained dip and recovery per cycle. The bolt rests fully opaque, matching the static
 * icon, so the pulse reads as a single slow surge rather than a flicker.
 */
@Composable
private fun ThunderIcon(
    tint: Color,
    contentDescription: String?,
    modifier: Modifier,
    progress: Float,
) {
    val boltAlpha = 1f - THUNDER_PULSE_DEPTH * sin(progress * PI.toFloat()).coerceIn(0f, 1f)
    Box(modifier = modifier) {
        Icon(
            painter = painterResource(Res.drawable.ic_weather_thunderstorm_cloud),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.matchParentSize(),
        )
        Icon(
            painter = painterResource(Res.drawable.ic_weather_thunderstorm_bolt),
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer { alpha = boltAlpha },
        )
    }
}

private const val THUNDER_PULSE_DEPTH = 0.45f
