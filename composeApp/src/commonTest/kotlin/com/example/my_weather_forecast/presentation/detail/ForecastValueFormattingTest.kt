package com.example.my_weather_forecast.presentation.detail

import kotlin.test.Test
import kotlin.test.assertEquals

class ForecastValueFormattingTest {

    @Test
    fun givenAPrecipitationProbability_whenFormatted_thenItRoundsToAWholePercent() {
        assertEquals(0, 0.0.toForecastPercent())
        assertEquals(36, 0.356.toForecastPercent())
        assertEquals(100, 1.0.toForecastPercent())
    }

    @Test
    fun givenAProviderProbabilityOutsideItsDocumentedRange_whenFormatted_thenItStaysWithinPercentBounds() {
        assertEquals(0, (-0.1).toForecastPercent())
        assertEquals(100, 1.1.toForecastPercent())
    }

    @Test
    fun givenAForecastTemperature_whenFormatted_thenItRoundsToAWholeDegree() {
        assertEquals(19, 18.6.toForecastTemperature())
        assertEquals(-3, (-2.6).toForecastTemperature())
    }
}
