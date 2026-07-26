package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.HourlyForecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import com.example.my_weather_forecast.testutil.FakeWeatherRepository
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.math.abs
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

    private fun setContentWithArea(stale: Boolean = false): FakeWeatherRepository {
        val savedLocationRepository = FakeSavedLocationRepository()
        val weatherRepository = FakeWeatherRepository()
        runBlocking { savedLocationRepository.add(chicago) }
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = stale))

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
        composeTestRule.onNodeWithContentDescription("Mon", substring = true).assertExists()
    }

    @Test
    fun givenAResolvedForecast_whenHeroRenders_thenConditionAndMetricHierarchyIsVisible() {
        setContentWithArea()

        composeTestRule.onNodeWithText("Clouds", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Humidity", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Wind", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Rain", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun givenAResolvedForecast_whenPanelsRender_thenHourlyAndDailySectionsAreDistinct() {
        setContentWithArea()

        composeTestRule.onNodeWithText("Today by hour").assertExists()
        composeTestRule.onNodeWithText("Daily outlook").assertExists()
    }

    @Test
    fun givenMixedHourlyConditions_whenHourlyPanelRenders_thenEachItemSummarizesItsReadingHierarchy() {
        val hourly = listOf(
            HourlyForecast(
                time = Instant.fromEpochSeconds(0),
                temp = 18.6,
                pop = 0.0,
                windSpeed = 2.0,
                condition = WeatherCondition(
                    owmCode = 500,
                    group = "Rain",
                    description = "light rain",
                    icon = WeatherIcon.RAIN,
                    isDaytime = true,
                ),
            ),
        )
        composeTestRule.setContent {
            WeatherForecastTheme {
                HourlyRainStrip(hourly)
            }
        }

        composeTestRule.onNodeWithText("Today by hour").assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Rain, 19 degrees, 0 percent chance of rain", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun givenNoRemainingHours_whenHourlyPanelRenders_thenItExplainsTheEmptyState() {
        composeTestRule.setContent {
            WeatherForecastTheme {
                HourlyRainStrip(emptyList())
            }
        }

        composeTestRule.onNodeWithText("No more hourly forecasts today.").assertIsDisplayed()
    }

    @Test
    fun givenManyHours_whenTheLastHourIsRequested_thenTheHourlyPanelScrollsHorizontallyToIt() {
        val condition = WeatherCondition(
            owmCode = 800,
            group = "Clear",
            description = "clear sky",
            icon = WeatherIcon.CLEAR,
            isDaytime = true,
        )
        val hourly = (0..8).map { index ->
            HourlyForecast(
                time = Instant.fromEpochSeconds(index * 3_600L),
                temp = 20.0 + index,
                pop = index / 10.0,
                windSpeed = 2.0,
                condition = condition,
            )
        }
        composeTestRule.setContent {
            WeatherForecastTheme {
                HourlyRainStrip(hourly, modifier = Modifier.requiredWidth(320.dp))
            }
        }

        composeTestRule.onNodeWithTag(HOURLY_FORECAST_LIST_TEST_TAG)
            .performScrollToNode(hasContentDescription("Clear, 28 degrees", substring = true))

        composeTestRule
            .onNodeWithContentDescription("Clear, 28 degrees", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun givenDailyTemperaturesWithDifferentWidths_whenRowsRender_thenTemperatureColumnsStayAligned() {
        val today = LocalDate(2026, 7, 26)
        val condition = WeatherCondition(
            owmCode = 500,
            group = "Rain",
            description = "light rain",
            icon = WeatherIcon.RAIN,
            isDaytime = true,
        )
        val daily = listOf(
            DailyForecast(today, 22.0, 31.0, 60, 2.0, 0.0, condition),
            DailyForecast(LocalDate(2026, 7, 27), -120.0, -110.0, 70, 3.0, 1.0, condition),
        )
        composeTestRule.setContent {
            WeatherForecastTheme {
                DailyForecastPanel(
                    daily = daily,
                    today = today,
                    units = Units.METRIC,
                    modifier = Modifier.requiredWidth(390.dp),
                )
            }
        }

        val firstTemperatureX = composeTestRule
            .onNodeWithText("H 31°", useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot
            .left
        val secondTemperatureX = composeTestRule
            .onNodeWithText("H -110°", useUnmergedTree = true)
            .fetchSemanticsNode()
            .boundsInRoot
            .left

        assertTrue(abs(firstTemperatureX - secondTemperatureX) < 1f)
    }

    @Test
    fun givenCompactWidthAndLargeText_whenDailyRowsRender_thenReadingsStayInsideThePanel() {
        val today = LocalDate(2026, 7, 26)
        val daily = DailyForecast(
            date = today,
            tempMin = 22.0,
            tempMax = 31.0,
            humidity = 60,
            windSpeed = 2.0,
            pop = 1.0,
            condition = WeatherCondition(
                owmCode = 500,
                group = "Rain",
                description = "light rain",
                icon = WeatherIcon.RAIN,
                isDaytime = true,
            ),
        )
        composeTestRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                WeatherForecastTheme {
                    DailyForecastPanel(
                        daily = listOf(daily),
                        today = today,
                        units = Units.METRIC,
                        modifier = Modifier.requiredWidth(320.dp),
                    )
                }
            }
        }

        val panelBounds = composeTestRule
            .onNodeWithTag(DAILY_FORECAST_PANEL_TEST_TAG)
            .fetchSemanticsNode()
            .boundsInRoot
        listOf("H 31°", "L 22°", "100% rain", "Wind 2 m/s  ·  Humidity 60%").forEach { label ->
            val readingBounds = composeTestRule
                .onNodeWithText(label, useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot
            assertTrue(readingBounds.left >= panelBounds.left)
            assertTrue(readingBounds.right <= panelBounds.right)
        }
    }

    @Test
    fun givenAStaleForecast_whenHeroRenders_thenMergedSummaryCommunicatesStaleness() {
        setContentWithArea(stale = true)

        composeTestRule
            .onNodeWithContentDescription("Data may be out of date", substring = true)
            .assertIsDisplayed()
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
