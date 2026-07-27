package com.example.my_weather_forecast.presentation.settings

import app.cash.turbine.test
import com.example.my_weather_forecast.core.preference.SettingsThemePreference
import com.example.my_weather_forecast.core.preference.SettingsUnitsPreference
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.testutil.FakeThemePreference
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import com.example.my_weather_forecast.testutil.runMainDispatcherTest
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.TestScope

class SettingsViewModelTest {

    private val unitsPreference = FakeUnitsPreference()
    private val themePreference = FakeThemePreference()

    private fun testSettings(body: suspend TestScope.(SettingsViewModel) -> Unit) = runMainDispatcherTest {
        val viewModel = SettingsViewModel(unitsPreference = unitsPreference, themePreference = themePreference)
        body(viewModel)
    }

    @Test
    fun givenNothingChanged_whenRead_thenReflectsTheUnderlyingPreferenceDefaults() = testSettings { viewModel ->
        assertEquals(Units.METRIC, viewModel.units.value)
        assertEquals(ThemeMode.SYSTEM, viewModel.themeMode.value)
    }

    @Test
    fun whenSetUnitsCalled_thenUnitsPreferenceIsUpdated() = testSettings { viewModel ->
        viewModel.setUnits(Units.IMPERIAL)

        assertEquals(Units.IMPERIAL, unitsPreference.units.value)
    }

    @Test
    fun whenSetThemeModeCalled_thenThemePreferenceIsUpdated() = testSettings { viewModel ->
        viewModel.setThemeMode(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, themePreference.mode.value)
    }

    @Test
    fun givenUnitsPreferenceChangesElsewhere_whenObserved_thenViewModelReflectsTheNewValue() = testSettings { viewModel ->
        viewModel.units.test {
            assertEquals(Units.METRIC, awaitItem())
            unitsPreference.setUnits(Units.IMPERIAL)
            assertEquals(Units.IMPERIAL, awaitItem())
        }
    }

    @Test
    fun givenSelectionsChanged_whenViewModelIsRecreated_thenBothSelectionsPersist() =
        runMainDispatcherTest {
            val settings = MapSettings()
            val firstViewModel = SettingsViewModel(
                unitsPreference = SettingsUnitsPreference(settings),
                themePreference = SettingsThemePreference(settings),
            )

            firstViewModel.setUnits(Units.IMPERIAL)
            firstViewModel.setThemeMode(ThemeMode.DARK)

            val recreatedViewModel = SettingsViewModel(
                unitsPreference = SettingsUnitsPreference(settings),
                themePreference = SettingsThemePreference(settings),
            )

            assertEquals(Units.IMPERIAL, recreatedViewModel.units.value)
            assertEquals(ThemeMode.DARK, recreatedViewModel.themeMode.value)
        }
}
