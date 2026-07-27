package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.theme.ForecastPanelTokens
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import com.example.my_weather_forecast.presentation.theme.readableName
import com.example.my_weather_forecast.presentation.theme.WeatherConditionIcon
import kotlin.math.roundToInt
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.area_high
import myweatherforecast.composeapp.generated.resources.area_low
import myweatherforecast.composeapp.generated.resources.area_rain
import myweatherforecast.composeapp.generated.resources.daily_accessibility
import myweatherforecast.composeapp.generated.resources.daily_humidity_line
import myweatherforecast.composeapp.generated.resources.daily_wind_line
import myweatherforecast.composeapp.generated.resources.day_fri
import myweatherforecast.composeapp.generated.resources.day_mon
import myweatherforecast.composeapp.generated.resources.day_sat
import myweatherforecast.composeapp.generated.resources.day_sun
import myweatherforecast.composeapp.generated.resources.day_thu
import myweatherforecast.composeapp.generated.resources.day_today
import myweatherforecast.composeapp.generated.resources.day_tue
import myweatherforecast.composeapp.generated.resources.day_wed
import myweatherforecast.composeapp.generated.resources.detail_daily_hint
import myweatherforecast.composeapp.generated.resources.detail_daily_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal const val DAILY_FORECAST_PANEL_TEST_TAG = "daily_forecast_panel"

@Composable
fun DailyForecastPanel(
    daily: List<DailyForecast>,
    today: LocalDate,
    units: Units,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    val dateLabels = daily.map { it.date }.dailyDateLabels()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag(DAILY_FORECAST_PANEL_TEST_TAG),
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
                title = stringResource(Res.string.detail_daily_title),
                hint = stringResource(Res.string.detail_daily_hint),
            )
            Spacer(modifier = Modifier.height(ForecastPanelTokens.HeaderBottomSpace))
            daily.forEachIndexed { index, forecast ->
                DailyRow(
                    daily = forecast,
                    today = today,
                    dateLabel = dateLabels[index],
                    units = units,
                )
                if (index < daily.lastIndex) {
                    HorizontalDivider(
                        thickness = ForecastPanelTokens.BorderWidth,
                        color = colors.divider,
                    )
                }
            }
        }
    }
}

@Composable
fun DailyRow(daily: DailyForecast, today: LocalDate, dateLabel: String, units: Units, modifier: Modifier = Modifier) {
    val dayLabel = daily.date.dayLabel(today)
    val conditionName = daily.condition.icon.readableName()
    val windUnitLabel = stringResource(units.windSpeedUnitLabelRes())
    val tempMax = daily.tempMax.toForecastTemperature()
    val tempMin = daily.tempMin.toForecastTemperature()
    val popPercent = daily.pop.toForecastPercent()
    val windSpeed = daily.windSpeed.roundToInt()
    val accessibilityDescription = stringResource(
        Res.string.daily_accessibility,
        dayLabel, dateLabel, conditionName, tempMax, tempMin, popPercent, windSpeed, windUnitLabel, daily.humidity,
    )

    val windLine = stringResource(Res.string.daily_wind_line, windSpeed, windUnitLabel)
    val humidityLine = stringResource(Res.string.daily_humidity_line, daily.humidity)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ForecastPanelTokens.DailyVerticalPadding)
            .semantics(mergeDescendants = true) { contentDescription = accessibilityDescription },
    ) {
        BoxWithConstraints {
            val useStackedLayout =
                maxWidth < ForecastPanelTokens.DailyCompactBreakpoint ||
                    LocalDensity.current.fontScale >= ForecastPanelTokens.DailyLargeFontScale
            if (useStackedLayout) {
                Column {
                    DailyStackedReading(
                        dayLabel = dayLabel,
                        dateLabel = dateLabel,
                        daily = daily,
                        tempMax = tempMax,
                        tempMin = tempMin,
                        popPercent = popPercent,
                    )
                    DailySupportingLines(
                        windLine = windLine,
                        humidityLine = humidityLine,
                        modifier = Modifier.padding(top = ForecastPanelTokens.DailySupportingTopSpace),
                    )
                }
            } else {
                DailyColumnReading(
                    dayLabel = dayLabel,
                    dateLabel = dateLabel,
                    daily = daily,
                    tempMax = tempMax,
                    tempMin = tempMin,
                    popPercent = popPercent,
                    windLine = windLine,
                    humidityLine = humidityLine,
                )
            }
        }
    }
}

