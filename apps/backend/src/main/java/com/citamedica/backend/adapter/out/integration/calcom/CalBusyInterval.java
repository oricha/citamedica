package com.citamedica.backend.adapter.out.integration.calcom;

import java.time.LocalDateTime;

public record CalBusyInterval(LocalDateTime start, LocalDateTime end) {

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return start.isBefore(otherEnd) && otherStart.isBefore(end);
    }
}
