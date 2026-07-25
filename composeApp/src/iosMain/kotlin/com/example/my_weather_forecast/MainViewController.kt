package com.example.my_weather_forecast

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController

@OptIn(ExperimentalComposeUiApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        // https://kotlinlang.org/docs/multiplatform/compose-navigation.html#back-gesture
        enableBackGesture = true
    },
) {
    App()
}
