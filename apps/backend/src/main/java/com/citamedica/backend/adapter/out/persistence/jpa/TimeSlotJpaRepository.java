package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.SlotStatus;
import com.citamedica.backend.domain.model.TimeSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotJpaRepository extends JpaRepository<TimeSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TimeSlot t WHERE t.id = :id AND t.deletedAt IS NULL")
    Optional<TimeSlot> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TimeSlot t WHERE t.doctor.id = :doctorId AND t.startTime = :start AND t.status = :status AND t.deletedAt IS NULL")
    Optional<TimeSlot> findForUpdateByDoctorAndStartAndStatus(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("status") SlotStatus status);

    @Query("SELECT t FROM TimeSlot t WHERE t.appointment.id = :appointmentId AND t.deletedAt IS NULL")
    Optional<TimeSlot> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Query("SELECT t FROM TimeSlot t WHERE t.doctor.id = :doctorId AND t.startTime >= :from AND t.startTime < :to AND t.status = :status AND t.deletedAt IS NULL")
    Page<TimeSlot> findAvailableInRange(
            @Param("doctorId") Long doctorId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") SlotStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(t) FROM TimeSlot t WHERE t.doctor.id = :doctorId AND t.startTime >= :from AND t.startTime < :to AND t.status = :status AND t.deletedAt IS NULL")
    long countAvailableInRange(
            @Param("doctorId") Long doctorId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") SlotStatus status);

    List<TimeSlot> findByDoctorIdAndStartTimeBetweenAndStatusAndDeletedAtIsNull(
            Long doctorId, LocalDateTime from, LocalDateTime to, SlotStatus status);

    boolean existsByDoctorIdAndStartTimeAndDeletedAtIsNull(Long doctorId, LocalDateTime startTime);

    @Modifying
    @Query("DELETE FROM TimeSlot t WHERE t.doctor.id = :doctorId AND t.startTime < :cutoff AND t.status IN :statuses AND t.deletedAt IS NULL")
    int deleteByDoctorIdAndStartTimeBeforeAndStatusIn(
            @Param("doctorId") Long doctorId,
            @Param("cutoff") LocalDateTime cutoff,
            @Param("statuses") Collection<SlotStatus> statuses);
}
