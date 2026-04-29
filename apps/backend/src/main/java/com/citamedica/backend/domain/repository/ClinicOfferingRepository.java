package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.ClinicOffering;

import java.util.List;
import java.util.Optional;

public interface ClinicOfferingRepository {

    Optional<ClinicOffering> findById(Long id);

    List<ClinicOffering> findByClinicIdAndActiveTrue(Long clinicId);

    List<ClinicOffering> findByClinicId(Long clinicId);

    ClinicOffering save(ClinicOffering entity);

    void deleteAll();
}
