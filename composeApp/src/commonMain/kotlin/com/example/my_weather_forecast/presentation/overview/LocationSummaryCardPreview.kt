package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import org.jetbrains.compose.ui.tooling.preview.Preview

private val previewArea = AreaSummary(
    id = 1,
    name = "Chicago",
    currentTemp = 21.0,
    icon = WeatherIcon.RAIN,
    isDaytime = true,
    todayHigh = 24.0,
    todayLow = 15.0,
    rainChance = 0.6,
    stale = false,
)

@Composable
private fun LocationSummaryCardPreview(
    area: AreaSummary,
    darkTheme: Boolean,
) {
    WeatherPlatformBehaviorProvider {
        WeatherForecastTheme(darkTheme = darkTheme) {
            Surface {
                LocationSummaryCard(
                    area = area,
                    onClick = {},
                    onRemove = {},
                    modifier = Modifier.padding(WeatherSpacing.Lg),
                )
            }
        }
    }
}

@Preview
@Composable
private fun LocationSummaryCardLightPreview() {
    LocationSummaryCardPreview(area = previewArea, darkTheme = false)
}

@Preview
@Composable
private fun LocationSummaryCardDarkPreview() {
    LocationSummaryCardPreview(area = previewArea, darkTheme = true)
}

@Preview
@Composable
private fun LocationSummaryCardLongStaleNightLightPreview() {
    LocationSummaryCardPreview(
        area = previewArea.copy(
            name = "San Fernando del Valle de Catamarca",
            icon = WeatherIcon.CLEAR,
            isDaytime = false,
            stale = true,
        ),
        darkTheme = false,
    )
}

@Preview
@Composable
private fun LocationSummaryCardLongStaleNightDarkPreview() {
    LocationSummaryCardPreview(
        area = previewArea.copy(
            name = "San Fernando del Valle de Catamarca",
            icon = WeatherIcon.CLEAR,
            isDaytime = false,
            stale = true,
        ),
        darkTheme = true,
    )
}
