package com.example.my_weather_forecast.presentation.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.unit.dp

object WeatherSpacing {
    val None = 0.dp
    val Hairline = 2.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 20.dp
    val Xxl = 24.dp
    val Section = 32.dp
    val SectionLarge = 40.dp
    val Screen = 48.dp
    val Hero = 64.dp
}

object WeatherRadii {
    val Compact = 8.dp
    val Control = 12.dp
    val SmallCard = 16.dp
    val SearchField = 18.dp
    val Card = 22.dp
    val Hero = 28.dp
    val Full = 999.dp
}

object WeatherElevation {
    val Flat = 0.dp
    val Resting = 1.dp
    val Floating = 3.dp
    val Overlay = 8.dp
}

object WeatherLayout {
    val CompactBreakpoint = 600.dp
    val ExpandedBreakpoint = 840.dp
    val CompactGutter = 16.dp
    val MediumGutter = 24.dp
    val ExpandedGutter = 32.dp
    val FormMaxWidth = 600.dp
    val ReadingMaxWidth = 720.dp
    val OverviewMaxWidth = 960.dp
    val PageMaxWidth = 1_120.dp
    val MinimumTouchTarget = 48.dp
    val CompactAppBarHeight = 56.dp
}

object SearchFieldTokens {
    val Height = 54.dp
    val HorizontalPadding = 16.dp
    val LeadingIconSize = 20.dp
    val LeadingIconGap = 12.dp
    val TrailingVisualSize = 20.dp
    val TrailingTouchTarget = WeatherLayout.MinimumTouchTarget
    val TrailingEndPadding = 4.dp
    val FocusRingWidth = 2.dp
    val ShapeRadius = WeatherRadii.SearchField
}

object SearchResultTokens {
    val GroupRadius = WeatherRadii.Card
    val GroupVerticalPadding = WeatherSpacing.Xs
    val RowMinHeight = 68.dp
    val RowHorizontalPadding = WeatherSpacing.Lg
    val RowVerticalPadding = WeatherSpacing.Sm
    val LeadingContainerSize = 40.dp
    val LeadingIconSize = 20.dp
    val LeadingGap = WeatherSpacing.Md
    val TextLineGap = WeatherSpacing.Hairline
    val TrailingSlotSize = WeatherLayout.MinimumTouchTarget
    val TrailingIconSize = 20.dp
    val SeparatorThickness = 1.dp
    val SeparatorStartInset = 68.dp
    const val PressedScale = 0.995f
}

object WeatherMotion {
    const val InstantMillis = 50
    const val PressMillis = 120
    const val FastMillis = 160
    const val StandardMillis = 220
    const val NavigationMillis = 300
    const val SlowMillis = 420

    val StandardEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val EnterEasing = CubicBezierEasing(0f, 0f, 0f, 1f)
    val ExitEasing = CubicBezierEasing(0.3f, 0f, 1f, 1f)
    val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    const val CardPressedScale = 0.985f
    const val HeroMotionTranslationDp = 3
    const val HeroMotionRotationDegrees = 3f
    const val HeroMotionScale = 1.025f
    const val HeroAmbientDurationMillis = 2_400
    const val HeroAmbientRestMillis = 1_800
    const val HeroThunderMinimumIntervalMillis = 6_000
    const val ReducedMotionFadeMillis = 100
}
