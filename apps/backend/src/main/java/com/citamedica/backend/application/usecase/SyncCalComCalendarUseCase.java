package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.out.integration.calcom.CalBusyInterval;
import com.citamedica.backend.adapter.out.integration.calcom.CalComCalendarSyncClient;
import com.citamedica.backend.domain.model.AvailabilitySyncLog;
import com.citamedica.backend.domain.model.AvailabilitySyncStatus;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.model.TimeSlot;
import com.citamedica.backend.domain.repository.AvailabilitySyncLogRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.TimeSlotRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SyncCalComCalendarUseCase {

    private static final Logger log = LoggerFactory.getLogger(SyncCalComCalendarUseCase.class);

    private final DoctorRepository doctorRepository;
    private final CalComCalendarSyncClient calComCalendarSyncClient;
    private final TimeSlotRepository timeSlotRepository;
    private final AvailabilitySyncLogRepository availabilitySyncLogRepository;

    public SyncCalComCalendarUseCase(
            DoctorRepository doctorRepository,
            CalComCalendarSyncClient calComCalendarSyncClient,
            TimeSlotRepository timeSlotRepository,
            AvailabilitySyncLogRepository availabilitySyncLogRepository) {
        this.doctorRepository = doctorRepository;
        this.calComCalendarSyncClient = calComCalendarSyncClient;
        this.timeSlotRepository = timeSlotRepository;
        this.availabilitySyncLogRepository = availabilitySyncLogRepository;
    }

    @Transactional
    public void execute(Long doctorIdOrNull) {
        List<Doctor> doctors = new ArrayList<>();
        if (doctorIdOrNull != null) {
            doctors.add(doctorRepository.findById(doctorIdOrNull)
                    .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorIdOrNull)));
        } else {
            doctors.addAll(doctorRepository.findByActiveTrue());
        }

        for (Doctor doctor : doctors) {
            syncOneDoctor(doctor);
        }
    }

    private void syncOneDoctor(Doctor doctor) {
        long started = System.currentTimeMillis();
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusDays(90);
        AvailabilitySyncLog logEntry = new AvailabilitySyncLog();
        logEntry.setDoctor(doctor);
        logEntry.setSyncTimestamp(LocalDateTime.now());

        try {
            String username = doctor.getCalUsername();
            if (username == null || username.isBlank()) {
                logEntry.setStatus(AvailabilitySyncStatus.PARTIAL);
                logEntry.setEventsFetched(0);
                logEntry.setConflictsFound(0);
                logEntry.setSyncDurationSeconds((int) ((System.currentTimeMillis() - started) / 1000));
                availabilitySyncLogRepository.save(logEntry);
                return;
            }

            List<CalBusyInterval> busy = calComCalendarSyncClient.fetchBusyForUsername(username, from, to);
            logEntry.setEventsFetched(busy.size());

            List<TimeSlot> slots = timeSlotRepository.findByDoctorIdAndStartTimeBetweenAndStatus(
                    doctor.getId(), from, to, SlotStatus.AVAILABLE);
            int conflicts = 0;
            for (TimeSlot slot : slots) {
                for (CalBusyInterval interval : busy) {
                    if (interval.overlaps(slot.getStartTime(), slot.getEndTime())) {
                        slot.markBlocked();
                        conflicts++;
                        break;
                    }
                }
            }
            timeSlotRepository.saveAll(slots);
            logEntry.setConflictsFound(conflicts);
            logEntry.setStatus(AvailabilitySyncStatus.SUCCESS);
        } catch (Exception ex) {
            log.warn("Cal.com sync failed for doctor {}: {}", doctor.getId(), ex.getMessage());
            logEntry.setStatus(AvailabilitySyncStatus.FAILED);
            logEntry.setErrorMessage(ex.getMessage());
        } finally {
            logEntry.setSyncDurationSeconds((int) ((System.currentTimeMillis() - started) / 1000));
            availabilitySyncLogRepository.save(logEntry);
        }
    }
}
