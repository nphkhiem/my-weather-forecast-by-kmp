package com.example.my_weather_forecast.presentation.search

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private val results = listOf(
    Location(id = 0, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0),
    Location(id = 0, name = "Chicago Heights", country = "US", state = "IL", lat = 41.5, lon = -87.63, sortOrder = 0),
)

@Composable
private fun SearchContentPreview(uiState: SearchUiState, query: String, darkTheme: Boolean) {
    WeatherForecastTheme(darkTheme = darkTheme) {
        Surface {
            SearchContent(
                uiState = uiState,
                query = query,
                onQueryChange = {},
                onLocationClick = {},
                autoFocusSearch = false,
            )
        }
    }
}

@Preview
@Composable
private fun SearchIdleLightPreview() = SearchContentPreview(SearchUiState.Idle, query = "", darkTheme = false)

@Preview
@Composable
private fun SearchIdleDarkPreview() = SearchContentPreview(SearchUiState.Idle, query = "", darkTheme = true)

@Preview
@Composable
private fun SearchLoadingLightPreview() = SearchContentPreview(SearchUiState.Loading, query = "Chic", darkTheme = false)

@Preview
@Composable
private fun SearchLoadingDarkPreview() = SearchContentPreview(SearchUiState.Loading, query = "Chic", darkTheme = true)

@Preview
@Composable
private fun SearchResultsLightPreview() = SearchContentPreview(SearchUiState.Results(results), query = "Chicago", darkTheme = false)

@Preview
@Composable
private fun SearchResultsDarkPreview() = SearchContentPreview(SearchUiState.Results(results), query = "Chicago", darkTheme = true)

@Preview
@Composable
private fun SearchEmptyLightPreview() = SearchContentPreview(SearchUiState.Empty, query = "Nonexistent Xyz", darkTheme = false)

@Preview
@Composable
private fun SearchEmptyDarkPreview() = SearchContentPreview(SearchUiState.Empty, query = "Nonexistent Xyz", darkTheme = true)

@Preview
@Composable
private fun SearchErrorLightPreview() =
    SearchContentPreview(SearchUiState.Error(WeatherError.Network), query = "Chicago", darkTheme = false)

@Preview
@Composable
private fun SearchErrorDarkPreview() =
    SearchContentPreview(SearchUiState.Error(WeatherError.Network), query = "Chicago", darkTheme = true)
