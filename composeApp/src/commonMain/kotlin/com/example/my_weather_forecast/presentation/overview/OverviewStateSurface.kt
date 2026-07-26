package com.example.my_weather_forecast.presentation.overview

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.OverviewStateTokens
import com.example.my_weather_forecast.presentation.theme.WeatherElevation
import com.example.my_weather_forecast.presentation.theme.WeatherMotion
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

internal const val OVERVIEW_STATE_SURFACE_TEST_TAG = "overview_state_surface"
internal const val OVERVIEW_SNACKBAR_TEST_TAG = "overview_snackbar"

@Composable
internal fun OverviewStateSurface(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: DrawableResource? = null,
    iconTint: Color = WeatherTheme.colors.accent,
    showProgress: Boolean = false,
    primaryActionLabel: String? = null,
    onPrimaryAction: () -> Unit = {},
    secondaryActionLabel: String? = null,
    onSecondaryAction: () -> Unit = {},
) {
    val colors = WeatherTheme.colors

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = OverviewStateTokens.SurfaceMinHeight)
            .testTag(OVERVIEW_STATE_SURFACE_TEST_TAG)
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = RoundedCornerShape(WeatherRadii.Card),
        color = colors.surface,
        border = BorderStroke(OverviewStateTokens.BorderWidth, colors.border),
        shadowElevation = WeatherElevation.Resting,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(OverviewStateTokens.SurfacePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StateIndicator(
                icon = icon,
                iconTint = iconTint,
                showProgress = showProgress,
                modifier = Modifier,
            )
            Spacer(modifier = Modifier.height(WeatherSpacing.Lg))
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                color = colors.textPrimary,
                style = WeatherTypography.ItemTitle,
            )
            Spacer(modifier = Modifier.height(WeatherSpacing.Xs))
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                color = colors.textSecondary,
                style = WeatherTypography.Body,
                textAlign = TextAlign.Center,
            )
            if (primaryActionLabel != null) {
                Spacer(modifier = Modifier.height(WeatherSpacing.Xl))
                OverviewStateAction(
                    label = primaryActionLabel,
                    primary = true,
                    onClick = onPrimaryAction,
                )
            }
            if (secondaryActionLabel != null) {
                Spacer(modifier = Modifier.height(WeatherSpacing.Sm))
                OverviewStateAction(
                    label = secondaryActionLabel,
                    primary = false,
                    onClick = onSecondaryAction,
                )
            }
        }
    }
}

@Composable
private fun StateIndicator(
    icon: DrawableResource?,
    iconTint: Color,
    showProgress: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    Box(
        modifier = modifier
            .size(OverviewStateTokens.IndicatorContainerSize)
            .clip(RoundedCornerShape(WeatherRadii.SmallCard))
            .background(colors.surfaceMuted),
        contentAlignment = Alignment.Center,
    ) {
        when {
            showProgress -> CircularProgressIndicator(
                modifier = Modifier.size(OverviewStateTokens.ProgressSize),
                color = colors.accent,
                strokeWidth = OverviewStateTokens.ProgressStrokeWidth,
            )

            icon != null -> Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(OverviewStateTokens.IndicatorIconSize),
                tint = iconTint,
            )
        }
    }
}

@Composable
private fun OverviewStateAction(
    label: String,
    primary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = WeatherTheme.colors
    val pressFeedback = LocalWeatherPlatformBehavior.current.pressFeedback
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val usesTonalFeedback = pressFeedback == WeatherPressFeedback.TONAL_HIGHLIGHT
    val restingColor = if (primary) colors.accent else colors.surfaceElevated
    val pressedColor = if (primary) colors.accentPressed else colors.searchResultPressed
    val backgroundColor by animateColorAsState(
        targetValue = if (usesTonalFeedback && (isPressed || isFocused)) {
            pressedColor
        } else {
            restingColor
        },
        animationSpec = tween(
            durationMillis = WeatherMotion.PressMillis,
            easing = WeatherMotion.StandardEasing,
        ),
        label = "overview state action",
    )
    val shape = RoundedCornerShape(WeatherRadii.Control)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = OverviewStateTokens.ActionHeight)
            .clip(shape)
            .background(backgroundColor)
            .then(
                if (primary) {
                    Modifier
                } else {
                    Modifier.border(
                        width = OverviewStateTokens.BorderWidth,
                        color = colors.border,
                        shape = shape,
                    )
                },
            )
            .clickable(
                interactionSource = interactionSource,
                indication = when (pressFeedback) {
                    WeatherPressFeedback.MATERIAL_RIPPLE -> ripple(
                        bounded = true,
                        color = if (primary) colors.onAccent else colors.accent,
                    )

                    WeatherPressFeedback.TONAL_HIGHLIGHT -> null
                },
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = WeatherSpacing.Lg),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = if (primary) colors.onAccent else colors.accent,
            style = WeatherTypography.BodyStrong,
        )
    }
}

@Composable
internal fun OverviewInlineStatus(
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    Row(
        modifier = modifier
            .heightIn(min = OverviewStateTokens.InlineStatusHeight)
            .clip(RoundedCornerShape(WeatherRadii.Full))
            .background(colors.surfaceMuted)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = WeatherSpacing.Md),
        horizontalArrangement = Arrangement.spacedBy(WeatherSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(OverviewStateTokens.InlineProgressSize),
            color = colors.accent,
            strokeWidth = OverviewStateTokens.InlineProgressStrokeWidth,
        )
        Text(
            text = label,
            color = colors.textSecondary,
            style = WeatherTypography.Micro,
        )
    }
}

@Composable
internal fun OverviewSnackbarContent(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    val colors = WeatherTheme.colors
    Snackbar(
        modifier = modifier
            .padding(horizontal = WeatherSpacing.Lg)
            .testTag(OVERVIEW_SNACKBAR_TEST_TAG),
        action = actionLabel?.let {
            {
                TextButton(onClick = onAction) {
                    Text(
                        text = actionLabel,
                        color = colors.accent,
                        style = WeatherTypography.BodyStrong,
                    )
                }
            }
        },
        shape = RoundedCornerShape(WeatherRadii.SmallCard),
        containerColor = colors.surfaceElevated,
        contentColor = colors.textPrimary,
    ) {
        Text(
            text = message,
            style = WeatherTypography.Body,
        )
    }
}
