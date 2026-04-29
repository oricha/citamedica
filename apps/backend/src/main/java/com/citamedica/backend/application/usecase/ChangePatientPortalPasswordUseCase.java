package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangePatientPortalPasswordUseCase {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePatientPortalPasswordUseCase(PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(Long patientId, String currentPassword, String newPassword) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
        if (!patient.isPortalAccessEnabled() || patient.getPortalPasswordHash() == null) {
            throw new InvalidDomainOperationException("Portal access is not enabled for this account");
        }
        if (!passwordEncoder.matches(currentPassword, patient.getPortalPasswordHash())) {
            throw new InvalidDomainOperationException("Current password is incorrect");
        }
        patient.setPortalPasswordHash(passwordEncoder.encode(newPassword));
        patientRepository.save(patient);
    }
}
