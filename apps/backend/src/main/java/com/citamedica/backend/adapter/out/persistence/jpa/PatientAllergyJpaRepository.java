package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.medical.ClinicalSeverity;
import com.citamedica.backend.domain.model.medical.PatientAllergy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientAllergyJpaRepository extends JpaRepository<PatientAllergy, Long> {

    List<PatientAllergy> findByPatient_IdAndDeletedAtIsNullOrderByIdAsc(Long patientId);

    Page<PatientAllergy> findByPatient_IdAndDeletedAtIsNull(Long patientId, Pageable pageable);

    List<PatientAllergy> findByPatient_IdAndSeverityAndDeletedAtIsNull(Long patientId, ClinicalSeverity severity);

    List<PatientAllergy> findByPatient_IdAndSeverityInAndDeletedAtIsNull(
            Long patientId,
            List<ClinicalSeverity> severities);

    Page<PatientAllergy> findByPatient_IdAndAllergenNameContainingIgnoreCaseAndDeletedAtIsNull(
            Long patientId,
            String part,
            Pageable pageable);
}
