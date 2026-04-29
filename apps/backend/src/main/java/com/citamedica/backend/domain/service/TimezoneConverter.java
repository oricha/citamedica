package com.citamedica.backend.domain.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class TimezoneConverter {

    private TimezoneConverter() {}

    public static ZonedDateTime toZoned(LocalDateTime local, ZoneId zone) {
        return local.atZone(zone);
    }

    public static LocalDateTime convertToUtc(LocalDateTime localWallClock, ZoneId doctorZone) {
        ZonedDateTime z = localWallClock.atZone(doctorZone);
        return z.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
    }

    public static LocalDateTime convertFromUtcToZone(LocalDateTime utcLocal, ZoneId doctorZone) {
        ZonedDateTime z = utcLocal.atZone(ZoneId.of("UTC"));
        return z.withZoneSameInstant(doctorZone).toLocalDateTime();
    }
}
