package com.example.my_weather_forecast.presentation.search

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.presentation.platform.WeatherPressFeedback
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private val results = listOf(
    Location(
        id = 0,
        name = "Ho Chi Minh City",
        country = "Vietnam",
        state = null,
        lat = 10.8231,
        lon = 106.6297,
        sortOrder = 0,
    ),
    Location(
        id = 0,
        name = "Ho Chi Minh",
        country = "Vietnam",
        state = "Tiền Giang",
        lat = 10.4493,
        lon = 106.3421,
        sortOrder = 0,
    ),
    Location(
        id = 0,
        name = "Ho Chi Minh",
        country = "Vietnam",
        state = "Đồng Nai",
        lat = 10.9574,
        lon = 106.8426,
        sortOrder = 0,
    ),
    Location(
        id = 0,
        name = "Ho Chi Minh",
        country = "Vietnam",
        state = "Bình Dương",
        lat = 11.3254,
        lon = 106.4770,
        sortOrder = 0,
    ),
)

@Composable
private fun SearchContentPreview(
    uiState: SearchUiState,
    query: String,
    darkTheme: Boolean,
    feedback: SearchEvent? = null,
) {
    WeatherForecastTheme(darkTheme = darkTheme) {
        Surface {
            SearchContent(
                uiState = uiState,
                query = query,
                onQueryChange = {},
                onLocationClick = {},
                feedback = feedback,
                pressFeedback = WeatherPressFeedback.TONAL_HIGHLIGHT,
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
private fun SearchResultsLightPreview() = SearchContentPreview(SearchUiState.Results(results), query = "Ho Chi", darkTheme = false)

@Preview
@Composable
private fun SearchResultsDarkPreview() = SearchContentPreview(SearchUiState.Results(results), query = "Ho Chi", darkTheme = true)

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

@Preview
@Composable
private fun SearchAtLimitLightPreview() = SearchContentPreview(
    uiState = SearchUiState.Results(results),
    query = "Ho Chi",
    darkTheme = false,
    feedback = SearchEvent.AtLimit,
)

@Preview
@Composable
private fun SearchAtLimitDarkPreview() = SearchContentPreview(
    uiState = SearchUiState.Results(results),
    query = "Ho Chi",
    darkTheme = true,
    feedback = SearchEvent.AtLimit,
)

@Preview
@Composable
private fun SearchAlreadySavedLightPreview() = SearchContentPreview(
    uiState = SearchUiState.Results(results),
    query = "Ho Chi",
    darkTheme = false,
    feedback = SearchEvent.AlreadySaved,
)

@Preview
@Composable
private fun SearchAlreadySavedDarkPreview() = SearchContentPreview(
    uiState = SearchUiState.Results(results),
    query = "Ho Chi",
    darkTheme = true,
    feedback = SearchEvent.AlreadySaved,
)

@Preview
@Composable
private fun SearchAddFailedLightPreview() = SearchContentPreview(
    uiState = SearchUiState.Results(results),
    query = "Ho Chi",
    darkTheme = false,
    feedback = SearchEvent.AddFailed,
)

@Preview
@Composable
private fun SearchAddFailedDarkPreview() = SearchContentPreview(
    uiState = SearchUiState.Results(results),
    query = "Ho Chi",
    darkTheme = true,
    feedback = SearchEvent.AddFailed,
)
