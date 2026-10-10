package com.jorgelillo.whoslying

import android.app.Application
import com.jorgelillo.whoslying.data.stateRepository

/** App-wide singletons (manual DI: the app is small enough not to need Hilt). */
class WhosLyingApplication : Application() {
    val repository by lazy { stateRepository(this) }
}
