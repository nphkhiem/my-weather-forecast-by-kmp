package com.example.my_weather_forecast.presentation.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import com.example.my_weather_forecast.testutil.FakeWeatherRepository
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val chicago = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )

    private fun viewModel(savedLocationRepository: FakeSavedLocationRepository, weatherRepository: FakeWeatherRepository) =
        DetailViewModel(
            locationId = chicago.id,
            savedLocationRepository = savedLocationRepository,
            weatherRepository = weatherRepository,
            unitsPreference = FakeUnitsPreference(),
        )

    private fun setContentWithArea(): FakeWeatherRepository {
        val savedLocationRepository = FakeSavedLocationRepository()
        val weatherRepository = FakeWeatherRepository()
        runBlocking { savedLocationRepository.add(chicago) }
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    DetailScreen(
                        locationId = chicago.id,
                        onBack = {},
                        viewModel = viewModel(savedLocationRepository, weatherRepository),
                    )
                }
            }
        }
        return weatherRepository
    }

    @Test
    fun givenAResolvedForecast_whenLaunched_thenDetailRendersItsFullForecast() {
        setContentWithArea()

        composeTestRule.onNodeWithText("Chicago").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mon", substring = false).assertIsDisplayed()
    }

    @Test
    fun givenTheBackButton_whenTapped_thenOnBackIsInvoked() {
        val savedLocationRepository = FakeSavedLocationRepository()
        val weatherRepository = FakeWeatherRepository()
        runBlocking { savedLocationRepository.add(chicago) }
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))
        var backInvoked = false

        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    DetailScreen(
                        locationId = chicago.id,
                        onBack = { backInvoked = true },
                        viewModel = viewModel(savedLocationRepository, weatherRepository),
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assertTrue(backInvoked)
    }

    @Test
    fun givenNoCache_whenAreaNoLongerSaved_thenErrorMessageShown() {
        val savedLocationRepository = FakeSavedLocationRepository()
        val weatherRepository = FakeWeatherRepository()
        weatherRepository.setObservation(chicago.id, ForecastObservation.Error(WeatherError.Network))

        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    DetailScreen(
                        locationId = chicago.id,
                        onBack = {},
                        viewModel = viewModel(savedLocationRepository, weatherRepository),
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Could not find weather data for this area.").assertIsDisplayed()
    }

    @Test
    fun givenAreaWithCache_whenPulledToRefresh_thenRefreshIsCalled() {
        val weatherRepository = setContentWithArea()

        composeTestRule.onNodeWithTag(DETAIL_CONTENT_TEST_TAG)
            .performTouchInput { swipeDown(startY = 0f, endY = bottom) }
        composeTestRule.waitForIdle()

        assertEquals(1, weatherRepository.refreshCallCount)
    }
}
