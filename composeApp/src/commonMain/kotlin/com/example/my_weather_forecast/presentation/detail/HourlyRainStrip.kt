package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.example.my_weather_forecast.domain.model.HourlyForecast
import com.example.my_weather_forecast.presentation.theme.ForecastPanelTokens
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import com.example.my_weather_forecast.presentation.theme.accentColor
import com.example.my_weather_forecast.presentation.theme.readableName
import com.example.my_weather_forecast.presentation.theme.toDrawableResource
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.area_rain
import myweatherforecast.composeapp.generated.resources.detail_hourly_empty
import myweatherforecast.composeapp.generated.resources.detail_hourly_hint
import myweatherforecast.composeapp.generated.resources.detail_hourly_title
import myweatherforecast.composeapp.generated.resources.hourly_accessibility
import myweatherforecast.composeapp.generated.resources.temp_degrees
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal const val HOURLY_FORECAST_LIST_TEST_TAG = "hourly_forecast_list"

@Composable
fun HourlyRainStrip(hourly: List<HourlyForecast>, modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(WeatherRadii.Card),
        color = colors.weatherSurface,
        contentColor = colors.textPrimary,
        border = BorderStroke(
            ForecastPanelTokens.BorderWidth,
            colors.border.copy(alpha = ForecastPanelTokens.BorderAlpha),
        ),
    ) {
        Column(modifier = Modifier.padding(ForecastPanelTokens.Padding)) {
            ForecastPanelHeading(
                title = stringResource(Res.string.detail_hourly_title),
                hint = stringResource(Res.string.detail_hourly_hint),
            )
            Spacer(modifier = Modifier.height(ForecastPanelTokens.HeaderBottomSpace))
            if (hourly.isEmpty()) {
                Text(
                    text = stringResource(Res.string.detail_hourly_empty),
                    color = colors.textSecondary,
                    style = WeatherTypography.Body,
                    modifier = Modifier.padding(vertical = ForecastPanelTokens.EmptyVerticalPadding),
                )
            } else {
                LazyRow(
                    modifier = Modifier.testTag(HOURLY_FORECAST_LIST_TEST_TAG),
                    horizontalArrangement = Arrangement.spacedBy(ForecastPanelTokens.HourGap),
                ) {
                    items(hourly, key = { it.time.toEpochMilliseconds() }) { hour ->
                        HourlyForecastItem(hour)
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyForecastItem(hour: HourlyForecast, modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    val hourLabel = hour.time.toHourLabel()
    val conditionName = hour.condition.icon.readableName()
    val temperature = hour.temp.toForecastTemperature()
    val popPercent = hour.pop.toForecastPercent()
    val accessibilityDescription = stringResource(
        Res.string.hourly_accessibility,
        hourLabel,
        conditionName,
        temperature,
        popPercent,
    )
    Surface(
        modifier = modifier
            .widthIn(min = ForecastPanelTokens.HourMinWidth)
            .clearAndSetSemantics { contentDescription = accessibilityDescription },
        shape = MaterialTheme.shapes.medium,
        color = colors.surfaceMuted,
        contentColor = colors.textPrimary,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ForecastPanelTokens.HourContentGap),
            modifier = Modifier.padding(
                horizontal = ForecastPanelTokens.HourHorizontalPadding,
                vertical = ForecastPanelTokens.HourVerticalPadding,
            ),
        ) {
            Text(
                text = hourLabel,
                color = colors.textSecondary,
                style = WeatherTypography.Caption,
            )
            Icon(
                painter = painterResource(hour.condition.icon.toDrawableResource()),
                contentDescription = null,
                tint = hour.condition.icon.accentColor(
                    isDaytime = hour.condition.isDaytime,
                    darkTheme = WeatherTheme.darkTheme,
                ),
                modifier = Modifier.size(ForecastPanelTokens.HourIconSize),
            )
            Text(
                text = stringResource(Res.string.temp_degrees, temperature),
                style = WeatherTypography.ItemTitle,
            )
            Text(
                text = stringResource(Res.string.area_rain, popPercent),
                color = colors.textSecondary,
                style = WeatherTypography.Micro,
            )
        }
    }
}

@Composable
internal fun ForecastPanelHeading(title: String, hint: String, modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ForecastPanelTokens.HeaderGap),
    ) {
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            color = colors.textPrimary,
            style = WeatherTypography.SectionTitle,
        )
        Text(
            text = hint,
            color = colors.textSecondary,
            style = WeatherTypography.Caption,
        )
    }
}
