package com.example.my_weather_forecast.presentation.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.core.preference.ThemePreference
import com.example.my_weather_forecast.core.preference.nextThemeMode

private val LocalWeatherColors = staticCompositionLocalOf { WeatherColorSchemes.Light }
private val LocalWeatherDarkTheme = staticCompositionLocalOf { false }

object WeatherTheme {
    val colors: WeatherColors
        @Composable get() = LocalWeatherColors.current

    val darkTheme: Boolean
        @Composable get() = LocalWeatherDarkTheme.current
}

@Composable
fun WeatherForecastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val darkThemeProgress by animateFloatAsState(
        targetValue = if (darkTheme) 1f else 0f,
        animationSpec = tween(
            durationMillis = WeatherMotion.FastMillis,
            easing = WeatherMotion.StandardEasing,
        ),
        label = "weather theme crossfade",
    )
    val weatherColors = lerpWeatherColors(
        start = WeatherColorSchemes.Light,
        stop = WeatherColorSchemes.Dark,
        fraction = darkThemeProgress,
    )
    val materialColors = lerpColorSchemes(
        start = weatherColorScheme(darkTheme = false),
        stop = weatherColorScheme(darkTheme = true),
        fraction = darkThemeProgress,
    )
    CompositionLocalProvider(
        LocalWeatherColors provides weatherColors,
        LocalWeatherDarkTheme provides darkTheme,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = WeatherMaterialTypography,
            shapes = WeatherShapes,
            content = content,
        )
    }
}

/**
 * Resolves [ThemeMode] against the live system setting. An explicit Light/Dark override is only
 * good until the OS theme actually changes, at which point it's cleared back to System (see
 * [nextThemeMode]) — the OS setting always wins over a stale in-app choice.
 */
@Composable
fun rememberEffectiveDarkTheme(themePreference: ThemePreference): Boolean {
    val systemDark = isSystemInDarkTheme()
    val mode by themePreference.mode.collectAsStateWithLifecycle()
    var previousSystemDark by remember { mutableStateOf(systemDark) }

    LaunchedEffect(systemDark) {
        val systemThemeChanged = systemDark != previousSystemDark
        if (systemThemeChanged) {
            val next = nextThemeMode(mode, systemThemeChanged = true)
            if (next != mode) themePreference.setMode(next)
        }
        previousSystemDark = systemDark
    }

    return when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }
}
