package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.presentation.components.WeatherScreenFrame
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.add_area
import myweatherforecast.composeapp.generated.resources.app_title
import myweatherforecast.composeapp.generated.resources.area_removed
import myweatherforecast.composeapp.generated.resources.ic_add
import myweatherforecast.composeapp.generated.resources.ic_settings
import myweatherforecast.composeapp.generated.resources.refresh_partial_failure
import myweatherforecast.composeapp.generated.resources.settings
import myweatherforecast.composeapp.generated.resources.undo
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun OverviewScreen(
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val undoLabel = stringResource(Res.string.undo)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is OverviewEvent.OpenDetail -> onOpenDetail(event.locationId)
                is OverviewEvent.RefreshPartiallyFailed ->
                    snackbarHostState.showSnackbar(getString(Res.string.refresh_partial_failure))
                is OverviewEvent.AreaRemoved -> {
                    val result = snackbarHostState.showSnackbar(
                        message = getString(Res.string.area_removed, event.name),
                        actionLabel = undoLabel,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoRemove()
                    }
                }
            }
        }
    }

    WeatherScreenFrame(
        title = stringResource(Res.string.app_title),
        modifier = modifier,
        contentMaxWidth = WeatherLayout.OverviewMaxWidth,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        actions = {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    painter = painterResource(Res.drawable.ic_settings),
                    contentDescription = stringResource(Res.string.settings),
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenSearch) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = stringResource(Res.string.add_area),
                )
            }
        },
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            OverviewContent(
                uiState = uiState,
                onAreaClick = viewModel::onAreaClick,
                onRemove = viewModel::removeArea,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
