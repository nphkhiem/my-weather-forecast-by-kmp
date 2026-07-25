package com.example.my_weather_forecast.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.MotionDurationScale

@Immutable
enum class WeatherPressFeedback {
    MATERIAL_RIPPLE,
    TONAL_HIGHLIGHT,
}

@Immutable
enum class WeatherHapticCue {
    SELECTION,
    SUCCESS,
}

@Immutable
enum class WeatherSystemBarIconTone {
    DARK,
    LIGHT,
}

@Stable
interface WeatherPlatformBehavior {
    val pressFeedback: WeatherPressFeedback
    val reducedMotion: Boolean

    fun requestHaptic(cue: WeatherHapticCue)

    fun dismissKeyboard()

    fun setSystemBarIconTone(tone: WeatherSystemBarIconTone)
}

@Stable
internal class DefaultWeatherPlatformBehavior(
    override val pressFeedback: WeatherPressFeedback,
    override val reducedMotion: Boolean,
    private val onHapticRequest: (WeatherHapticCue) -> Unit,
    private val onKeyboardDismiss: () -> Unit,
    private val onSystemBarRequest: (WeatherSystemBarIconTone) -> Unit,
) : WeatherPlatformBehavior {
    override fun requestHaptic(cue: WeatherHapticCue) {
        onHapticRequest(cue)
    }

    override fun dismissKeyboard() {
        onKeyboardDismiss()
    }

    override fun setSystemBarIconTone(tone: WeatherSystemBarIconTone) {
        onSystemBarRequest(tone)
    }
}

val LocalWeatherPlatformBehavior = staticCompositionLocalOf<WeatherPlatformBehavior> {
    error("WeatherPlatformBehaviorProvider is missing")
}

@Composable
fun WeatherPlatformBehaviorProvider(content: @Composable () -> Unit) {
    val behavior = rememberWeatherPlatformBehavior()

    CompositionLocalProvider(
        LocalWeatherPlatformBehavior provides behavior,
        content = content,
    )
}

@Composable
fun WeatherSystemBars(iconTone: WeatherSystemBarIconTone) {
    val behavior = LocalWeatherPlatformBehavior.current

    SideEffect {
        behavior.setSystemBarIconTone(iconTone)
    }
}

@Composable
internal fun isReducedMotionEnabled(): Boolean {
    val motionDurationScale = rememberCoroutineScope().coroutineContext[MotionDurationScale]
    return motionDurationScale?.scaleFactor == 0f
}

@Composable
internal expect fun rememberWeatherPlatformBehavior(): WeatherPlatformBehavior

internal expect val weatherPressFeedback: WeatherPressFeedback
