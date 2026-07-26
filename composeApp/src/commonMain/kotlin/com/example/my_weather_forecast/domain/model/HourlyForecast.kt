package com.example.my_weather_forecast.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class HourlyForecast(
    val time: Instant,
    val temp: Double,
    /** Probability of precipitation, 0.0..1.0. */
    val pop: Double,
    val windSpeed: Double,
    val condition: WeatherCondition = WeatherCondition(
        owmCode = 0,
        group = "Unknown",
        description = "unknown conditions",
        icon = WeatherIcon.UNKNOWN,
        isDaytime = true,
    ),
)
