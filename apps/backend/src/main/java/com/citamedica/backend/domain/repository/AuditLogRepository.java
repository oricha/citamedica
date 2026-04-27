package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditLogRepository {
    Optional<AuditLog> findById(Long id);

    List<AuditLog> findByEntityAndEntityId(String entity, Long entityId);

    List<AuditLog> findByAtAfter(LocalDateTime at);

    AuditLog save(AuditLog entity);
}
