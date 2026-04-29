package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.AvailabilitySyncLog;

import java.util.List;
import java.util.Optional;

public interface AvailabilitySyncLogRepository {

    AvailabilitySyncLog save(AvailabilitySyncLog entity);

    Optional<AvailabilitySyncLog> findFirstByOrderBySyncTimestampDesc();

    List<AvailabilitySyncLog> findTop10ByOrderBySyncTimestampDesc();

    void deleteAll();
}
