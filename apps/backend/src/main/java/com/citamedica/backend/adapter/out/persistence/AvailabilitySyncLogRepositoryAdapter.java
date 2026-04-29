package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.AvailabilitySyncLogJpaRepository;
import com.citamedica.backend.domain.model.AvailabilitySyncLog;
import com.citamedica.backend.domain.repository.AvailabilitySyncLogRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AvailabilitySyncLogRepositoryAdapter implements AvailabilitySyncLogRepository {

    private final AvailabilitySyncLogJpaRepository jpa;

    public AvailabilitySyncLogRepositoryAdapter(AvailabilitySyncLogJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public AvailabilitySyncLog save(AvailabilitySyncLog entity) {
        return jpa.save(entity);
    }

    @Override
    public Optional<AvailabilitySyncLog> findFirstByOrderBySyncTimestampDesc() {
        return jpa.findFirstByOrderBySyncTimestampDesc();
    }

    @Override
    public List<AvailabilitySyncLog> findTop10ByOrderBySyncTimestampDesc() {
        return jpa.findTop10ByOrderBySyncTimestampDesc();
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
