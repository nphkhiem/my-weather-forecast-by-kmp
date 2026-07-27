package com.example.my_weather_forecast.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class WeatherThemeAnimationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenThemeChanges_whenCrossfadeIsInProgress_thenCanvasUsesAnIntermediateColor() {
        val darkTheme = mutableStateOf(false)
        var renderedCanvas = Color.Unspecified
        var renderedErrorContainer = Color.Unspecified
        val lightErrorContainer = weatherColorScheme(darkTheme = false).errorContainer
        val darkErrorContainer = weatherColorScheme(darkTheme = true).errorContainer
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            WeatherForecastTheme(darkTheme = darkTheme.value) {
                val colors = WeatherTheme.colors
                val materialColors = MaterialTheme.colorScheme
                SideEffect {
                    renderedCanvas = colors.canvas
                    renderedErrorContainer = materialColors.errorContainer
                }
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(WeatherColorSchemes.Light.canvas, renderedCanvas)
            assertEquals(lightErrorContainer, renderedErrorContainer)
        }

        composeTestRule.runOnUiThread {
            darkTheme.value = true
        }
        composeTestRule.mainClock.advanceTimeBy(WeatherMotion.FastMillis / 2L)

        composeTestRule.runOnIdle {
            assertNotEquals(WeatherColorSchemes.Light.canvas, renderedCanvas)
            assertNotEquals(WeatherColorSchemes.Dark.canvas, renderedCanvas)
            assertNotEquals(lightErrorContainer, renderedErrorContainer)
            assertNotEquals(darkErrorContainer, renderedErrorContainer)
        }

        composeTestRule.mainClock.advanceTimeBy(WeatherMotion.FastMillis.toLong())

        composeTestRule.runOnIdle {
            assertEquals(WeatherColorSchemes.Dark.canvas, renderedCanvas)
            assertEquals(darkErrorContainer, renderedErrorContainer)
        }
    }
}
