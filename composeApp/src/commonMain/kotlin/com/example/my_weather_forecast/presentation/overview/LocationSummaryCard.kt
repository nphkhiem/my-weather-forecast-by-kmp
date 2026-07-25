package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.presentation.theme.AnimatedWeatherIcon
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.conditionPalette
import com.example.my_weather_forecast.presentation.theme.readableName
import kotlin.math.roundToInt
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.area_accessibility
import myweatherforecast.composeapp.generated.resources.area_accessibility_stale_suffix
import myweatherforecast.composeapp.generated.resources.area_summary_line
import myweatherforecast.composeapp.generated.resources.delete_area
import myweatherforecast.composeapp.generated.resources.ic_delete
import myweatherforecast.composeapp.generated.resources.temp_degrees
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSummaryCard(
    area: AreaSummary,
    onClick: () -> Unit,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove(area.id)
                true
            } else {
                false
            }
        },
    )

    val todayHigh = area.todayHigh.roundToInt()
    val todayLow = area.todayLow.roundToInt()
    val rainChance = (area.rainChance * 100).roundToInt()
    val currentTemp = area.currentTemp.roundToInt()
    val staleSuffix = if (area.stale) stringResource(Res.string.area_accessibility_stale_suffix) else ""
    val accessibilityDescription = stringResource(
        Res.string.area_accessibility,
        area.name, currentTemp, todayHigh, todayLow, rainChance, staleSuffix,
    )
    val palette = area.icon.conditionPalette(
        isDaytime = area.isDaytime,
        darkTheme = WeatherTheme.darkTheme,
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = stringResource(Res.string.delete_area, area.name),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        Card(
            onClick = onClick,
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(
                    Brush.linearGradient(listOf(palette.gradientStart, palette.gradientEnd)),
                    MaterialTheme.shapes.medium,
                )
                .semantics { contentDescription = accessibilityDescription },
        ) {
            CompositionLocalProvider(LocalContentColor provides palette.onGradient) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnimatedWeatherIcon(
                        icon = area.icon,
                        isDaytime = area.isDaytime,
                        contentDescription = area.icon.readableName(),
                        modifier = Modifier.size(40.dp),
                    )
                    Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                        Text(area.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(Res.string.area_summary_line, todayHigh, todayLow, rainChance),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Text(
                        stringResource(Res.string.temp_degrees, currentTemp),
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }
        }
    }
}
