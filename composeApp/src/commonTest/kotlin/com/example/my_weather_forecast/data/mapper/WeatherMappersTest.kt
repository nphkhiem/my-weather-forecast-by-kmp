package com.example.my_weather_forecast.data.mapper

import com.example.my_weather_forecast.data.remote.dto.OneCallResponseDto
import com.example.my_weather_forecast.data.remote.dto.WeatherConditionDto
import com.example.my_weather_forecast.data.remote.dto.fixtures.ONE_CALL_SAMPLE_JSON
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherMappersTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val response = json.decodeFromString(OneCallResponseDto.serializer(), ONE_CALL_SAMPLE_JSON)
    private val location = Location(id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0)

    @Test
    fun givenADailyDto_whenMapped_thenFieldsMatchAndDateIsInLocationsLocalContext() {
        val domain = response.daily.first().toDomain(response.timezoneOffsetSeconds)

        assertEquals(LocalDate(2024, 1, 1), domain.date)
        assertEquals(275.15, domain.tempMin)
        assertEquals(283.15, domain.tempMax)
        assertEquals(80, domain.humidity)
        assertEquals(5.2, domain.windSpeed)
        assertEquals(0.6, domain.pop)
        assertEquals(500, domain.condition.owmCode)
        assertEquals(WeatherIcon.RAIN, domain.condition.icon)
    }

    @Test
    fun givenTheFullDailyList_whenMapped_thenOrderedTodayThroughPlusSeven() {
        val daily = response.daily.map { it.toDomain(response.timezoneOffsetSeconds) }

        assertEquals(8, daily.size)
        assertEquals(LocalDate(2024, 1, 1), daily.first().date)
        assertEquals(LocalDate(2024, 1, 8), daily.last().date)
        daily.zipWithNext().forEach { (today, tomorrow) -> assertTrue(today.date < tomorrow.date) }
    }

    @Test
    fun givenCurrentAndHourly_whenMapped_thenCurrentPopComesFromNearestHour() {
        val current = response.current.toDomain(pop = response.hourly.first().pop)

        assertEquals(0.35, current.pop)
        assertEquals(282.55, current.temp)
        assertEquals(281.87, current.feelsLike)
        assertEquals(72, current.humidity)
        assertEquals(3.6, current.windSpeed)
        assertEquals(803, current.condition.owmCode)
    }

    @Test
    fun givenAnHourlyCondition_whenMappedThenSerialized_thenConditionRemainsInTheCachedPayload() {
        val hourly = response.hourly.first().toDomain()

        val payload = json.encodeToString(hourly)

        assertTrue("\"condition\"" in payload)
        assertTrue("\"owmCode\":500" in payload)
        assertTrue("\"icon\":\"RAIN\"" in payload)
    }

    @Test
    fun givenAFullOneCallResponse_whenMapped_thenForecastAssemblesLocationUnitsAndFetchedAt() {
        val fetchedAt = Instant.fromEpochSeconds(1704124800)

        val forecast = response.toDomain(location, Units.METRIC, fetchedAt)

        assertEquals(location, forecast.location)
        assertEquals(Units.METRIC, forecast.units)
        assertEquals(fetchedAt, forecast.fetchedAt)
        assertEquals(8, forecast.daily.size)
        assertEquals(3, forecast.hourly.size)
        assertEquals(response.hourly.first().pop, forecast.current.pop)
    }

    @Test
    fun givenWeatherConditionCodes_whenMapped_thenClassifiedIntoDomainIcon() {
        assertEquals(WeatherIcon.THUNDERSTORM, condition(211).icon)
        assertEquals(WeatherIcon.DRIZZLE, condition(310).icon)
        assertEquals(WeatherIcon.RAIN, condition(521).icon)
        assertEquals(WeatherIcon.SNOW, condition(611).icon)
        assertEquals(WeatherIcon.ATMOSPHERE, condition(741).icon)
        assertEquals(WeatherIcon.CLEAR, condition(800).icon)
        assertEquals(WeatherIcon.CLOUDS, condition(802).icon)
        assertEquals(WeatherIcon.UNKNOWN, condition(999).icon)
    }

    @Test
    fun givenADaytimeIconSuffix_whenMapped_thenIsDaytimeTrue() {
        assertTrue(condition(800, icon = "01d").isDaytime)
    }

    @Test
    fun givenANighttimeIconSuffix_whenMapped_thenIsDaytimeFalse() {
        assertEquals(false, condition(800, icon = "01n").isDaytime)
    }

    private fun condition(id: Int, icon: String = "01d") =
        WeatherConditionDto(id = id, main = "Test", description = "test", icon = icon).toDomain()
}
