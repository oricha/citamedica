package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.prescription.ElectronicPrescriptionDtos;
import com.citamedica.backend.domain.repository.ElectronicPrescriptionRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListElectronicPrescriptionsForPatientUseCase {

    private final ElectronicPrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;

    public ListElectronicPrescriptionsForPatientUseCase(
            ElectronicPrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public Page<ElectronicPrescriptionDtos.ElectronicPrescriptionResponse> execute(Long patientId, Pageable pageable) {
        if (patientRepository.findById(patientId).isEmpty()) {
            throw new EntityNotFoundDomainException("Patient not found: " + patientId);
        }
        return prescriptionRepository.findByPatientIdOrderByIssuedAtDesc(patientId, pageable)
                .map(ElectronicPrescriptionDtos.ElectronicPrescriptionResponse::from);
    }
}
