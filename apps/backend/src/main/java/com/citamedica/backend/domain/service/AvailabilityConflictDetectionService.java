package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;

import java.time.LocalDateTime;
import java.util.List;

public class AvailabilityConflictDetectionService {

    public boolean hasConflictWithBlock(LocalDateTime start, LocalDateTime end, List<DoctorAvailabilityBlock> blocks) {
        for (DoctorAvailabilityBlock b : blocks) {
            if (b.isDeleted()) {
                continue;
            }
            if (TimeSlotGenerationService.rangesOverlap(start, end, b.getStartTime(), b.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasConflictWithConcurrentLimit(long overlappingAppointmentCount, int maxConcurrent) {
        return overlappingAppointmentCount >= maxConcurrent;
    }

    public boolean hasConflictWithExternalEvent(LocalDateTime slotStart, LocalDateTime slotEnd,
                                                LocalDateTime externalStart, LocalDateTime externalEnd) {
        return TimeSlotGenerationService.rangesOverlap(slotStart, slotEnd, externalStart, externalEnd);
    }
}
