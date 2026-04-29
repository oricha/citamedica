package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.model.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository {

    Optional<TimeSlot> findById(Long id);

    Optional<TimeSlot> findByIdForUpdate(Long id);

    Optional<TimeSlot> findForUpdateByDoctorAndStartAndStatus(Long doctorId, LocalDateTime startTime, SlotStatus status);

    Optional<TimeSlot> findByAppointmentId(Long appointmentId);

    List<TimeSlot> findAvailableByDoctorAndRange(Long doctorId, LocalDateTime from, LocalDateTime to, int page, int size);

    long countAvailableByDoctorAndRange(Long doctorId, LocalDateTime from, LocalDateTime to);

    int deleteOldSlots(Long doctorId, LocalDateTime olderThan, List<SlotStatus> statuses);

    List<TimeSlot> saveAll(Iterable<TimeSlot> slots);

    TimeSlot save(TimeSlot slot);

    List<TimeSlot> findByDoctorIdAndStartTimeBetweenAndStatus(
            Long doctorId, LocalDateTime from, LocalDateTime to, SlotStatus status);

    boolean existsByDoctorAndStart(Long doctorId, LocalDateTime startTime);

    void deleteAll();
}
