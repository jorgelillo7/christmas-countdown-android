package com.jorgelillo.whoslying.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jorgelillo.whoslying.domain.GameSettings
import com.jorgelillo.whoslying.domain.WordPack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "whos_lying")

/** What survives between games: the group, the last setup, your packs, recently used words and scores. */
@Serializable
data class SavedState(
    val players: List<String> = emptyList(),
    val settings: GameSettings = GameSettings(),
    val customPacks: List<WordPack> = emptyList(),
    val recentWords: List<String> = emptyList(),
    /** Running points per player across games, until the group resets them. */
    val scores: Map<String, Int> = emptyMap(),
    /** Games finished in the current match. */
    val roundsPlayed: Int = 0,
)

/** The whole saved state as one JSON document in DataStore. */
class StateRepository(private val context: Context) {

    private val key = stringPreferencesKey("state")
    private val json = Json { ignoreUnknownKeys = true }

    val state: Flow<SavedState> = context.dataStore.data.map { decode(it[key]) }

    suspend fun update(transform: (SavedState) -> SavedState) {
        context.dataStore.edit { prefs -> prefs[key] = json.encodeToString(transform(decode(prefs[key]))) }
    }

    private fun decode(raw: String?): SavedState =
        raw?.let { runCatching { json.decodeFromString<SavedState>(it) }.getOrNull() } ?: SavedState()
}
