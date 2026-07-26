package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.HourlyForecast
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.ui.tooling.preview.Preview

private val previewToday = LocalDate(2026, 7, 26)
private val previewConditions = listOf(
    WeatherIcon.CLEAR,
    WeatherIcon.CLOUDS,
    WeatherIcon.DRIZZLE,
    WeatherIcon.RAIN,
    WeatherIcon.THUNDERSTORM,
    WeatherIcon.SNOW,
    WeatherIcon.ATMOSPHERE,
)

private fun panelPreviewCondition(icon: WeatherIcon, isDaytime: Boolean = true) = WeatherCondition(
    owmCode = 800,
    group = icon.name,
    description = icon.name.lowercase(),
    icon = icon,
    isDaytime = isDaytime,
)

private fun panelPreviewHourly(count: Int, zeroPrecipitation: Boolean = false) = (0 until count).map { index ->
    HourlyForecast(
        time = Instant.fromEpochSeconds(1785020400L + index * 3_600L),
        temp = 28.0 - index * 0.6,
        pop = if (zeroPrecipitation) 0.0 else (index * 0.13).coerceAtMost(1.0),
        windSpeed = 2.5,
        condition = panelPreviewCondition(
            icon = previewConditions[index % previewConditions.size],
            isDaytime = index < 5,
        ),
    )
}

private fun panelPreviewDaily(count: Int, zeroPrecipitation: Boolean = false) = (0 until count).map { index ->
    DailyForecast(
        date = previewToday.plus(DatePeriod(days = index)),
        tempMin = 22.0 - index,
        tempMax = 31.0 - index * 0.5,
        humidity = 56 + index * 4,
        windSpeed = 2.0 + index * 0.7,
        pop = if (zeroPrecipitation) 0.0 else (index * 0.16).coerceAtMost(1.0),
        condition = panelPreviewCondition(previewConditions[index % previewConditions.size]),
    )
}

@Composable
private fun ForecastPanelsPreview(
    hourly: List<HourlyForecast>,
    daily: List<DailyForecast>,
    darkTheme: Boolean,
    width: Dp,
    height: Dp,
    fontScale: Float = 1f,
) {
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
        WeatherForecastTheme(darkTheme = darkTheme) {
            Surface(
                modifier = Modifier.requiredSize(width, height),
                color = WeatherTheme.colors.canvas,
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = WeatherLayout.ReadingMaxWidth)
                            .verticalScroll(rememberScrollState())
                            .padding(WeatherSpacing.Lg),
                        verticalArrangement = Arrangement.spacedBy(WeatherSpacing.Lg),
                    ) {
                        HourlyRainStrip(hourly)
                        DailyForecastPanel(
                            daily = daily,
                            today = previewToday,
                            units = Units.METRIC,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ForecastPanelsCompactMixedLightPreview() = ForecastPanelsPreview(
    hourly = panelPreviewHourly(count = 10),
    daily = panelPreviewDaily(count = 7),
    darkTheme = false,
    width = 390.dp,
    height = 844.dp,
)

@Preview
@Composable
private fun ForecastPanelsCompactMixedDarkPreview() = ForecastPanelsPreview(
    hourly = panelPreviewHourly(count = 10),
    daily = panelPreviewDaily(count = 7),
    darkTheme = true,
    width = 390.dp,
    height = 844.dp,
)

@Preview
@Composable
private fun ForecastPanelsShortZeroRainPreview() = ForecastPanelsPreview(
    hourly = panelPreviewHourly(count = 2, zeroPrecipitation = true),
    daily = panelPreviewDaily(count = 2, zeroPrecipitation = true),
    darkTheme = false,
    width = 320.dp,
    height = 640.dp,
)

@Preview
@Composable
private fun ForecastPanelsNoRemainingHoursPreview() = ForecastPanelsPreview(
    hourly = emptyList(),
    daily = panelPreviewDaily(count = 3),
    darkTheme = false,
    width = 390.dp,
    height = 700.dp,
)

@Preview
@Composable
private fun ForecastPanelsLargeTextPreview() = ForecastPanelsPreview(
    hourly = panelPreviewHourly(count = 3),
    daily = panelPreviewDaily(count = 3),
    darkTheme = false,
    width = 390.dp,
    height = 844.dp,
    fontScale = 2f,
)

@Preview
@Composable
private fun ForecastPanelsExpandedPreview() = ForecastPanelsPreview(
    hourly = panelPreviewHourly(count = 12),
    daily = panelPreviewDaily(count = 7),
    darkTheme = false,
    width = 900.dp,
    height = 700.dp,
)
