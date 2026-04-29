package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.prescription.ElectronicPrescriptionDtos;
import com.citamedica.backend.domain.model.ElectronicPrescriptionStatus;
import com.citamedica.backend.domain.repository.ElectronicPrescriptionRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class GetMyElectronicPrescriptionUseCase {

    private final ElectronicPrescriptionRepository prescriptionRepository;

    public GetMyElectronicPrescriptionUseCase(ElectronicPrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional(readOnly = true)
    public ElectronicPrescriptionDtos.ElectronicPrescriptionResponse execute(Long patientId, Long prescriptionId) {
        var rx = prescriptionRepository.findDetailById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Prescription not found: " + prescriptionId));
        if (rx.getPatient() == null || !rx.getPatient().getId().equals(patientId)) {
            throw new EntityNotFoundDomainException("Prescription not found: " + prescriptionId);
        }
        if (rx.getStatus() != ElectronicPrescriptionStatus.ACTIVE) {
            throw new EntityNotFoundDomainException("Prescription not found: " + prescriptionId);
        }
        if (rx.getValidUntil() != null && rx.getValidUntil().isBefore(LocalDate.now())) {
            throw new EntityNotFoundDomainException("Prescription not found: " + prescriptionId);
        }
        return ElectronicPrescriptionDtos.ElectronicPrescriptionResponse.from(rx);
    }
}
