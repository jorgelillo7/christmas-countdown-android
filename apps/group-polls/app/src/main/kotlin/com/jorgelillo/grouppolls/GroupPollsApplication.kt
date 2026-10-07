package com.jorgelillo.grouppolls

import android.app.Application
import com.jorgelillo.grouppolls.data.FakePollRepository
import com.jorgelillo.grouppolls.data.LocalStore
import com.jorgelillo.grouppolls.data.PollRepository

/** App-wide singletons (manual DI: the app is small enough not to need Hilt). */
class GroupPollsApplication : Application() {
    val store by lazy { LocalStore(this) }

    /** In-memory until the Firebase project exists; then the Firestore implementation. */
    val repository: PollRepository by lazy { FakePollRepository() }
}
