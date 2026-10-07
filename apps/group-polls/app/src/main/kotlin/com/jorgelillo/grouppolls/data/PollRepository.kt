package com.jorgelillo.grouppolls.data

import com.jorgelillo.grouppolls.domain.Poll
import com.jorgelillo.grouppolls.domain.Vote
import kotlinx.coroutines.flow.Flow

/**
 * Where polls and votes live. [FakePollRepository] keeps everything in memory (development and
 * smoke tests); the Firestore implementation is the real one.
 */
interface PollRepository {
    /** This install's anonymous id (Firebase anonymous auth uid). */
    suspend fun userId(): String

    /** Public polls of the last week, not hidden by reports, newest first. */
    fun feed(): Flow<List<Poll>>

    /** One poll by its code; null while loading or when the code doesn't exist. */
    fun poll(code: String): Flow<Poll?>

    /** Who voted what. Only readable on private polls; empty on public ones. */
    fun votes(code: String): Flow<List<Vote>>

    suspend fun create(poll: Poll)

    /** Casts a final vote; fails if this user already voted or the poll is closed. */
    suspend fun vote(code: String, vote: Vote)

    /** Creator only: stops accepting votes from now on. */
    suspend fun close(code: String)

    suspend fun report(code: String)

    /** "Delete my data": removes the user's name from their polls and votes; votes stay counted. */
    suspend fun anonymize()
}

class PollException(message: String) : Exception(message)
