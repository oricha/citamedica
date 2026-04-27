package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.ClinicJpaRepository;
import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.repository.ClinicRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClinicRepositoryAdapter implements ClinicRepository {

    private final ClinicJpaRepository jpa;

    public ClinicRepositoryAdapter(ClinicJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Clinic> findAll() {
        return jpa.findAll();
    }

    @Override
    public Optional<Clinic> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Clinic> findBySlug(String slug) {
        return jpa.findBySlug(slug);
    }

    @Override
    public Clinic save(Clinic entity) {
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
