package com.jorgelillo.whoslying

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jorgelillo.core.designsystem.installLilloWindow
import com.jorgelillo.core.designsystem.setLilloContent
import com.jorgelillo.whoslying.ui.WhosLyingApp
import com.jorgelillo.whoslying.ui.theme.NeonColorScheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installLilloWindow(darkDesign = true)
        super.onCreate(savedInstanceState)
        val app = application as WhosLyingApplication
        setLilloContent(NeonColorScheme) {
            WhosLyingApp(app)
        }
    }
}
