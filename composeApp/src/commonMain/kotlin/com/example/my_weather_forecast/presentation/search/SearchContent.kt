package com.example.my_weather_forecast.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.error_generic_search
import myweatherforecast.composeapp.generated.resources.error_network_try_again
import myweatherforecast.composeapp.generated.resources.error_not_found_search
import myweatherforecast.composeapp.generated.resources.error_rate_limited
import myweatherforecast.composeapp.generated.resources.error_unauthorized_search
import myweatherforecast.composeapp.generated.resources.location_result_accessibility
import myweatherforecast.composeapp.generated.resources.location_subtitle_with_state
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchContent(
    uiState: SearchUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onLocationClick: (Location) -> Unit,
    onSearch: () -> Unit = {},
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
                .padding(16.dp),
        )
        when (uiState) {
            is SearchUiState.Idle -> Unit
            is SearchUiState.Loading -> Unit
            is SearchUiState.Empty -> MessageRow(stringResource(Res.string.error_not_found_search))
            is SearchUiState.Error -> {
                val message = uiState.error.toMessage()
                MessageRow(message)
            }
            is SearchUiState.Results -> ResultsList(uiState.locations, onLocationClick)
        }
    }
}

@Composable
private fun MessageRow(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun WeatherError.toMessage(): String = when (this) {
    WeatherError.Network -> stringResource(Res.string.error_network_try_again)
    WeatherError.RateLimited -> stringResource(Res.string.error_rate_limited)
    WeatherError.Unauthorized -> stringResource(Res.string.error_unauthorized_search)
    WeatherError.NotFound -> stringResource(Res.string.error_not_found_search)
    WeatherError.AtLimit, WeatherError.AlreadySaved, is WeatherError.Unknown -> stringResource(Res.string.error_generic_search)
}

@Composable
private fun ResultsList(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(locations, key = { "${it.lat},${it.lon}" }) { location ->
            SearchResultRow(location = location, onClick = { onLocationClick(location) })
        }
    }
}

@Composable
private fun SearchResultRow(
    location: Location,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtitle = location.subtitle()
    val accessibilityDescription = stringResource(Res.string.location_result_accessibility, location.name, subtitle)
    ListItem(
        headlineContent = { Text(location.name) },
        supportingContent = { Text(subtitle) },
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = accessibilityDescription },
    )
}

@Composable
private fun Location.subtitle(): String =
    if (state != null) stringResource(Res.string.location_subtitle_with_state, state, country) else country
