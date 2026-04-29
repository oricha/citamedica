package com.citamedica.backend.domain.service;

import java.time.LocalTime;
import java.util.Set;

public class AvailabilityConfigurationService {

    private static final Set<Integer> ALLOWED_DURATIONS = Set.of(15, 30, 45, 60);

    public void validateSlotDurationMinutes(int minutes) {
        if (!ALLOWED_DURATIONS.contains(minutes)) {
            throw new IllegalArgumentException("slot_duration_minutes must be one of 15, 30, 45, 60");
        }
    }

    public void validateWorkingWindow(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start and end time are required");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end time must be after start time");
        }
    }
}
