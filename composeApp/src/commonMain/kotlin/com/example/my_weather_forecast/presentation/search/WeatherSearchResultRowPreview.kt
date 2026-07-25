package com.example.my_weather_forecast.presentation.search

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import org.jetbrains.compose.ui.tooling.preview.Preview

private val previewSearchLocation = Location(
    id = 0,
    name = "Ho Chi Minh City",
    country = "Vietnam",
    state = null,
    lat = 10.8231,
    lon = 106.6297,
    sortOrder = 0,
)

@Composable
private fun WeatherSearchResultRowPreview(
    darkTheme: Boolean,
    pressed: Boolean,
) {
    WeatherForecastTheme(darkTheme = darkTheme) {
        Surface {
            val interactionSource = remember { MutableInteractionSource() }

            LaunchedEffect(interactionSource, pressed) {
                if (pressed) {
                    interactionSource.emit(PressInteraction.Press(Offset.Zero))
                }
            }

            WeatherSearchResultRow(
                location = previewSearchLocation,
                pressFeedback = WeatherPressFeedback.TONAL_HIGHLIGHT,
                onClick = {},
                modifier = Modifier.padding(WeatherSpacing.Lg),
                interactionSource = interactionSource,
            )
        }
    }
}

@Preview
@Composable
private fun WeatherSearchResultRowLightPreview() {
    WeatherSearchResultRowPreview(darkTheme = false, pressed = false)
}

@Preview
@Composable
private fun WeatherSearchResultRowDarkPreview() {
    WeatherSearchResultRowPreview(darkTheme = true, pressed = false)
}

@Preview
@Composable
private fun WeatherSearchResultRowPressedLightPreview() {
    WeatherSearchResultRowPreview(darkTheme = false, pressed = true)
}

@Preview
@Composable
private fun WeatherSearchResultRowPressedDarkPreview() {
    WeatherSearchResultRowPreview(darkTheme = true, pressed = true)
}
