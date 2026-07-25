package com.example.my_weather_forecast.presentation.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalForeignApi::class)
class WeatherPlatformBehaviorSmokeTest {

    @Test
    fun givenIosTarget_whenPressFeedbackIsRead_thenTonalHighlightIsUsed() {
        assertEquals(WeatherPressFeedback.TONAL_HIGHLIGHT, weatherPressFeedback)
    }

    @Test
    fun givenSystemBarTone_whenMapped_thenUIKitReceivesMatchingContrast() {
        assertEquals(
            UIStatusBarStyleDarkContent,
            WeatherSystemBarIconTone.DARK.toStatusBarStyle(),
        )
        assertEquals(
            UIStatusBarStyleLightContent,
            WeatherSystemBarIconTone.LIGHT.toStatusBarStyle(),
        )
    }
}
