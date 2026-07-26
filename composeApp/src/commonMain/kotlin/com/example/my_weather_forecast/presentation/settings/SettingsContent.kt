package com.example.my_weather_forecast.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.theme.SettingsTokens
import com.example.my_weather_forecast.presentation.theme.WeatherElevation
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_device
import myweatherforecast.composeapp.generated.resources.settings_intro_eyebrow
import myweatherforecast.composeapp.generated.resources.settings_intro_message
import myweatherforecast.composeapp.generated.resources.settings_intro_title
import myweatherforecast.composeapp.generated.resources.settings_system_note_message
import myweatherforecast.composeapp.generated.resources.settings_system_note_title
import myweatherforecast.composeapp.generated.resources.theme_section_label
import myweatherforecast.composeapp.generated.resources.theme_section_message
import myweatherforecast.composeapp.generated.resources.units_section_label
import myweatherforecast.composeapp.generated.resources.units_section_message
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

const val SETTINGS_CONTENT_TEST_TAG = "settings_content"

@Composable
fun SettingsContent(
    units: Units,
    themeMode: ThemeMode,
    onUnitsSelected: (Units) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag(SETTINGS_CONTENT_TEST_TAG)
            .verticalScroll(rememberScrollState())
            .padding(SettingsTokens.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(SettingsTokens.ContentGap),
    ) {
        SettingsIntro()
        SettingsPreferenceSurface(
            title = stringResource(Res.string.theme_section_label),
            message = stringResource(Res.string.theme_section_message),
        ) {
            ThemePreviewSelector(
                selectedMode = themeMode,
                onModeSelected = onThemeModeSelected,
            )
        }
        SettingsPreferenceSurface(
            title = stringResource(Res.string.units_section_label),
            message = stringResource(Res.string.units_section_message),
        ) {
            UnitsSegmentedSelector(
                selectedUnits = units,
                onUnitsSelected = onUnitsSelected,
            )
        }
        SettingsSystemNote()
    }
}

@Composable
private fun SettingsSystemNote(modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(WeatherRadii.SmallCard),
        color = colors.surfaceMuted,
        contentColor = colors.textPrimary,
    ) {
        Row(
            modifier = Modifier.padding(SettingsTokens.SystemNotePadding),
            horizontalArrangement = Arrangement.spacedBy(SettingsTokens.SystemNoteGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(SettingsTokens.SystemNoteIconContainerSize),
                shape = CircleShape,
                color = colors.searchResultPressed,
                contentColor = colors.accent,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_device),
                        contentDescription = null,
                        modifier = Modifier.size(SettingsTokens.SystemNoteIconSize),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SettingsTokens.SystemNoteTextGap),
            ) {
                Text(
                    text = stringResource(Res.string.settings_system_note_title),
                    style = WeatherTypography.BodyStrong,
                )
                Text(
                    text = stringResource(Res.string.settings_system_note_message),
                    color = colors.textSecondary,
                    style = WeatherTypography.Caption,
                )
            }
        }
    }
}

@Composable
private fun SettingsIntro(modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SettingsTokens.IntroGap),
    ) {
        Text(
            text = stringResource(Res.string.settings_intro_eyebrow),
            color = colors.accent,
            style = WeatherTypography.Label,
        )
        Text(
            text = stringResource(Res.string.settings_intro_title),
            modifier = Modifier.semantics { heading() },
            color = colors.textPrimary,
            style = WeatherTypography.SectionTitle,
        )
        Text(
            text = stringResource(Res.string.settings_intro_message),
            color = colors.textSecondary,
            style = WeatherTypography.Body,
        )
    }
}

@Composable
private fun SettingsPreferenceSurface(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = WeatherTheme.colors
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(WeatherRadii.Card),
        color = colors.surface,
        contentColor = colors.textPrimary,
        border = BorderStroke(SettingsTokens.BorderWidth, colors.border),
        shadowElevation = WeatherElevation.Resting,
    ) {
        Column(
            modifier = Modifier.padding(SettingsTokens.SectionPadding),
            verticalArrangement = Arrangement.spacedBy(SettingsTokens.SectionContentGap),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SettingsTokens.SectionHeaderGap)) {
                Text(
                    text = title,
                    modifier = Modifier.semantics { heading() },
                    style = WeatherTypography.ItemTitle,
                )
                Text(
                    text = message,
                    color = colors.textSecondary,
                    style = WeatherTypography.Caption,
                )
            }
            content()
        }
    }
}
