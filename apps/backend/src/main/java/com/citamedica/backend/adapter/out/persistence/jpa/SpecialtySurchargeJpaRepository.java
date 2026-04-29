package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.SpecialtySurcharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialtySurchargeJpaRepository extends JpaRepository<SpecialtySurcharge, Long> {

    List<SpecialtySurcharge> findBySpecialtyIdAndClinicId(Long specialtyId, Long clinicId);

    List<SpecialtySurcharge> findBySpecialtyIdAndClinicIsNull(Long specialtyId);
}
