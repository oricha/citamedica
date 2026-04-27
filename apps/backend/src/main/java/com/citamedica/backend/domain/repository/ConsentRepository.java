package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Consent;
import com.citamedica.backend.domain.model.ConsentType;

import java.util.List;
import java.util.Optional;

public interface ConsentRepository {
    Optional<Consent> findById(Long id);

    List<Consent> findByPatientId(Long patientId);

    List<Consent> findByPatientIdAndType(Long patientId, ConsentType type);

    Consent save(Consent entity);

    void deleteById(Long id);
}
