package com.jorgelillo.christmascountdown

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jorgelillo.christmascountdown.ui.ChristmasApp
import com.jorgelillo.christmascountdown.ui.theme.ChristmasColorScheme
import com.jorgelillo.core.designsystem.LilloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        // The design is always dark, so system bar icons are always light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val app = application as ChristmasCountdownApplication
        setContent {
            LilloTheme(colorScheme = ChristmasColorScheme) {
                ChristmasApp(app)
            }
        }
    }
}
