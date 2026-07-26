package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.example.my_weather_forecast.presentation.theme.DetailStateTokens
import com.example.my_weather_forecast.presentation.theme.WeatherRadii
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import com.example.my_weather_forecast.presentation.theme.WeatherTheme
import com.example.my_weather_forecast.presentation.theme.WeatherTypography
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_weather_unknown
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DetailLoadingSurface(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(WeatherSpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(WeatherSpacing.Lg),
    ) {
        DetailStateCard(
            title = title,
            message = message,
            showProgress = true,
            iconTint = WeatherTheme.colors.accent,
            liveRegionMode = LiveRegionMode.Polite,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DetailStateTokens.HeroMinHeight)
                .testTag("detail_loading_hero"),
        )
        DetailLoadingPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailStateTokens.HourlyPlaceholderHeight)
                .testTag("detail_loading_hourly"),
        )
        DetailLoadingPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(DetailStateTokens.DailyPlaceholderHeight)
                .testTag("detail_loading_daily"),
        )
    }
}

@Composable
internal fun DetailErrorSurface(
    title: String,
    message: String,
    retryLabel: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(WeatherSpacing.Lg),
        contentAlignment = Alignment.Center,
    ) {
        DetailStateCard(
            title = title,
            message = message,
            showProgress = false,
            iconTint = WeatherTheme.colors.error,
            liveRegionMode = LiveRegionMode.Assertive,
            actionLabel = retryLabel,
            onAction = onRetry,
        )
    }
}

@Composable
private fun DetailStateCard(
    title: String,
    message: String,
    showProgress: Boolean,
    iconTint: Color,
    liveRegionMode: LiveRegionMode,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val colors = WeatherTheme.colors
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = liveRegionMode },
        shape = RoundedCornerShape(WeatherRadii.Card),
        color = colors.surface,
        contentColor = colors.textPrimary,
        border = BorderStroke(DetailStateTokens.BorderWidth, colors.border),
    ) {
        Column(
            modifier = Modifier.padding(DetailStateTokens.SurfacePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(WeatherSpacing.Md),
        ) {
            Surface(
                shape = RoundedCornerShape(WeatherRadii.SmallCard),
                color = colors.surfaceMuted,
            ) {
                Box(
                    modifier = Modifier.size(DetailStateTokens.IndicatorContainerSize),
                    contentAlignment = Alignment.Center,
                ) {
                    if (showProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(DetailStateTokens.ProgressSize),
                            color = colors.accent,
                            strokeWidth = DetailStateTokens.ProgressStrokeWidth,
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.ic_weather_unknown),
                            contentDescription = null,
                            modifier = Modifier.size(DetailStateTokens.IndicatorIconSize),
                            tint = iconTint,
                        )
                    }
                }
            }
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                color = colors.textPrimary,
                style = WeatherTypography.ItemTitle,
            )
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                color = colors.textSecondary,
                style = WeatherTypography.Body,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onAction != null) {
                Surface(
                    onClick = onAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = DetailStateTokens.ActionHeight),
                    shape = RoundedCornerShape(WeatherRadii.Control),
                    color = colors.accent,
                    contentColor = colors.onAccent,
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = WeatherSpacing.Lg),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = actionLabel, style = WeatherTypography.BodyStrong)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLoadingPlaceholder(modifier: Modifier = Modifier) {
    val colors = WeatherTheme.colors
    Surface(
        modifier = modifier.clearAndSetSemantics {},
        shape = RoundedCornerShape(WeatherRadii.Card),
        color = colors.surface,
        border = BorderStroke(DetailStateTokens.BorderWidth, colors.border),
    ) {
        Column(
            modifier = Modifier.padding(DetailStateTokens.SurfacePadding),
            verticalArrangement = Arrangement.spacedBy(WeatherSpacing.Md),
        ) {
            PlaceholderLine(widthFraction = 0.42f)
            PlaceholderLine(widthFraction = 0.72f)
            PlaceholderLine(widthFraction = 0.56f)
        }
    }
}

@Composable
private fun PlaceholderLine(
    widthFraction: Float,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(DetailStateTokens.PlaceholderLineHeight),
        shape = RoundedCornerShape(WeatherRadii.Full),
        color = WeatherTheme.colors.surfaceMuted,
    ) {}
}
