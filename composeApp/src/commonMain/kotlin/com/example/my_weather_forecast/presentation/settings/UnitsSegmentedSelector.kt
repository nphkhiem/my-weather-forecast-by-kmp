package com.example.my_weather_forecast.presentation.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.testTag
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.SettingsTokens
import com.example.my_weather_forecast.presentation.theme.WeatherMotion
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_check
import myweatherforecast.composeapp.generated.resources.units_imperial
import myweatherforecast.composeapp.generated.resources.units_imperial_accessibility
import myweatherforecast.composeapp.generated.resources.units_imperial_symbol
import myweatherforecast.composeapp.generated.resources.units_metric
import myweatherforecast.composeapp.generated.resources.units_metric_accessibility
import myweatherforecast.composeapp.generated.resources.units_metric_symbol
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun UnitsSegmentedSelector(
    selectedUnits: Units,
    onUnitsSelected: (Units) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WeatherRadii.SmallCard))
            .background(WeatherTheme.colors.surfaceMuted)
            .padding(SettingsTokens.SegmentContainerPadding)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(SettingsTokens.SegmentGap),
    ) {
        Units.entries.forEach { units ->
            UnitsSegment(
                units = units,
                selected = units == selectedUnits,
                onSelected = { onUnitsSelected(units) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun UnitsSegment(
    units: Units,
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
            selected -> colors.surface
            usesTonalFeedback && (behavior.isPressed || behavior.isFocused) ->
                colors.searchResultPressed
            else -> colors.surfaceMuted
        },
        animationSpec = tween(WeatherMotion.PressMillis, easing = WeatherMotion.StandardEasing),
        label = "units segment background",
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            behavior.isFocused -> colors.textPrimary
            selected -> colors.accent
            else -> colors.surfaceMuted
        },
        animationSpec = tween(WeatherMotion.PressMillis, easing = WeatherMotion.StandardEasing),
        label = "units segment border",
    )
    val resources = units.resources()
    val label = stringResource(resources.label)
    val symbol = stringResource(resources.symbol)
    val accessibilityLabel = stringResource(resources.accessibilityLabel)
    val shape = RoundedCornerShape(WeatherRadii.Control)

    Box(
        modifier = modifier
            .heightIn(min = SettingsTokens.SegmentMinHeight)
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
                accessibilityLabel = accessibilityLabel,
                testTag = "units_option_${units.name}",
                rippleColor = colors.accent,
            )
            .padding(horizontal = SettingsTokens.SegmentHorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selected) {
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
                            modifier = Modifier
                                .size(SettingsTokens.SelectionMarkIconSize)
                                .testTag("units_selection_mark_${units.name}"),
                        )
                    }
                }
                Spacer(modifier = Modifier.size(SettingsTokens.SegmentLabelGap))
            }
            Text(
                text = symbol,
                color = if (selected) colors.accent else colors.textSecondary,
                style = WeatherTypography.SectionTitle,
            )
            Text(
                text = label,
                modifier = Modifier.padding(start = SettingsTokens.SegmentLabelGap),
                color = if (selected) colors.textPrimary else colors.textSecondary,
                style = WeatherTypography.BodyStrong,
            )
        }
    }
}

private data class UnitsResources(
    val label: StringResource,
    val symbol: StringResource,
    val accessibilityLabel: StringResource,
)

private fun Units.resources(): UnitsResources = when (this) {
    Units.METRIC -> UnitsResources(
        label = Res.string.units_metric,
        symbol = Res.string.units_metric_symbol,
        accessibilityLabel = Res.string.units_metric_accessibility,
    )
    Units.IMPERIAL -> UnitsResources(
        label = Res.string.units_imperial,
        symbol = Res.string.units_imperial_symbol,
        accessibilityLabel = Res.string.units_imperial_accessibility,
    )
}
