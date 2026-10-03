package com.jorgelillo.christmascountdown.domain

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdventCalendarTest {

    @Test
    fun lockedOutsideDecember() {
        assertEquals(0, AdventCalendar.unlockedDoors(LocalDate.of(2026, 11, 30)))
        assertEquals(0, AdventCalendar.unlockedDoors(LocalDate.of(2027, 1, 1)))
        assertFalse(AdventCalendar.isAdventSeason(LocalDate.of(2026, 10, 2)))
    }

    @Test
    fun oneDoorPerDayInDecember() {
        val today = LocalDate.of(2026, 12, 5)
        assertEquals(5, AdventCalendar.unlockedDoors(today))
        assertTrue(AdventCalendar.canOpen(5, today))
        assertFalse(AdventCalendar.canOpen(6, today))
        assertFalse(AdventCalendar.canOpen(0, today))
    }

    @Test
    fun allDoorsOpenFromChristmasEve() {
        assertEquals(24, AdventCalendar.unlockedDoors(LocalDate.of(2026, 12, 24)))
        assertEquals(24, AdventCalendar.unlockedDoors(LocalDate.of(2026, 12, 31)))
    }
}
