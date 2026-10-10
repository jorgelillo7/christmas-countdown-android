package com.jorgelillo.core.designsystem

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Call first in `onCreate`, before `super.onCreate`: splash screen and edge-to-edge drawing.
 * [darkDesign] = the app's background is dark, so the system bar icons are light.
 */
fun ComponentActivity.installLilloWindow(darkDesign: Boolean) {
    installSplashScreen()
    val bars = if (darkDesign) {
        SystemBarStyle.dark(Color.TRANSPARENT)
    } else {
        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
    }
    enableEdgeToEdge(statusBarStyle = bars, navigationBarStyle = bars)
}

/** The app's content in its theme, with test tags exposed for scripts/tap-text.sh. */
fun ComponentActivity.setLilloContent(colorScheme: ColorScheme, content: @Composable () -> Unit) {
    setContent {
        LilloTheme(colorScheme = colorScheme) {
            Box(Modifier.fillMaxSize().then(TestTagsAsResourceIds)) { content() }
        }
    }
}
