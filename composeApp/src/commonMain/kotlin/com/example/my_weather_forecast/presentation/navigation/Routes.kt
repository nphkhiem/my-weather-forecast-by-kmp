package com.example.my_weather_forecast.presentation.navigation

import kotlinx.serialization.Serializable

object Routes {
    @Serializable
    data object Overview

    @Serializable
    data object Search

    @Serializable
    data object Settings

    @Serializable
    data class Detail(val locationId: Long)
}
