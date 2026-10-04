package com.jorgelillo.decisionwheel.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jorgelillo.decisionwheel.domain.AppState
import com.jorgelillo.decisionwheel.domain.Wheel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "decision_wheel")

/**
 * The whole app state (wheels, history, settings) as one JSON document in DataStore.
 * On first launch it is seeded with the localized preset wheels.
 */
class StateRepository(private val context: Context, private val seed: () -> List<Wheel>) {

    private val key = stringPreferencesKey("state")
    private val json = Json { ignoreUnknownKeys = true }

    val state: Flow<AppState> = context.dataStore.data.map { prefs -> decode(prefs[key]) }

    suspend fun update(transform: (AppState) -> AppState) {
        context.dataStore.edit { prefs -> prefs[key] = json.encodeToString(transform(decode(prefs[key]))) }
    }

    private fun decode(raw: String?): AppState =
        raw?.let { runCatching { json.decodeFromString<AppState>(it) }.getOrNull() } ?: AppState(wheels = seed())
}
