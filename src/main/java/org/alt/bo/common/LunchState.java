package org.alt.bo.common;

import java.time.LocalTime;

public record LunchState(boolean taken, LocalTime start, LocalTime end) {

    private static final LocalTime LUNCH_WINDOW_START = LocalTime.of(12, 0);
    private static final LocalTime LUNCH_WINDOW_END = LocalTime.of(15, 0);
    private static final int LUNCH_DURATION = 60;

    public static LunchState notTaken() {
        return new LunchState(false, null, null);
    }

    public boolean isDuringLunch(LocalTime time) {
        if (!taken || start == null) return false;
        return !time.isBefore(start) && time.isBefore(end);
    }

    public static boolean isInLunchWindow(LocalTime time) {
        return !time.isBefore(LUNCH_WINDOW_START) && time.isBefore(LUNCH_WINDOW_END);
    }

    public static LunchState schedule(LocalTime start) {
        return new LunchState(true, start, start.plusMinutes(LUNCH_DURATION));
    }
}