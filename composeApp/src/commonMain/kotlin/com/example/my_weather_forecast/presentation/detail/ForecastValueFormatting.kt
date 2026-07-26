package com.example.my_weather_forecast.presentation.detail

import kotlin.math.roundToInt

internal fun Double.toForecastPercent(): Int = (coerceIn(0.0, 1.0) * 100).roundToInt()

internal fun Double.toForecastTemperature(): Int = roundToInt()
