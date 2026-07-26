package com.example.my_weather_forecast.presentation.detail

import app.cash.turbine.test
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import com.example.my_weather_forecast.testutil.FakeWeatherRepository
import com.example.my_weather_forecast.testutil.runMainDispatcherTest
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class DetailViewModelTest {

    private val savedLocationRepository = FakeSavedLocationRepository()
    private val weatherRepository = FakeWeatherRepository()
    private val unitsPreference = FakeUnitsPreference()

    private val chicago = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )

    private fun testDetail(locationId: Long = chicago.id, body: suspend TestScope.(DetailViewModel) -> Unit) =
        runMainDispatcherTest {
            val viewModel = DetailViewModel(
                locationId = locationId,
                savedLocationRepository = savedLocationRepository,
                weatherRepository = weatherRepository,
                unitsPreference = unitsPreference,
            )
            body(viewModel)
        }

    @Test
    fun givenALocationId_whenObserved_thenSuccessWithTheFullForecast() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        val forecast = sampleForecast(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(forecast, stale = false))

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            val success = awaitItem()
            assertIs<DetailUiState.Success>(success)
            assertEquals(forecast, success.forecast)
            assertEquals(forecast.daily, success.forecast.daily)
            assertEquals(forecast.hourly, success.forecast.hourly)
        }
    }

    @Test
    fun givenAStaleCache_whenObserved_thenSuccessFlaggedStaleWithLastUpdated() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        val forecast = sampleForecast(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(forecast, stale = true))

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            val success = awaitItem()
            assertIs<DetailUiState.Success>(success)
            assertEquals(true, success.stale)
            assertEquals(forecast.fetchedAt, success.lastUpdated)
        }
    }

    @Test
    fun givenARefreshFailureWithCache_whenObserved_thenSuccessRetainsTheFailureContext() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        val forecast = sampleForecast(chicago)
        weatherRepository.setObservation(
            chicago.id,
            ForecastObservation.Success(
                forecast = forecast,
                stale = true,
                error = WeatherError.Network,
            ),
        )

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            val success = awaitItem()
            assertIs<DetailUiState.Success>(success)
            assertEquals(forecast, success.forecast)
            assertEquals(WeatherError.Network, success.refreshError)
        }
    }

    @Test
    fun givenNoCacheAndOffline_whenObserved_thenError() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Error(WeatherError.Network))

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            assertEquals(DetailUiState.Error(WeatherError.Network), awaitItem())
        }
    }

    @Test
    fun givenTheAreaIsNoLongerSaved_whenObserved_thenError() = testDetail { viewModel ->
        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            assertEquals(DetailUiState.Error(WeatherError.NotFound), awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenUnitsPreferenceChanges_whenObserved_thenWeatherRepositoryObservesWithTheNewUnits() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            assertEquals(Units.METRIC, weatherRepository.lastObservedUnits)

            // The fake returns the same cached forecast regardless of units, so the resulting
            // uiState is value-equal and StateFlow conflates it away; assert the side effect
            // (which units observe() was actually called with) instead of awaiting a new item.
            unitsPreference.setUnits(Units.IMPERIAL)
            advanceUntilIdle()
            assertEquals(Units.IMPERIAL, weatherRepository.lastObservedUnits)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun givenARefresh_whenInFlight_thenIsRefreshingReflectsProgressAndWeatherRepositoryIsCalled() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.isRefreshing.test {
                assertEquals(false, awaitItem())
                viewModel.refresh()
                assertEquals(true, awaitItem())
                assertEquals(false, awaitItem())
            }
        }

        assertEquals(1, weatherRepository.refreshCallCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenCachedSuccess_whenManualRefreshFails_thenCachedForecastRemainsWithFailureContext() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        val forecast = sampleForecast(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(forecast, stale = false))
        weatherRepository.refreshResult = AppResult.Failure(WeatherError.Network)

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            assertEquals(forecast, (awaitItem() as DetailUiState.Success).forecast)

            viewModel.refresh()
            advanceUntilIdle()

            val afterFailure = awaitItem()
            assertIs<DetailUiState.Success>(afterFailure)
            assertEquals(forecast, afterFailure.forecast)
            assertEquals(WeatherError.Network, afterFailure.refreshError)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenManualRefreshFailure_whenFreshForecastArrives_thenFailureContextClears() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        val cached = sampleForecast(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(cached, stale = false))
        weatherRepository.refreshResult = AppResult.Failure(WeatherError.Network)

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            awaitItem()

            viewModel.refresh()
            advanceUntilIdle()
            assertEquals(WeatherError.Network, (awaitItem() as DetailUiState.Success).refreshError)

            val fresh = sampleForecast(chicago, fetchedAtEpochMillis = 1704124860_000L)
            weatherRepository.setObservation(chicago.id, ForecastObservation.Success(fresh, stale = false))

            val recovered = awaitItem()
            assertIs<DetailUiState.Success>(recovered)
            assertEquals(fresh, recovered.forecast)
            assertNull(recovered.refreshError)
        }
    }
}
