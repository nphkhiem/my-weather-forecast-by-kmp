package com.example.my_weather_forecast.presentation.settings

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.example.my_weather_forecast.core.preference.SettingsThemePreference
import com.example.my_weather_forecast.core.preference.SettingsUnitsPreference
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherHapticCue
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.platform.WeatherSystemBarIconTone
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.testutil.FakeThemePreference
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import com.russhwolf.settings.SharedPreferencesSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

        composeTestRule.onNodeWithText("Make it yours").assertIsDisplayed()
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Temperature & wind").assertIsDisplayed()
        composeTestRule.onNodeWithText("System stays in sync.").assertIsDisplayed()
    }

    @Test
    fun givenDefaultPreferences_whenSelectorsRender_thenActiveOptionsExposeRadioSelection() {
        setContent()

        composeTestRule.onNodeWithTag("theme_option_SYSTEM")
            .assertIsSelected()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Role,
                    Role.RadioButton,
                ),
            )
        composeTestRule.onNodeWithTag("theme_option_DARK").assertIsNotSelected()
        composeTestRule.onNodeWithTag("units_option_METRIC")
            .assertIsSelected()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Role,
                    Role.RadioButton,
                ),
            )
        composeTestRule.onNodeWithTag("units_option_IMPERIAL").assertIsNotSelected()
        composeTestRule.onNodeWithTag("units_selection_mark_METRIC", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("units_selection_mark_IMPERIAL", useUnmergedTree = true)
            .assertDoesNotExist()
    }

    @Test
    fun givenDarkSelected_whenTapped_thenThemePreferenceIsUpdated() {
        val themePreference = FakeThemePreference()
        setContent(themePreference = themePreference)

        composeTestRule.onNodeWithTag("theme_option_DARK").performClick()

        assertEquals(ThemeMode.DARK, themePreference.mode.value)
        composeTestRule.onNodeWithText("System stays in sync.").assertIsDisplayed()
    }

    @Test
    fun givenImperialSelected_whenTapped_thenUnitsPreferenceIsUpdated() {
        val unitsPreference = FakeUnitsPreference()
        setContent(unitsPreference = unitsPreference)

        composeTestRule.onNodeWithTag("units_option_IMPERIAL").performClick()

        assertEquals(Units.IMPERIAL, unitsPreference.units.value)
    }

    @Test
    fun givenAnUnselectedOption_whenTapped_thenSelectionHapticIsRequested() {
        val platformBehavior = RecordingPlatformBehavior()
        composeTestRule.setContent {
            CompositionLocalProvider(LocalWeatherPlatformBehavior provides platformBehavior) {
                WeatherForecastTheme {
                    SettingsContent(
                        units = Units.METRIC,
                        themeMode = ThemeMode.SYSTEM,
                        onUnitsSelected = {},
                        onThemeModeSelected = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("theme_option_DARK").performClick()

        assertEquals(listOf(WeatherHapticCue.SELECTION), platformBehavior.hapticCues)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun givenAnUnselectedOptionHasFocus_whenEnterIsPressed_thenItIsSelected() {
        var selectedUnits = Units.METRIC
        lateinit var inputModeManager: InputModeManager
        composeTestRule.setContent {
            inputModeManager = LocalInputModeManager.current
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    SettingsContent(
                        units = selectedUnits,
                        themeMode = ThemeMode.SYSTEM,
                        onUnitsSelected = { selectedUnits = it },
                        onThemeModeSelected = {},
                    )
                }
            }
        }

        composeTestRule.runOnIdle {
            assertTrue(inputModeManager.requestInputMode(InputMode.Keyboard))
        }
        composeTestRule.onNodeWithTag("units_option_IMPERIAL")
            .requestFocus()
            .assertIsFocused()
            .performKeyInput { pressKey(Key.Enter) }

        composeTestRule.runOnIdle {
            assertEquals(Units.IMPERIAL, selectedUnits)
        }
    }

    @Test
    fun givenSelectionsPersisted_whenScreenModelIsRecreated_thenSelectedStatesAreRestored() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = context.getSharedPreferences(
            "settings_screen_persistence_test",
            Context.MODE_PRIVATE,
        )
        sharedPreferences.edit().clear().commit()
        val settings = SharedPreferencesSettings(sharedPreferences)
        val viewModel = mutableStateOf(
            SettingsViewModel(
                unitsPreference = SettingsUnitsPreference(settings),
                themePreference = SettingsThemePreference(settings),
            ),
        )
        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    SettingsScreen(
                        onBack = {},
                        viewModel = viewModel.value,
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("theme_option_DARK").performClick()
        composeTestRule.onNodeWithTag("units_option_IMPERIAL").performClick()
        composeTestRule.runOnUiThread {
            viewModel.value = SettingsViewModel(
                unitsPreference = SettingsUnitsPreference(settings),
                themePreference = SettingsThemePreference(settings),
            )
        }

        composeTestRule.onNodeWithTag("theme_option_DARK").assertIsSelected()
        composeTestRule.onNodeWithTag("units_option_IMPERIAL").assertIsSelected()
    }

    @Test
    fun givenNarrowWidthAndLargeText_whenSelectorsRender_thenLabelsStayInsideTheirControls() {
        composeTestRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                WeatherPlatformBehaviorProvider {
                    WeatherForecastTheme {
                        Box(modifier = Modifier.requiredWidth(320.dp)) {
                            SettingsContent(
                                units = Units.METRIC,
                                themeMode = ThemeMode.SYSTEM,
                                onUnitsSelected = {},
                                onThemeModeSelected = {},
                            )
                        }
                    }
                }
            }
        }

        listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK).forEach { mode ->
            val cardBounds = composeTestRule
                .onNodeWithTag("theme_option_${mode.name}")
                .fetchSemanticsNode()
                .boundsInRoot
            val labelBounds = composeTestRule
                .onNodeWithText(
                    when (mode) {
                        ThemeMode.SYSTEM -> "System"
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                    },
                    useUnmergedTree = true,
                )
                .fetchSemanticsNode()
                .boundsInRoot

            assertTrue(labelBounds.left >= cardBounds.left)
            assertTrue(labelBounds.right <= cardBounds.right)
        }
        listOf(
            Units.METRIC to listOf("°C", "Metric"),
            Units.IMPERIAL to listOf("°F", "Imperial"),
        ).forEach { (units, labels) ->
            val segmentBounds = composeTestRule
                .onNodeWithTag("units_option_${units.name}")
                .fetchSemanticsNode()
                .boundsInRoot

            labels.forEach { label ->
                val labelBounds = composeTestRule
                    .onNodeWithText(label, useUnmergedTree = true)
                    .fetchSemanticsNode()
                    .boundsInRoot
                assertTrue(labelBounds.left >= segmentBounds.left)
                assertTrue(labelBounds.right <= segmentBounds.right)
            }
        }
        val selectedUnitLabelBounds = composeTestRule
            .onNodeWithText("Metric", useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot
        val selectedUnitMarkBounds = composeTestRule
            .onNodeWithTag("units_selection_mark_METRIC", useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot

        assertTrue(
            "Selected unit label $selectedUnitLabelBounds overlaps mark $selectedUnitMarkBounds",
            selectedUnitMarkBounds.right <= selectedUnitLabelBounds.left,
        )
    }

    private class RecordingPlatformBehavior : WeatherPlatformBehavior {
        val hapticCues = mutableListOf<WeatherHapticCue>()

        override val pressFeedback = WeatherPressFeedback.TONAL_HIGHLIGHT
        override val reducedMotion = false

        override fun requestHaptic(cue: WeatherHapticCue) {
            hapticCues += cue
        }

        override fun dismissKeyboard() = Unit

        override fun setSystemBarIconTone(tone: WeatherSystemBarIconTone) = Unit
    }
}
