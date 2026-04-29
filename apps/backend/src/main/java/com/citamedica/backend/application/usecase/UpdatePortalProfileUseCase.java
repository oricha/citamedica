package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePortalProfileUseCase {

    private final PatientRepository patientRepository;

    public UpdatePortalProfileUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public Patient execute(Long patientId, String phone, String languagePreference) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
        if (phone != null) {
            patient.setPhone(phone);
        }
        if (languagePreference != null) {
            patient.setLanguagePreference(languagePreference);
        }
        return patientRepository.save(patient);
    }
}
