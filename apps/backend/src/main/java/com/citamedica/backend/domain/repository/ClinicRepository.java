package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Clinic;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port: persist and load clinics (persistence-agnostic).
 */
public interface ClinicRepository {
    List<Clinic> findAll();

    Optional<Clinic> findById(Long id);

    Optional<Clinic> findBySlug(String slug);

    Clinic save(Clinic entity);

    void deleteById(Long id);

    void deleteAll();
}
