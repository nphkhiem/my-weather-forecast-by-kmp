package com.example.my_weather_forecast.presentation.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlin.math.PI
import kotlin.math.sin
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_weather_precipitation_cloud
import myweatherforecast.composeapp.generated.resources.ic_weather_thunderstorm_bolt
import myweatherforecast.composeapp.generated.resources.ic_weather_thunderstorm_cloud
import org.jetbrains.compose.resources.painterResource

/**
 * A weather icon tinted with its condition's accent color and given a small, cheap idle
 * animation via [rememberInfiniteTransition] + property transforms/Canvas draws only — no path
 * morphing, no bitmap work, so it stays inexpensive on both platforms.
 */
@Composable
fun AnimatedWeatherIcon(
    icon: WeatherIcon,
    isDaytime: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val painter = painterResource(icon.toDrawableResource())
    val tint = icon.accentColor(isDaytime = isDaytime, darkTheme = WeatherTheme.darkTheme)
    val transition = rememberInfiniteTransition(label = "weatherIconMotion")

    when {
        icon == WeatherIcon.CLEAR && isDaytime -> {
            val rotation by transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing)),
                label = "sunRotation",
            )
            Icon(painter, contentDescription, tint = tint, modifier = modifier.graphicsLayer { rotationZ = rotation })
        }

        icon == WeatherIcon.CLEAR && !isDaytime -> {
            val scale by transition.animateFloat(
                initialValue = 0.92f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(tween(2_200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "moonPulse",
            )
            Icon(painter, contentDescription, tint = tint, modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale })
        }

        icon == WeatherIcon.CLOUDS -> {
            // graphicsLayer translations are raw pixels, not dp, so the drift amount is resolved
            // through density here — a raw Float amplitude would be sub-pixel (invisible) on
            // most screens.
            val driftPx = with(LocalDensity.current) { 5.dp.toPx() }
            val drift by transition.animateFloat(
                initialValue = -driftPx,
                targetValue = driftPx,
                animationSpec = infiniteRepeatable(tween(2_400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                label = "cloudDrift",
            )
            Icon(painter, contentDescription, tint = tint, modifier = modifier.graphicsLayer { translationX = drift })
        }

        icon == WeatherIcon.RAIN -> FallingPrecipitation(
            tint = tint,
            contentDescription = contentDescription,
            modifier = modifier,
            transition = transition,
            dropXFractions = listOf(0.32f, 0.5f, 0.68f),
            cycleDurationMillis = 850,
            dropLengthFraction = 0.16f,
            strokeWidthFraction = 0.09f,
        )

        icon == WeatherIcon.DRIZZLE -> FallingPrecipitation(
            tint = tint,
            contentDescription = contentDescription,
            modifier = modifier,
            transition = transition,
            dropXFractions = listOf(0.38f, 0.62f),
            cycleDurationMillis = 1_300,
            dropLengthFraction = 0.1f,
            strokeWidthFraction = 0.06f,
        )

        icon == WeatherIcon.SNOW -> FallingPrecipitation(
            tint = tint,
            contentDescription = contentDescription,
            modifier = modifier,
            transition = transition,
            dropXFractions = listOf(0.38f, 0.62f),
            cycleDurationMillis = 2_000,
            dropLengthFraction = 0.07f,
            strokeWidthFraction = 0.09f,
            swayFraction = 0.06f,
        )

        icon == WeatherIcon.THUNDERSTORM -> {
            val flashAlpha by transition.animateFloat(
                initialValue = 1f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    keyframes {
                        durationMillis = 2_200
                        1f at 0
                        1f at 1_100
                        0.08f at 1_200
                        1f at 1_280
                        0.08f at 1_360
                        1f at 1_460
                        1f at 2_200
                    },
                ),
                label = "boltFlash",
            )
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
                    modifier = Modifier.matchParentSize().graphicsLayer { alpha = flashAlpha },
                )
            }
        }

        else -> Icon(painter, contentDescription, tint = tint, modifier = modifier)
    }
}

/**
 * Cloud body only — no static drop/flake marks baked in, since [FallingPrecipitation] draws the
 * only accents here itself, animated. A static mark alongside the animated ones would just look
 * like visual clutter/duplication.
 */
@Composable
private fun FallingPrecipitation(
    tint: Color,
    contentDescription: String?,
    modifier: Modifier,
    transition: InfiniteTransition,
    dropXFractions: List<Float>,
    cycleDurationMillis: Int,
    dropLengthFraction: Float,
    strokeWidthFraction: Float,
    swayFraction: Float = 0f,
) {
    val cycle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(cycleDurationMillis, easing = LinearEasing)),
        label = "precipitationFall",
    )
    Box(modifier = modifier) {
        Icon(
            painter = painterResource(Res.drawable.ic_weather_precipitation_cloud),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.matchParentSize(),
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            val startY = size.height * 0.5f
            val endY = size.height * 0.95f
            val dropLength = size.height * dropLengthFraction
            val strokeWidthPx = size.minDimension * strokeWidthFraction
            dropXFractions.forEachIndexed { index, xFraction ->
                val progress = (cycle + index / dropXFractions.size.toFloat()) % 1f
                val alpha = sin(progress * PI.toFloat()).coerceIn(0f, 1f)
                val sway = if (swayFraction > 0f) sin(progress * 2 * PI.toFloat()) * size.width * swayFraction else 0f
                val centerX = size.width * xFraction + sway
                val centerY = startY + (endY - startY) * progress
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
