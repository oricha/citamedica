package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivatePatientPortalUseCase {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public ActivatePatientPortalUseCase(PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(Long patientId, String rawPassword) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
        patient.setPortalPasswordHash(passwordEncoder.encode(rawPassword));
        patient.setPortalAccessEnabled(true);
        patientRepository.save(patient);
    }
}
