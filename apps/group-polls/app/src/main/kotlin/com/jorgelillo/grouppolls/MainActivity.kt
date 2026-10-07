package com.jorgelillo.grouppolls

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jorgelillo.core.designsystem.LilloTheme
import com.jorgelillo.grouppolls.ui.GroupPollsApp
import com.jorgelillo.grouppolls.ui.PollsViewModel
import com.jorgelillo.grouppolls.ui.theme.PollsColorScheme

class MainActivity : ComponentActivity() {

    private val vm: PollsViewModel by viewModels {
        val app = application as GroupPollsApplication
        PollsViewModel.factory(app.repository, app.store, resources.configuration.locales[0].language)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        // The design is light, so system bar icons are dark.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) handleLink(intent)
        setContent {
            LilloTheme(colorScheme = PollsColorScheme) {
                // Test tags become resource ids so scripts/tap-text.sh can find buttons by tag.
                Box(Modifier.fillMaxSize().semantics { testTagsAsResourceId = true }) {
                    GroupPollsApp(vm)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleLink(intent)
    }

    /** `https://jorgelillo7.github.io/q/?c=CODE` opens that poll. */
    private fun handleLink(intent: Intent?) {
        intent?.dataString?.let(vm::open)
    }
}
