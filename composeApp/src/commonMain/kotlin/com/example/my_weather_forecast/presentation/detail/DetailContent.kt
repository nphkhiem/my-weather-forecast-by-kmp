package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.CurrentConditions
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.theme.AnimatedWeatherIcon
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.palette
import com.example.my_weather_forecast.presentation.theme.readableName
import kotlin.math.roundToInt
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.current_accessibility
import myweatherforecast.composeapp.generated.resources.current_summary_line
import myweatherforecast.composeapp.generated.resources.error_generic_pull_refresh
import myweatherforecast.composeapp.generated.resources.error_network_pull_refresh
import myweatherforecast.composeapp.generated.resources.error_not_found_weather
import myweatherforecast.composeapp.generated.resources.error_rate_limited
import myweatherforecast.composeapp.generated.resources.error_unauthorized_weather
import myweatherforecast.composeapp.generated.resources.feels_like
import myweatherforecast.composeapp.generated.resources.stale_suffix
import myweatherforecast.composeapp.generated.resources.temp_degrees
import myweatherforecast.composeapp.generated.resources.updated_at
import org.jetbrains.compose.resources.stringResource

const val DETAIL_CONTENT_TEST_TAG = "detail_content"

@Composable
fun DetailContent(uiState: DetailUiState, modifier: Modifier = Modifier) {
    val taggedModifier = modifier.testTag(DETAIL_CONTENT_TEST_TAG)
    when (uiState) {
        is DetailUiState.Loading -> LoadingContent(taggedModifier)
        is DetailUiState.Error -> ErrorContent(uiState.error, taggedModifier)
        is DetailUiState.Success -> SuccessContent(uiState, taggedModifier)
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(error: WeatherError, modifier: Modifier = Modifier) {
    val message = error.toMessage()
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun WeatherError.toMessage(): String = when (this) {
    WeatherError.Network -> stringResource(Res.string.error_network_pull_refresh)
    WeatherError.RateLimited -> stringResource(Res.string.error_rate_limited)
    WeatherError.Unauthorized -> stringResource(Res.string.error_unauthorized_weather)
    WeatherError.NotFound -> stringResource(Res.string.error_not_found_weather)
    WeatherError.AtLimit, WeatherError.AlreadySaved, is WeatherError.Unknown ->
        stringResource(Res.string.error_generic_pull_refresh)
}

@Composable
private fun SuccessContent(state: DetailUiState.Success, modifier: Modifier = Modifier) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val dateLabels = remember(state.forecast.daily) { state.forecast.daily.map { it.date }.dailyDateLabels() }
    val palette = state.forecast.current.condition.palette(darkTheme = WeatherTheme.darkTheme)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides palette.onGradient) {
            UpdatedBanner(state.lastUpdated, state.stale)
            CurrentConditionsHeader(state.forecast.current, state.forecast.units)
            Column {
                HourlyRainStrip(state.forecast.hourly.todayOnly(today))
                state.forecast.daily.forEachIndexed { index, daily ->
                    DailyRow(daily = daily, today = today, dateLabel = dateLabels[index], units = state.forecast.units)
                }
            }
        }
    }
}

@Composable
private fun UpdatedBanner(lastUpdated: Instant, stale: Boolean, modifier: Modifier = Modifier) {
    val label = stringResource(Res.string.updated_at, lastUpdated.toClockLabel()) +
        if (stale) stringResource(Res.string.stale_suffix) else ""
    Text(text = label, style = MaterialTheme.typography.labelMedium, modifier = modifier.fillMaxWidth())
}

@Composable
private fun CurrentConditionsHeader(current: CurrentConditions, units: Units, modifier: Modifier = Modifier) {
    val conditionName = current.condition.icon.readableName()
    val windUnitLabel = stringResource(units.windSpeedUnitLabelRes())
    val temp = current.temp.roundToInt()
    val feelsLike = current.feelsLike.roundToInt()
    val windSpeed = current.windSpeed.roundToInt()
    val popPercent = (current.pop * 100).roundToInt()
    val accessibilityDescription = stringResource(
        Res.string.current_accessibility,
        conditionName, temp, feelsLike, current.humidity, windSpeed, windUnitLabel, popPercent,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = accessibilityDescription },
    ) {
        AnimatedWeatherIcon(
            icon = current.condition.icon,
            isDaytime = current.condition.isDaytime,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
        )
        Text(stringResource(Res.string.temp_degrees, temp), style = MaterialTheme.typography.displayMedium)
        Text(stringResource(Res.string.feels_like, feelsLike), style = MaterialTheme.typography.bodyLarge)
        Text(
            stringResource(Res.string.current_summary_line, current.humidity, windSpeed, windUnitLabel, popPercent),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
