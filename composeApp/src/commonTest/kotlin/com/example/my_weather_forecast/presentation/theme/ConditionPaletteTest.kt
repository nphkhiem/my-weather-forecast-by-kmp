package com.example.my_weather_forecast.presentation.theme

import androidx.compose.ui.graphics.luminance
import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConditionPaletteTest {

    @Test
    fun givenEveryConditionInEachTheme_whenMapped_thenEachHasADistinctGradientStart() {
        listOf(false, true).forEach { darkTheme ->
            val starts = WeatherIcon.entries.map {
                it.conditionPalette(isDaytime = true, darkTheme = darkTheme).gradientStart
            }

            assertEquals(WeatherIcon.entries.size, starts.toSet().size)
        }
    }

    @Test
    fun givenAnyLocalTimeInLightTheme_whenMapped_thenWashStaysLightWithDarkContent() {
        WeatherIcon.entries.forEach { icon ->
            listOf(false, true).forEach { isDaytime ->
                val palette = icon.conditionPalette(isDaytime = isDaytime, darkTheme = false)

                assertTrue(palette.gradientStart.luminance() > 0.65f)
                assertTrue(palette.gradientEnd.luminance() > 0.65f)
                assertTrue(palette.onGradient.luminance() < 0.20f)
            }
        }
    }

    @Test
    fun givenAnyLocalTimeInDarkTheme_whenMapped_thenWashStaysDarkWithLightContent() {
        WeatherIcon.entries.forEach { icon ->
            listOf(false, true).forEach { isDaytime ->
                val palette = icon.conditionPalette(isDaytime = isDaytime, darkTheme = true)

                assertTrue(palette.gradientStart.luminance() < 0.10f)
                assertTrue(palette.gradientEnd.luminance() < 0.10f)
                assertTrue(palette.onGradient.luminance() > 0.70f)
            }
        }
    }

    @Test
    fun givenTheSamePaletteRequestedTwice_whenCompared_thenGradientStaysWithinASoftLightnessRange() {
        val palette = WeatherIcon.RAIN.conditionPalette(isDaytime = true, darkTheme = false)

        val startLuminance = palette.gradientStart.luminance()
        val endLuminance = palette.gradientEnd.luminance()
        assertTrue((endLuminance - startLuminance) < 0.2f, "gradient should be soft, not a dramatic jump")
    }

    @Test
    fun givenEveryConditionInEachTheme_whenReadingAccentColor_thenEachHasADistinctColor() {
        listOf(false, true).forEach { darkTheme ->
            val accents = WeatherIcon.entries.map {
                it.accentColor(isDaytime = true, darkTheme = darkTheme)
            }

            assertEquals(WeatherIcon.entries.size, accents.toSet().size)
        }
    }

    @Test
    fun givenAnyCondition_whenReadingAccentColor_thenDayAndNightAccentsDiffer() {
        WeatherIcon.entries.forEach { icon ->
            val day = icon.accentColor(isDaytime = true, darkTheme = false)
            val night = icon.accentColor(isDaytime = false, darkTheme = false)

            assertTrue(day != night, "day and night accents for $icon should differ")
        }
    }
}
