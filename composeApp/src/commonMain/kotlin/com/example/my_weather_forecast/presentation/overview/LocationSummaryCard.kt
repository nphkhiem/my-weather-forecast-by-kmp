package com.example.my_weather_forecast.presentation.overview

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.style.TextOverflow
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.OverviewCardTokens
import com.example.my_weather_forecast.presentation.theme.WeatherElevation
import com.example.my_weather_forecast.presentation.theme.WeatherMotion
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import com.example.my_weather_forecast.presentation.theme.conditionPalette
import com.example.my_weather_forecast.presentation.theme.readableName
import com.example.my_weather_forecast.presentation.theme.toDrawableResource
import kotlin.math.roundToInt
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.area_accessibility
import myweatherforecast.composeapp.generated.resources.area_accessibility_stale_suffix
import myweatherforecast.composeapp.generated.resources.area_high
import myweatherforecast.composeapp.generated.resources.area_low
import myweatherforecast.composeapp.generated.resources.area_rain
import myweatherforecast.composeapp.generated.resources.area_stale
import myweatherforecast.composeapp.generated.resources.delete_area
import myweatherforecast.composeapp.generated.resources.ic_delete
import myweatherforecast.composeapp.generated.resources.temp_degrees
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSummaryCard(
    area: AreaSummary,
    onClick: () -> Unit,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove(area.id)
                true
            } else {
                false
            }
        },
    )

    val todayHigh = area.todayHigh.roundToInt()
    val todayLow = area.todayLow.roundToInt()
    val rainChance = (area.rainChance * 100).roundToInt()
    val currentTemp = area.currentTemp.roundToInt()
    val staleSuffix = if (area.stale) stringResource(Res.string.area_accessibility_stale_suffix) else ""
    val accessibilityDescription = stringResource(
        Res.string.area_accessibility,
        area.name, currentTemp, todayHigh, todayLow, rainChance, staleSuffix,
    )
    val palette = area.icon.conditionPalette(
        isDaytime = area.isDaytime,
        darkTheme = WeatherTheme.darkTheme,
    )
    val colors = WeatherTheme.colors
    val shape = RoundedCornerShape(WeatherRadii.Card)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val pressFeedback = LocalWeatherPlatformBehavior.current.pressFeedback
    val usesTonalFeedback = pressFeedback == WeatherPressFeedback.TONAL_HIGHLIGHT
    val pressedOverlay by animateColorAsState(
        targetValue = if (usesTonalFeedback && (isPressed || isFocused)) {
            colors.searchResultPressed.copy(alpha = OverviewCardTokens.PressedOverlayAlpha)
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = WeatherMotion.PressMillis,
            easing = WeatherMotion.StandardEasing,
        ),
        label = "overview card press",
    )
    val pressedScale by animateFloatAsState(
        targetValue = if (usesTonalFeedback && isPressed) {
            WeatherMotion.CardPressedScale
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = WeatherMotion.PressMillis,
            easing = WeatherMotion.StandardEasing,
        ),
        label = "overview card scale",
    )
    val deleteLabel = stringResource(Res.string.delete_area, area.name)
    val cardClick = onClick

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer, shape)
                    .padding(horizontal = WeatherSpacing.Xl),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_delete),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = OverviewCardTokens.MinHeight)
                .graphicsLayer {
                    scaleX = pressedScale
                    scaleY = pressedScale
                }
                .shadow(WeatherElevation.Resting, shape)
                .clip(shape)
                .background(
                    Brush.linearGradient(listOf(palette.gradientStart, palette.gradientEnd)),
                    shape,
                )
                .border(
                    width = OverviewCardTokens.BorderWidth,
                    color = colors.border.copy(alpha = OverviewCardTokens.BorderAlpha),
                    shape = shape,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = when (pressFeedback) {
                        WeatherPressFeedback.MATERIAL_RIPPLE -> ripple(
                            bounded = true,
                            color = palette.accent,
                        )

                        WeatherPressFeedback.TONAL_HIGHLIGHT -> null
                    },
                    role = Role.Button,
                    onClick = onClick,
                )
                .clearAndSetSemantics {
                    role = Role.Button
                    contentDescription = accessibilityDescription
                    onClick(action = {
                        cardClick()
                        true
                    })
                    customActions = listOf(
                        CustomAccessibilityAction(
                            label = deleteLabel,
                            action = {
                                onRemove(area.id)
                                true
                            },
                        ),
                    )
                },
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(pressedOverlay),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = OverviewCardTokens.MinHeight)
                    .padding(
                        horizontal = OverviewCardTokens.HorizontalPadding,
                        vertical = OverviewCardTokens.VerticalPadding,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(OverviewCardTokens.LeadingContainerSize)
                        .clip(RoundedCornerShape(WeatherRadii.SmallCard))
                        .background(
                            Color.White.copy(
                                alpha = if (WeatherTheme.darkTheme) {
                                    OverviewCardTokens.DarkIconSurfaceAlpha
                                } else {
                                    OverviewCardTokens.LightIconSurfaceAlpha
                                },
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(area.icon.toDrawableResource()),
                        contentDescription = null,
                        modifier = Modifier.size(OverviewCardTokens.WeatherIconSize),
                        tint = palette.accent,
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = OverviewCardTokens.LeadingGap)
                        .weight(1f),
                ) {
                    Text(
                        text = area.name,
                        color = palette.onGradient,
                        style = WeatherTypography.ItemTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(OverviewCardTokens.TitleToConditionGap))
                    Text(
                        text = area.icon.readableName(),
                        color = colors.textSecondary,
                        style = WeatherTypography.Caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(OverviewCardTokens.ConditionToMetricsGap))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(OverviewCardTokens.MetricGap),
                        verticalArrangement = Arrangement.spacedBy(OverviewCardTokens.TitleToConditionGap),
                    ) {
                        OverviewMetric(stringResource(Res.string.area_high, todayHigh))
                        OverviewMetric(stringResource(Res.string.area_low, todayLow))
                        OverviewMetric(stringResource(Res.string.area_rain, rainChance))
                    }
                    if (area.stale) {
                        Spacer(modifier = Modifier.height(OverviewCardTokens.StaleGap))
                        Text(
                            text = stringResource(Res.string.area_stale),
                            color = colors.warning,
                            style = WeatherTypography.Micro,
                        )
                    }
                }
                Text(
                    text = stringResource(Res.string.temp_degrees, currentTemp),
                    // Centred against the name, condition, and metrics block rather than pinned to
                    // the top, so the two sides of the card read as one row.
                    modifier = Modifier
                        .padding(start = WeatherSpacing.Sm)
                        .align(Alignment.CenterVertically),
                    color = palette.onGradient,
                    style = WeatherTypography.CardTemperature,
                )
            }
        }
    }
}

@Composable
private fun OverviewMetric(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        color = WeatherTheme.colors.textSecondary,
        style = WeatherTypography.Micro,
    )
}
