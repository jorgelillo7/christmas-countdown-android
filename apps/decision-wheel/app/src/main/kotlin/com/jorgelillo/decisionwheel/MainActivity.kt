package com.jorgelillo.decisionwheel

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jorgelillo.core.designsystem.installLilloWindow
import com.jorgelillo.core.designsystem.setLilloContent
import com.jorgelillo.decisionwheel.data.Shortcuts
import com.jorgelillo.decisionwheel.ui.DecisionWheelApp
import com.jorgelillo.decisionwheel.ui.theme.WheelColorScheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installLilloWindow(darkDesign = true)
        super.onCreate(savedInstanceState)
        val app = application as DecisionWheelApplication
        setLilloContent(WheelColorScheme) {
            DecisionWheelApp(app, openWheelId = intent?.getStringExtra(Shortcuts.EXTRA_WHEEL_ID))
        }
    }
}
