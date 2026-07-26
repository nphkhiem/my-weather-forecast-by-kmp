package com.example.my_weather_forecast.data.mapper

import com.example.my_weather_forecast.data.remote.dto.CurrentDto
import com.example.my_weather_forecast.data.remote.dto.DailyDto
import com.example.my_weather_forecast.data.remote.dto.GeoResultDto
import com.example.my_weather_forecast.data.remote.dto.HourlyDto
import com.example.my_weather_forecast.data.remote.dto.OneCallResponseDto
import com.example.my_weather_forecast.data.remote.dto.WeatherConditionDto
import com.example.my_weather_forecast.domain.model.CurrentConditions
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.HourlyForecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun WeatherConditionDto.toDomain(): WeatherCondition = WeatherCondition(
    owmCode = id,
    group = main,
    description = description,
    icon = id.toWeatherIcon(),
    // OWM's icon code always ends in "d" (day) or "n" (night), tied to that location's own
    // sunrise/sunset rather than the device's local clock.
    isDaytime = !icon.endsWith("n"),
)

private fun Int.toWeatherIcon(): WeatherIcon = when (this) {
    in 200..299 -> WeatherIcon.THUNDERSTORM
    in 300..399 -> WeatherIcon.DRIZZLE
    in 500..599 -> WeatherIcon.RAIN
    in 600..699 -> WeatherIcon.SNOW
    in 700..799 -> WeatherIcon.ATMOSPHERE
    800 -> WeatherIcon.CLEAR
    in 801..804 -> WeatherIcon.CLOUDS
    else -> WeatherIcon.UNKNOWN
}

// OWM's `current` block has no pop field; callers pass in the nearest hour's pop instead.
fun CurrentDto.toDomain(pop: Double): CurrentConditions = CurrentConditions(
    temp = temp,
    feelsLike = feelsLike,
    humidity = humidity,
    windSpeed = windSpeed,
    pop = pop,
    condition = weather.first().toDomain(),
)

fun HourlyDto.toDomain(): HourlyForecast = HourlyForecast(
    time = Instant.fromEpochSeconds(dt),
    temp = temp,
    pop = pop,
    windSpeed = windSpeed,
    condition = weather.first().toDomain(),
)

fun DailyDto.toDomain(timezoneOffsetSeconds: Int): DailyForecast = DailyForecast(
    date = Instant.fromEpochSeconds(dt + timezoneOffsetSeconds).toLocalDateTime(TimeZone.UTC).date,
    tempMin = temp.min,
    tempMax = temp.max,
    humidity = humidity,
    windSpeed = windSpeed,
    pop = pop,
    condition = weather.first().toDomain(),
)

// id/sortOrder are placeholders; a real value is assigned when the location is persisted (Phase 2).
fun GeoResultDto.toDomain(): Location = Location(
    id = 0L,
    name = name,
    country = country,
    state = state,
    lat = lat,
    lon = lon,
    sortOrder = 0,
)

fun OneCallResponseDto.toDomain(location: Location, units: Units, fetchedAt: Instant): Forecast = Forecast(
    location = location,
    current = current.toDomain(pop = hourly.first().pop),
    daily = daily.map { it.toDomain(timezoneOffsetSeconds) },
    hourly = hourly.map { it.toDomain() },
    units = units,
    fetchedAt = fetchedAt,
)
