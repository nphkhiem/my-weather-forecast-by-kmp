package com.example.my_weather_forecast.presentation.theme

import androidx.compose.ui.graphics.Color
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon

/**
 * A soft weather wash with paired content and icon colors. Theme selects the luminance family;
 * condition and local day/night only adjust the atmosphere within that family.
 */
data class ConditionPalette(
    val gradientStart: Color,
    val gradientEnd: Color,
    val onGradient: Color,
    val accent: Color,
)

private data class WeatherWash(
    val start: Color,
    val end: Color,
    val accentDay: Color,
    val accentNight: Color,
)

private object WeatherWashes {
    private val Light = mapOf(
        WeatherIcon.CLEAR to WeatherWash(
            Color(0xFFFFE5C5), Color(0xFFF1F7FC), Color(0xFFD9833A), Color(0xFF766A9C),
        ),
        WeatherIcon.CLOUDS to WeatherWash(
            Color(0xFFE1E8EC), Color(0xFFF6F8F8), Color(0xFF667984), Color(0xFF6D748F),
        ),
        WeatherIcon.RAIN to WeatherWash(
            Color(0xFFD2EAEA), Color(0xFFF0F7F5), Color(0xFF3F858A), Color(0xFF607C98),
        ),
        WeatherIcon.DRIZZLE to WeatherWash(
            Color(0xFFDCEDEE), Color(0xFFF3F8F7), Color(0xFF57979A), Color(0xFF6D879D),
        ),
        WeatherIcon.THUNDERSTORM to WeatherWash(
            Color(0xFFDDD8E8), Color(0xFFF2F0F7), Color(0xFF6E6094), Color(0xFF665A86),
        ),
        WeatherIcon.SNOW to WeatherWash(
            Color(0xFFE4EFF6), Color(0xFFF7FAFC), Color(0xFF6093B6), Color(0xFF7189A3),
        ),
        WeatherIcon.ATMOSPHERE to WeatherWash(
            Color(0xFFE7E8E2), Color(0xFFF6F6F2), Color(0xFF7C8178), Color(0xFF71767E),
        ),
        WeatherIcon.UNKNOWN to WeatherWash(
            Color(0xFFEAECE8), Color(0xFFF7F8F5), Color(0xFF7A8586), Color(0xFF747A86),
        ),
    )

    private val Dark = mapOf(
        WeatherIcon.CLEAR to WeatherWash(
            Color(0xFF352F3C), Color(0xFF202A34), Color(0xFFF0AD67), Color(0xFFD4CFF0),
        ),
        WeatherIcon.CLOUDS to WeatherWash(
            Color(0xFF293139), Color(0xFF1D252B), Color(0xFFB4C0C6), Color(0xFFB8BDD2),
        ),
        WeatherIcon.RAIN to WeatherWash(
            Color(0xFF20363A), Color(0xFF192A30), Color(0xFF87C9CB), Color(0xFF9DB8CE),
        ),
        WeatherIcon.DRIZZLE to WeatherWash(
            Color(0xFF26393C), Color(0xFF1D2B30), Color(0xFF9AD1D1), Color(0xFFA5BECE),
        ),
        WeatherIcon.THUNDERSTORM to WeatherWash(
            Color(0xFF302B40), Color(0xFF202532), Color(0xFFB8A9D8), Color(0xFFC2B8DE),
        ),
        WeatherIcon.SNOW to WeatherWash(
            Color(0xFF283842), Color(0xFF1D2930), Color(0xFFA9D0E5), Color(0xFFB7CCE0),
        ),
        WeatherIcon.ATMOSPHERE to WeatherWash(
            Color(0xFF2D3231), Color(0xFF202625), Color(0xFFBDC2B7), Color(0xFFB7BDC0),
        ),
        WeatherIcon.UNKNOWN to WeatherWash(
            Color(0xFF2B3233), Color(0xFF202628), Color(0xFFB5C0C1), Color(0xFFB6BBC7),
        ),
    )

    fun resolve(icon: WeatherIcon, darkTheme: Boolean): WeatherWash =
        checkNotNull((if (darkTheme) Dark else Light)[icon])
}

fun WeatherIcon.accentColor(isDaytime: Boolean, darkTheme: Boolean): Color =
    conditionPalette(isDaytime = isDaytime, darkTheme = darkTheme).accent

fun WeatherCondition.palette(darkTheme: Boolean): ConditionPalette =
    icon.conditionPalette(isDaytime = isDaytime, darkTheme = darkTheme)

fun WeatherIcon.conditionPalette(isDaytime: Boolean, darkTheme: Boolean): ConditionPalette {
    val wash = WeatherWashes.resolve(icon = this, darkTheme = darkTheme)
    val themeColors = if (darkTheme) WeatherColorSchemes.Dark else WeatherColorSchemes.Light
    return ConditionPalette(
        gradientStart = wash.start,
        gradientEnd = wash.end,
        onGradient = themeColors.textPrimary,
        accent = if (isDaytime) wash.accentDay else wash.accentNight,
    )
}
