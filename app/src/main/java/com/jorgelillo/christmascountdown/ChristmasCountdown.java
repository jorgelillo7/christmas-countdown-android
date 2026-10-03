package com.jorgelillo.christmascountdown;

import java.util.Calendar;

/** Pure date logic for the countdown, kept free of Android types so it can be unit tested. */
public final class ChristmasCountdown {

    private ChristmasCountdown() {
    }

    /** True for the whole of December 25th in the calendar's time zone. */
    public static boolean isChristmasDay(Calendar now) {
        return now.get(Calendar.MONTH) == Calendar.DECEMBER
                && now.get(Calendar.DAY_OF_MONTH) == 25;
    }

    /**
     * Milliseconds from {@code now} until the start of the next December 25th. On Christmas Day
     * itself this returns 0; from December 26th onwards it counts towards next year's Christmas.
     */
    public static long millisUntilChristmas(Calendar now) {
        if (isChristmasDay(now)) {
            return 0;
        }
        Calendar target = (Calendar) now.clone();
        target.set(now.get(Calendar.YEAR), Calendar.DECEMBER, 25, 0, 0, 0);
        target.set(Calendar.MILLISECOND, 0);
        if (!target.after(now)) {
            target.add(Calendar.YEAR, 1);
        }
        return target.getTimeInMillis() - now.getTimeInMillis();
    }

    /** Splits a duration into {days, hours, minutes, seconds}. */
    public static long[] split(long millis) {
        long seconds = millis / 1000;
        return new long[]{
                seconds / 86400,
                (seconds % 86400) / 3600,
                (seconds % 3600) / 60,
                seconds % 60
        };
    }
}
