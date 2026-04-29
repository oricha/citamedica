package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.ClinicOfferingJpaRepository;
import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClinicOfferingRepositoryAdapter implements ClinicOfferingRepository {

    private final ClinicOfferingJpaRepository jpa;

    public ClinicOfferingRepositoryAdapter(ClinicOfferingJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<ClinicOffering> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<ClinicOffering> findByClinicIdAndActiveTrue(Long clinicId) {
        return jpa.findByClinicIdAndActiveTrueOrderByNameAsc(clinicId);
    }

    @Override
    public List<ClinicOffering> findByClinicId(Long clinicId) {
        return jpa.findByClinicIdOrderByNameAsc(clinicId);
    }

    @Override
    public ClinicOffering save(ClinicOffering entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
