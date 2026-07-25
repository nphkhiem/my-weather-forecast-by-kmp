package com.example.my_weather_forecast.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasImeAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class WeatherSearchFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenSearchEntry_whenFieldAppears_thenItAutofocusesWithSearchSemantics() {
        setField()

        val field = composeTestRule.onNodeWithTag(WEATHER_SEARCH_FIELD_TEST_TAG)
        val bounds = field.getUnclippedBoundsInRoot()

        field
            .assertIsDisplayed()
            .assertIsFocused()
            .assert(hasSetTextAction())
            .assert(hasImeAction(ImeAction.Search))
        assertEquals(54f, (bounds.bottom - bounds.top).value, 0.2f)
        composeTestRule
            .onAllNodesWithContentDescription("Search for a city")
            .assertCountEquals(1)
    }

    @Test
    fun givenAnEmptyQuery_whenTextIsEntered_thenChangeIsForwarded() {
        var latestQuery = ""
        setField(onQueryObserved = { latestQuery = it })

        composeTestRule
            .onNodeWithTag(WEATHER_SEARCH_FIELD_TEST_TAG)
            .performTextInput("Hanoi")

        composeTestRule.runOnIdle {
            assertEquals("Hanoi", latestQuery)
        }
    }

    @Test
    fun givenANonEmptyQuery_whenClearIsTapped_thenQueryClearsAndFieldKeepsFocus() {
        var latestQuery = "Hanoi"
        setField(initialQuery = latestQuery, onQueryObserved = { latestQuery = it })

        val clear = composeTestRule.onNodeWithContentDescription("Clear search")
        val bounds = clear.getUnclippedBoundsInRoot()

        clear.assertIsDisplayed().assertHasClickAction().performClick()

        composeTestRule.runOnIdle {
            assertEquals("", latestQuery)
        }
        assertTrue(bounds.right - bounds.left >= 48.dp)
        assertTrue(bounds.bottom - bounds.top >= 48.dp)
        composeTestRule.onNodeWithTag(WEATHER_SEARCH_FIELD_TEST_TAG).assertIsFocused()
    }

    @Test
    fun givenTheSearchImeAction_whenInvoked_thenSearchCallbackRuns() {
        var searchInvocations = 0
        setField(initialQuery = "Hanoi", onSearch = { searchInvocations++ })

        composeTestRule
            .onNodeWithTag(WEATHER_SEARCH_FIELD_TEST_TAG)
            .performImeAction()

        assertEquals(1, searchInvocations)
    }

    @Test
    fun givenLoadingChanges_whenTrailingContentSwaps_thenItsGeometryStaysStable() {
        val loading = mutableStateOf(false)
        setField(initialQuery = "Hanoi", loading = { loading.value })

        val clearBounds = composeTestRule
            .onNodeWithTag(WEATHER_SEARCH_TRAILING_SLOT_TEST_TAG, useUnmergedTree = true)
            .getUnclippedBoundsInRoot()

        composeTestRule.runOnIdle {
            loading.value = true
        }

        val loadingBounds = composeTestRule
            .onNodeWithTag(WEATHER_SEARCH_TRAILING_SLOT_TEST_TAG, useUnmergedTree = true)
            .getUnclippedBoundsInRoot()

        assertEquals(clearBounds, loadingBounds)
        composeTestRule
            .onNodeWithContentDescription("Searching", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    private fun setField(
        initialQuery: String = "",
        loading: () -> Boolean = { false },
        onQueryObserved: (String) -> Unit = {},
        onSearch: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            var query by remember { mutableStateOf(initialQuery) }

            WeatherForecastTheme {
                WeatherSearchField(
                    query = query,
                    isLoading = loading(),
                    onQueryChange = {
                        query = it
                        onQueryObserved(it)
                    },
                    onSearch = onSearch,
                    autoFocus = true,
                )
            }
        }
    }
}
