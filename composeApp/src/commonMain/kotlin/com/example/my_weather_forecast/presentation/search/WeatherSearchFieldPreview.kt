package com.example.my_weather_forecast.presentation.search

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun WeatherSearchFieldReference(
    query: String,
    isLoading: Boolean,
    isFocused: Boolean,
    darkTheme: Boolean,
) {
    WeatherForecastTheme(darkTheme = darkTheme) {
        Surface {
            val interactionSource = remember { MutableInteractionSource() }

            LaunchedEffect(interactionSource, isFocused) {
                if (isFocused) {
                    interactionSource.emit(FocusInteraction.Focus())
                }
            }

            WeatherSearchField(
                query = query,
                isLoading = isLoading,
                onQueryChange = {},
                onSearch = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WeatherSpacing.Lg),
                autoFocus = false,
                interactionSource = interactionSource,
            )
        }
    }
}

@Preview
@Composable
private fun WeatherSearchFieldIdleLightPreview() {
    WeatherSearchFieldReference(query = "", isLoading = false, isFocused = false, darkTheme = false)
}

@Preview
@Composable
private fun WeatherSearchFieldIdleDarkPreview() {
    WeatherSearchFieldReference(query = "", isLoading = false, isFocused = false, darkTheme = true)
}

@Preview
@Composable
private fun WeatherSearchFieldFocusedLightPreview() {
    WeatherSearchFieldReference(query = "", isLoading = false, isFocused = true, darkTheme = false)
}

@Preview
@Composable
private fun WeatherSearchFieldFocusedDarkPreview() {
    WeatherSearchFieldReference(query = "", isLoading = false, isFocused = true, darkTheme = true)
}

@Preview
@Composable
private fun WeatherSearchFieldTypingLightPreview() {
    WeatherSearchFieldReference(query = "Ho Chi", isLoading = false, isFocused = true, darkTheme = false)
}

@Preview
@Composable
private fun WeatherSearchFieldTypingDarkPreview() {
    WeatherSearchFieldReference(query = "Ho Chi", isLoading = false, isFocused = true, darkTheme = true)
}

@Preview
@Composable
private fun WeatherSearchFieldClearLightPreview() {
    WeatherSearchFieldReference(query = "Hanoi", isLoading = false, isFocused = false, darkTheme = false)
}

@Preview
@Composable
private fun WeatherSearchFieldClearDarkPreview() {
    WeatherSearchFieldReference(query = "Hanoi", isLoading = false, isFocused = false, darkTheme = true)
}

@Preview
@Composable
private fun WeatherSearchFieldLoadingLightPreview() {
    WeatherSearchFieldReference(query = "Da Nang", isLoading = true, isFocused = true, darkTheme = false)
}

@Preview
@Composable
private fun WeatherSearchFieldLoadingDarkPreview() {
    WeatherSearchFieldReference(query = "Da Nang", isLoading = true, isFocused = true, darkTheme = true)
}
