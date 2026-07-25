package com.example.my_weather_forecast.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object WeatherTypography {
    private val PlatformSans = FontFamily.Default

    val HeroTemperature = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 56.sp,
        lineHeight = 60.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = (-1.2).sp,
    )
    val ScreenTitle = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.3).sp,
    )
    val CardTemperature = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.3).sp,
    )
    val SectionTitle = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold,
    )
    val ItemTitle = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
    )
    val Body = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
    )
    val BodyStrong = Body.copy(fontWeight = FontWeight.Medium)
    val Label = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium,
    )
    val Caption = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
    )
    val Micro = TextStyle(
        fontFamily = PlatformSans,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Medium,
    )
}

internal val WeatherMaterialTypography = Typography(
    displayMedium = WeatherTypography.HeroTemperature,
    headlineSmall = WeatherTypography.CardTemperature,
    titleLarge = WeatherTypography.ScreenTitle,
    titleMedium = WeatherTypography.SectionTitle,
    titleSmall = WeatherTypography.ItemTitle,
    bodyLarge = WeatherTypography.Body,
    bodyMedium = WeatherTypography.Body,
    bodySmall = WeatherTypography.Caption,
    labelLarge = WeatherTypography.BodyStrong,
    labelMedium = WeatherTypography.Label,
    labelSmall = WeatherTypography.Micro,
)

internal val WeatherShapes = Shapes(
    extraSmall = RoundedCornerShape(WeatherRadii.Compact),
    small = RoundedCornerShape(WeatherRadii.Control),
    medium = RoundedCornerShape(WeatherRadii.SmallCard),
    large = RoundedCornerShape(WeatherRadii.Card),
    extraLarge = RoundedCornerShape(WeatherRadii.Hero),
)
