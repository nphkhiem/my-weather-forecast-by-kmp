package com.example.my_weather_forecast.presentation.overview

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeLeft
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.usecase.AddLocationUseCase
import com.example.my_weather_forecast.domain.usecase.ObserveSavedLocationsUseCase
import com.example.my_weather_forecast.domain.usecase.RemoveLocationUseCase
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import com.example.my_weather_forecast.testutil.FakeWeatherRepository
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OverviewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val chicago = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )

    private fun viewModel(savedLocationRepository: FakeSavedLocationRepository, weatherRepository: FakeWeatherRepository) =
        OverviewViewModel(
            observeSavedLocationsUseCase = ObserveSavedLocationsUseCase(savedLocationRepository),
            removeLocationUseCase = RemoveLocationUseCase(savedLocationRepository),
            addLocationUseCase = AddLocationUseCase(savedLocationRepository),
            weatherRepository = weatherRepository,
            unitsPreference = FakeUnitsPreference(),
        )

    private fun setContentWithArea(
        onOpenSearch: () -> Unit = {},
        onOpenDetail: (Long) -> Unit = {},
    ): Pair<FakeSavedLocationRepository, FakeWeatherRepository> {
        val savedLocationRepository = FakeSavedLocationRepository()
        val weatherRepository = FakeWeatherRepository()
        runBlocking { savedLocationRepository.add(chicago) }
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    OverviewScreen(
                        onOpenSearch = onOpenSearch,
                        onOpenSettings = {},
                        onOpenDetail = onOpenDetail,
                        viewModel = viewModel(savedLocationRepository, weatherRepository),
                    )
                }
            }
        }
        return savedLocationRepository to weatherRepository
    }

    @Test
    fun givenSavedAreas_whenLaunched_thenOverviewRendersThem() {
        setContentWithArea()

        composeTestRule.onNodeWithText("Chicago", substring = true, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Your places").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 of 6").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add a place").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clouds", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("H 24°", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("L 15°", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("20% rain", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun givenSavedArea_whenCardClicked_thenDetailOpens() {
        var openedLocationId: Long? = null
        setContentWithArea(onOpenDetail = { openedLocationId = it })

        composeTestRule
            .onNodeWithContentDescription(
                "Chicago, 21 degrees, high 24, low 15, 20 percent chance of rain",
            )
            .performClick()
        composeTestRule.waitForIdle()

        assertEquals(chicago.id, openedLocationId)
    }

    @Test
    fun givenSavedAreas_whenAddPlaceClicked_thenSearchOpens() {
        var searchOpened = false
        setContentWithArea(onOpenSearch = { searchOpened = true })

        composeTestRule.onNodeWithText("Add a place").performClick()

        assertEquals(true, searchOpened)
    }

    @Test
    fun givenSavedArea_whenRendered_thenCardIsOneMergedActionWithDecorativeIcon() {
        setContentWithArea()

        composeTestRule
            .onAllNodesWithContentDescription(
                "Chicago, 21 degrees, high 24, low 15, 20 percent chance of rain",
            )
            .assertCountEquals(1)
        composeTestRule
            .onNodeWithContentDescription(
                "Chicago, 21 degrees, high 24, low 15, 20 percent chance of rain",
            )
            .assertHasClickAction()
        composeTestRule.onAllNodesWithContentDescription("Clouds").assertCountEquals(0)
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun givenSavedArea_whenAccessibilityDeleteRuns_thenAreaIsRemoved() {
        setContentWithArea()

        composeTestRule
            .onNodeWithContentDescription(
                "Chicago, 21 degrees, high 24, low 15, 20 percent chance of rain",
            )
            .performCustomAccessibilityActionWithLabel("Delete Chicago")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Chicago removed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Undo").assertIsDisplayed()
    }

    @Test
    fun givenNoSavedAreas_whenLaunched_thenEmptyStateShowsAddCityCta() {
        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    OverviewScreen(
                        onOpenSearch = {},
                        onOpenSettings = {},
                        onOpenDetail = {},
                        viewModel = viewModel(FakeSavedLocationRepository(), FakeWeatherRepository()),
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("No saved areas yet. Tap + to add one.").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add area").assertIsDisplayed()
    }

    @Test
    fun givenSavedAreas_whenPulledToRefresh_thenRefreshIsCalled() {
        val (_, weatherRepository) = setContentWithArea()

        composeTestRule.onNodeWithTag(OVERVIEW_CONTENT_TEST_TAG)
            .performTouchInput { swipeDown(startY = 0f, endY = bottom) }
        composeTestRule.waitForIdle()

        assertEquals(1, weatherRepository.refreshCallCount)
    }

    @Test
    fun givenACardIsSwiped_whenSwipeCompletes_thenAreaRemovedAndUndoSnackbarAppears() {
        setContentWithArea()

        composeTestRule.onNodeWithText("Chicago", substring = true, useUnmergedTree = true)
            .performTouchInput { swipeLeft() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Chicago removed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Undo").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription(
                "Chicago, 21 degrees, high 24, low 15, 20 percent chance of rain",
            )
            .assertIsDisplayed()
    }
}
