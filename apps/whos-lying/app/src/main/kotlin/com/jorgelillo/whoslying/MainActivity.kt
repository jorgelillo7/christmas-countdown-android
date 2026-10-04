package com.jorgelillo.whoslying

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jorgelillo.core.designsystem.LilloTheme
import com.jorgelillo.whoslying.ui.WhosLyingApp
import com.jorgelillo.whoslying.ui.theme.NeonColorScheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        // The design is always dark, so system bar icons are always light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val app = application as WhosLyingApplication
        setContent {
            LilloTheme(colorScheme = NeonColorScheme) {
                // Test tags become resource ids so scripts/tap-text.sh can find buttons by tag.
                Box(Modifier.fillMaxSize().semantics { testTagsAsResourceId = true }) {
                    WhosLyingApp(app)
                }
            }
        }
    }
}
