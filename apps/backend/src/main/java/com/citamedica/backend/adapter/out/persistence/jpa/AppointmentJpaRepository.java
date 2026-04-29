package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentJpaRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.startAt >= :dayStart AND a.startAt < :dayEnd ORDER BY a.startAt")
    List<Appointment> findByDoctorIdForLocalDay(
            @Param("doctorId") Long doctorId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    List<Appointment> findByCalBookingId(String calBookingId);

    List<Appointment> findByStartAtBetween(LocalDateTime start, LocalDateTime end);

    Page<Appointment> findByPatient_IdOrderByStartAtDesc(Long patientId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = :status AND a.startAt < :endAt AND a.endAt > :startAt")
    long countOverlapping(
            @Param("doctorId") Long doctorId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("status") AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.status = :status AND a.startAt < :endAt AND a.endAt > :startAt AND a.id <> :excludeId")
    long countOverlappingExcluding(
            @Param("doctorId") Long doctorId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("status") AppointmentStatus status,
            @Param("excludeId") Long excludeAppointmentId);
}
