package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private fun previewArea(
    id: Long,
    name: String,
    icon: WeatherIcon,
    stale: Boolean = false,
    isDaytime: Boolean = true,
    currentTemp: Double = 21.0,
    todayHigh: Double = 24.0,
    todayLow: Double = 15.0,
    rainChance: Double = 0.3,
) = AreaSummary(
    id = id,
    name = name,
    currentTemp = currentTemp,
    icon = icon,
    isDaytime = isDaytime,
    todayHigh = todayHigh,
    todayLow = todayLow,
    rainChance = rainChance,
    stale = stale,
)

private val oneArea = listOf(
    previewArea(
        id = 1,
        name = "Ho Chi Minh City",
        icon = WeatherIcon.CLEAR,
        currentTemp = 32.0,
        todayHigh = 35.0,
        todayLow = 28.0,
        rainChance = 0.0,
    ),
)

private val sixAreas = listOf(
    oneArea.first(),
    previewArea(2, "Hà Nội", WeatherIcon.RAIN, rainChance = 0.66),
    previewArea(3, "Đà Nẵng", WeatherIcon.CLOUDS, currentTemp = 28.0),
    previewArea(4, "Sa Pa", WeatherIcon.RAIN, stale = true, currentTemp = 17.0, rainChance = 1.0),
    previewArea(5, "Reykjavík", WeatherIcon.SNOW, isDaytime = false, currentTemp = -2.0),
    previewArea(
        id = 6,
        name = "San Fernando del Valle de Catamarca",
        icon = WeatherIcon.CLEAR,
        isDaytime = false,
        currentTemp = 19.0,
    ),
)

@Composable
private fun OverviewContentPreview(
    uiState: OverviewUiState,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    WeatherPlatformBehaviorProvider {
        WeatherForecastTheme(darkTheme = darkTheme) {
            Surface(modifier = modifier) {
                OverviewContent(uiState = uiState, onAreaClick = {}, onRemove = {})
            }
        }
    }
}

@Preview
@Composable
private fun OverviewLoadingLightPreview() = OverviewContentPreview(OverviewUiState.Loading, darkTheme = false)

@Preview
@Composable
private fun OverviewLoadingDarkPreview() = OverviewContentPreview(OverviewUiState.Loading, darkTheme = true)

@Preview
@Composable
private fun OverviewEmptyLightPreview() = OverviewContentPreview(OverviewUiState.Empty, darkTheme = false)

@Preview
@Composable
private fun OverviewEmptyDarkPreview() = OverviewContentPreview(OverviewUiState.Empty, darkTheme = true)

@Preview
@Composable
private fun OverviewErrorLightPreview() = OverviewContentPreview(OverviewUiState.Error(WeatherError.Network), darkTheme = false)

@Preview
@Composable
private fun OverviewErrorDarkPreview() = OverviewContentPreview(OverviewUiState.Error(WeatherError.Network), darkTheme = true)

@Preview
@Composable
private fun OverviewSuccessOneAreaCompactLightPreview() = OverviewContentPreview(
    uiState = OverviewUiState.Success(oneArea),
    darkTheme = false,
    modifier = Modifier.width(390.dp).height(844.dp),
)

@Preview
@Composable
private fun OverviewSuccessOneAreaCompactDarkPreview() = OverviewContentPreview(
    uiState = OverviewUiState.Success(oneArea),
    darkTheme = true,
    modifier = Modifier.width(390.dp).height(844.dp),
)

@Preview
@Composable
private fun OverviewSuccessSixAreasCompactLightPreview() = OverviewContentPreview(
    uiState = OverviewUiState.Success(sixAreas),
    darkTheme = false,
    modifier = Modifier.width(390.dp).height(844.dp),
)

@Preview
@Composable
private fun OverviewSuccessSixAreasCompactDarkPreview() = OverviewContentPreview(
    uiState = OverviewUiState.Success(sixAreas),
    darkTheme = true,
    modifier = Modifier.width(390.dp).height(844.dp),
)

@Preview
@Composable
private fun OverviewSuccessSixAreasExpandedLightPreview() = OverviewContentPreview(
    uiState = OverviewUiState.Success(sixAreas),
    darkTheme = false,
    modifier = Modifier.width(900.dp).height(700.dp),
)

@Preview
@Composable
private fun OverviewSuccessSixAreasExpandedDarkPreview() = OverviewContentPreview(
    uiState = OverviewUiState.Success(sixAreas),
    darkTheme = true,
    modifier = Modifier.width(900.dp).height(700.dp),
)
