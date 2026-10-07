package com.jorgelillo.grouppolls.domain

/** Who can open a poll: everyone from the feed, or only people with the link. */
enum class Visibility { PUBLIC, PRIVATE }

/** The two answers every poll has. */
enum class Side { RED, BLUE }

/** How long a poll accepts votes, chosen by its creator. */
enum class Duration(val millis: Long?) {
    ONE_DAY(24L * 60 * 60 * 1000),
    SEVEN_DAYS(7L * 24 * 60 * 60 * 1000),
    NO_LIMIT(null),
    ;

    /** When a poll created at [createdAt] stops accepting votes; null means never. */
    fun closesAt(createdAt: Long): Long? = millis?.let { createdAt + it }
}

data class Poll(
    /** Random invite code; also the poll's id and, for private polls, its only key. */
    val code: String,
    val visibility: Visibility,
    val question: String,
    val red: String,
    val blue: String,
    val creatorId: String,
    /** Shown on private polls only; public polls never show names. */
    val creatorName: String,
    val createdAt: Long,
    val closesAt: Long?,
    val redVotes: Int = 0,
    val blueVotes: Int = 0,
    /** Hidden from the feed after enough reports. */
    val hidden: Boolean = false,
) {
    val totalVotes: Int get() = redVotes + blueVotes

    fun isOpen(now: Long): Boolean = closesAt == null || now < closesAt

    fun label(side: Side): String = if (side == Side.RED) red else blue
}

/** One person's final vote and their guess of the winning side. */
data class Vote(
    val voterId: String,
    /** Present on private polls (results show who voted what); null on public ones. */
    val voterName: String?,
    val side: Side,
    val prediction: Side,
    val votedAt: Long,
)
