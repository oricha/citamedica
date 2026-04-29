package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivatePatientPortalUseCase {

    private final PatientRepository patientRepository;

    public DeactivatePatientPortalUseCase(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public void execute(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
        patient.setPortalAccessEnabled(false);
        patient.setPortalPasswordHash(null);
        patientRepository.save(patient);
    }
}
