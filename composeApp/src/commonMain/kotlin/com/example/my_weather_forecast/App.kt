package com.example.my_weather_forecast

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.my_weather_forecast.core.preference.ThemePreference
import com.example.my_weather_forecast.presentation.navigation.WeatherNavHost
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.platform.WeatherSystemBarIconTone
import com.example.my_weather_forecast.presentation.platform.WeatherSystemBars
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.presentation.theme.rememberEffectiveDarkTheme
import org.koin.compose.koinInject

@Composable
fun App(themePreference: ThemePreference = koinInject()) {
    WeatherPlatformBehaviorProvider {
        val darkTheme = rememberEffectiveDarkTheme(themePreference)

        WeatherForecastTheme(darkTheme = darkTheme) {
            WeatherSystemBars(
                iconTone = if (darkTheme) {
                    WeatherSystemBarIconTone.LIGHT
                } else {
                    WeatherSystemBarIconTone.DARK
                },
            )
            Surface(modifier = Modifier.fillMaxSize()) {
                WeatherNavHost()
            }
        }
    }
}
