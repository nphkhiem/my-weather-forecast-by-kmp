package com.example.my_weather_forecast.presentation.platform

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WeatherPlatformBehaviorSmokeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenAndroidHost_whenPlatformBehaviorIsRequested_thenNativeActionsAreAvailable() {
        lateinit var behavior: WeatherPlatformBehavior

        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                behavior = LocalWeatherPlatformBehavior.current
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(WeatherPressFeedback.MATERIAL_RIPPLE, behavior.pressFeedback)
            behavior.requestHaptic(WeatherHapticCue.SELECTION)
            behavior.requestHaptic(WeatherHapticCue.SUCCESS)
            behavior.dismissKeyboard()
            behavior.setSystemBarIconTone(WeatherSystemBarIconTone.DARK)
        }
    }
}
