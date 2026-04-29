package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.medical.PatientMedication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PatientMedicationJpaRepository extends JpaRepository<PatientMedication, Long> {

    Page<PatientMedication> findByPatient_IdAndDeletedAtIsNull(Long patientId, Pageable pageable);

    @Query("""
            SELECT m FROM PatientMedication m
            WHERE m.patient.id = :patientId AND m.deletedAt IS NULL
            AND (m.endDate IS NULL OR m.endDate >= :today)
            """)
    List<PatientMedication> findActiveForPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today);

    Page<PatientMedication> findByPatient_IdAndMedicationNameContainingIgnoreCaseAndDeletedAtIsNull(
            Long patientId,
            String namePart,
            Pageable pageable);

    @Query("""
            SELECT m FROM PatientMedication m
            WHERE m.patient.id = :patientId AND m.deletedAt IS NULL
            AND (m.endDate IS NULL OR m.endDate >= :today)
            """)
    Page<PatientMedication> findActivePage(
            @Param("patientId") Long patientId,
            @Param("today") LocalDate today,
            Pageable pageable);
}
