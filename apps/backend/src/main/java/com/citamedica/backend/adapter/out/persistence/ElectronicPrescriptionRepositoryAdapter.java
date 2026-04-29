package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.ElectronicPrescriptionJpaRepository;
import com.citamedica.backend.domain.model.ElectronicPrescription;
import com.citamedica.backend.domain.model.ElectronicPrescriptionStatus;
import com.citamedica.backend.domain.repository.ElectronicPrescriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class ElectronicPrescriptionRepositoryAdapter implements ElectronicPrescriptionRepository {

    private final ElectronicPrescriptionJpaRepository jpa;

    public ElectronicPrescriptionRepositoryAdapter(ElectronicPrescriptionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public ElectronicPrescription save(ElectronicPrescription entity) {
        return jpa.save(entity);
    }

    @Override
    public Optional<ElectronicPrescription> findDetailById(Long id) {
        return jpa.findDetailById(id);
    }

    @Override
    public Page<ElectronicPrescription> findByPatientIdOrderByIssuedAtDesc(Long patientId, Pageable pageable) {
        return jpa.findByPatient_IdOrderByIssuedAtDesc(patientId, pageable);
    }

    @Override
    public Page<ElectronicPrescription> findPortalActiveForPatient(Long patientId, LocalDate today, Pageable pageable) {
        return jpa.findPortalActive(patientId, ElectronicPrescriptionStatus.ACTIVE, today, pageable);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
