package com.example.my_weather_forecast.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.overview.AreaSummary
import com.example.my_weather_forecast.presentation.overview.OverviewContent
import com.example.my_weather_forecast.presentation.overview.OverviewUiState
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.search.SearchContent
import com.example.my_weather_forecast.presentation.search.SearchUiState
import com.example.my_weather_forecast.presentation.settings.SettingsContent
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Cross-cutting accessibility and content-stress checks. These assert the properties every
 * refreshed screen must hold, rather than the details of any one screen, so a regression on any
 * route is caught in one place.
 *
 * Right-to-left is deliberately out of scope: the app ships English only, so RTL would only apply
 * to an English UI on an RTL device. The gap is recorded rather than partially covered.
 */
class AccessibilityPassTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenOverview_whenRendered_thenInteractiveTargetsMeetTheMinimumSize() {
        assertTargetsFor(Screen.OVERVIEW, fontScale = 1f)
    }

    @Test
    fun givenSearch_whenRendered_thenInteractiveTargetsMeetTheMinimumSize() {
        assertTargetsFor(Screen.SEARCH, fontScale = 1f)
    }

    @Test
    fun givenSettings_whenRendered_thenInteractiveTargetsMeetTheMinimumSize() {
        assertTargetsFor(Screen.SETTINGS, fontScale = 1f)
    }

    @Test
    fun givenOverviewAtLargeText_thenInteractiveTargetsStillMeetTheMinimumSize() {
        assertTargetsFor(Screen.OVERVIEW, fontScale = LARGE_FONT_SCALE)
    }

    @Test
    fun givenSearchAtLargeText_thenInteractiveTargetsStillMeetTheMinimumSize() {
        assertTargetsFor(Screen.SEARCH, fontScale = LARGE_FONT_SCALE)
    }

    @Test
    fun givenSettingsAtLargeText_thenInteractiveTargetsStillMeetTheMinimumSize() {
        assertTargetsFor(Screen.SETTINGS, fontScale = LARGE_FONT_SCALE)
    }

    @Test
    fun givenNarrowWidthAndLargeText_whenLongPlaceNamesRender_thenOverviewTargetsSurvive() {
        assertTargetsFor(Screen.OVERVIEW, fontScale = LARGE_FONT_SCALE, width = NARROW_WIDTH)
    }

    @Test
    fun givenNarrowWidthAndLargeText_whenLongResultNamesRender_thenSearchTargetsSurvive() {
        assertTargetsFor(Screen.SEARCH, fontScale = LARGE_FONT_SCALE, width = NARROW_WIDTH)
    }

    private fun assertTargetsFor(screen: Screen, fontScale: Float, width: Dp = DEFAULT_WIDTH) {
        composeTestRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                WeatherPlatformBehaviorProvider {
                    WeatherForecastTheme {
                        Box(modifier = Modifier.requiredWidth(width)) { screen.Content() }
                    }
                }
            }
        }

        val targets = composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick))
            .fetchSemanticsNodes()

        assertTrue("$screen exposed no interactive target", targets.isNotEmpty())
        val density = composeTestRule.density
        targets.forEach { node ->
            val heightDp = with(density) { node.boundsInRoot.height.toDp() }
            val widthDp = with(density) { node.boundsInRoot.width.toDp() }
            assertTrue(
                "$screen target measured $widthDp x $heightDp, " +
                    "below the ${WeatherLayout.MinimumTouchTarget} minimum",
                heightDp >= WeatherLayout.MinimumTouchTarget &&
                    widthDp >= WeatherLayout.MinimumTouchTarget,
            )
        }
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun givenAnExternalKeyboard_whenASavedPlaceIsFocusedAndEntered_thenItOpens() {
        var opened: Long? = null
        lateinit var inputModeManager: InputModeManager
        composeTestRule.setContent {
            inputModeManager = LocalInputModeManager.current
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    OverviewContent(
                        uiState = OverviewUiState.Success(longNameAreas),
                        onAreaClick = { opened = it },
                        onRemove = {},
                        onAddArea = {},
                    )
                }
            }
        }

        composeTestRule.runOnIdle { assertTrue(inputModeManager.requestInputMode(InputMode.Keyboard)) }
        composeTestRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.OnClick))[0]
            .requestFocus()
            .assertIsFocused()
            .performKeyInput { pressKey(Key.Enter) }

        composeTestRule.runOnIdle { assertEquals(longNameAreas.first().id, opened) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun givenAnExternalKeyboard_whenASearchResultIsFocusedAndEntered_thenItIsSelected() {
        var selected: Location? = null
        lateinit var inputModeManager: InputModeManager
        composeTestRule.setContent {
            inputModeManager = LocalInputModeManager.current
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    SearchContent(
                        uiState = SearchUiState.Results(longNameLocations),
                        query = "san",
                        onQueryChange = {},
                        onLocationClick = { selected = it },
                        autoFocusSearch = false,
                    )
                }
            }
        }

        composeTestRule.runOnIdle { assertTrue(inputModeManager.requestInputMode(InputMode.Keyboard)) }
        composeTestRule
            .onAllNodesWithTag(SEARCH_RESULT_ROW_TAG)[0]
            .requestFocus()
            .assertIsFocused()
            .performKeyInput { pressKey(Key.Enter) }

        composeTestRule.runOnIdle { assertEquals(longNameLocations.first(), selected) }
    }

    private enum class Screen {
        OVERVIEW,
        SEARCH,
        SETTINGS,
        ;

        @Composable
        fun Content() = when (this) {
            OVERVIEW -> OverviewContent(
                uiState = OverviewUiState.Success(longNameAreas),
                onAreaClick = {},
                onRemove = {},
                onAddArea = {},
            )

            SEARCH -> SearchContent(
                uiState = SearchUiState.Results(longNameLocations),
                query = "san",
                onQueryChange = {},
                onLocationClick = {},
                autoFocusSearch = false,
            )

            SETTINGS -> SettingsContent(
                units = Units.METRIC,
                themeMode = ThemeMode.SYSTEM,
                onUnitsSelected = {},
                onThemeModeSelected = {},
            )
        }
    }

    private companion object {
        const val LARGE_FONT_SCALE = 2f
        val DEFAULT_WIDTH = 390.dp
        val NARROW_WIDTH = 320.dp
        const val SEARCH_RESULT_ROW_TAG = "weather_search_result_row"

        /** Deliberately long place names, to stress the hierarchy the refresh introduced. */
        val longNameAreas = listOf(
            AreaSummary(
                id = 1,
                name = "San Fernando del Valle de Catamarca",
                currentTemp = 21.0,
                icon = WeatherIcon.CLOUDS,
                isDaytime = true,
                todayHigh = 24.0,
                todayLow = 15.0,
                rainChance = 0.2,
                stale = false,
            ),
            AreaSummary(
                id = 2,
                name = "Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch",
                currentTemp = 9.0,
                icon = WeatherIcon.RAIN,
                isDaytime = false,
                todayHigh = 11.0,
                todayLow = 4.0,
                rainChance = 0.9,
                stale = true,
            ),
        )

        val longNameLocations = listOf(
            Location(
                id = 1,
                name = "San Fernando del Valle de Catamarca",
                country = "AR",
                state = "Catamarca Province",
                lat = -28.47,
                lon = -65.79,
                sortOrder = 0,
            ),
            Location(
                id = 2,
                name = "Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch",
                country = "GB",
                state = "Isle of Anglesey",
                lat = 53.22,
                lon = -4.20,
                sortOrder = 1,
            ),
        )
    }
}
