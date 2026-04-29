package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.medical.MedicalCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalConditionJpaRepository extends JpaRepository<MedicalCondition, Long> {

    Page<MedicalCondition> findByPatient_IdAndDeletedAtIsNull(Long patientId, Pageable pageable);

    List<MedicalCondition> findByPatient_IdAndResolutionDateIsNullAndDeletedAtIsNull(Long patientId);

    Page<MedicalCondition> findByPatient_IdAndResolutionDateIsNullAndDeletedAtIsNull(Long patientId, Pageable pageable);

    Page<MedicalCondition> findByPatient_IdAndResolutionDateIsNullAndDeletedAtIsNullAndConditionNameContainingIgnoreCase(
            Long patientId,
            String namePart,
            Pageable pageable);

    Page<MedicalCondition> findByPatient_IdAndConditionNameContainingIgnoreCaseAndDeletedAtIsNull(
            Long patientId,
            String namePart,
            Pageable pageable);
}
