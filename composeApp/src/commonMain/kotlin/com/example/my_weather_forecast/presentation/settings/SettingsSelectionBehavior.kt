package com.example.my_weather_forecast.presentation.settings

import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherHapticCue
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback

internal data class SettingsSelectionBehavior(
    val interactionSource: MutableInteractionSource,
    val isPressed: Boolean,
    val isFocused: Boolean,
    val pressFeedback: WeatherPressFeedback,
    val onSelect: () -> Unit,
)

@Composable
internal fun rememberSettingsSelectionBehavior(
    selected: Boolean,
    onSelected: () -> Unit,
): SettingsSelectionBehavior {
    val platformBehavior = LocalWeatherPlatformBehavior.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    return SettingsSelectionBehavior(
        interactionSource = interactionSource,
        isPressed = isPressed,
        isFocused = isFocused,
        pressFeedback = platformBehavior.pressFeedback,
        onSelect = {
            if (!selected) {
                platformBehavior.requestHaptic(WeatherHapticCue.SELECTION)
                onSelected()
            }
        },
    )
}

internal fun Modifier.settingsSelectable(
    selected: Boolean,
    behavior: SettingsSelectionBehavior,
    accessibilityLabel: String,
    testTag: String,
    rippleColor: Color,
): Modifier = selectable(
    selected = selected,
    interactionSource = behavior.interactionSource,
    indication = behavior.indication(rippleColor),
    role = Role.RadioButton,
    onClick = behavior.onSelect,
).semantics(mergeDescendants = true) {
    contentDescription = accessibilityLabel
}.testTag(testTag)

private fun SettingsSelectionBehavior.indication(rippleColor: Color): Indication? =
    when (pressFeedback) {
        WeatherPressFeedback.MATERIAL_RIPPLE -> ripple(
            bounded = true,
            color = rippleColor,
        )

        WeatherPressFeedback.TONAL_HIGHLIGHT -> null
    }
