package com.example.my_weather_forecast.presentation.platform

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.view.WindowCompat

internal actual val weatherPressFeedback = WeatherPressFeedback.MATERIAL_RIPPLE

@Composable
internal actual fun rememberWeatherPlatformBehavior(): WeatherPlatformBehavior {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val reducedMotion = isReducedMotionEnabled()

    return remember(context, hapticFeedback, keyboardController, reducedMotion) {
        DefaultWeatherPlatformBehavior(
            pressFeedback = weatherPressFeedback,
            reducedMotion = reducedMotion,
            onHapticRequest = { cue ->
                hapticFeedback.performHapticFeedback(cue.toHapticFeedbackType())
            },
            onKeyboardDismiss = {
                keyboardController?.hide()
            },
            onSystemBarRequest = { tone ->
                context.findActivity()?.setSystemBarIconTone(tone)
            },
        )
    }
}

private fun WeatherHapticCue.toHapticFeedbackType(): HapticFeedbackType = when (this) {
    WeatherHapticCue.SELECTION -> HapticFeedbackType.SegmentTick
    WeatherHapticCue.SUCCESS -> HapticFeedbackType.Confirm
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Activity.setSystemBarIconTone(tone: WeatherSystemBarIconTone) {
    val useDarkIcons = tone == WeatherSystemBarIconTone.DARK
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = useDarkIcons
        isAppearanceLightNavigationBars = useDarkIcons
    }
}
