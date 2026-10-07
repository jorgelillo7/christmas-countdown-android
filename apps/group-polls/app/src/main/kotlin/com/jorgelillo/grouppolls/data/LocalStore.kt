package com.jorgelillo.grouppolls.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jorgelillo.grouppolls.domain.Side
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "group_polls")

/** What this phone remembers: who you are, which polls you know and how you voted. */
@Serializable
data class LocalState(
    val name: String = "",
    val acceptedTerms: Boolean = false,
    /** Polls you created or opened from a link, newest first. */
    val myPolls: List<String> = emptyList(),
    /** Your vote and prediction per poll code. */
    val votes: Map<String, MyVote> = emptyMap(),
    /** Prediction results already counted (poll codes), so each counts once. */
    val resolvedPredictions: Set<String> = emptySet(),
    val predictionHits: Int = 0,
    val predictionsResolved: Int = 0,
    /** Creators whose polls you chose not to see. */
    val hiddenCreators: Set<String> = emptySet(),
    val reported: Set<String> = emptySet(),
)

@Serializable
data class MyVote(val side: Side, val prediction: Side)

/** The whole local state as one JSON document in DataStore. */
class LocalStore(private val context: Context) {

    private val key = stringPreferencesKey("state")
    private val json = Json { ignoreUnknownKeys = true }

    val state: Flow<LocalState> = context.dataStore.data.map { decode(it[key]) }

    suspend fun update(transform: (LocalState) -> LocalState) {
        context.dataStore.edit { prefs -> prefs[key] = json.encodeToString(transform(decode(prefs[key]))) }
    }

    private fun decode(raw: String?): LocalState =
        raw?.let { runCatching { json.decodeFromString<LocalState>(it) }.getOrNull() } ?: LocalState()
}
