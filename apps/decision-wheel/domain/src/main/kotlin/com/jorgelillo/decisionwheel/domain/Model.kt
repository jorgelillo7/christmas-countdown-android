package com.jorgelillo.decisionwheel.domain

import kotlinx.serialization.Serializable

@Serializable
data class Wheel(
    val id: String,
    val name: String,
    val options: List<String>,
)

/** A result the user accepted ("Let's go!"). Only accepted results count as history. */
@Serializable
data class Decision(
    val wheelId: String,
    val option: String,
    val atEpochMillis: Long,
)

@Serializable
data class AppState(
    val wheels: List<Wheel> = emptyList(),
    val history: List<Decision> = emptyList(),
    val avoidRepeats: Boolean = true,
    val soundOn: Boolean = true,
) {
    fun wheel(id: String): Wheel? = wheels.firstOrNull { it.id == id }

    fun historyOf(wheelId: String): List<Decision> =
        history.filter { it.wheelId == wheelId }.sortedByDescending { it.atEpochMillis }

    fun withDecision(decision: Decision): AppState =
        copy(history = (listOf(decision) + history).take(MAX_HISTORY))

    companion object {
        const val MAX_HISTORY = 300
    }
}
