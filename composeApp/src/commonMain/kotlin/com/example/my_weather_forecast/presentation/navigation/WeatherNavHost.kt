package com.example.my_weather_forecast.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.my_weather_forecast.presentation.detail.DetailScreen
import com.example.my_weather_forecast.presentation.overview.OverviewScreen
import com.example.my_weather_forecast.presentation.search.SearchScreen
import com.example.my_weather_forecast.presentation.settings.SettingsScreen

@Composable
fun WeatherNavHost(navController: NavHostController = rememberNavController()) {
    WeatherNavHost(
        navController = navController,
        overviewContent = { onOpenSearch, onOpenSettings, onOpenDetail ->
            OverviewScreen(
                onOpenSearch = onOpenSearch,
                onOpenSettings = onOpenSettings,
                onOpenDetail = onOpenDetail,
            )
        },
        searchContent = { onBack -> SearchScreen(onBack = onBack) },
        settingsContent = { onBack -> SettingsScreen(onBack = onBack) },
        detailContent = { locationId, onBack ->
            DetailScreen(locationId = locationId, onBack = onBack)
        },
    )
}

@Composable
internal fun WeatherNavHost(
    navController: NavHostController,
    overviewContent: @Composable (
        onOpenSearch: () -> Unit,
        onOpenSettings: () -> Unit,
        onOpenDetail: (Long) -> Unit,
    ) -> Unit,
    searchContent: @Composable (onBack: () -> Unit) -> Unit,
    settingsContent: @Composable (onBack: () -> Unit) -> Unit,
    detailContent: @Composable (locationId: Long, onBack: () -> Unit) -> Unit,
) {
    NavHost(navController = navController, startDestination = Routes.Overview) {
        composable<Routes.Overview> {
            overviewContent(
                { navController.navigate(Routes.Search) },
                { navController.navigate(Routes.Settings) },
                { locationId -> navController.navigate(Routes.Detail(locationId)) },
            )
        }
        composable<Routes.Search> {
            searchContent { navController.popBackStack() }
        }
        composable<Routes.Settings> {
            settingsContent { navController.popBackStack() }
        }
        composable<Routes.Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<Routes.Detail>()
            detailContent(
                route.locationId,
                { navController.popBackStack() },
            )
        }
    }
}
