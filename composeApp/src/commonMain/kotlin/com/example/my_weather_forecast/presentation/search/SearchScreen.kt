package com.example.my_weather_forecast.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.presentation.components.WeatherScreenFrame
import com.example.my_weather_forecast.presentation.platform.LocalWeatherPlatformBehavior
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.add_area_title
import myweatherforecast.composeapp.generated.resources.back
import myweatherforecast.composeapp.generated.resources.ic_back
import myweatherforecast.composeapp.generated.resources.search_add_failed
import myweatherforecast.composeapp.generated.resources.search_already_saved
import myweatherforecast.composeapp.generated.resources.search_at_limit
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
    val snackbarHostState = remember { SnackbarHostState() }
    val atLimitMessage = stringResource(Res.string.search_at_limit)
    val alreadySavedMessage = stringResource(Res.string.search_already_saved)
    val addFailedMessage = stringResource(Res.string.search_add_failed)
    val platformBehavior = LocalWeatherPlatformBehavior.current

    val onBackDismissingKeyboard: () -> Unit = {
        platformBehavior.dismissKeyboard()
        onBack()
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SearchEvent.Added -> onBackDismissingKeyboard()
                SearchEvent.AtLimit -> snackbarHostState.showSnackbar(atLimitMessage)
                SearchEvent.AlreadySaved -> snackbarHostState.showSnackbar(alreadySavedMessage)
                SearchEvent.AddFailed -> snackbarHostState.showSnackbar(addFailedMessage)
            }
        }
    }

    WeatherScreenFrame(
        title = stringResource(Res.string.add_area_title),
        modifier = modifier,
        contentMaxWidth = WeatherLayout.FormMaxWidth,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            onQueryChange = viewModel::onQueryChange,
            onLocationClick = viewModel::addLocation,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
