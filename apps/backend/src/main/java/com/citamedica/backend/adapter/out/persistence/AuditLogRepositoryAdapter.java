package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.AuditLogJpaRepository;
import com.citamedica.backend.domain.model.AuditLog;
import com.citamedica.backend.domain.repository.AuditLogRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpa;

    public AuditLogRepositoryAdapter(AuditLogJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<AuditLog> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<AuditLog> findByEntityAndEntityId(String entity, Long entityId) {
        return jpa.findByEntityAndEntityId(entity, entityId);
    }

    @Override
    public List<AuditLog> findByAtAfter(LocalDateTime at) {
        return jpa.findByAtAfter(at);
    }

    @Override
    public AuditLog save(AuditLog entity) {
        return jpa.save(entity);
    }
}
