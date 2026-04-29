package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    Optional<Patient> findById(Long id);

    List<Patient> findAll();

    Optional<Patient> findByEmail(String email);

    Patient save(Patient entity);

    void deleteById(Long id);

    void deleteAll();
}
