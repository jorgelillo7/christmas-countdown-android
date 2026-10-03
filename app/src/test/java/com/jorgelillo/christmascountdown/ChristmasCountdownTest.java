package com.jorgelillo.christmascountdown;

import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class ChristmasCountdownTest {

    private static final long SECOND = 1000L;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    private static Calendar at(int year, int month, int day, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.clear();
        c.set(year, month, day, hour, minute, second);
        return c;
    }

    @Test
    public void countsDownWithinTheSameYear() {
        Calendar now = at(2026, Calendar.OCTOBER, 2, 12, 0, 0);
        assertEquals(83 * DAY + 12 * HOUR, ChristmasCountdown.millisUntilChristmas(now));
    }

    @Test
    public void lastSecondBeforeChristmas() {
        Calendar now = at(2026, Calendar.DECEMBER, 24, 23, 59, 59);
        assertEquals(SECOND, ChristmasCountdown.millisUntilChristmas(now));
    }

    @Test
    public void christmasDayShowsTheMessageAllDay() {
        assertTrue(ChristmasCountdown.isChristmasDay(at(2026, Calendar.DECEMBER, 25, 0, 0, 0)));
        assertTrue(ChristmasCountdown.isChristmasDay(at(2026, Calendar.DECEMBER, 25, 23, 59, 59)));
        assertEquals(0, ChristmasCountdown.millisUntilChristmas(at(2026, Calendar.DECEMBER, 25, 18, 0, 0)));
    }

    @Test
    public void afterChristmasRollsOverToNextYear() {
        Calendar now = at(2026, Calendar.DECEMBER, 26, 0, 0, 0);
        assertFalse(ChristmasCountdown.isChristmasDay(now));
        assertEquals(364 * DAY, ChristmasCountdown.millisUntilChristmas(now));
        assertEquals(359 * DAY, ChristmasCountdown.millisUntilChristmas(at(2026, Calendar.DECEMBER, 31, 0, 0, 0)));
    }

    @Test
    public void splitsIntoDaysHoursMinutesSeconds() {
        long[] parts = ChristmasCountdown.split(3 * DAY + 4 * HOUR + 5 * MINUTE + 6 * SECOND + 999);
        assertArrayEquals(new long[]{3, 4, 5, 6}, parts);
    }
}
