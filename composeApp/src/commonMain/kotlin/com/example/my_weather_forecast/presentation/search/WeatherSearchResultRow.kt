package com.example.my_weather_forecast.presentation.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.SearchResultTokens
import com.example.my_weather_forecast.presentation.theme.WeatherMotion
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_add
import myweatherforecast.composeapp.generated.resources.ic_location
import myweatherforecast.composeapp.generated.resources.location_result_accessibility
import myweatherforecast.composeapp.generated.resources.location_subtitle_with_state
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal const val WEATHER_SEARCH_RESULT_ROW_TEST_TAG = "weather_search_result_row"

@Composable
fun WeatherSearchResultRow(
    location: Location,
    pressFeedback: WeatherPressFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = WeatherTheme.colors
    val subtitle = location.subtitle()
    val accessibilityDescription = stringResource(
        Res.string.location_result_accessibility,
        location.name,
        subtitle,
    )
    val isPressed by interactionSource.collectIsPressedAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val usesTonalFeedback = pressFeedback == WeatherPressFeedback.TONAL_HIGHLIGHT
    val backgroundColor by animateColorAsState(
        targetValue = if (usesTonalFeedback && (isPressed || isFocused)) {
            colors.searchResultPressed
        } else {
            Color.Transparent
        },
        animationSpec = tween(
            durationMillis = WeatherMotion.PressMillis,
            easing = WeatherMotion.StandardEasing,
        ),
        label = "search result background",
    )
    val pressedScale by animateFloatAsState(
        targetValue = if (usesTonalFeedback && isPressed) {
            SearchResultTokens.PressedScale
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = WeatherMotion.PressMillis,
            easing = WeatherMotion.StandardEasing,
        ),
        label = "search result scale",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = SearchResultTokens.RowMinHeight)
            .graphicsLayer {
                scaleX = pressedScale
                scaleY = pressedScale
            }
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = when (pressFeedback) {
                    WeatherPressFeedback.MATERIAL_RIPPLE -> ripple(
                        bounded = true,
                        color = colors.accent,
                    )

                    WeatherPressFeedback.TONAL_HIGHLIGHT -> null
                },
                role = Role.Button,
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = accessibilityDescription
            }
            .testTag(WEATHER_SEARCH_RESULT_ROW_TEST_TAG)
            .padding(
                horizontal = SearchResultTokens.RowHorizontalPadding,
                vertical = SearchResultTokens.RowVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(SearchResultTokens.LeadingContainerSize)
                .clip(RoundedCornerShape(WeatherRadii.Control))
                .background(colors.surfaceMuted),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_location),
                contentDescription = null,
                modifier = Modifier.size(SearchResultTokens.LeadingIconSize),
                tint = colors.accent,
            )
        }
        Spacer(modifier = Modifier.width(SearchResultTokens.LeadingGap))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                modifier = Modifier.clearAndSetSemantics {},
                color = colors.textPrimary,
                style = WeatherTypography.ItemTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(SearchResultTokens.TextLineGap))
            Text(
                text = subtitle,
                modifier = Modifier.clearAndSetSemantics {},
                color = colors.textSecondary,
                style = WeatherTypography.Caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier.size(SearchResultTokens.TrailingSlotSize),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(SearchResultTokens.TrailingIconSize),
                tint = colors.accent,
            )
        }
    }
}

@Composable
internal fun Location.subtitle(): String =
    if (state != null) {
        stringResource(Res.string.location_subtitle_with_state, state, country)
    } else {
        country
    }
