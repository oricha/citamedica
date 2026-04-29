package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;
import com.citamedica.backend.domain.model.DoctorAvailabilityConfiguration;
import com.citamedica.backend.domain.model.ScheduleDayOfWeek;
import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotGenerationService {

    public List<TimeSlot> generateSlotsForDoctor(
            Doctor doctor,
            List<DoctorAvailabilityConfiguration> configurations,
            List<DoctorAvailabilityBlock> activeBlocks,
            LocalDate rangeStartInclusive,
            LocalDate rangeEndInclusive,
            LocalDateTime now) {

        List<TimeSlot> slots = new ArrayList<>();

        for (LocalDate day = rangeStartInclusive; !day.isAfter(rangeEndInclusive); day = day.plusDays(1)) {
            ScheduleDayOfWeek dow = ScheduleDayOfWeek.fromJava(day.getDayOfWeek());
            DoctorAvailabilityConfiguration config = configurations.stream()
                    .filter(c -> c.getDayOfWeek() == dow)
                    .findFirst()
                    .orElse(null);
            if (config == null) {
                continue;
            }

            LocalTime t = config.getStartTime();
            LocalTime dayEnd = config.getEndTime();
            int step = config.getSlotDurationMinutes();

            while (!t.plusMinutes(step).isAfter(dayEnd)) {
                LocalDateTime slotStart = LocalDateTime.of(day, t);
                LocalDateTime slotEnd = slotStart.plusMinutes(step);

                if (!slotEnd.isAfter(now)) {
                    t = t.plusMinutes(step);
                    continue;
                }

                if (isBlocked(slotStart, slotEnd, activeBlocks)) {
                    t = t.plusMinutes(step);
                    continue;
                }

                TimeSlot slot = new TimeSlot();
                slot.setDoctor(doctor);
                slot.setStartTime(slotStart);
                slot.setEndTime(slotEnd);
                slot.setStatus(SlotStatus.AVAILABLE);
                slot.setCreatedAt(now);
                slots.add(slot);

                t = t.plusMinutes(step);
            }
        }

        return slots;
    }

    private boolean isBlocked(LocalDateTime start, LocalDateTime end, List<DoctorAvailabilityBlock> blocks) {
        for (DoctorAvailabilityBlock b : blocks) {
            if (b.isDeleted()) {
                continue;
            }
            if (rangesOverlap(start, end, b.getStartTime(), b.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    public static boolean rangesOverlap(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }
}
