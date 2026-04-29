package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.BlockType;
import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;
import com.citamedica.backend.domain.model.DoctorAvailabilityConfiguration;
import com.citamedica.backend.domain.model.ScheduleDayOfWeek;
import com.citamedica.backend.domain.model.SlotStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSlotGenerationServiceTest {

    @Test
    void generatesSlotsRespectingBlocks() {
        Clinic clinic = new Clinic();
        clinic.setId(1L);
        Doctor doctor = new Doctor();
        doctor.setId(99L);
        doctor.setClinic(clinic);
        doctor.setTimezone("UTC");

        DoctorAvailabilityConfiguration mon = new DoctorAvailabilityConfiguration();
        mon.setDayOfWeek(ScheduleDayOfWeek.MONDAY);
        mon.setStartTime(LocalTime.of(9, 0));
        mon.setEndTime(LocalTime.of(10, 0));
        mon.setSlotDurationMinutes(30);

        DoctorAvailabilityBlock lunch = new DoctorAvailabilityBlock();
        lunch.setStartTime(LocalDateTime.of(2026, 5, 4, 9, 30));
        lunch.setEndTime(LocalDateTime.of(2026, 5, 4, 9, 45));
        lunch.setBlockType(BlockType.LUNCH);

        var svc = new TimeSlotGenerationService();
        LocalDate monday = LocalDate.of(2026, 5, 4);
        var slots = svc.generateSlotsForDoctor(
                doctor,
                List.of(mon),
                List.of(lunch),
                monday,
                monday,
                LocalDateTime.of(2026, 5, 4, 8, 0));

        assertEquals(1, slots.size());
        assertEquals(LocalDateTime.of(2026, 5, 4, 9, 0), slots.get(0).getStartTime());
        assertEquals(LocalDateTime.of(2026, 5, 4, 9, 30), slots.get(0).getEndTime());
        assertEquals(SlotStatus.AVAILABLE, slots.get(0).getStatus());
        assertTrue(slots.stream().noneMatch(s -> s.getStartTime().equals(LocalDateTime.of(2026, 5, 4, 9, 30))));
    }
}
