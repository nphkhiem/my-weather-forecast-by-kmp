package com.example.my_weather_forecast.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.my_weather_forecast.domain.model.WeatherIcon
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun WeatherThemeReference(modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(WeatherSpacing.Xxl),
            verticalArrangement = Arrangement.spacedBy(WeatherSpacing.Lg),
        ) {
            Text("Today’s weather", style = MaterialTheme.typography.titleLarge)
            Text(
                "Soft atmosphere, clear hierarchy, and platform-native type.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text("24°", style = MaterialTheme.typography.displayMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(WeatherSpacing.Sm),
            ) {
                WeatherIcon.entries.forEach { icon ->
                    val palette = icon.conditionPalette(
                        isDaytime = true,
                        darkTheme = WeatherTheme.darkTheme,
                    )
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .size(WeatherLayout.MinimumTouchTarget)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(palette.gradientStart, palette.gradientEnd),
                                ),
                                shape = MaterialTheme.shapes.medium,
                            ),
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        shape = MaterialTheme.shapes.medium,
                        content = {},
                    )
                }
            }
            Surface(
                color = WeatherTheme.colors.surfaceMuted,
                shape = MaterialTheme.shapes.large,
                tonalElevation = WeatherElevation.Resting,
            ) {
                Text(
                    text = "Rain easing later · High 27° · Low 22°",
                    modifier = Modifier.padding(WeatherSpacing.Lg),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview
@Composable
private fun WeatherThemeLightPreview() {
    WeatherForecastTheme(darkTheme = false) {
        WeatherThemeReference()
    }
}

@Preview
@Composable
private fun WeatherThemeDarkPreview() {
    WeatherForecastTheme(darkTheme = true) {
        WeatherThemeReference()
    }
}
