package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.TimeSlotJpaRepository;
import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.model.TimeSlot;
import com.citamedica.backend.domain.repository.TimeSlotRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TimeSlotRepositoryAdapter implements TimeSlotRepository {

    private final TimeSlotJpaRepository jpa;

    public TimeSlotRepositoryAdapter(TimeSlotJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<TimeSlot> findById(Long id) {
        return jpa.findById(id).filter(s -> s.getDeletedAt() == null);
    }

    @Override
    @Transactional
    public Optional<TimeSlot> findByIdForUpdate(Long id) {
        return jpa.findByIdForUpdate(id);
    }

    @Override
    @Transactional
    public Optional<TimeSlot> findForUpdateByDoctorAndStartAndStatus(Long doctorId, LocalDateTime startTime, SlotStatus status) {
        return jpa.findForUpdateByDoctorAndStartAndStatus(doctorId, startTime, status);
    }

    @Override
    public Optional<TimeSlot> findByAppointmentId(Long appointmentId) {
        return jpa.findByAppointmentId(appointmentId);
    }

    @Override
    public List<TimeSlot> findAvailableByDoctorAndRange(Long doctorId, LocalDateTime from, LocalDateTime to, int page, int size) {
        if (size <= 0) {
            return List.of();
        }
        return jpa.findAvailableInRange(
                doctorId,
                from,
                to,
                SlotStatus.AVAILABLE,
                PageRequest.of(page, size, Sort.by("startTime"))
        ).getContent();
    }

    @Override
    public long countAvailableByDoctorAndRange(Long doctorId, LocalDateTime from, LocalDateTime to) {
        return jpa.countAvailableInRange(doctorId, from, to, SlotStatus.AVAILABLE);
    }

    @Override
    @Transactional
    public int deleteOldSlots(Long doctorId, LocalDateTime olderThan, List<SlotStatus> statuses) {
        return jpa.deleteByDoctorIdAndStartTimeBeforeAndStatusIn(doctorId, olderThan, statuses);
    }

    @Override
    public List<TimeSlot> saveAll(Iterable<TimeSlot> slots) {
        List<TimeSlot> result = new ArrayList<>();
        jpa.saveAll(slots).forEach(result::add);
        return result;
    }

    @Override
    public TimeSlot save(TimeSlot slot) {
        return jpa.save(slot);
    }

    @Override
    public List<TimeSlot> findByDoctorIdAndStartTimeBetweenAndStatus(
            Long doctorId, LocalDateTime from, LocalDateTime to, SlotStatus status) {
        return jpa.findByDoctorIdAndStartTimeBetweenAndStatusAndDeletedAtIsNull(doctorId, from, to, status);
    }

    @Override
    public boolean existsByDoctorAndStart(Long doctorId, LocalDateTime startTime) {
        return jpa.existsByDoctorIdAndStartTimeAndDeletedAtIsNull(doctorId, startTime);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
