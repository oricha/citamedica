package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.SpecialtySurcharge;

import java.util.List;
import java.util.Optional;

public interface SpecialtySurchargeRepository {

    List<SpecialtySurcharge> findBySpecialtyIdAndClinicId(Long specialtyId, Long clinicId);

    List<SpecialtySurcharge> findBySpecialtyIdAndClinicIdIsNull(Long specialtyId);

    SpecialtySurcharge save(SpecialtySurcharge entity);

    void deleteAll();
}
