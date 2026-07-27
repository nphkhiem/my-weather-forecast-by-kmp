package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.presentation.components.WeatherScreenFrame
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
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

    DetailScreenContent(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onRefresh = viewModel::refresh,
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailScreenContent(
    uiState: DetailUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = (uiState as? DetailUiState.Success)
        ?.forecast
        ?.current
        ?.condition
        ?.palette(darkTheme = WeatherTheme.darkTheme)
    val headerContentColor = palette?.onGradient ?: MaterialTheme.colorScheme.onSurface

    WeatherScreenFrame(
        title = uiState.screenTitle(),
        modifier = modifier,
        contentMaxWidth = WeatherLayout.ReadingMaxWidth,
        expandedContentMaxWidth = WeatherLayout.PageMaxWidth,
        onNavigationClick = onBack,
        navigationIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_back),
                contentDescription = stringResource(Res.string.back),
            )
        },
        backgroundBrush = palette?.let {
            Brush.verticalGradient(listOf(it.gradientStart, it.gradientEnd))
        },
        topBarContentColor = headerContentColor,
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            DetailContent(
                uiState = uiState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun DetailUiState.screenTitle(): String =
    if (this is DetailUiState.Success) forecast.location.name else stringResource(Res.string.forecast_title)
