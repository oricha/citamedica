package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.ElectronicPrescription;
import com.citamedica.backend.domain.model.ElectronicPrescriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ElectronicPrescriptionJpaRepository extends JpaRepository<ElectronicPrescription, Long> {

    @EntityGraph(attributePaths = {"lines", "prescriber", "patient"})
    @Query("select e from ElectronicPrescription e where e.id = :id")
    Optional<ElectronicPrescription> findDetailById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"lines", "prescriber"})
    Page<ElectronicPrescription> findByPatient_IdOrderByIssuedAtDesc(Long patientId, Pageable pageable);

    @EntityGraph(attributePaths = {"lines", "prescriber"})
    @Query("""
            select e from ElectronicPrescription e
            where e.patient.id = :patientId
            and e.status = :status
            and (e.validUntil is null or e.validUntil >= :today)
            """)
    Page<ElectronicPrescription> findPortalActive(
            @Param("patientId") Long patientId,
            @Param("status") ElectronicPrescriptionStatus status,
            @Param("today") LocalDate today,
            Pageable pageable);
}
