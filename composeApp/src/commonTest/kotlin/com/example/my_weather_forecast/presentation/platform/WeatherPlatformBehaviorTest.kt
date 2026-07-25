package com.example.my_weather_forecast.presentation.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherPlatformBehaviorTest {

    private val hapticRequests = mutableListOf<WeatherHapticCue>()
    private val systemBarRequests = mutableListOf<WeatherSystemBarIconTone>()
    private var keyboardDismissed = false

    private val behavior = DefaultWeatherPlatformBehavior(
        pressFeedback = WeatherPressFeedback.TONAL_HIGHLIGHT,
        reducedMotion = true,
        onHapticRequest = hapticRequests::add,
        onKeyboardDismiss = { keyboardDismissed = true },
        onSystemBarRequest = systemBarRequests::add,
    )

    @Test
    fun givenPlatformBehavior_whenPolicyIsRead_thenPlatformValuesAreExposed() {
        assertEquals(WeatherPressFeedback.TONAL_HIGHLIGHT, behavior.pressFeedback)
        assertTrue(behavior.reducedMotion)
    }

    @Test
    fun givenSelectionAndSuccessCues_whenHapticsAreRequested_thenBothReachThePlatform() {
        behavior.requestHaptic(WeatherHapticCue.SELECTION)
        behavior.requestHaptic(WeatherHapticCue.SUCCESS)

        assertEquals(
            listOf(WeatherHapticCue.SELECTION, WeatherHapticCue.SUCCESS),
            hapticRequests,
        )
    }

    @Test
    fun givenFocusedInput_whenKeyboardDismissalIsRequested_thenThePlatformIsCalled() {
        behavior.dismissKeyboard()

        assertTrue(keyboardDismissed)
    }

    @Test
    fun givenChangingBackgrounds_whenSystemBarTonesAreRequested_thenEveryToneReachesThePlatform() {
        behavior.setSystemBarIconTone(WeatherSystemBarIconTone.DARK)
        behavior.setSystemBarIconTone(WeatherSystemBarIconTone.LIGHT)

        assertEquals(
            listOf(WeatherSystemBarIconTone.DARK, WeatherSystemBarIconTone.LIGHT),
            systemBarRequests,
        )
    }
}
