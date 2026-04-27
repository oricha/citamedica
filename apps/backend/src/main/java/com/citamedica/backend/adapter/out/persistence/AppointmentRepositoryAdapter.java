package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.AppointmentJpaRepository;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.repository.AppointmentRepository;
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
