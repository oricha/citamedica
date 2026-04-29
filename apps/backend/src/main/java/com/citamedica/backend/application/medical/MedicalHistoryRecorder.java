package com.citamedica.backend.application.medical;

import com.citamedica.backend.adapter.out.persistence.jpa.MedicalHistoryAuditLogJpaRepository;
import com.citamedica.backend.adapter.out.persistence.jpa.MedicalHistoryEventJpaRepository;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.model.medical.MedicalHistoryAuditAction;
import com.citamedica.backend.domain.model.medical.MedicalHistoryAuditLog;
import com.citamedica.backend.domain.model.medical.MedicalHistoryEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class MedicalHistoryRecorder {

    private final MedicalHistoryEventJpaRepository eventJpaRepository;
    private final MedicalHistoryAuditLogJpaRepository auditLogJpaRepository;

    public MedicalHistoryRecorder(
            MedicalHistoryEventJpaRepository eventJpaRepository,
            MedicalHistoryAuditLogJpaRepository auditLogJpaRepository) {
        this.eventJpaRepository = eventJpaRepository;
        this.auditLogJpaRepository = auditLogJpaRepository;
    }

    @Transactional
    public void appendTimelineEvent(
            Patient patient,
            String eventType,
            LocalDateTime eventDate,
            String title,
            String description,
            Long sourceRecordId,
            String sourceRecordType) {
        MedicalHistoryEvent event = new MedicalHistoryEvent();
        event.setPatient(patient);
        event.setEventType(eventType);
        event.setEventDate(eventDate);
        event.setEventTitle(title);
        event.setEventDescription(description);
        event.setSourceRecordId(sourceRecordId);
        event.setSourceRecordType(sourceRecordType);
        eventJpaRepository.save(event);
    }

    @Transactional
    public void audit(
            Patient patient,
            String recordType,
            Long recordId,
            MedicalHistoryAuditAction action,
            String changedDataJson,
            String actorId,
            String actorIp) {
        MedicalHistoryAuditLog log = new MedicalHistoryAuditLog();
        log.setPatient(patient);
        log.setRecordType(recordType);
        log.setRecordId(recordId);
        log.setAction(action);
        log.setChangedData(changedDataJson);
        log.setActorId(actorId);
        log.setActorIp(actorIp);
        auditLogJpaRepository.save(log);
    }
}
