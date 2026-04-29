package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.exception.domain.DuplicateEntityException;

import java.util.Optional;

public class DoctorDomainService {

    public void validateEmailUniqueness(DoctorRepository doctorRepository, String email, Long doctorIdToExclude) {
        Optional<Doctor> existing = doctorRepository.findByEmail(email);
        if (existing.isPresent() && !existing.get().getId().equals(doctorIdToExclude)) {
            throw new DuplicateEntityException("Doctor email already exists");
        }
    }
}
