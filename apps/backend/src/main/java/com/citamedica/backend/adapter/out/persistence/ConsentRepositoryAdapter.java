package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.ConsentJpaRepository;
import com.citamedica.backend.domain.model.Consent;
import com.citamedica.backend.domain.model.ConsentType;
import com.citamedica.backend.domain.repository.ConsentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ConsentRepositoryAdapter implements ConsentRepository {

    private final ConsentJpaRepository jpa;

    public ConsentRepositoryAdapter(ConsentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Consent> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Consent> findByPatientId(Long patientId) {
        return jpa.findByPatientId(patientId);
    }

    @Override
    public List<Consent> findByPatientIdAndType(Long patientId, ConsentType type) {
        return jpa.findByPatientIdAndType(patientId, type);
    }

    @Override
    public Consent save(Consent entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }
}
