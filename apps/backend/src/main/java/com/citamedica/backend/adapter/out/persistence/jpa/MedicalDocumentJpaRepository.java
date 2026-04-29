package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.medical.MedicalDocument;
import com.citamedica.backend.domain.model.medical.MedicalDocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MedicalDocumentJpaRepository extends JpaRepository<MedicalDocument, Long>,
        JpaSpecificationExecutor<MedicalDocument> {

    Page<MedicalDocument> findByPatient_IdAndDeletedAtIsNull(Long patientId, Pageable pageable);

    Page<MedicalDocument> findByPatient_IdAndDocumentTypeAndDeletedAtIsNull(
            Long patientId,
            MedicalDocumentType type,
            Pageable pageable);

    @Query("""
            SELECT d FROM MedicalDocument d
            WHERE d.patient.id = :patientId AND d.deletedAt IS NULL
            AND d.uploadedAt BETWEEN :from AND :to
            """)
    Page<MedicalDocument> findByPatientAndUploadedBetween(
            @Param("patientId") Long patientId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);

    Optional<MedicalDocument> findByIdAndPatient_Id(Long id, Long patientId);

    Page<MedicalDocument> findByPatient_IdAndFileHashAndDeletedAtIsNull(Long patientId, String hash, Pageable pageable);

    Optional<MedicalDocument> findByIdAndDeletedAtIsNull(Long id);
}
