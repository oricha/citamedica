package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.NotificationPreference;

import java.util.Optional;

public interface NotificationPreferenceRepository {
    NotificationPreference save(NotificationPreference preference);
    Optional<NotificationPreference> findByPatientId(Long patientId);
    void deleteAll();
}
