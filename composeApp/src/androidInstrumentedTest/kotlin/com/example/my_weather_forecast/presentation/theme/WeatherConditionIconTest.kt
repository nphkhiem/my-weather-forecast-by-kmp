package com.example.my_weather_forecast.presentation.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherHapticCue
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.platform.WeatherSystemBarIconTone
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class WeatherConditionIconTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun givenStaticMotion_whenTimeAdvances_thenTheIconNeverChanges() {
        assertTrue(
            "A static weather icon must not animate",
            framesAreIdentical(motion = WeatherIconMotion.STATIC, reducedMotion = false),
        )
    }

    @Test
    fun givenReducedMotion_whenHeroMotionIsRequested_thenTheIconStaysStill() {
        assertTrue(
            "Reduced motion must replace the hero loop with a stable static state",
            framesAreIdentical(motion = WeatherIconMotion.HERO, reducedMotion = true),
        )
    }

    @Test
    fun givenHeroMotion_whenTimeAdvances_thenTheIconAnimates() {
        assertFalse(
            "The Detail hero icon must carry one ambient animation",
            framesAreIdentical(motion = WeatherIconMotion.HERO, reducedMotion = false),
        )
    }

    @Test
    fun givenNoPlatformBehaviorProvider_whenAStaticIconRenders_thenItStillDraws() {
        composeTestRule.setContent {
            WeatherForecastTheme(darkTheme = false) {
                Box(modifier = Modifier.testTag(ICON_TAG)) {
                    WeatherConditionIcon(
                        icon = WeatherIcon.RAIN,
                        isDaytime = true,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(ICON_TAG).assertIsDisplayed()
    }

    private fun framesAreIdentical(
        motion: WeatherIconMotion,
        reducedMotion: Boolean,
    ): Boolean {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalWeatherPlatformBehavior provides StubPlatformBehavior(reducedMotion),
            ) {
                WeatherForecastTheme(darkTheme = false) {
                    Box(modifier = Modifier.testTag(ICON_TAG)) {
                        WeatherConditionIcon(
                            icon = WeatherIcon.CLEAR,
                            isDaytime = true,
                            contentDescription = null,
                            motion = motion,
                            modifier = Modifier.size(96.dp),
                        )
                    }
                }
            }
        }

        composeTestRule.mainClock.advanceTimeBy(FIRST_FRAME_MILLIS)
        val first = capture()
        composeTestRule.mainClock.advanceTimeBy(SAMPLE_GAP_MILLIS)
        val second = capture()

        return first.contentEquals(second)
    }

    private fun capture(): IntArray =
        composeTestRule.onNodeWithTag(ICON_TAG).captureToImage().pixels()

    private fun ImageBitmap.pixels(): IntArray {
        val map = toPixelMap()
        return IntArray(map.width * map.height) { index ->
            map[index % map.width, index / map.width].toArgb()
        }
    }

    private class StubPlatformBehavior(
        override val reducedMotion: Boolean,
    ) : WeatherPlatformBehavior {
        override val pressFeedback = WeatherPressFeedback.MATERIAL_RIPPLE

        override fun requestHaptic(cue: WeatherHapticCue) = Unit

        override fun dismissKeyboard() = Unit

        override fun setSystemBarIconTone(tone: WeatherSystemBarIconTone) = Unit
    }

    private companion object {
        const val ICON_TAG = "weather_condition_icon"
        const val FIRST_FRAME_MILLIS = 16L
        const val SAMPLE_GAP_MILLIS = 700L
    }
}
