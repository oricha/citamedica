package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.medical.MedicalProcedure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface MedicalProcedureJpaRepository extends JpaRepository<MedicalProcedure, Long> {

    Page<MedicalProcedure> findByPatient_IdAndDeletedAtIsNull(Long patientId, Pageable pageable);

    @Query("""
            SELECT p FROM MedicalProcedure p
            WHERE p.patient.id = :patientId AND p.deletedAt IS NULL
            AND p.procedureDate BETWEEN :from AND :to
            """)
    Page<MedicalProcedure> findByPatientAndProcedureDateBetween(
            @Param("patientId") Long patientId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable);

    Page<MedicalProcedure> findByPatient_IdAndProcedureNameContainingIgnoreCaseAndDeletedAtIsNull(
            Long patientId,
            String part,
            Pageable pageable);
}
