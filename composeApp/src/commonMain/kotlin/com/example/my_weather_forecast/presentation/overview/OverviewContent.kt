package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.presentation.theme.OverviewCardTokens
import com.example.my_weather_forecast.presentation.theme.OverviewStateTokens
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.error_network_try_again
import myweatherforecast.composeapp.generated.resources.error_not_found_weather
import myweatherforecast.composeapp.generated.resources.error_rate_limited
import myweatherforecast.composeapp.generated.resources.error_unauthorized_weather
import myweatherforecast.composeapp.generated.resources.ic_add
import myweatherforecast.composeapp.generated.resources.ic_weather_unknown
import myweatherforecast.composeapp.generated.resources.overview_add_place
import myweatherforecast.composeapp.generated.resources.overview_empty_message
import myweatherforecast.composeapp.generated.resources.overview_empty_title
import myweatherforecast.composeapp.generated.resources.overview_error_generic
import myweatherforecast.composeapp.generated.resources.overview_error_title
import myweatherforecast.composeapp.generated.resources.overview_loading_message
import myweatherforecast.composeapp.generated.resources.overview_loading_title
import myweatherforecast.composeapp.generated.resources.overview_places_count
import myweatherforecast.composeapp.generated.resources.overview_places_hint
import myweatherforecast.composeapp.generated.resources.overview_places_title
import myweatherforecast.composeapp.generated.resources.overview_refreshing
import myweatherforecast.composeapp.generated.resources.overview_retry
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

const val OVERVIEW_CONTENT_TEST_TAG = "overview_content"
private const val MAX_SAVED_AREAS = 6

@Composable
fun OverviewContent(
    uiState: OverviewUiState,
    onAreaClick: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    onAddArea: () -> Unit = {},
    onRefresh: () -> Unit = {},
    isRefreshing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val taggedModifier = modifier.testTag(OVERVIEW_CONTENT_TEST_TAG)
    when (uiState) {
        is OverviewUiState.Loading -> LoadingContent(taggedModifier)
        is OverviewUiState.Empty -> EmptyContent(
            onAddArea = onAddArea,
            modifier = taggedModifier,
        )
        is OverviewUiState.Error -> ErrorContent(
            error = uiState.error,
            onRefresh = onRefresh,
            onAddArea = onAddArea,
            modifier = taggedModifier,
        )
        is OverviewUiState.Success -> SuccessContent(
            areas = uiState.areas,
            onAreaClick = onAreaClick,
            onRemove = onRemove,
            onAddArea = onAddArea,
            isRefreshing = isRefreshing,
            modifier = taggedModifier,
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    OverviewCollection(
        areaCount = null,
        modifier = modifier,
    ) {
        item(key = "loading_state") {
            OverviewStateSurface(
                title = stringResource(Res.string.overview_loading_title),
                message = stringResource(Res.string.overview_loading_message),
                showProgress = true,
            )
        }
    }
}

@Composable
private fun EmptyContent(
    onAddArea: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OverviewCollection(
        areaCount = 0,
        modifier = modifier,
    ) {
        item(key = "empty_state_spacing") {
            Spacer(modifier = Modifier.height(OverviewStateTokens.EmptyTopSpacerHeight))
        }
        item(key = "empty_state") {
            OverviewStateSurface(
                title = stringResource(Res.string.overview_empty_title),
                message = stringResource(Res.string.overview_empty_message),
                icon = Res.drawable.ic_add,
                primaryActionLabel = stringResource(Res.string.overview_add_place),
                onPrimaryAction = onAddArea,
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: WeatherError,
    onRefresh: () -> Unit,
    onAddArea: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OverviewCollection(
        areaCount = null,
        modifier = modifier,
    ) {
        item(key = "error_state") {
            OverviewStateSurface(
                title = stringResource(Res.string.overview_error_title),
                message = error.toMessage(),
                icon = Res.drawable.ic_weather_unknown,
                iconTint = WeatherTheme.colors.error,
                primaryActionLabel = stringResource(Res.string.overview_retry),
                onPrimaryAction = onRefresh,
                secondaryActionLabel = stringResource(Res.string.overview_add_place),
                onSecondaryAction = onAddArea,
            )
        }
    }
}

@Composable
private fun WeatherError.toMessage(): String = when (this) {
    WeatherError.Network -> stringResource(Res.string.error_network_try_again)
    WeatherError.RateLimited -> stringResource(Res.string.error_rate_limited)
    WeatherError.Unauthorized -> stringResource(Res.string.error_unauthorized_weather)
    WeatherError.NotFound -> stringResource(Res.string.error_not_found_weather)
    WeatherError.AtLimit, WeatherError.AlreadySaved, is WeatherError.Unknown ->
        stringResource(Res.string.overview_error_generic)
}

@Composable
private fun SuccessContent(
    areas: List<AreaSummary>,
    onAreaClick: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    onAddArea: () -> Unit,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
) {
    OverviewCollection(
        areaCount = areas.size,
        isRefreshing = isRefreshing,
        modifier = modifier,
    ) {
        items(areas, key = { it.id }) { area ->
            LocationSummaryCard(
                area = area,
                onClick = { onAreaClick(area.id) },
                onRemove = onRemove,
            )
        }
        item(key = "add_place") {
            AddPlaceAction(onClick = onAddArea)
        }
    }
}

@Composable
private fun OverviewCollection(
    areaCount: Int?,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = WeatherSpacing.Lg,
            top = WeatherSpacing.Sm,
            end = WeatherSpacing.Lg,
            bottom = WeatherSpacing.Xxl,
        ),
        verticalArrangement = Arrangement.spacedBy(WeatherSpacing.Md),
    ) {
        item(key = "overview_header") {
            OverviewSectionHeader(
                areaCount = areaCount,
                isRefreshing = isRefreshing,
            )
        }
        content()
    }
}

@Composable
private fun OverviewSectionHeader(
    areaCount: Int?,
    isRefreshing: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WeatherSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.overview_places_title),
                modifier = Modifier.semantics { heading() },
                color = colors.textPrimary,
                style = WeatherTypography.SectionTitle,
            )
            Text(
                text = stringResource(Res.string.overview_places_hint),
                color = colors.textSecondary,
                style = WeatherTypography.Caption,
            )
        }
        when {
            isRefreshing -> OverviewInlineStatus(
                label = stringResource(Res.string.overview_refreshing),
            )

            areaCount != null -> Surface(
                color = colors.surfaceMuted,
                contentColor = colors.textSecondary,
                shape = RoundedCornerShape(WeatherRadii.Full),
            ) {
                Text(
                    text = stringResource(
                        Res.string.overview_places_count,
                        areaCount,
                        MAX_SAVED_AREAS,
                    ),
                    modifier = Modifier.padding(
                        horizontal = WeatherSpacing.Md,
                        vertical = WeatherSpacing.Sm,
                    ),
                    style = WeatherTypography.Micro,
                )
            }
        }
    }
}

@Composable
private fun AddPlaceAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = WeatherTheme.colors
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(WeatherRadii.SearchField),
        color = colors.surfaceElevated,
        contentColor = colors.accent,
        border = BorderStroke(OverviewCardTokens.BorderWidth, colors.border),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = OverviewCardTokens.AddActionHeight)
                .padding(horizontal = WeatherSpacing.Lg),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(OverviewCardTokens.AddActionIconSize),
            )
            Text(
                text = stringResource(Res.string.overview_add_place),
                modifier = Modifier.padding(start = WeatherSpacing.Sm),
                style = WeatherTypography.BodyStrong,
            )
        }
    }
}
