package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.NotificationPreferenceJpaRepository;
import com.citamedica.backend.domain.model.NotificationPreference;
import com.citamedica.backend.domain.repository.NotificationPreferenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class NotificationPreferenceRepositoryAdapter implements NotificationPreferenceRepository {

    private final NotificationPreferenceJpaRepository jpa;

    public NotificationPreferenceRepositoryAdapter(NotificationPreferenceJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public NotificationPreference save(NotificationPreference preference) {
        return jpa.save(preference);
    }

    @Override
    public Optional<NotificationPreference> findByPatientId(Long patientId) {
        return jpa.findByPatientId(patientId);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
