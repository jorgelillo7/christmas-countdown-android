package com.jorgelillo.tournaments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jorgelillo.core.designsystem.LilloTheme
import com.jorgelillo.core.designsystem.TestTagsAsResourceIds
import com.jorgelillo.tournaments.ui.TournamentsApp
import com.jorgelillo.tournaments.ui.TournamentsViewModel
import com.jorgelillo.tournaments.ui.theme.ArenaColorScheme

class MainActivity : ComponentActivity() {

    private val vm: TournamentsViewModel by viewModels {
        TournamentsViewModel.factory((application as TournamentsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        // The design is always dark, so system bar icons are always light.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) handleLink(intent)
        setContent {
            LilloTheme(colorScheme = ArenaColorScheme) {
                // Test tags become resource ids so scripts/tap-text.sh can find buttons by tag.
                Box(Modifier.fillMaxSize().then(TestTagsAsResourceIds)) {
                    TournamentsApp(vm)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleLink(intent)
    }

    /** A tournament link opened from the browser or a chat, or text shared to the app. */
    private fun handleLink(intent: Intent?) {
        val text = when (intent?.action) {
            Intent.ACTION_VIEW -> intent.dataString
            Intent.ACTION_SEND -> intent.getStringExtra(Intent.EXTRA_TEXT)
            else -> null
        }
        text?.let(vm::importText)
    }
}
