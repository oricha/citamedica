package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.medical.MedicalHistoryAuditAction;
import com.citamedica.backend.domain.model.medical.MedicalHistoryAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MedicalHistoryAuditLogJpaRepository extends JpaRepository<MedicalHistoryAuditLog, Long> {

    Page<MedicalHistoryAuditLog> findByPatient_IdOrderByCreatedAtDesc(Long patientId, Pageable pageable);

    Page<MedicalHistoryAuditLog> findByRecordTypeAndRecordIdOrderByCreatedAtDesc(
            String recordType,
            Long recordId,
            Pageable pageable);

    Page<MedicalHistoryAuditLog> findByActionAndCreatedAtBetweenOrderByCreatedAtDesc(
            MedicalHistoryAuditAction action,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);
}
