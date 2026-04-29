package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.NotificationPreference;
import com.citamedica.backend.domain.repository.NotificationPreferenceRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.domain.service.NotificationPreferenceDomainService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdateNotificationPreferencesUseCase {

    private final NotificationPreferenceRepository preferenceRepository;
    private final PatientRepository patientRepository;
    private final NotificationPreferenceDomainService preferenceDomainService;

    public UpdateNotificationPreferencesUseCase(
            NotificationPreferenceRepository preferenceRepository,
            PatientRepository patientRepository,
            NotificationPreferenceDomainService preferenceDomainService) {
        this.preferenceRepository = preferenceRepository;
        this.patientRepository = patientRepository;
        this.preferenceDomainService = preferenceDomainService;
    }

    @Transactional
    public NotificationPreference execute(Long patientId, boolean emailEnabled, boolean smsEnabled, String phone) {
        preferenceDomainService.validate(smsEnabled, phone);

        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));

        NotificationPreference preference = preferenceRepository.findByPatientId(patientId)
                .orElseGet(() -> {
                    NotificationPreference created = new NotificationPreference();
                    created.setPatient(patient);
                    created.setCreatedAt(LocalDateTime.now());
                    return created;
                });

        patient.updateNotificationPreferences(preference, emailEnabled, smsEnabled, phone);
        return preferenceRepository.save(preference);
    }
}
