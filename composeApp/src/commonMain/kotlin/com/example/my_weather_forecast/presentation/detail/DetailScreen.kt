package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.palette
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.back
import myweatherforecast.composeapp.generated.resources.forecast_title
import myweatherforecast.composeapp.generated.resources.ic_back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun DetailScreen(
    locationId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = koinViewModel(key = locationId.toString()) { parametersOf(locationId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val palette = (uiState as? DetailUiState.Success)
        ?.forecast
        ?.current
        ?.condition
        ?.palette(darkTheme = WeatherTheme.darkTheme)
    val headerContainerColor = palette?.gradientStart ?: MaterialTheme.colorScheme.surface
    val headerContentColor = palette?.onGradient ?: MaterialTheme.colorScheme.onSurface

    Scaffold(
        modifier = modifier,
        containerColor = headerContainerColor,
        // Detail draws its own edge-to-edge gradient; reserving a separate bottom/side inset
        // here would leave a gap showing this solid containerColor instead of that gradient.
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text(uiState.screenTitle(), color = headerContentColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = stringResource(Res.string.back),
                            tint = headerContentColor,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
        ) {
            DetailContent(uiState = uiState, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun DetailUiState.screenTitle(): String =
    if (this is DetailUiState.Success) forecast.location.name else stringResource(Res.string.forecast_title)
