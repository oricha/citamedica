package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.SpecialtySurchargeJpaRepository;
import com.citamedica.backend.domain.model.SpecialtySurcharge;
import com.citamedica.backend.domain.repository.SpecialtySurchargeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SpecialtySurchargeRepositoryAdapter implements SpecialtySurchargeRepository {

    private final SpecialtySurchargeJpaRepository jpa;

    public SpecialtySurchargeRepositoryAdapter(SpecialtySurchargeJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<SpecialtySurcharge> findBySpecialtyIdAndClinicId(Long specialtyId, Long clinicId) {
        return jpa.findBySpecialtyIdAndClinicId(specialtyId, clinicId);
    }

    @Override
    public List<SpecialtySurcharge> findBySpecialtyIdAndClinicIdIsNull(Long specialtyId) {
        return jpa.findBySpecialtyIdAndClinicIsNull(specialtyId);
    }

    @Override
    public SpecialtySurcharge save(SpecialtySurcharge entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
