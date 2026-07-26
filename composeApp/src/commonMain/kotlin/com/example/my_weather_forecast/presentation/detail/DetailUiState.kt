package com.example.my_weather_forecast.presentation.detail

import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Forecast
import kotlinx.datetime.Instant

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val forecast: Forecast,
        val stale: Boolean,
        val lastUpdated: Instant,
        val refreshError: WeatherError? = null,
    ) : DetailUiState

    data class Error(val error: WeatherError) : DetailUiState
}
