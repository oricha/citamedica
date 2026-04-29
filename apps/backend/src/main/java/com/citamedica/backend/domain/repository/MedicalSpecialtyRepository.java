package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.MedicalSpecialty;

import java.util.List;
import java.util.Optional;

public interface MedicalSpecialtyRepository {

    List<MedicalSpecialty> findAllOrderByName();

    Optional<MedicalSpecialty> findById(Long id);

    Optional<MedicalSpecialty> findByCode(String code);

    MedicalSpecialty save(MedicalSpecialty entity);

    void deleteAll();
}
