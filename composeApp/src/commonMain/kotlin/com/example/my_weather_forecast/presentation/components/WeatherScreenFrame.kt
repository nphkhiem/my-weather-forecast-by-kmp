package com.example.my_weather_forecast.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.example.my_weather_forecast.presentation.platform.WeatherSystemBarIconTone
import com.example.my_weather_forecast.presentation.platform.WeatherSystemBars
import com.example.my_weather_forecast.presentation.theme.WeatherLayout
import com.example.my_weather_forecast.presentation.theme.WeatherSpacing
import com.example.my_weather_forecast.presentation.theme.WeatherTheme

internal const val WEATHER_SCREEN_CONTENT_TEST_TAG = "weather_screen_content"
internal const val WEATHER_TOP_BAR_CONTENT_TEST_TAG = "weather_top_bar_content"
internal const val WEATHER_TOP_BAR_NAVIGATION_TEST_TAG = "weather_top_bar_navigation"

enum class WeatherTopBarAlignment {
    START,
    CENTER,
}

@Composable
fun WeatherScreenFrame(
    title: String,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = WeatherLayout.PageMaxWidth,
    onNavigationClick: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    topBarAlignment: WeatherTopBarAlignment = if (onNavigationClick == null) {
        WeatherTopBarAlignment.START
    } else {
        WeatherTopBarAlignment.CENTER
    },
    actions: @Composable RowScope.() -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    backgroundBrush: Brush? = null,
    topBarContentColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = WeatherTheme.colors
    val resolvedBackground = backgroundBrush ?: Brush.verticalGradient(
        colors = listOf(colors.canvas, colors.canvasSecondary),
    )
    val resolvedTopBarContentColor = if (topBarContentColor == Color.Unspecified) {
        MaterialTheme.colorScheme.onBackground
    } else {
        topBarContentColor
    }

    WeatherSystemBars(
        iconTone = if (WeatherTheme.darkTheme) {
            WeatherSystemBarIconTone.LIGHT
        } else {
            WeatherSystemBarIconTone.DARK
        },
    )

    Box(modifier = modifier.fillMaxSize().background(resolvedBackground)) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                WeatherTopBar(
                    title = title,
                    contentMaxWidth = contentMaxWidth,
                    alignment = topBarAlignment,
                    onNavigationClick = onNavigationClick,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    contentColor = resolvedTopBarContentColor,
                )
            },
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = contentMaxWidth)
                        .fillMaxSize()
                        .testTag(WEATHER_SCREEN_CONTENT_TEST_TAG),
                    content = content,
                )
            }
        }
    }
}

@Composable
fun WeatherTopBar(
    title: String,
    modifier: Modifier = Modifier,
    contentMaxWidth: Dp = WeatherLayout.PageMaxWidth,
    alignment: WeatherTopBarAlignment = WeatherTopBarAlignment.START,
    onNavigationClick: (() -> Unit)? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                ),
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Box(
                modifier = Modifier
                    .widthIn(max = contentMaxWidth)
                    .fillMaxWidth()
                    .height(WeatherLayout.CompactAppBarHeight)
                    .testTag(WEATHER_TOP_BAR_CONTENT_TEST_TAG)
                    .padding(horizontal = WeatherSpacing.Lg),
            ) {
                when (alignment) {
                    WeatherTopBarAlignment.START -> StartAlignedTopBarContent(
                        title = title,
                        onNavigationClick = onNavigationClick,
                        navigationIcon = navigationIcon,
                        actions = actions,
                    )

                    WeatherTopBarAlignment.CENTER -> CenterAlignedTopBarContent(
                        title = title,
                        onNavigationClick = onNavigationClick,
                        navigationIcon = navigationIcon,
                        actions = actions,
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxScope.StartAlignedTopBarContent(
    title: String,
    onNavigationClick: (() -> Unit)?,
    navigationIcon: (@Composable () -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WeatherNavigationButton(onNavigationClick, navigationIcon)
        WeatherTopBarTitle(
            title = title,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )
        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

@Composable
private fun BoxScope.CenterAlignedTopBarContent(
    title: String,
    onNavigationClick: (() -> Unit)?,
    navigationIcon: (@Composable () -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
) {
    Box(modifier = Modifier.align(Alignment.CenterStart)) {
        WeatherNavigationButton(onNavigationClick, navigationIcon)
    }
    WeatherTopBarTitle(
        title = title,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxWidth()
            .padding(horizontal = WeatherLayout.CompactAppBarHeight),
    )
    Row(
        modifier = Modifier.align(Alignment.CenterEnd),
        verticalAlignment = Alignment.CenterVertically,
        content = actions,
    )
}

@Composable
private fun WeatherNavigationButton(
    onNavigationClick: (() -> Unit)?,
    navigationIcon: (@Composable () -> Unit)?,
) {
    if (onNavigationClick == null || navigationIcon == null) return

    IconButton(
        onClick = onNavigationClick,
        modifier = Modifier
            .sizeIn(
                minWidth = WeatherLayout.MinimumTouchTarget,
                minHeight = WeatherLayout.MinimumTouchTarget,
            )
            .testTag(WEATHER_TOP_BAR_NAVIGATION_TEST_TAG),
        content = navigationIcon,
    )
}

@Composable
private fun WeatherTopBarTitle(
    title: String,
    textAlign: TextAlign,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier.semantics { heading() },
        color = LocalContentColor.current,
        style = if (textAlign == TextAlign.Start) {
            MaterialTheme.typography.titleLarge
        } else {
            MaterialTheme.typography.titleSmall
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = textAlign,
    )
}
