package com.jorgelillo.grouppolls.domain

import kotlin.math.roundToInt

/** Percentages that always add up to 100 (largest remainder), or 0/0 with no votes. */
data class Split(val red: Int, val blue: Int) {
    companion object {
        fun of(redVotes: Int, blueVotes: Int): Split {
            val total = redVotes + blueVotes
            if (total == 0) return Split(0, 0)
            val red = (redVotes * 100.0 / total).roundToInt()
            return Split(red, 100 - red)
        }
    }
}

/** The side ahead right now, or null on a tie (including no votes). */
fun Poll.leader(): Side? = when {
    redVotes > blueVotes -> Side.RED
    blueVotes > redVotes -> Side.BLUE
    else -> null
}

/**
 * Wii-style prediction score, kept on the device. A prediction counts once its poll is closed
 * (open-ended polls never resolve) and has a winner.
 */
data class PredictionRecord(val hits: Int = 0, val resolved: Int = 0) {
    val accuracy: Int? get() = if (resolved == 0) null else (hits * 100.0 / resolved).roundToInt()

    fun add(prediction: Side, poll: Poll, now: Long): PredictionRecord {
        if (poll.isOpen(now)) return this
        val winner = poll.leader() ?: return this
        return copy(hits = hits + if (prediction == winner) 1 else 0, resolved = resolved + 1)
    }
}
