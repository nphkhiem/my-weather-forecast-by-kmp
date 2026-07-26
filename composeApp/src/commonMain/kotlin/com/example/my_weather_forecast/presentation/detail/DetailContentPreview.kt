package com.example.my_weather_forecast.presentation.detail

import androidx.compose.runtime.Composable
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.CurrentConditions
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.HourlyForecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

private val chicago = Location(id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0)

private val clearSky = previewCondition(WeatherIcon.CLEAR, isDaytime = true)

private val previewForecast = Forecast(
    location = chicago,
    current = CurrentConditions(temp = 22.0, feelsLike = 21.0, humidity = 60, windSpeed = 4.5, pop = 0.2, condition = clearSky),
    daily = (0..6).map { offset ->
        DailyForecast(
            date = LocalDate(2024, 1, 1 + offset),
            tempMin = 15.0 + offset,
            tempMax = 24.0 + offset,
            humidity = 55 + offset,
            windSpeed = 3.0 + offset,
            pop = 0.1 * offset,
            condition = clearSky,
        )
    },
    hourly = (0..11).map { hour ->
        val hourlyIcon = WeatherIcon.entries[hour % WeatherIcon.entries.size]
        HourlyForecast(
            time = Instant.fromEpochMilliseconds(1704124800_000L + hour * 3_600_000L),
            temp = 20.0 + hour,
            pop = 0.05 * hour,
            windSpeed = 3.0,
            condition = previewCondition(hourlyIcon, isDaytime = hour < 6),
        )
    },
    units = Units.METRIC,
    fetchedAt = Instant.fromEpochMilliseconds(1704124800_000L),
)

@Composable
private fun DetailScreenPreview(
    uiState: DetailUiState,
    darkTheme: Boolean,
    isRefreshing: Boolean = false,
) {
    WeatherForecastTheme(darkTheme = darkTheme) {
        DetailScreenContent(
            uiState = uiState,
            isRefreshing = isRefreshing,
            onRefresh = {},
            onBack = {},
        )
    }
}

private fun previewCondition(icon: WeatherIcon, isDaytime: Boolean = true) = WeatherCondition(
    owmCode = 800,
    group = icon.name,
    description = icon.name.lowercase(),
    icon = icon,
    isDaytime = isDaytime,
)

private fun previewState(
    icon: WeatherIcon = WeatherIcon.CLEAR,
    isDaytime: Boolean = true,
    stale: Boolean = false,
    location: Location = chicago,
): DetailUiState.Success {
    val forecast = previewForecast.copy(
        location = location,
        current = previewForecast.current.copy(
            condition = previewCondition(icon = icon, isDaytime = isDaytime),
        ),
    )
    return DetailUiState.Success(
        forecast = forecast,
        stale = stale,
        lastUpdated = forecast.fetchedAt,
    )
}

@Preview
@Composable
private fun DetailLoadingLightPreview() = DetailScreenPreview(DetailUiState.Loading, darkTheme = false)

@Preview
@Composable
private fun DetailLoadingDarkPreview() = DetailScreenPreview(DetailUiState.Loading, darkTheme = true)

@Preview
@Composable
private fun DetailErrorLightPreview() = DetailScreenPreview(DetailUiState.Error(WeatherError.Network), darkTheme = false)

@Preview
@Composable
private fun DetailErrorDarkPreview() = DetailScreenPreview(DetailUiState.Error(WeatherError.Network), darkTheme = true)

@Preview
@Composable
private fun DetailRefreshingLightPreview() = DetailScreenPreview(
    uiState = previewState(),
    darkTheme = false,
    isRefreshing = true,
)

@Preview
@Composable
private fun DetailRefreshingDarkPreview() = DetailScreenPreview(
    uiState = previewState(),
    darkTheme = true,
    isRefreshing = true,
)

@Preview
@Composable
private fun DetailCachedOfflineLightPreview() = DetailScreenPreview(
    uiState = previewState(stale = true).copy(refreshError = WeatherError.Network),
    darkTheme = false,
)

@Preview
@Composable
private fun DetailCachedOfflineDarkPreview() = DetailScreenPreview(
    uiState = previewState(stale = true).copy(refreshError = WeatherError.Network),
    darkTheme = true,
)

@Preview
@Composable
private fun DetailClearDayFreshLightPreview() = DetailScreenPreview(previewState(), darkTheme = false)

@Preview
@Composable
private fun DetailClearDayFreshDarkPreview() = DetailScreenPreview(previewState(), darkTheme = true)

@Preview
@Composable
private fun DetailClearNightLightPreview() = DetailScreenPreview(
    previewState(isDaytime = false),
    darkTheme = false,
)

@Preview
@Composable
private fun DetailClearNightDarkPreview() = DetailScreenPreview(
    previewState(isDaytime = false),
    darkTheme = true,
)

@Preview
@Composable
private fun DetailCloudsPreview() = DetailScreenPreview(
    previewState(icon = WeatherIcon.CLOUDS),
    darkTheme = false,
)

@Preview
@Composable
private fun DetailRainStalePreview() = DetailScreenPreview(
    previewState(icon = WeatherIcon.RAIN, stale = true),
    darkTheme = false,
)

@Preview
@Composable
private fun DetailDrizzlePreview() = DetailScreenPreview(
    previewState(icon = WeatherIcon.DRIZZLE),
    darkTheme = true,
)

@Preview
@Composable
private fun DetailThunderstormPreview() = DetailScreenPreview(
    previewState(icon = WeatherIcon.THUNDERSTORM, isDaytime = false),
    darkTheme = true,
)

@Preview
@Composable
private fun DetailSnowPreview() = DetailScreenPreview(
    previewState(icon = WeatherIcon.SNOW),
    darkTheme = false,
)

@Preview
@Composable
private fun DetailAtmospherePreview() = DetailScreenPreview(
    previewState(icon = WeatherIcon.ATMOSPHERE),
    darkTheme = false,
)

@Preview
@Composable
private fun DetailUnknownPreview() = DetailScreenPreview(
    previewState(icon = WeatherIcon.UNKNOWN, isDaytime = false),
    darkTheme = true,
)

@Preview
@Composable
private fun DetailLongLocationPreview() = DetailScreenPreview(
    previewState(
        location = chicago.copy(name = "Thành phố Hồ Chí Minh Metropolitan Area"),
    ),
    darkTheme = false,
)
