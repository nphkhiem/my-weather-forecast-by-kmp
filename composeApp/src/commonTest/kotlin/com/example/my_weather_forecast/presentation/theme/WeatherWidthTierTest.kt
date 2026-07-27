package com.example.my_weather_forecast.presentation.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherWidthTierTest {

    @Test
    fun givenPhoneWidths_whenTiered_thenTheyStayCompact() {
        listOf(320.dp, 360.dp, 411.dp, 480.dp, 599.dp).forEach { width ->
            assertEquals(WeatherWidthTier.COMPACT, WeatherLayout.widthTier(width), "$width")
        }
    }

    @Test
    fun givenTheCompactBreakpoint_whenTiered_thenItIsAlreadyMedium() {
        assertEquals(
            WeatherWidthTier.MEDIUM,
            WeatherLayout.widthTier(WeatherLayout.CompactBreakpoint),
        )
    }

    @Test
    fun givenLargeTabletWidths_whenTiered_thenTheyExpand() {
        listOf(840.dp, 1_024.dp, 1_280.dp).forEach { width ->
            assertEquals(WeatherWidthTier.EXPANDED, WeatherLayout.widthTier(width), "$width")
        }
    }

    @Test
    fun givenASplitScreenWidth_whenTiered_thenItFallsBackToCompact() {
        assertEquals(WeatherWidthTier.COMPACT, WeatherLayout.widthTier(400.dp))
    }

    @Test
    fun givenWideningWidths_whenTiered_thenTiersNeverGoBackwards() {
        val tiers = (200..1_400 step 20).map { WeatherLayout.widthTier(it.dp).ordinal }

        assertEquals(tiers.sorted(), tiers, "Width tiers must increase monotonically")
    }

    @Test
    fun givenEachTier_whenGuttered_thenGuttersGrowWithAvailableWidth() {
        val compact = WeatherLayout.gutter(WeatherWidthTier.COMPACT)
        val medium = WeatherLayout.gutter(WeatherWidthTier.MEDIUM)
        val expanded = WeatherLayout.gutter(WeatherWidthTier.EXPANDED)

        assertTrue(compact < medium, "compact $compact must be tighter than medium $medium")
        assertTrue(medium < expanded, "medium $medium must be tighter than expanded $expanded")
    }

    @Test
    fun givenOnlyTheExpandedTier_whenAskedForSideBySide_thenCompactAndMediumStaySingleColumn() {
        assertTrue(WeatherWidthTier.EXPANDED.supportsSideBySide)
        assertTrue(!WeatherWidthTier.MEDIUM.supportsSideBySide)
        assertTrue(!WeatherWidthTier.COMPACT.supportsSideBySide)
    }
}
