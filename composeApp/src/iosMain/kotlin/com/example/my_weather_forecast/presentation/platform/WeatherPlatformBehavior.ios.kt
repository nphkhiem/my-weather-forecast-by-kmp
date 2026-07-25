package com.example.my_weather_forecast.presentation.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.uikit.LocalUIViewController
import com.example.my_weather_forecast.WeatherViewController
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewController

internal actual val weatherPressFeedback = WeatherPressFeedback.TONAL_HIGHLIGHT

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun rememberWeatherPlatformBehavior(): WeatherPlatformBehavior {
    val viewController = LocalUIViewController.current
    val hapticFeedback = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val reducedMotion = isReducedMotionEnabled()

    return remember(viewController, hapticFeedback, keyboardController, reducedMotion) {
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
                viewController.findWeatherViewController()?.setSystemBarIconTone(tone)
            },
        )
    }
}

private fun WeatherHapticCue.toHapticFeedbackType(): HapticFeedbackType = when (this) {
    WeatherHapticCue.SELECTION -> HapticFeedbackType.SegmentTick
    WeatherHapticCue.SUCCESS -> HapticFeedbackType.Confirm
}

private tailrec fun UIViewController.findWeatherViewController(): WeatherViewController? = when (this) {
    is WeatherViewController -> this
    else -> parentViewController?.findWeatherViewController()
}

internal fun WeatherSystemBarIconTone.toStatusBarStyle(): UIStatusBarStyle = when (this) {
    WeatherSystemBarIconTone.DARK -> UIStatusBarStyleDarkContent
    WeatherSystemBarIconTone.LIGHT -> UIStatusBarStyleLightContent
}
