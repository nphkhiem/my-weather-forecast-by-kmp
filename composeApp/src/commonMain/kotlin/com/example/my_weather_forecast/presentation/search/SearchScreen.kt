package com.example.my_weather_forecast.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.presentation.components.WeatherScreenFrame
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.add_area_title
import myweatherforecast.composeapp.generated.resources.back
import myweatherforecast.composeapp.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val platformBehavior = LocalWeatherPlatformBehavior.current
    var feedback by remember { mutableStateOf<SearchEvent?>(null) }

    val onBackDismissingKeyboard: () -> Unit = {
        platformBehavior.dismissKeyboard()
        onBack()
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SearchEvent.Added -> onBackDismissingKeyboard()
                SearchEvent.AtLimit,
                SearchEvent.AlreadySaved,
                SearchEvent.AddFailed,
                -> feedback = event
            }
        }
    }

    WeatherScreenFrame(
        title = stringResource(Res.string.add_area_title),
        modifier = modifier,
        contentMaxWidth = WeatherLayout.FormMaxWidth,
        onNavigationClick = onBackDismissingKeyboard,
        navigationIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_back),
                contentDescription = stringResource(Res.string.back),
            )
        },
    ) {
        SearchContent(
            uiState = uiState,
            query = query,
            onQueryChange = {
                feedback = null
                viewModel.onQueryChange(it)
            },
            onLocationClick = {
                feedback = null
                viewModel.addLocation(it)
            },
            onSearch = platformBehavior::dismissKeyboard,
            feedback = feedback,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
