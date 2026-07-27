package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.usecase.AddLocationUseCase
import com.example.my_weather_forecast.domain.usecase.ObserveSavedLocationsUseCase
import com.example.my_weather_forecast.domain.usecase.RemoveLocationUseCase
import com.example.my_weather_forecast.presentation.components.WEATHER_SCREEN_CONTENT_TEST_TAG
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import com.example.my_weather_forecast.testutil.FakeWeatherRepository
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlin.math.absoluteValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OverviewScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val chicago = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )

    private fun viewModel(
        savedLocationRepository: FakeSavedLocationRepository,
        weatherRepository: FakeWeatherRepository,
    ) = OverviewViewModel(
        observeSavedLocationsUseCase = ObserveSavedLocationsUseCase(savedLocationRepository),
        removeLocationUseCase = RemoveLocationUseCase(savedLocationRepository),
        addLocationUseCase = AddLocationUseCase(savedLocationRepository),
        weatherRepository = weatherRepository,
        unitsPreference = FakeUnitsPreference(),
    )

    private fun setContentWithArea(
        onOpenSearch: () -> Unit = {},
        onOpenDetail: (Long) -> Unit = {},
        stale: Boolean = false,
    ): Pair<FakeSavedLocationRepository, FakeWeatherRepository> {
        val savedLocationRepository = FakeSavedLocationRepository()
        val weatherRepository = FakeWeatherRepository()
        runBlocking { savedLocationRepository.add(chicago) }
        weatherRepository.setObservation(
            chicago.id,
            ForecastObservation.Success(sampleForecast(chicago), stale = stale),
        )

        setContent(
            savedLocationRepository = savedLocationRepository,
            weatherRepository = weatherRepository,
            onOpenSearch = onOpenSearch,
            onOpenDetail = onOpenDetail,
        )
        return savedLocationRepository to weatherRepository
    }

    private fun setContent(
        savedLocationRepository: FakeSavedLocationRepository,
        weatherRepository: FakeWeatherRepository,
        onOpenSearch: () -> Unit = {},
        onOpenDetail: (Long) -> Unit = {},
    ) {
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
    fun givenNoSavedAreas_whenLaunched_thenEmptyStateShowsAddPlaceAction() {
        var searchOpened = false
        setContent(
            savedLocationRepository = FakeSavedLocationRepository(),
            weatherRepository = FakeWeatherRepository(),
            onOpenSearch = { searchOpened = true },
        )

        composeTestRule.onNodeWithText("Your weather starts here").assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Save a place to see its forecast at a glance.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Add a place").assertIsDisplayed().performClick()

        assertEquals(true, searchOpened)
        composeTestRule.onAllNodesWithContentDescription("Add area").assertCountEquals(0)
    }

    @Test
    fun givenNoSavedAreas_whenLaunched_thenEmptyStateReachesScreenCenter() {
        setContent(
            savedLocationRepository = FakeSavedLocationRepository(),
            weatherRepository = FakeWeatherRepository(),
        )

        val screenCenterY = composeTestRule.onRoot().fetchSemanticsNode().boundsInRoot.center.y
        val emptyStateBottom = composeTestRule
            .onNodeWithTag(OVERVIEW_STATE_SURFACE_TEST_TAG)
            .fetchSemanticsNode()
            .boundsInRoot
            .bottom

        assertTrue(
            "Expected empty-state bottom $emptyStateBottom to reach screen center $screenCenterY",
            emptyStateBottom >= screenCenterY,
        )
    }

    @Test
    fun givenOverview_whenRendered_thenPlacesSectionHasComfortableTopInset() {
        setContent(
            savedLocationRepository = FakeSavedLocationRepository(),
            weatherRepository = FakeWeatherRepository(),
        )

        val contentTop = composeTestRule
            .onNodeWithTag(WEATHER_SCREEN_CONTENT_TEST_TAG)
            .fetchSemanticsNode()
            .boundsInRoot
            .top
        val placesTop = composeTestRule
            .onNodeWithText("Your places")
            .fetchSemanticsNode()
            .boundsInRoot
            .top
        val minimumInset = with(composeTestRule.density) { 12.dp.toPx() }

        assertTrue(
            "Expected places section inset ${placesTop - contentTop} to be at least $minimumInset",
            placesTop - contentTop >= minimumInset,
        )
    }

    @Test
    fun givenSavedAreasStillLoading_whenLaunched_thenReservedLoadingStateIsShown() {
        val savedLocationRepository = FakeSavedLocationRepository()
        runBlocking { savedLocationRepository.add(chicago) }

        setContent(savedLocationRepository, FakeWeatherRepository())

        composeTestRule.onNodeWithText("Checking your places").assertIsDisplayed()
        composeTestRule.onNodeWithText("Loading the latest forecasts.").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Chicago", substring = true).assertCountEquals(0)
    }

    @Test
    fun givenNoCachedForecast_whenLoadingFails_thenRetryAndAddRemainAvailable() {
        var searchOpened = false
        val savedLocationRepository = FakeSavedLocationRepository()
        val weatherRepository = FakeWeatherRepository()
        runBlocking { savedLocationRepository.add(chicago) }
        weatherRepository.setObservation(chicago.id, ForecastObservation.Error(WeatherError.Network))
        setContent(
            savedLocationRepository = savedLocationRepository,
            weatherRepository = weatherRepository,
            onOpenSearch = { searchOpened = true },
        )

        composeTestRule.onNodeWithText("Forecasts are unavailable").assertIsDisplayed()
        composeTestRule.onNodeWithText("No internet connection. Try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try again").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Add a place").performClick()

        assertEquals(1, weatherRepository.refreshCallCount)
        assertEquals(true, searchOpened)
    }

    @Test
    fun givenCachedForecast_whenRefreshIsInProgress_thenContentStaysVisibleWithStatus() {
        val (_, weatherRepository) = setContentWithArea()
        val refreshGate = CompletableDeferred<Unit>()
        weatherRepository.refreshGate = refreshGate

        composeTestRule.onNodeWithTag(OVERVIEW_CONTENT_TEST_TAG)
            .performTouchInput { swipeDown(startY = 0f, endY = bottom) }

        composeTestRule.onNodeWithText("Updating forecasts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chicago", substring = true, useUnmergedTree = true).assertIsDisplayed()

        refreshGate.complete(Unit)
        composeTestRule.waitForIdle()
    }

    @Test
    fun givenCachedForecast_whenRefreshPartiallyFails_thenContentStaysVisibleWithFeedback() {
        val (_, weatherRepository) = setContentWithArea()
        weatherRepository.refreshResult = AppResult.Failure(WeatherError.Network)

        composeTestRule.onNodeWithTag(OVERVIEW_CONTENT_TEST_TAG)
            .performTouchInput { swipeDown(startY = 0f, endY = bottom) }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("overview_snackbar").assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Some forecasts could not be updated. Showing available information.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Chicago", substring = true, useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun givenCachedForecastIsStale_whenRendered_thenForecastRemainsVisibleWithFreshnessContext() {
        setContentWithArea(stale = true)

        composeTestRule.onNodeWithText("Data may be out of date", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription(
                "Chicago, 21 degrees, high 24, low 15, 20 percent chance of rain, data may be out of date",
            )
            .assertIsDisplayed()
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

    @Test
    fun givenAPhoneWidth_whenOverviewRenders_thenSavedPlacesStackInOneColumn() {
        setTwoAreasAtWidth(400.dp)

        val first = cardBounds("Chicago")
        val second = cardBounds("Denver")

        assertTrue(
            "Chicago bottom ${first.bottom} must sit above Denver top ${second.top} on a phone",
            first.bottom <= second.top,
        )
    }

    @Test
    fun givenAnExpandedWidth_whenOverviewRenders_thenSavedPlacesShareARow() {
        setTwoAreasAtWidth(1_000.dp)

        val first = cardBounds("Chicago")
        val second = cardBounds("Denver")

        assertTrue(
            "Chicago right ${first.right} must sit left of Denver left ${second.left} when expanded",
            first.right <= second.left,
        )
    }

    private fun cardBounds(city: String) = composeTestRule
        .onNodeWithContentDescription(city, substring = true)
        .getUnclippedBoundsInRoot()

    private fun setTwoAreasAtWidth(width: Dp) {
        val denver = Location(
            id = 2, name = "Denver", country = "US", state = "CO", lat = 39.74, lon = -104.98, sortOrder = 1,
        )
        val savedLocationRepository = FakeSavedLocationRepository()
        val weatherRepository = FakeWeatherRepository()
        runBlocking {
            savedLocationRepository.add(chicago)
            savedLocationRepository.add(denver)
        }
        listOf(chicago, denver).forEach { location ->
            weatherRepository.setObservation(
                location.id,
                ForecastObservation.Success(sampleForecast(location), stale = false),
            )
        }

        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    Box(modifier = Modifier.requiredWidth(width)) {
                        OverviewScreen(
                            onOpenSearch = {},
                            onOpenSettings = {},
                            onOpenDetail = {},
                            viewModel = viewModel(savedLocationRepository, weatherRepository),
                        )
                    }
                }
            }
        }
        composeTestRule.waitForIdle()
    }


    @Test
    fun givenASavedPlaceCard_whenRendered_thenTheTemperatureCentresAgainstTheLeftContent() {
        setContentWithArea()

        val name = textDpBounds("Chicago")
        val lastMetric = textDpBounds("20% rain")
        val temperature = textDpBounds("21°")
        val leftCentre = (name.top + lastMetric.bottom) / 2
        val temperatureCentre = (temperature.top + temperature.bottom) / 2

        assertTrue(
            "Temperature centre $temperatureCentre must line up with left content centre $leftCentre",
            (temperatureCentre - leftCentre).value.absoluteValue <= 1f,
        )
    }

    private fun textDpBounds(text: String) = composeTestRule
        .onNodeWithText(text, substring = true, useUnmergedTree = true)
        .getUnclippedBoundsInRoot()

}
