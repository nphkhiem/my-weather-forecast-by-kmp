package com.example.my_weather_forecast.presentation.search

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.my_weather_forecast.presentation.theme.SearchFieldTokens
import com.example.my_weather_forecast.presentation.theme.WeatherMotion
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.clear_search
import myweatherforecast.composeapp.generated.resources.ic_clear
import myweatherforecast.composeapp.generated.resources.ic_search
import myweatherforecast.composeapp.generated.resources.search_label
import myweatherforecast.composeapp.generated.resources.searching
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal const val WEATHER_SEARCH_FIELD_TEST_TAG = "weather_search_field"
internal const val WEATHER_SEARCH_TRAILING_SLOT_TEST_TAG = "weather_search_trailing_slot"

@Composable
fun WeatherSearchField(
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val colors = WeatherTheme.colors
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val shape = RoundedCornerShape(SearchFieldTokens.ShapeRadius)
    val searchLabel = stringResource(Res.string.search_label)
    val clearLabel = stringResource(Res.string.clear_search)
    val searchingLabel = stringResource(Res.string.searching)
    val containerColor by animateColorAsState(
        targetValue = if (isFocused) colors.searchFieldFocused else colors.searchField,
        animationSpec = tween(
            durationMillis = WeatherMotion.FastMillis,
            easing = WeatherMotion.StandardEasing,
        ),
        label = "search field container",
    )
    val focusRingColor by animateColorAsState(
        targetValue = if (isFocused) colors.focus else Color.Transparent,
        animationSpec = tween(
            durationMillis = WeatherMotion.FastMillis,
            easing = WeatherMotion.StandardEasing,
        ),
        label = "search field focus ring",
    )

    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            withFrameNanos { }
            focusRequester.requestFocus()
        }
    }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(SearchFieldTokens.Height)
            .clip(shape)
            .background(containerColor)
            .border(SearchFieldTokens.FocusRingWidth, focusRingColor, shape)
            .focusRequester(focusRequester)
            .semantics { contentDescription = searchLabel }
            .testTag(WEATHER_SEARCH_FIELD_TEST_TAG),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        singleLine = true,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(colors.accent),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = SearchFieldTokens.HorizontalPadding,
                        end = SearchFieldTokens.TrailingEndPadding,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(SearchFieldTokens.LeadingIconSize),
                    tint = colors.accent,
                )
                Spacer(modifier = Modifier.width(SearchFieldTokens.LeadingIconGap))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = searchLabel,
                            modifier = Modifier.clearAndSetSemantics {},
                            color = colors.textSecondary,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                }
                SearchTrailingSlot(
                    query = query,
                    isLoading = isLoading,
                    clearLabel = clearLabel,
                    searchingLabel = searchingLabel,
                    onClear = {
                        onQueryChange("")
                        focusRequester.requestFocus()
                    },
                )
            }
        },
    )
}

@Composable
private fun SearchTrailingSlot(
    query: String,
    isLoading: Boolean,
    clearLabel: String,
    searchingLabel: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors

    Box(
        modifier = modifier
            .size(SearchFieldTokens.TrailingTouchTarget)
            .testTag(WEATHER_SEARCH_TRAILING_SLOT_TEST_TAG),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isLoading -> CircularProgressIndicator(
                modifier = Modifier
                    .size(SearchFieldTokens.TrailingVisualSize)
                    .semantics { contentDescription = searchingLabel },
                color = colors.accent,
                strokeWidth = SearchFieldTokens.FocusRingWidth,
            )

            query.isNotEmpty() -> IconButton(
                onClick = onClear,
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_clear),
                    contentDescription = clearLabel,
                    modifier = Modifier.size(SearchFieldTokens.TrailingVisualSize),
                    tint = colors.iconSecondary,
                )
            }
        }
    }
}
