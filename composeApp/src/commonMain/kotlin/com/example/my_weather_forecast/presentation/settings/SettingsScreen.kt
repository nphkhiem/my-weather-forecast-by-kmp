package com.example.my_weather_forecast.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.presentation.components.WeatherScreenFrame
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.back
import myweatherforecast.composeapp.generated.resources.ic_back
import myweatherforecast.composeapp.generated.resources.settings_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val units by viewModel.units.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    WeatherScreenFrame(
        title = stringResource(Res.string.settings_title),
        modifier = modifier,
        contentMaxWidth = WeatherLayout.FormMaxWidth,
        onNavigationClick = onBack,
        navigationIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_back),
                contentDescription = stringResource(Res.string.back),
            )
        },
    ) {
        SettingsContent(
            units = units,
            themeMode = themeMode,
            onUnitsSelected = viewModel::setUnits,
            onThemeModeSelected = viewModel::setThemeMode,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
