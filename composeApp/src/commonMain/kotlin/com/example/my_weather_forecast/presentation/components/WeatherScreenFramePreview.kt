package com.example.my_weather_forecast.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun WeatherScreenFrameReference() {
    WeatherPlatformBehaviorProvider {
        WeatherForecastTheme {
            WeatherScreenFrame(
                title = "Weather",
                contentMaxWidth = WeatherLayout.FormMaxWidth,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(WeatherSpacing.Lg),
                    verticalArrangement = Arrangement.spacedBy(WeatherSpacing.Lg),
                ) {
                    Text("Adaptive content frame", style = MaterialTheme.typography.titleMedium)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Text(
                            text = "Content stays readable while the atmospheric canvas reaches every edge.",
                            modifier = Modifier.padding(WeatherSpacing.Xxl),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun WeatherScreenFrameCompactPreview() {
    Surface(modifier = Modifier.requiredSize(width = 390.dp, height = 844.dp)) {
        WeatherScreenFrameReference()
    }
}

@Preview
@Composable
private fun WeatherScreenFrameExpandedPreview() {
    Surface(modifier = Modifier.requiredSize(width = 1_000.dp, height = 700.dp)) {
        WeatherScreenFrameReference()
    }
}
