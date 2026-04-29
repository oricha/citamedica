package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.AvailabilitySyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilitySyncLogJpaRepository extends JpaRepository<AvailabilitySyncLog, Long> {

    Optional<AvailabilitySyncLog> findFirstByOrderBySyncTimestampDesc();

    List<AvailabilitySyncLog> findTop10ByOrderBySyncTimestampDesc();
}
