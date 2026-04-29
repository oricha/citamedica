package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.DuplicateEntityException;

import java.util.Optional;

public class PatientDomainService {

    public void validateEmailUniqueness(PatientRepository patientRepository, String email, Long patientIdToExclude) {
        Optional<Patient> existing = patientRepository.findByEmail(email);
        if (existing.isPresent() && !existing.get().getId().equals(patientIdToExclude)) {
            throw new DuplicateEntityException("Email already exists");
        }
    }
}
