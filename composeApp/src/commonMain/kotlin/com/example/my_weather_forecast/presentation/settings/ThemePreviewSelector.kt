package com.example.my_weather_forecast.presentation.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.SettingsTokens
import com.example.my_weather_forecast.presentation.theme.WeatherColorSchemes
import com.example.my_weather_forecast.presentation.theme.WeatherMotion
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_check
import myweatherforecast.composeapp.generated.resources.theme_dark
import myweatherforecast.composeapp.generated.resources.theme_light
import myweatherforecast.composeapp.generated.resources.theme_system
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ThemePreviewSelector(
    selectedMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(SettingsTokens.ThemeOptionGap),
    ) {
        ThemeMode.entries.forEach { mode ->
            ThemePreviewOption(
                mode = mode,
                selected = mode == selectedMode,
                onSelected = { onModeSelected(mode) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ThemePreviewOption(
    mode: ThemeMode,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    val behavior = rememberSettingsSelectionBehavior(
        selected = selected,
        onSelected = onSelected,
    )
    val usesTonalFeedback = behavior.pressFeedback == WeatherPressFeedback.TONAL_HIGHLIGHT
    val backgroundColor by animateColorAsState(
        targetValue = when {
            selected -> colors.searchResultPressed
            usesTonalFeedback && (behavior.isPressed || behavior.isFocused) ->
                colors.searchResultPressed
            else -> colors.surfaceMuted
        },
        animationSpec = tween(WeatherMotion.PressMillis, easing = WeatherMotion.StandardEasing),
        label = "theme option background",
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            behavior.isFocused -> colors.textPrimary
            selected -> colors.accent
            else -> colors.border
        },
        animationSpec = tween(WeatherMotion.PressMillis, easing = WeatherMotion.StandardEasing),
        label = "theme option border",
    )
    val label = stringResource(mode.labelResource())
    val shape = RoundedCornerShape(WeatherRadii.SmallCard)

    Column(
        modifier = modifier
            .heightIn(min = SettingsTokens.ThemeOptionMinHeight)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = when {
                    behavior.isFocused -> SettingsTokens.FocusedBorderWidth
                    selected -> SettingsTokens.SelectedBorderWidth
                    else -> SettingsTokens.BorderWidth
                },
                color = borderColor,
                shape = shape,
            )
            .settingsSelectable(
                selected = selected,
                behavior = behavior,
                accessibilityLabel = label,
                testTag = "theme_option_${mode.name}",
                rippleColor = colors.accent,
            )
            .padding(SettingsTokens.ThemeOptionPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SettingsTokens.ThemeLabelGap),
    ) {
        ThemeMiniature(mode = mode)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = colors.textPrimary,
                style = WeatherTypography.Label,
            )
            if (selected) {
                Spacer(modifier = Modifier.size(SettingsTokens.ThemeLabelGap))
                Surface(
                    modifier = Modifier.size(SettingsTokens.SelectionMarkSize),
                    shape = CircleShape,
                    color = colors.accent,
                    contentColor = colors.onAccent,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_check),
                            contentDescription = null,
                            modifier = Modifier.size(SettingsTokens.SelectionMarkIconSize),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeMiniature(
    mode: ThemeMode,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(WeatherRadii.Control)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SettingsTokens.ThemePreviewHeight)
            .clip(shape)
            .border(SettingsTokens.BorderWidth, WeatherTheme.colors.border, shape)
            .clearAndSetSemantics {},
    ) {
        when (mode) {
            ThemeMode.SYSTEM -> {
                ThemeMiniaturePane(dark = false, modifier = Modifier.weight(1f))
                ThemeMiniaturePane(dark = true, modifier = Modifier.weight(1f))
            }
            ThemeMode.LIGHT -> ThemeMiniaturePane(dark = false, modifier = Modifier.weight(1f))
            ThemeMode.DARK -> ThemeMiniaturePane(dark = true, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ThemeMiniaturePane(
    dark: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = if (dark) WeatherColorSchemes.Dark else WeatherColorSchemes.Light
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(colors.canvas)
            .padding(SettingsTokens.ThemePreviewPadding),
        verticalArrangement = Arrangement.spacedBy(SettingsTokens.ThemePreviewPadding),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.58f)
                .height(SettingsTokens.ThemePreviewLineHeight)
                .background(colors.accent, RoundedCornerShape(WeatherRadii.Full)),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(colors.surface, RoundedCornerShape(WeatherRadii.Compact)),
        )
    }
}

private fun ThemeMode.labelResource(): StringResource = when (this) {
    ThemeMode.SYSTEM -> Res.string.theme_system
    ThemeMode.LIGHT -> Res.string.theme_light
    ThemeMode.DARK -> Res.string.theme_dark
}
