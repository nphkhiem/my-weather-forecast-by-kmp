package com.example.my_weather_forecast.presentation.settings

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun SettingsContentPreview(
    themeMode: ThemeMode,
    units: Units,
    darkTheme: Boolean,
    fontScale: Float = 1f,
    width: Dp = 390.dp,
) {
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
        WeatherPlatformBehaviorProvider {
            WeatherForecastTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier
                        .requiredWidth(width)
                        .height(800.dp),
                ) {
                    SettingsContent(
                        units = units,
                        themeMode = themeMode,
                        onUnitsSelected = {},
                        onThemeModeSelected = {},
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SettingsSystemMetricLightPreview() = SettingsContentPreview(
    themeMode = ThemeMode.SYSTEM,
    units = Units.METRIC,
    darkTheme = false,
)

@Preview
@Composable
private fun SettingsLightImperialPreview() = SettingsContentPreview(
    themeMode = ThemeMode.LIGHT,
    units = Units.IMPERIAL,
    darkTheme = false,
)

@Preview
@Composable
private fun SettingsDarkMetricDarkPreview() = SettingsContentPreview(
    themeMode = ThemeMode.DARK,
    units = Units.METRIC,
    darkTheme = true,
)

@Preview
@Composable
private fun SettingsLargeTextPreview() = SettingsContentPreview(
    themeMode = ThemeMode.SYSTEM,
    units = Units.IMPERIAL,
    darkTheme = false,
    fontScale = 2f,
    width = 320.dp,
)

@Preview
@Composable
private fun SettingsExpandedDarkPreview() = SettingsContentPreview(
    themeMode = ThemeMode.SYSTEM,
    units = Units.IMPERIAL,
    darkTheme = true,
    width = 700.dp,
)
