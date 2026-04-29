package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.AppointmentJpaRepository;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentRepositoryAdapter implements AppointmentRepository {

    private final AppointmentJpaRepository jpa;

    public AppointmentRepositoryAdapter(AppointmentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        return jpa.findByDoctorIdForLocalDay(doctorId, dayStart, dayEnd);
    }

    @Override
    public List<Appointment> findByCalBookingId(String calBookingId) {
        return jpa.findByCalBookingId(calBookingId);
    }

    @Override
    public List<Appointment> findByStartAtBetween(LocalDateTime start, LocalDateTime end) {
        return jpa.findByStartAtBetween(start, end);
    }

    @Override
    public Page<Appointment> findByPatientIdOrderByStartAtDesc(Long patientId, Pageable pageable) {
        return jpa.findByPatient_IdOrderByStartAtDesc(patientId, pageable);
    }

    @Override
    public long countOverlappingScheduled(Long doctorId, LocalDateTime startAt, LocalDateTime endAt) {
        return jpa.countOverlapping(doctorId, startAt, endAt, AppointmentStatus.SCHEDULED);
    }

    @Override
    public long countOverlappingScheduledExcluding(Long doctorId, LocalDateTime startAt, LocalDateTime endAt, Long excludeAppointmentId) {
        return jpa.countOverlappingExcluding(doctorId, startAt, endAt, AppointmentStatus.SCHEDULED, excludeAppointmentId);
    }

    @Override
    public Appointment save(Appointment entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
