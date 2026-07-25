package com.example.my_weather_forecast.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehaviorProvider
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class WeatherScreenFrameTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenAReadableMaximumWidth_whenFrameIsWide_thenTopBarAndBodyShareTheConstraint() {
        setFrame(contentMaxWidth = 240.dp)

        val topBarBounds = composeTestRule
            .onNodeWithTag(WEATHER_TOP_BAR_CONTENT_TEST_TAG)
            .getUnclippedBoundsInRoot()
        val bodyBounds = composeTestRule
            .onNodeWithTag(WEATHER_SCREEN_CONTENT_TEST_TAG)
            .getUnclippedBoundsInRoot()

        assertEquals(240.dp, topBarBounds.right - topBarBounds.left)
        assertEquals(240.dp, bodyBounds.right - bodyBounds.left)
        assertTrue(bodyBounds.top >= topBarBounds.bottom)
    }

    @Test
    fun givenNavigation_whenTopBarIsShown_thenTargetIsReachableAndInvokesBack() {
        var backInvocations = 0
        setFrame(onBack = { backInvocations++ })

        val navigationNode = composeTestRule.onNodeWithTag(WEATHER_TOP_BAR_NAVIGATION_TEST_TAG)
        val navigationBounds = navigationNode.getUnclippedBoundsInRoot()

        navigationNode.assertIsDisplayed().performClick()

        assertTrue(navigationBounds.right - navigationBounds.left >= 48.dp)
        assertTrue(navigationBounds.bottom - navigationBounds.top >= 48.dp)
        assertEquals(1, backInvocations)
    }

    private fun setFrame(
        contentMaxWidth: androidx.compose.ui.unit.Dp = 600.dp,
        onBack: (() -> Unit)? = null,
    ) {
        composeTestRule.setContent {
            WeatherPlatformBehaviorProvider {
                WeatherForecastTheme {
                    WeatherScreenFrame(
                        title = "Weather",
                        contentMaxWidth = contentMaxWidth,
                        onNavigationClick = onBack,
                        navigationIcon = onBack?.let {
                            {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .semantics { contentDescription = "Back" },
                                )
                            }
                        },
                    ) {
                        Box(modifier = Modifier.fillMaxSize().testTag(BODY_TEST_TAG))
                    }
                }
            }
        }
    }

    private companion object {
        const val BODY_TEST_TAG = "weather_frame_test_body"
    }
}
