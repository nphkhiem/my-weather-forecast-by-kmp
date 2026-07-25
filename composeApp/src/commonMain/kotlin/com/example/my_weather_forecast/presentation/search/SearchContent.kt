package com.example.my_weather_forecast.presentation.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.SearchResultTokens
import com.example.my_weather_forecast.presentation.theme.WeatherElevation
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.error_generic_search
import myweatherforecast.composeapp.generated.resources.error_network_try_again
import myweatherforecast.composeapp.generated.resources.error_not_found_search
import myweatherforecast.composeapp.generated.resources.error_rate_limited
import myweatherforecast.composeapp.generated.resources.error_unauthorized_search
import myweatherforecast.composeapp.generated.resources.ic_location
import myweatherforecast.composeapp.generated.resources.ic_search
import myweatherforecast.composeapp.generated.resources.search_add_failed
import myweatherforecast.composeapp.generated.resources.search_already_saved
import myweatherforecast.composeapp.generated.resources.search_at_limit
import myweatherforecast.composeapp.generated.resources.search_precision_hint
import myweatherforecast.composeapp.generated.resources.search_results_count
import myweatherforecast.composeapp.generated.resources.search_results_hint
import myweatherforecast.composeapp.generated.resources.search_results_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

internal const val SEARCH_RESULTS_TITLE_TEST_TAG = "search_results_title"
internal const val SEARCH_INLINE_FEEDBACK_TEST_TAG = "search_inline_feedback"

@Composable
fun SearchContent(
    uiState: SearchUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onLocationClick: (Location) -> Unit,
    onSearch: () -> Unit = {},
    feedback: SearchEvent? = null,
    pressFeedback: WeatherPressFeedback = LocalWeatherPlatformBehavior.current.pressFeedback,
    modifier: Modifier = Modifier,
    autoFocusSearch: Boolean = true,
) {
    Column(modifier = modifier.fillMaxSize()) {
        WeatherSearchField(
            query = query,
            isLoading = uiState is SearchUiState.Loading,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            autoFocus = autoFocusSearch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = WeatherSpacing.Lg,
                    top = WeatherSpacing.Lg,
                    end = WeatherSpacing.Lg,
                ),
        )

        feedback?.message()?.let { message ->
            SearchInlineFeedback(
                message = message,
                event = feedback,
                modifier = Modifier.padding(
                    start = WeatherSpacing.Lg,
                    top = WeatherSpacing.Md,
                    end = WeatherSpacing.Lg,
                ),
            )
        }

        when (uiState) {
            is SearchUiState.Idle,
            is SearchUiState.Loading,
            -> Unit

            is SearchUiState.Empty -> SearchStateMessage(
                message = stringResource(Res.string.error_not_found_search),
                isError = false,
            )

            is SearchUiState.Error -> SearchStateMessage(
                message = uiState.error.toMessage(),
                isError = true,
            )

            is SearchUiState.Results -> SearchResultsCollection(
                locations = uiState.locations,
                pressFeedback = pressFeedback,
                onLocationClick = onLocationClick,
                modifier = Modifier.weight(1f),
                topPadding = if (feedback == null) WeatherSpacing.Xl else WeatherSpacing.Md,
            )
        }
    }
}

@Composable
private fun SearchResultsCollection(
    locations: List<Location>,
    pressFeedback: WeatherPressFeedback,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier,
    topPadding: Dp = WeatherSpacing.Xl,
) {
    val colors = WeatherTheme.colors

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = WeatherSpacing.Lg,
            top = topPadding,
            end = WeatherSpacing.Lg,
            bottom = WeatherSpacing.Lg,
        ),
    ) {
        item {
            SearchResultsSummary(resultCount = locations.size)
        }
        item {
            Spacer(modifier = Modifier.height(WeatherSpacing.Md))
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SearchResultTokens.GroupRadius),
                color = colors.surface,
                border = BorderStroke(SearchResultTokens.SeparatorThickness, colors.border),
                shadowElevation = WeatherElevation.Resting,
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = SearchResultTokens.GroupVerticalPadding,
                    ),
                ) {
                    locations.forEachIndexed { index, location ->
                        key(location.lat, location.lon) {
                            WeatherSearchResultRow(
                                location = location,
                                pressFeedback = pressFeedback,
                                onClick = { onLocationClick(location) },
                            )
                        }
                        if (index < locations.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(
                                    start = SearchResultTokens.SeparatorStartInset,
                                    end = SearchResultTokens.RowHorizontalPadding,
                                ),
                                thickness = SearchResultTokens.SeparatorThickness,
                                color = colors.divider,
                            )
                        }
                    }
                }
            }
        }
        item {
            SearchPrecisionHint(
                modifier = Modifier.padding(
                    start = WeatherSpacing.Sm,
                    top = WeatherSpacing.Md,
                    end = WeatherSpacing.Sm,
                ),
            )
        }
    }
}

