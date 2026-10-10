package com.jorgelillo.tournaments.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jorgelillo.tournaments.domain.Library
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "tournaments")

/** Every tournament as one JSON document in DataStore: no database, nothing leaves the phone. */
class LibraryRepository(private val context: Context) {

    private val key = stringPreferencesKey("library")
    private val json = Json { ignoreUnknownKeys = true }

    val library: Flow<Library> = context.dataStore.data.map { prefs -> decode(prefs[key]) }

    /** Applies [transform] atomically and returns what it returned alongside the new library. */
    suspend fun <R> update(transform: (Library) -> Pair<Library, R>): R {
        var result: R? = null
        context.dataStore.edit { prefs ->
            val (library, r) = transform(decode(prefs[key]))
            prefs[key] = json.encodeToString(Library.serializer(), library)
            result = r
        }
        @Suppress("UNCHECKED_CAST")
        return result as R
    }

    private fun decode(raw: String?): Library =
        raw?.let { runCatching { json.decodeFromString(Library.serializer(), it) }.getOrNull() } ?: Library()
}
