package com.example.my_weather_forecast.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.testutil.FakeThemePreference
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setContent(
        unitsPreference: FakeUnitsPreference = FakeUnitsPreference(),
        themePreference: FakeThemePreference = FakeThemePreference(),
    ): SettingsViewModel {
        val viewModel = SettingsViewModel(unitsPreference = unitsPreference, themePreference = themePreference)
        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    SettingsScreen(onBack = {}, viewModel = viewModel)
                }
            }
        }
        return viewModel
    }

    @Test
    fun givenTheScreenIsShown_thenBothSectionsAreVisible() {
        setContent()

        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Units").assertIsDisplayed()
    }

    @Test
    fun givenDarkSelected_whenTapped_thenThemePreferenceIsUpdated() {
        val themePreference = FakeThemePreference()
        setContent(themePreference = themePreference)

        composeTestRule.onNodeWithText("Dark").performClick()

        assertEquals(ThemeMode.DARK, themePreference.mode.value)
    }

    @Test
    fun givenImperialSelected_whenTapped_thenUnitsPreferenceIsUpdated() {
        val unitsPreference = FakeUnitsPreference()
        setContent(unitsPreference = unitsPreference)

        composeTestRule.onNodeWithText("Imperial (°F)").performClick()

        assertEquals(Units.IMPERIAL, unitsPreference.units.value)
    }
}
