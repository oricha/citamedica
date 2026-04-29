package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.medical.MedicalHistoryEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MedicalHistoryEventJpaRepository extends JpaRepository<MedicalHistoryEvent, Long> {

    long countByPatient_Id(Long patientId);

    Page<MedicalHistoryEvent> findByPatient_IdOrderByEventDateDesc(Long patientId, Pageable pageable);

    Page<MedicalHistoryEvent> findByPatient_IdAndEventTypeOrderByEventDateDesc(
            Long patientId,
            String eventType,
            Pageable pageable);

    @Query("""
            SELECT e FROM MedicalHistoryEvent e
            WHERE e.patient.id = :patientId
            AND e.eventDate BETWEEN :from AND :to
            ORDER BY e.eventDate DESC
            """)
    Page<MedicalHistoryEvent> findByPatientAndEventDateBetween(
            @Param("patientId") Long patientId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
