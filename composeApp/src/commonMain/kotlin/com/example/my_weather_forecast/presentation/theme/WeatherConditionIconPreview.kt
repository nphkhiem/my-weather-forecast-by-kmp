package com.example.my_weather_forecast.presentation.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherHapticCue
import com.example.my_weather_forecast.presentation.platform.WeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.platform.WeatherSystemBarIconTone
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Previews render each gesture's rest state, which is exactly what a reduced-motion or paused hero
 * shows, so a static and a hero preview of the same condition should look identical at rest.
 */
@Composable
private fun ConditionIconGrid(
    motion: WeatherIconMotion,
    isDaytime: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        WeatherIcon.entries.forEach { icon ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                WeatherConditionIcon(
                    icon = icon,
                    isDaytime = isDaytime,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    motion = motion,
                )
                Text(
                    text = icon.name.take(5),
                    style = WeatherTypography.Label,
                    color = WeatherTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun PreviewBehavior(
    reducedMotion: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalWeatherPlatformBehavior provides PreviewPlatformBehavior(reducedMotion),
        content = content,
    )
}

private class PreviewPlatformBehavior(
    override val reducedMotion: Boolean,
) : WeatherPlatformBehavior {
    override val pressFeedback = WeatherPressFeedback.MATERIAL_RIPPLE

    override fun requestHaptic(cue: WeatherHapticCue) = Unit

    override fun dismissKeyboard() = Unit

    override fun setSystemBarIconTone(tone: WeatherSystemBarIconTone) = Unit
}

@Preview
@Composable
private fun StaticConditionIconsLightPreview() {
    PreviewBehavior(reducedMotion = false) {
        WeatherForecastTheme(darkTheme = false) {
            Surface { ConditionIconGrid(motion = WeatherIconMotion.STATIC) }
        }
    }
}

@Preview
@Composable
private fun StaticConditionIconsDarkPreview() {
    PreviewBehavior(reducedMotion = false) {
        WeatherForecastTheme(darkTheme = true) {
            Surface { ConditionIconGrid(motion = WeatherIconMotion.STATIC) }
        }
    }
}

@Preview
@Composable
private fun StaticConditionIconsNightPreview() {
    PreviewBehavior(reducedMotion = false) {
        WeatherForecastTheme(darkTheme = false) {
            Surface { ConditionIconGrid(motion = WeatherIconMotion.STATIC, isDaytime = false) }
        }
    }
}

@Preview
@Composable
private fun HeroConditionIconsLightPreview() {
    PreviewBehavior(reducedMotion = false) {
        WeatherForecastTheme(darkTheme = false) {
            Surface { ConditionIconGrid(motion = WeatherIconMotion.HERO) }
        }
    }
}

@Preview
@Composable
private fun HeroConditionIconsDarkPreview() {
    PreviewBehavior(reducedMotion = false) {
        WeatherForecastTheme(darkTheme = true) {
            Surface { ConditionIconGrid(motion = WeatherIconMotion.HERO) }
        }
    }
}

@Preview
@Composable
private fun HeroConditionIconsNightPreview() {
    PreviewBehavior(reducedMotion = false) {
        WeatherForecastTheme(darkTheme = false) {
            Surface { ConditionIconGrid(motion = WeatherIconMotion.HERO, isDaytime = false) }
        }
    }
}

@Preview
@Composable
private fun ReducedMotionConditionIconsLightPreview() {
    PreviewBehavior(reducedMotion = true) {
        WeatherForecastTheme(darkTheme = false) {
            Surface { ConditionIconGrid(motion = WeatherIconMotion.HERO) }
        }
    }
}

@Preview
@Composable
private fun ReducedMotionConditionIconsDarkPreview() {
    PreviewBehavior(reducedMotion = true) {
        WeatherForecastTheme(darkTheme = true) {
            Surface { ConditionIconGrid(motion = WeatherIconMotion.HERO) }
        }
    }
}
