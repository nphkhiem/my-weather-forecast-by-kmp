package com.example.my_weather_forecast.presentation.search

import app.cash.turbine.test
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.usecase.AddLocationUseCase
import com.example.my_weather_forecast.testutil.FakeCitySearchRepository
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import com.example.my_weather_forecast.testutil.runMainDispatcherTest
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SearchViewModelTest {

    private val citySearchRepository = FakeCitySearchRepository()
    private val savedLocationRepository = FakeSavedLocationRepository()

    private val london = Location(
        id = 0, name = "London", country = "GB", state = null, lat = 51.5074, lon = -0.1278, sortOrder = 0,
    )

    private fun viewModel() = SearchViewModel(
        citySearchRepository = citySearchRepository,
        addLocationUseCase = AddLocationUseCase(savedLocationRepository),
    )

    @Test
    fun givenAQuery_whenTyped_thenAfterDebounceSearchCityIsCalledOnce() = runMainDispatcherTest {
        citySearchRepository.result = AppResult.Success(listOf(london))
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())

            viewModel.onQueryChange("Lon")
            viewModel.onQueryChange("Lond")
            viewModel.onQueryChange("London")

            assertEquals(SearchUiState.Loading, awaitItem())
            val results = awaitItem()
            assertIs<SearchUiState.Results>(results)
            assertEquals(1, results.locations.size)
        }
        assertEquals(1, citySearchRepository.searchCallCount)
    }

    @Test
    fun givenResults_whenAddAndCountLessThan6_thenAddedEventAndAreaPersisted() = runMainDispatcherTest {
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.addLocation(london)
            assertEquals(SearchEvent.Added, awaitItem())
        }

        val saved = savedLocationRepository.observeAll().first()
        assertEquals(1, saved.size)
        assertEquals("London", saved.first().name)
    }

    @Test
    fun given6AlreadySaved_whenAdd_thenAtLimitEventAndNothingPersisted() = runMainDispatcherTest {
        val existing = (1..6).map {
            Location(id = it.toLong(), name = "City$it", country = "US", state = null, lat = it.toDouble(), lon = it.toDouble(), sortOrder = it - 1)
        }
        existing.forEach { savedLocationRepository.add(it) }
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.addLocation(london)
            assertEquals(SearchEvent.AtLimit, awaitItem())
        }
        assertEquals(6, savedLocationRepository.observeAll().first().size)
    }

    @Test
    fun givenEmptyResults_whenSearched_thenEmptyState() = runMainDispatcherTest {
        citySearchRepository.result = AppResult.Failure(WeatherError.NotFound)
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            viewModel.onQueryChange("Nonexistent City Xyz")
            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Empty, awaitItem())
        }
    }

    @Test
    fun givenResultsWithDuplicateCoordinates_whenSearched_thenOnlyOneIsKept() = runMainDispatcherTest {
        val budapestArea = Location(
            id = 0, name = "Sárospatak", country = "HU", state = null, lat = 47.8221556, lon = 17.6157716, sortOrder = 0,
        )
        val duplicate = budapestArea.copy(name = "Sarospatak")
        citySearchRepository.result = AppResult.Success(listOf(budapestArea, duplicate))
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            viewModel.onQueryChange("Sâp")
            assertEquals(SearchUiState.Loading, awaitItem())
            val results = awaitItem()
            assertIs<SearchUiState.Results>(results)
            assertEquals(1, results.locations.size)
        }
    }

    @Test
    fun givenOrderedUniqueResults_whenSearched_thenRepositoryOrderIsPreserved() = runMainDispatcherTest {
        val londonOntario = london.copy(
            name = "London",
            country = "CA",
            state = "Ontario",
            lat = 42.9849,
            lon = -81.2453,
        )
        val londonKentucky = london.copy(
            name = "London",
            country = "US",
            state = "Kentucky",
            lat = 37.1289,
            lon = -84.0833,
        )
        citySearchRepository.result = AppResult.Success(
            listOf(london, londonOntario, londonKentucky),
        )
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            viewModel.onQueryChange("London")
            assertEquals(SearchUiState.Loading, awaitItem())
            val results = awaitItem()
            assertIs<SearchUiState.Results>(results)
            assertEquals(
                listOf(london, londonOntario, londonKentucky),
                results.locations,
            )
        }
    }

    @Test
    fun givenAnAlreadySavedLocation_whenAdded_thenAlreadySavedEventIsEmitted() = runMainDispatcherTest {
        savedLocationRepository.add(london)
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.addLocation(london.copy(id = 99))
            assertEquals(SearchEvent.AlreadySaved, awaitItem())
        }

        assertEquals(1, savedLocationRepository.observeAll().first().size)
    }
}
