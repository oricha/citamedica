package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationPreferenceJpaRepository extends JpaRepository<NotificationPreference, Long> {
    Optional<NotificationPreference> findByPatientId(Long patientId);
}
