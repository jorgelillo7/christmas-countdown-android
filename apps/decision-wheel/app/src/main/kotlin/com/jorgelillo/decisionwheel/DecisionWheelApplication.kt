package com.jorgelillo.decisionwheel

import android.app.Application
import com.jorgelillo.decisionwheel.data.Presets
import com.jorgelillo.decisionwheel.data.stateRepository
import com.jorgelillo.decisionwheel.sound.TickPlayer

/** App-wide singletons (manual DI: the app is small enough not to need Hilt). */
class DecisionWheelApplication : Application() {
    val repository by lazy { stateRepository(this) { Presets.seed(this) } }
    val tickPlayer by lazy { TickPlayer(this) }
}
