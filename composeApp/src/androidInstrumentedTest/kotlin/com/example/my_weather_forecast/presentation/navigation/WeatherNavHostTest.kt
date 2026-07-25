package com.example.my_weather_forecast.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.pressBack
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WeatherNavHostTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            WeatherForecastTheme {
                WeatherNavHost(
                    navController = navController,
                    overviewContent = { onOpenSearch, onOpenSettings, onOpenDetail ->
                        Column {
                            NavigationAction(OPEN_SEARCH_TAG, onOpenSearch)
                            NavigationAction(OPEN_SETTINGS_TAG, onOpenSettings)
                            NavigationAction(OPEN_DETAIL_TAG) { onOpenDetail(LOCATION_ID) }
                        }
                    },
                    searchContent = { onBack -> NavigationAction(BACK_FROM_SEARCH_TAG, onBack) },
                    settingsContent = { onBack -> NavigationAction(BACK_FROM_SETTINGS_TAG, onBack) },
                    detailContent = { locationId, onBack ->
                        Column {
                            Text(locationId.toString(), modifier = Modifier.testTag(DETAIL_LOCATION_TAG))
                            NavigationAction(BACK_FROM_DETAIL_TAG, onBack)
                        }
                    },
                )
            }
        }
    }

    @Test
    fun givenOverview_whenSearchIsOpenedAndBackIsPressed_thenReturnsToOverview() {
        composeTestRule.onNodeWithTag(OPEN_SEARCH_TAG).performClick()
        assertCurrentRoute<Routes.Search>()

        composeTestRule.onNodeWithTag(BACK_FROM_SEARCH_TAG).performClick()
        assertCurrentRoute<Routes.Overview>()
    }

    @Test
    fun givenSearch_whenSystemBackIsPressed_thenReturnsToOverview() {
        composeTestRule.onNodeWithTag(OPEN_SEARCH_TAG).performClick()
        assertCurrentRoute<Routes.Search>()

        pressBack()
        assertCurrentRoute<Routes.Overview>()
    }

    @Test
    fun givenOverview_whenSettingsIsOpenedAndBackIsPressed_thenReturnsToOverview() {
        composeTestRule.onNodeWithTag(OPEN_SETTINGS_TAG).performClick()
        assertCurrentRoute<Routes.Settings>()

        composeTestRule.onNodeWithTag(BACK_FROM_SETTINGS_TAG).performClick()
        assertCurrentRoute<Routes.Overview>()
    }

    @Test
    fun givenOverview_whenDetailIsOpenedAndBackIsPressed_thenReturnsToOverview() {
        composeTestRule.onNodeWithTag(OPEN_DETAIL_TAG).performClick()
        assertCurrentRoute<Routes.Detail>()
        composeTestRule.onNodeWithTag(DETAIL_LOCATION_TAG).assertExists()

        composeTestRule.onNodeWithTag(BACK_FROM_DETAIL_TAG).performClick()
        assertCurrentRoute<Routes.Overview>()
    }

    private inline fun <reified T : Any> assertCurrentRoute() {
        composeTestRule.runOnIdle {
            assertTrue(navController.currentDestination?.hasRoute<T>() == true)
        }
    }

    @Composable
    private fun NavigationAction(tag: String, onClick: () -> Unit) {
        Text(
            text = tag,
            modifier = Modifier.testTag(tag).clickable(onClick = onClick),
        )
    }

    private companion object {
        const val LOCATION_ID = 42L
        const val OPEN_SEARCH_TAG = "open_search"
        const val OPEN_SETTINGS_TAG = "open_settings"
        const val OPEN_DETAIL_TAG = "open_detail"
        const val BACK_FROM_SEARCH_TAG = "back_from_search"
        const val BACK_FROM_SETTINGS_TAG = "back_from_settings"
        const val BACK_FROM_DETAIL_TAG = "back_from_detail"
        const val DETAIL_LOCATION_TAG = "detail_location"
    }
}
