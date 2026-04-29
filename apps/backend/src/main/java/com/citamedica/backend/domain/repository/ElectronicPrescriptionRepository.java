package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.ElectronicPrescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface ElectronicPrescriptionRepository {

    ElectronicPrescription save(ElectronicPrescription entity);

    Optional<ElectronicPrescription> findDetailById(Long id);

    Page<ElectronicPrescription> findByPatientIdOrderByIssuedAtDesc(Long patientId, Pageable pageable);

    Page<ElectronicPrescription> findPortalActiveForPatient(Long patientId, LocalDate today, Pageable pageable);

    void deleteAll();
}
