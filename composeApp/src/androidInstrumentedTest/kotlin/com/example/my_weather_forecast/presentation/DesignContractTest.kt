package com.example.my_weather_forecast.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.search.SearchContent
import com.example.my_weather_forecast.presentation.search.SearchUiState
import com.example.my_weather_forecast.presentation.theme.SearchFieldTokens
import com.example.my_weather_forecast.presentation.theme.SearchResultTokens
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Task 16 measures the approved design contract against what actually renders, rather than against
 * what the token file declares. A token can be correct while padding or a minimum height changes
 * the measured result.
 */
class DesignContractTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenTheSearchField_whenRendered_thenItMeasuresTheApprovedHeight() {
        renderSearch()

        val bounds = composeTestRule
            .onNodeWithTag(SEARCH_FIELD_TAG)
            .assertIsDisplayed()
            .getUnclippedBoundsInRoot()
        val height = bounds.bottom - bounds.top

        assertEquals(
            "Search field measured $height against the approved ${SearchFieldTokens.Height}",
            SearchFieldTokens.Height.value,
            height.value,
            1f,
        )
    }

    @Test
    fun givenSearchResults_whenRendered_thenEveryRowMeetsTheApprovedRhythm() {
        renderSearch()

        val rows = composeTestRule.onAllNodesWithTag(SEARCH_RESULT_ROW_TAG).fetchSemanticsNodes()
        assertTrue("Expected search result rows", rows.isNotEmpty())

        val density = composeTestRule.density
        rows.forEach { row ->
            val height = with(density) { row.boundsInRoot.height.toDp() }
            assertTrue(
                "Result row measured $height, below the approved ${SearchResultTokens.RowMinHeight}",
                height >= SearchResultTokens.RowMinHeight - 1.dp,
            )
        }
    }

    private fun renderSearch() {
        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    Box(modifier = Modifier.requiredWidth(390.dp)) {
                        SearchContent(
                            uiState = SearchUiState.Results(results),
                            query = "ho chi",
                            onQueryChange = {},
                            onLocationClick = {},
                            autoFocusSearch = false,
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val SEARCH_FIELD_TAG = "weather_search_field"
        const val SEARCH_RESULT_ROW_TAG = "weather_search_result_row"

        val results = listOf(
            Location(
                id = 1,
                name = "Ho Chi Minh City",
                country = "VN",
                state = null,
                lat = 10.82,
                lon = 106.63,
                sortOrder = 0,
            ),
            Location(
                id = 2,
                name = "Ho Chi Minh",
                country = "VN",
                state = "Tien Giang",
                lat = 10.45,
                lon = 106.34,
                sortOrder = 1,
            ),
        )
    }
}
