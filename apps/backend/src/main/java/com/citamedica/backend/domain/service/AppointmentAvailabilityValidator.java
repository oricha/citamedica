package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;
import com.citamedica.backend.domain.model.DoctorAvailabilityConfiguration;
import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.model.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentAvailabilityValidator {

    private final AvailabilityConflictDetectionService conflictDetectionService;

    public AppointmentAvailabilityValidator(AvailabilityConflictDetectionService conflictDetectionService) {
        this.conflictDetectionService = conflictDetectionService;
    }

    public void validateSlotMatchesAppointment(TimeSlot slot, LocalDateTime appointmentStart, LocalDateTime appointmentEnd) {
        if (slot == null) {
            throw new IllegalArgumentException("time slot is required");
        }
        if (!slot.getStartTime().equals(appointmentStart)) {
            throw new IllegalArgumentException("Appointment start does not match slot start");
        }
        if (!slot.getEndTime().equals(appointmentEnd)) {
            throw new IllegalArgumentException("Appointment end does not match slot end");
        }
        if (slot.getStatus() != SlotStatus.AVAILABLE) {
            throw new IllegalStateException("Time slot is not available");
        }
    }

    public void assertNotBlocked(LocalDateTime start, LocalDateTime end, List<DoctorAvailabilityBlock> blocks) {
        if (conflictDetectionService.hasConflictWithBlock(start, end, blocks)) {
            throw new IllegalArgumentException("Requested time overlaps a blocked period");
        }
    }

    public int resolveMaxConcurrent(DoctorAvailabilityConfiguration configOrNull, int fallback) {
        if (configOrNull == null) {
            return fallback;
        }
        return Math.max(1, configOrNull.getMaxConcurrentAppointments());
    }
}
