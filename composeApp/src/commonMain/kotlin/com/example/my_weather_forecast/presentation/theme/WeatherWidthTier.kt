package com.example.my_weather_forecast.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp

/**
 * The width bands every route shares. Screens branch on a tier rather than comparing raw dp, so
 * there is one place that decides what "wide" means and no route grows its own breakpoint.
 */
enum class WeatherWidthTier {
    COMPACT,
    MEDIUM,
    EXPANDED,
    ;

    /**
     * Only the widest tier places two reading columns side by side. Phones and medium widths keep
     * the approved single-column composition, so this stays a phone-first product.
     */
    val supportsSideBySide: Boolean get() = this == EXPANDED
}

/**
 * Defaults to [WeatherWidthTier.COMPACT] rather than failing, so a component rendered outside a
 * screen frame still lays out as a phone instead of crashing.
 */
val LocalWeatherWidthTier = staticCompositionLocalOf { WeatherWidthTier.COMPACT }

fun WeatherLayout.widthTier(width: Dp): WeatherWidthTier = when {
    width < CompactBreakpoint -> WeatherWidthTier.COMPACT
    width < ExpandedBreakpoint -> WeatherWidthTier.MEDIUM
    else -> WeatherWidthTier.EXPANDED
}

fun WeatherLayout.gutter(tier: WeatherWidthTier): Dp = when (tier) {
    WeatherWidthTier.COMPACT -> CompactGutter
    WeatherWidthTier.MEDIUM -> MediumGutter
    WeatherWidthTier.EXPANDED -> ExpandedGutter
}
