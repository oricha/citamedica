package com.citamedica.backend.service;

import com.citamedica.backend.domain.model.AuditLog;
import com.citamedica.backend.domain.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String actor, String action, String entity, Long entityId, String metadata) {
        AuditLog auditLog = new AuditLog(actor, action, entity, entityId, metadata);
        auditLogRepository.save(auditLog);
    }

    public void logAction(String actor, String action, String entity, Long entityId) {
        logAction(actor, action, entity, entityId, null);
    }
}