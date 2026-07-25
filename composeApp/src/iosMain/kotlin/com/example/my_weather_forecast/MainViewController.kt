package com.example.my_weather_forecast

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController
import com.example.my_weather_forecast.presentation.platform.WeatherSystemBarIconTone
import com.example.my_weather_forecast.presentation.platform.toStatusBarStyle
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.UIStatusBarStyle
import platform.UIKit.UIViewController
import platform.UIKit.addChildViewController
import platform.UIKit.didMoveToParentViewController

@OptIn(ExperimentalComposeUiApi::class, ExperimentalForeignApi::class)
internal class WeatherViewController : UIViewController(nibName = null, bundle = null) {
    private val composeViewController = ComposeUIViewController(
        configure = {
            // https://kotlinlang.org/docs/multiplatform/compose-navigation.html#back-gesture
            enableBackGesture = true
        },
    ) {
        App()
    }
    private var systemBarIconTone = WeatherSystemBarIconTone.DARK

    override fun viewDidLoad() {
        super.viewDidLoad()

        addChildViewController(composeViewController)
        val composeView = composeViewController.view
        composeView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(composeView)
        NSLayoutConstraint.activateConstraints(
            listOf(
                composeView.leadingAnchor.constraintEqualToAnchor(view.leadingAnchor),
                composeView.trailingAnchor.constraintEqualToAnchor(view.trailingAnchor),
                composeView.topAnchor.constraintEqualToAnchor(view.topAnchor),
                composeView.bottomAnchor.constraintEqualToAnchor(view.bottomAnchor),
            ),
        )
        composeViewController.didMoveToParentViewController(this)
    }

    override fun preferredStatusBarStyle(): UIStatusBarStyle = systemBarIconTone.toStatusBarStyle()

    fun setSystemBarIconTone(tone: WeatherSystemBarIconTone) {
        if (tone == systemBarIconTone) return

        systemBarIconTone = tone
        setNeedsStatusBarAppearanceUpdate()
    }
}

fun MainViewController(): UIViewController = WeatherViewController()
