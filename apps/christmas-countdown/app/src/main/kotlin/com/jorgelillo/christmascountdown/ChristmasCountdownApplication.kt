package com.jorgelillo.christmascountdown

import android.app.Application
import com.jorgelillo.christmascountdown.data.SettingsRepository
import com.jorgelillo.christmascountdown.music.MusicBoxPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/** Holds the app-wide singletons (manual DI: the app is small enough not to need Hilt). */
class ChristmasCountdownApplication : Application() {
    val appScope = CoroutineScope(SupervisorJob())
    val settings by lazy { SettingsRepository(this) }
    val musicPlayer by lazy { MusicBoxPlayer(appScope) }
}