@Composable
private fun SearchResultsSummary(
    resultCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    val resultCountLabel = stringResource(Res.string.search_results_count, resultCount)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.search_results_title),
                modifier = Modifier.testTag(SEARCH_RESULTS_TITLE_TEST_TAG),
                color = colors.textPrimary,
                style = WeatherTypography.ItemTitle,
            )
            Text(
                text = stringResource(Res.string.search_results_hint),
                color = colors.textSecondary,
                style = WeatherTypography.Caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(WeatherSpacing.Md))
        Surface(
            shape = RoundedCornerShape(WeatherRadii.Full),
            color = colors.surfaceMuted,
        ) {
            Text(
                text = resultCountLabel,
                modifier = Modifier.padding(
                    horizontal = WeatherSpacing.Sm,
                    vertical = WeatherSpacing.Xs,
                ),
                color = colors.textSecondary,
                style = WeatherTypography.Micro,
            )
        }
    }
}

@Composable
private fun SearchPrecisionHint(modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_location),
            contentDescription = null,
            modifier = Modifier.size(WeatherSpacing.Md),
            tint = colors.textTertiary,
        )
        Spacer(modifier = Modifier.width(WeatherSpacing.Sm))
        Text(
            text = stringResource(Res.string.search_precision_hint),
            color = colors.textTertiary,
            style = WeatherTypography.Micro,
        )
    }
}

@Composable
private fun SearchStateMessage(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = WeatherSpacing.Lg,
                top = WeatherSpacing.Xl,
                end = WeatherSpacing.Lg,
            )
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = RoundedCornerShape(WeatherRadii.Card),
        color = colors.surface,
        border = BorderStroke(SearchResultTokens.SeparatorThickness, colors.border),
        shadowElevation = WeatherElevation.Resting,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = SearchResultTokens.RowMinHeight)
                .padding(WeatherSpacing.Lg),
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
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(SearchResultTokens.LeadingIconSize),
                    tint = if (isError) colors.error else colors.accent,
                )
            }
            Spacer(modifier = Modifier.width(SearchResultTokens.LeadingGap))
            Text(
                text = message,
                color = colors.textSecondary,
                style = WeatherTypography.Body,
            )
        }
    }
}

@Composable
private fun SearchInlineFeedback(
    message: String,
    event: SearchEvent,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    val contentColor: Color = when (event) {
        SearchEvent.Added -> colors.success
        SearchEvent.AtLimit -> colors.warning
        SearchEvent.AlreadySaved -> colors.info
        SearchEvent.AddFailed -> colors.error
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag(SEARCH_INLINE_FEEDBACK_TEST_TAG)
            .semantics { liveRegion = LiveRegionMode.Polite },
        shape = RoundedCornerShape(WeatherRadii.Control),
        color = colors.surfaceMuted,
        border = BorderStroke(SearchResultTokens.SeparatorThickness, colors.divider),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(
                horizontal = WeatherSpacing.Lg,
                vertical = WeatherSpacing.Md,
            ),
            color = contentColor,
            style = WeatherTypography.BodyStrong,
        )
    }
}

@Composable
private fun SearchEvent.message(): String? = when (this) {
    SearchEvent.Added -> null
    SearchEvent.AtLimit -> stringResource(Res.string.search_at_limit)
    SearchEvent.AlreadySaved -> stringResource(Res.string.search_already_saved)
    SearchEvent.AddFailed -> stringResource(Res.string.search_add_failed)
}

@Composable
private fun WeatherError.toMessage(): String = when (this) {
    WeatherError.Network -> stringResource(Res.string.error_network_try_again)
    WeatherError.RateLimited -> stringResource(Res.string.error_rate_limited)
    WeatherError.Unauthorized -> stringResource(Res.string.error_unauthorized_search)
    WeatherError.NotFound -> stringResource(Res.string.error_not_found_search)
    WeatherError.AtLimit,
    WeatherError.AlreadySaved,
    is WeatherError.Unknown,
    -> stringResource(Res.string.error_generic_search)
}
