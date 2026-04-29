package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicJpaRepository extends JpaRepository<Clinic, Long> {
    Optional<Clinic> findBySlug(String slug);
}
