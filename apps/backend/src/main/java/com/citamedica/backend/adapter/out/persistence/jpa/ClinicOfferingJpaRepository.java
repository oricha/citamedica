package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.ClinicOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicOfferingJpaRepository extends JpaRepository<ClinicOffering, Long> {

    List<ClinicOffering> findByClinicIdAndActiveTrueOrderByNameAsc(Long clinicId);

    List<ClinicOffering> findByClinicIdOrderByNameAsc(Long clinicId);
}
