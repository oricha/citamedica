package com.citamedica.backend.domain.model;

import java.time.DayOfWeek;

/**
 * Day of week for recurring availability configuration (avoids clashing with {@link java.time.DayOfWeek} imports).
 */
public enum ScheduleDayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static ScheduleDayOfWeek fromJava(DayOfWeek dayOfWeek) {
        return valueOf(dayOfWeek.name());
    }

    public DayOfWeek toJava() {
        return DayOfWeek.valueOf(name());
    }
}
