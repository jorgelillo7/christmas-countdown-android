package com.jorgelillo.tournaments

import android.app.Application
import com.jorgelillo.tournaments.data.LibraryRepository

/** App-wide singletons (manual DI: the app is small enough not to need Hilt). */
class TournamentsApplication : Application() {
    val repository by lazy { LibraryRepository(this) }
}
