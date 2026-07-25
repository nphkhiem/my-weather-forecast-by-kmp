package com.example.my_weather_forecast.presentation.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherThemeTokensTest {

    @Test
    fun givenLightTheme_whenResolvingMaterialScheme_thenSemanticRolesUseLightWeatherColors() {
        val colors = WeatherColorSchemes.Light
        val scheme = weatherColorScheme(darkTheme = false)

        assertEquals(colors.canvas, scheme.background)
        assertEquals(colors.surface, scheme.surface)
        assertEquals(colors.textPrimary, scheme.onBackground)
        assertEquals(colors.textSecondary, scheme.onSurfaceVariant)
        assertEquals(colors.accent, scheme.primary)
        assertEquals(colors.border, scheme.outline)
    }

    @Test
    fun givenDarkTheme_whenResolvingMaterialScheme_thenSemanticRolesUseDarkWeatherColors() {
        val colors = WeatherColorSchemes.Dark
        val scheme = weatherColorScheme(darkTheme = true)

        assertEquals(colors.canvas, scheme.background)
        assertEquals(colors.surface, scheme.surface)
        assertEquals(colors.textPrimary, scheme.onBackground)
        assertEquals(colors.textSecondary, scheme.onSurfaceVariant)
        assertEquals(colors.accent, scheme.primary)
        assertEquals(colors.border, scheme.outline)
    }

    @Test
    fun givenLayoutTokens_whenCompared_thenTouchTargetsAndBreakpointsRemainUsable() {
        assertEquals(48.dp, WeatherLayout.MinimumTouchTarget)
        assertTrue(WeatherLayout.CompactBreakpoint < WeatherLayout.ExpandedBreakpoint)
        assertTrue(WeatherLayout.FormMaxWidth <= WeatherLayout.ReadingMaxWidth)
        assertTrue(WeatherLayout.ReadingMaxWidth <= WeatherLayout.PageMaxWidth)
    }
}
