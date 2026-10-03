package com.jorgelillo.christmascountdown.domain

import java.time.LocalDate
import java.time.Month

/** Advent calendar rules: door N opens on December N, from the 1st to the 24th. */
object AdventCalendar {

    const val DOORS = 24

    /** How many doors can be opened on [today]: 0 outside December, all of them from the 24th on. */
    fun unlockedDoors(today: LocalDate): Int =
        if (today.month == Month.DECEMBER) today.dayOfMonth.coerceAtMost(DOORS) else 0

    fun canOpen(door: Int, today: LocalDate): Boolean = door in 1..unlockedDoors(today)

    fun isAdventSeason(today: LocalDate): Boolean = unlockedDoors(today) > 0
}
