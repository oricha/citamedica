package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityAndEntityId(String entity, Long entityId);
    List<AuditLog> findByAtAfter(LocalDateTime at);
}