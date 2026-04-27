package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Optional<Appointment> findById(Long id);

    List<Appointment> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    List<Appointment> findByCalBookingId(String calBookingId);

    List<Appointment> findByStartAtBetween(LocalDateTime start, LocalDateTime end);

    Appointment save(Appointment entity);

    void deleteById(Long id);

    void deleteAll();
}
