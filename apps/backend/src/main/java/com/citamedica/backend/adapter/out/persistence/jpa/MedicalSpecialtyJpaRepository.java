package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.MedicalSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalSpecialtyJpaRepository extends JpaRepository<MedicalSpecialty, Long> {

    List<MedicalSpecialty> findAllByOrderByNameAsc();

    Optional<MedicalSpecialty> findByCode(String code);
}
