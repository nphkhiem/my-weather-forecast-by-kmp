package com.example.my_weather_forecast.presentation.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SearchContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val hoChiMinhCity = Location(
        id = 0,
        name = "Ho Chi Minh City",
        country = "Vietnam",
        state = null,
        lat = 10.8231,
        lon = 106.6297,
        sortOrder = 0,
    )
    private val hoChiMinhTienGiang = Location(
        id = 0,
        name = "Ho Chi Minh",
        country = "Vietnam",
        state = "Tiền Giang",
        lat = 10.4493,
        lon = 106.3421,
        sortOrder = 0,
    )

    @Test
    fun givenOrderedResults_whenRendered_thenRowsKeepOrderAndExposeOneMergedActionEach() {
        setContent(
            uiState = mutableStateOf(
                SearchUiState.Results(listOf(hoChiMinhCity, hoChiMinhTienGiang)),
            ),
        )

        val rows = composeTestRule
            .onAllNodesWithTag(WEATHER_SEARCH_RESULT_ROW_TEST_TAG)
            .fetchSemanticsNodes()
        val descriptions = rows.map {
            it.config[SemanticsProperties.ContentDescription].single()
        }

        assertEquals(
            listOf(
                "Ho Chi Minh City, Vietnam",
                "Ho Chi Minh, Tiền Giang, Vietnam",
            ),
            descriptions,
        )
        composeTestRule
            .onAllNodesWithContentDescription("Ho Chi Minh City, Vietnam")
            .assertCountEquals(1)
        composeTestRule
            .onAllNodesWithContentDescription("Ho Chi Minh, Tiền Giang, Vietnam")
            .assertCountEquals(1)
    }

    @Test
    fun givenAResult_whenWholeRowIsTapped_thenLocationClickIsForwarded() {
        var selectedLocation: Location? = null
        setContent(
            uiState = mutableStateOf(SearchUiState.Results(listOf(hoChiMinhCity))),
            onLocationClick = { selectedLocation = it },
        )

        composeTestRule
            .onNodeWithContentDescription("Ho Chi Minh City, Vietnam")
            .assertHasClickAction()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(hoChiMinhCity, selectedLocation)
        }
    }

    @Test
    fun givenAResult_whenRendered_thenRowMeetsTheApprovedMinimumHeight() {
        setContent(
            uiState = mutableStateOf(SearchUiState.Results(listOf(hoChiMinhCity))),
        )

        val bounds = composeTestRule
            .onNodeWithTag(WEATHER_SEARCH_RESULT_ROW_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertTrue(bounds.bottom - bounds.top >= 68.dp)
    }

    @Test
    fun givenSearchStateChanges_whenRendered_thenFeedbackStaysInTheResultRegion() {
        val uiState = mutableStateOf<SearchUiState>(SearchUiState.Idle)
        setContent(uiState = uiState)

        composeTestRule.onNodeWithTag(SEARCH_RESULTS_TITLE_TEST_TAG).assertDoesNotExist()

        setUiState(uiState, SearchUiState.Loading)
        composeTestRule
            .onNodeWithContentDescription("Searching", useUnmergedTree = true)
            .assertIsDisplayed()

        setUiState(uiState, SearchUiState.Empty)
        composeTestRule
            .onNodeWithText("No cities found. Try a different search.")
            .assertIsDisplayed()

        setUiState(uiState, SearchUiState.Error(WeatherError.Network))
        composeTestRule
            .onNodeWithText("No internet connection. Try again.")
            .assertIsDisplayed()

        setUiState(uiState, SearchUiState.Results(listOf(hoChiMinhCity)))
        composeTestRule.onNodeWithTag(SEARCH_RESULTS_TITLE_TEST_TAG).assertIsDisplayed()
        composeTestRule.onNodeWithText("1 found").assertIsDisplayed()
    }

    @Test
    fun givenAddFeedbackChanges_whenRendered_thenEveryMessageAppearsNearSearch() {
        val feedback = mutableStateOf<SearchEvent?>(SearchEvent.AtLimit)
        setContent(
            uiState = mutableStateOf(SearchUiState.Results(listOf(hoChiMinhCity))),
            feedback = feedback,
        )

        composeTestRule.onNodeWithText("You can save up to 6 areas").assertIsDisplayed()

        composeTestRule.runOnIdle {
            feedback.value = SearchEvent.AlreadySaved
        }
        composeTestRule.onNodeWithText("This area is already saved").assertIsDisplayed()

        composeTestRule.runOnIdle {
            feedback.value = SearchEvent.AddFailed
        }
        composeTestRule.onNodeWithText("Could not add this area").assertIsDisplayed()
        composeTestRule.onNodeWithTag(SEARCH_INLINE_FEEDBACK_TEST_TAG).assertIsDisplayed()
    }

    private fun setContent(
        uiState: MutableState<SearchUiState>,
        feedback: MutableState<SearchEvent?> = mutableStateOf(null),
        onLocationClick: (Location) -> Unit = {},
    ) {
        composeTestRule.setContent {
            WeatherForecastTheme {
                SearchContent(
                    uiState = uiState.value,
                    query = "Ho Chi",
                    onQueryChange = {},
                    onLocationClick = onLocationClick,
                    feedback = feedback.value,
                    pressFeedback = WeatherPressFeedback.TONAL_HIGHLIGHT,
                    autoFocusSearch = false,
                )
            }
        }
    }

    private fun setUiState(
        state: MutableState<SearchUiState>,
        value: SearchUiState,
    ) {
        composeTestRule.runOnIdle {
            state.value = value
        }
    }
}
