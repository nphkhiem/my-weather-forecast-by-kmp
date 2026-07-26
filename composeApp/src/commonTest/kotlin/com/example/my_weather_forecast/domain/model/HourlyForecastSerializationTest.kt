package com.example.my_weather_forecast.domain.model

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class HourlyForecastSerializationTest {

    @Test
    fun givenALegacyCachedHourWithoutACondition_whenDecoded_thenItUsesTheUnknownConditionFallback() {
        val legacyPayload = """
            {
              "time": "1970-01-01T00:00:00Z",
              "temp": 18.0,
              "pop": 0.0,
              "windSpeed": 2.0
            }
        """.trimIndent()

        val hourly = Json.decodeFromString<HourlyForecast>(legacyPayload)

        assertEquals(WeatherIcon.UNKNOWN, hourly.condition.icon)
    }
}