@Composable
private fun DailyColumnReading(
    dayLabel: String,
    dateLabel: String,
    daily: DailyForecast,
    tempMax: Int,
    tempMin: Int,
    popPercent: Int,
    windLine: String,
    humidityLine: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ForecastPanelTokens.DailyColumnGap),
    ) {
        DailyDateLabel(
            dayLabel = dayLabel,
            dateLabel = dateLabel,
            modifier = Modifier.weight(ForecastPanelTokens.DailyLabelWeight),
        )
        DailyConditionIcon(daily)
        DailyTemperature(
            tempMax = tempMax,
            tempMin = tempMin,
            modifier = Modifier.weight(ForecastPanelTokens.DailyTemperatureWeight),
        )
        // Rain, wind, and humidity form one right-hand block, so the day, icon, and temperature
        // centre against the whole block instead of hugging the top of the row.
        Column(
            modifier = Modifier.weight(ForecastPanelTokens.DailyReadingsWeight),
            verticalArrangement = Arrangement.spacedBy(ForecastPanelTokens.DailySupportingGap),
        ) {
            DailyRain(popPercent = popPercent, modifier = Modifier.fillMaxWidth())
            DailySupportingLines(windLine = windLine, humidityLine = humidityLine)
        }
    }
}

/** Wind and humidity each take their own line so their right edges align across rows. */
@Composable
private fun DailySupportingLines(
    windLine: String,
    humidityLine: String,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ForecastPanelTokens.DailySupportingGap),
    ) {
        listOf(windLine, humidityLine).forEach { supportingLine ->
            Text(
                text = supportingLine,
                color = colors.textTertiary,
                style = WeatherTypography.Caption,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DailyStackedReading(
    dayLabel: String,
    dateLabel: String,
    daily: DailyForecast,
    tempMax: Int,
    tempMin: Int,
    popPercent: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ForecastPanelTokens.DailyColumnGap),
    ) {
        DailyDateLabel(
            dayLabel = dayLabel,
            dateLabel = dateLabel,
            modifier = Modifier.weight(1f),
        )
        DailyConditionIcon(daily)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = ForecastPanelTokens.DailyStackedTopSpace),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ForecastPanelTokens.DailyColumnGap),
    ) {
        DailyTemperature(
            tempMax = tempMax,
            tempMin = tempMin,
            modifier = Modifier.weight(1f),
        )
        DailyRain(popPercent = popPercent)
    }
}

@Composable
private fun DailyDateLabel(dayLabel: String, dateLabel: String, modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Column(modifier = modifier) {
        // The inner column wraps to the wider of the two labels, so the date centres under the day
        // name while the pair stays left aligned inside its track.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ForecastPanelTokens.DailyLabelGap),
        ) {
            Text(
                text = dayLabel,
                color = colors.textPrimary,
                style = WeatherTypography.ItemTitle,
            )
            Text(
                text = dateLabel,
                color = colors.textSecondary,
                style = WeatherTypography.Micro,
            )
        }
    }
}

@Composable
private fun DailyConditionIcon(daily: DailyForecast, modifier: Modifier = Modifier) {
    WeatherConditionIcon(
        icon = daily.condition.icon,
        isDaytime = daily.condition.isDaytime,
        contentDescription = null,
        modifier = modifier.size(ForecastPanelTokens.DailyIconSize),
    )
}

@Composable
private fun DailyTemperature(tempMax: Int, tempMin: Int, modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.area_high, tempMax),
            color = colors.textPrimary,
            style = WeatherTypography.BodyStrong,
        )
        Text(
            text = stringResource(Res.string.area_low, tempMin),
            color = colors.textSecondary,
            style = WeatherTypography.Caption,
        )
    }
}

@Composable
private fun DailyRain(popPercent: Int, modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Text(
        text = stringResource(Res.string.area_rain, popPercent),
        modifier = modifier,
        color = if (popPercent > 0) colors.info else colors.textTertiary,
        style = WeatherTypography.Label,
        textAlign = TextAlign.End,
    )
}

@Composable
private fun LocalDate.dayLabel(today: LocalDate): String =
    if (this == today) stringResource(Res.string.day_today) else stringResource(dayOfWeek.shortLabelRes())

private fun DayOfWeek.shortLabelRes(): StringResource = when (this) {
    DayOfWeek.MONDAY -> Res.string.day_mon
    DayOfWeek.TUESDAY -> Res.string.day_tue
    DayOfWeek.WEDNESDAY -> Res.string.day_wed
    DayOfWeek.THURSDAY -> Res.string.day_thu
    DayOfWeek.FRIDAY -> Res.string.day_fri
    DayOfWeek.SATURDAY -> Res.string.day_sat
    DayOfWeek.SUNDAY -> Res.string.day_sun
}
