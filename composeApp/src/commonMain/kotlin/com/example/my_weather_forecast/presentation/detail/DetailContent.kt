package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.CurrentConditions
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.theme.AnimatedWeatherIcon
import com.example.my_weather_forecast.presentation.theme.DetailHeroTokens
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
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
import myweatherforecast.composeapp.generated.resources.current_humidity_label
import myweatherforecast.composeapp.generated.resources.current_percent_value
import myweatherforecast.composeapp.generated.resources.current_rain_label
import myweatherforecast.composeapp.generated.resources.current_wind_label
import myweatherforecast.composeapp.generated.resources.current_wind_value
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
    val palette = state.forecast.current.condition.palette(darkTheme = WeatherTheme.darkTheme)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides palette.onGradient) {
            CurrentConditionsHero(
                current = state.forecast.current,
                units = state.forecast.units,
                lastUpdated = state.lastUpdated,
                stale = state.stale,
            )
            HourlyRainStrip(state.forecast.hourly.todayOnly(today))
            DailyForecastPanel(
                daily = state.forecast.daily,
                today = today,
                units = state.forecast.units,
            )
        }
    }
}

@Composable
private fun CurrentConditionsHero(
    current: CurrentConditions,
    units: Units,
    lastUpdated: Instant,
    stale: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    val palette = current.condition.palette(darkTheme = WeatherTheme.darkTheme)
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
    val freshnessLabel = stringResource(Res.string.updated_at, lastUpdated.toClockLabel()) +
        if (stale) stringResource(Res.string.stale_suffix) else ""

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics {
                contentDescription = "$accessibilityDescription. $freshnessLabel"
            },
        shape = MaterialTheme.shapes.extraLarge,
        color = colors.weatherSurface,
        contentColor = colors.textPrimary,
        border = BorderStroke(
            DetailHeroTokens.BorderWidth,
            colors.border.copy(alpha = DetailHeroTokens.BorderAlpha),
        ),
    ) {
        Column(modifier = Modifier.padding(DetailHeroTokens.Padding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DetailHeroTokens.TopLineGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FreshnessStatus(
                    label = freshnessLabel,
                    stale = stale,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = conditionName,
                    color = palette.accent,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = DetailHeroTokens.MainMinHeight),
                horizontalArrangement = Arrangement.spacedBy(DetailHeroTokens.MainGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.temp_degrees, temp),
                        style = MaterialTheme.typography.displayMedium,
                    )
                    Text(
                        text = stringResource(Res.string.feels_like, feelsLike),
                        color = colors.textSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                AnimatedWeatherIcon(
                    icon = current.condition.icon,
                    isDaytime = current.condition.isDaytime,
                    contentDescription = null,
                    modifier = Modifier.size(DetailHeroTokens.IconSize),
                )
            }

            CurrentMetricRow(
                humidity = current.humidity,
                windSpeed = windSpeed,
                windUnitLabel = windUnitLabel,
                rainPercent = popPercent,
            )
        }
    }
}

@Composable
private fun FreshnessStatus(label: String, stale: Boolean, modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(WeatherSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(DetailHeroTokens.StatusDotSize)
                .background(
                    color = if (stale) colors.warning else colors.success,
                    shape = MaterialTheme.shapes.extraSmall,
                ),
        )
        Text(
            text = label,
            color = colors.textSecondary,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CurrentMetricRow(
    humidity: Int,
    windSpeed: Int,
    windUnitLabel: String,
    rainPercent: Int,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailHeroTokens.MetricDividerWidth)
                .background(colors.divider),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = DetailHeroTokens.MetricTopPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CurrentMetric(
                label = stringResource(Res.string.current_humidity_label),
                value = stringResource(Res.string.current_percent_value, humidity),
                modifier = Modifier.weight(1f),
            )
            MetricDivider()
            CurrentMetric(
                label = stringResource(Res.string.current_wind_label),
                value = stringResource(Res.string.current_wind_value, windSpeed, windUnitLabel),
                modifier = Modifier.weight(1f),
            )
            MetricDivider()
            CurrentMetric(
                label = stringResource(Res.string.current_rain_label),
                value = stringResource(Res.string.current_percent_value, rainPercent),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CurrentMetric(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(WeatherSpacing.Hairline),
    ) {
        Text(text = label, color = colors.textTertiary, style = MaterialTheme.typography.labelSmall)
        Text(text = value, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun MetricDivider(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .width(DetailHeroTokens.MetricDividerWidth)
            .height(DetailHeroTokens.MetricDividerHeight)
            .background(WeatherTheme.colors.divider),
    )
}
