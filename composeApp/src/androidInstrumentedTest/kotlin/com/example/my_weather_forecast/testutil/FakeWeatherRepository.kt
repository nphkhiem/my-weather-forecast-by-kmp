package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.repository.WeatherRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherRepository : WeatherRepository {
    private val observations = mutableMapOf<Long, MutableStateFlow<ForecastObservation>>()
    var refreshResult: AppResult<Unit> = AppResult.Success(Unit)
    var refreshGate: CompletableDeferred<Unit>? = null
    var refreshCallCount = 0
        private set
    var lastObservedUnits: Units? = null
        private set

    fun setObservation(locationId: Long, observation: ForecastObservation) {
        stateFor(locationId).value = observation
    }

    private fun stateFor(locationId: Long) = observations.getOrPut(locationId) { MutableStateFlow(ForecastObservation.Loading) }

    override fun observe(location: Location, units: Units): Flow<ForecastObservation> {
        lastObservedUnits = units
        return stateFor(location.id)
    }

    override suspend fun refresh(location: Location, units: Units): AppResult<Unit> {
        refreshCallCount++
        refreshGate?.await()
        return refreshResult
    }
}
