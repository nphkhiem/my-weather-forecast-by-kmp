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

object OverviewCardTokens {
    val MinHeight = 104.dp
    val HorizontalPadding = WeatherSpacing.Lg
    val VerticalPadding = WeatherSpacing.Md
    val LeadingContainerSize = 46.dp
    val WeatherIconSize = 36.dp
    val LeadingGap = WeatherSpacing.Md
    val TitleToConditionGap = WeatherSpacing.Hairline
    val ConditionToMetricsGap = WeatherSpacing.Sm
    val MetricGap = WeatherSpacing.Sm
    val StaleGap = WeatherSpacing.Xs
    val BorderWidth = 1.dp
    val AddActionHeight = 52.dp
    val AddActionIconSize = 20.dp
    const val LightIconSurfaceAlpha = 0.32f
    const val DarkIconSurfaceAlpha = 0.06f
    const val BorderAlpha = 0.46f
    const val PressedOverlayAlpha = 0.42f
}

object OverviewStateTokens {
    val SurfaceMinHeight = 204.dp
    val SurfacePadding = WeatherSpacing.Xxl
    val EmptyTopSpacerHeight = WeatherSpacing.Hero + WeatherSpacing.Xs
    val IndicatorContainerSize = 52.dp
    val IndicatorIconSize = 24.dp
    val ProgressSize = 22.dp
    val ProgressStrokeWidth = 2.dp
    val ActionHeight = WeatherLayout.MinimumTouchTarget
    val InlineStatusHeight = 32.dp
    val InlineProgressSize = 14.dp
    val InlineProgressStrokeWidth = 2.dp
    val BorderWidth = 1.dp
}

object DetailHeroTokens {
    val Padding = WeatherSpacing.Xl
    val TopLineGap = WeatherSpacing.Sm
    val MainMinHeight = 124.dp
    val IconSize = 96.dp
    val MainGap = WeatherSpacing.Lg
    val MetricTopPadding = WeatherSpacing.Md
    val MetricDividerHeight = 36.dp
    val MetricDividerWidth = 1.dp
    val BorderWidth = 1.dp
    val StatusDotSize = 6.dp
    val StatusProgressSize = 12.dp
    val StatusProgressStrokeWidth = 1.5.dp
    const val BorderAlpha = 0.56f
}

object DetailStateTokens {
    val HeroMinHeight = 204.dp
    val HourlyPlaceholderHeight = 176.dp
    val DailyPlaceholderHeight = 248.dp
    val SurfacePadding = WeatherSpacing.Xxl
    val IndicatorContainerSize = 52.dp
    val IndicatorIconSize = 24.dp
    val ProgressSize = 22.dp
    val ProgressStrokeWidth = 2.dp
    val PlaceholderLineHeight = 12.dp
    val ActionHeight = WeatherLayout.MinimumTouchTarget
    val BorderWidth = 1.dp
}

object SettingsTokens {
    val ContentPadding = WeatherSpacing.Lg
    val ContentGap = WeatherSpacing.Xl
    val IntroGap = WeatherSpacing.Xs
    val SectionPadding = WeatherSpacing.Xl
    val SectionHeaderGap = WeatherSpacing.Hairline
    val SectionContentGap = WeatherSpacing.Lg
    val ThemeOptionGap = WeatherSpacing.Sm
    val ThemeOptionMinHeight = 120.dp
    val ThemeOptionPadding = WeatherSpacing.Sm
    val ThemePreviewHeight = 58.dp
    val ThemePreviewPadding = WeatherSpacing.Xs
    val ThemePreviewLineHeight = 5.dp
    val ThemeLabelGap = WeatherSpacing.Sm
    val SegmentContainerPadding = WeatherSpacing.Xs
    val SegmentGap = WeatherSpacing.Xs
    val SegmentMinHeight = 52.dp
    val SegmentHorizontalPadding = WeatherSpacing.Md
    val SegmentLabelGap = WeatherSpacing.Sm
    val SystemNotePadding = WeatherSpacing.Lg
    val SystemNoteGap = WeatherSpacing.Md
    val SystemNoteTextGap = WeatherSpacing.Hairline
    val SystemNoteIconContainerSize = 36.dp
    val SystemNoteIconSize = 20.dp
    val BorderWidth = 1.dp
    val SelectedBorderWidth = 2.dp
    val FocusedBorderWidth = 3.dp
}

object ForecastPanelTokens {
    val Padding = WeatherSpacing.Xl
    val HeaderGap = WeatherSpacing.Hairline
    val HeaderBottomSpace = WeatherSpacing.Lg
    val BorderWidth = 1.dp
    val HourGap = WeatherSpacing.Sm
    val HourMinWidth = 76.dp
    val HourHorizontalPadding = WeatherSpacing.Sm
    val HourVerticalPadding = WeatherSpacing.Md
    val HourIconSize = 30.dp
    val HourContentGap = WeatherSpacing.Xs
    val EmptyVerticalPadding = WeatherSpacing.Sm
    val DailyVerticalPadding = WeatherSpacing.Md
    val DailyColumnGap = WeatherSpacing.Md
    val DailyIconSize = 30.dp
    val DailySupportingTopSpace = WeatherSpacing.Xs
    val DailyLabelGap = 6.dp
    val DailySupportingGap = 6.dp
    val DailyStackedTopSpace = WeatherSpacing.Sm
    val DailyCompactBreakpoint = 300.dp
    const val DailyLargeFontScale = 1.5f
    const val DailyLabelWeight = 1.1f
    const val DailyTemperatureWeight = 0.85f
    const val DailyRainWeight = 0.85f
    const val BorderAlpha = 0.56f
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
