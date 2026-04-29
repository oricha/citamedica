package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.PatientJpaRepository;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepositoryAdapter implements PatientRepository {

    private final PatientJpaRepository jpa;

    public PatientRepositoryAdapter(PatientJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Patient> findAll() {
        return jpa.findAll();
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public Optional<Patient> findByEmailIgnoreCase(String email) {
        return jpa.findByEmailIgnoreCase(email);
    }

    @Override
    public Patient save(Patient entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
