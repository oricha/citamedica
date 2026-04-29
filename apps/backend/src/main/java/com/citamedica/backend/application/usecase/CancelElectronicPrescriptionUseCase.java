package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.prescription.ElectronicPrescriptionDtos;
import com.citamedica.backend.domain.model.ElectronicPrescriptionStatus;
import com.citamedica.backend.domain.repository.ElectronicPrescriptionRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelElectronicPrescriptionUseCase {

    private final ElectronicPrescriptionRepository prescriptionRepository;

    public CancelElectronicPrescriptionUseCase(ElectronicPrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional
    public ElectronicPrescriptionDtos.ElectronicPrescriptionResponse execute(Long prescriptionId) {
        var rx = prescriptionRepository.findDetailById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Prescription not found: " + prescriptionId));
        if (rx.getStatus() != ElectronicPrescriptionStatus.ACTIVE) {
            throw new InvalidDomainOperationException("Only active prescriptions can be cancelled");
        }
        rx.setStatus(ElectronicPrescriptionStatus.CANCELLED);
        rx.setUpdatedAt(LocalDateTime.now());
        prescriptionRepository.save(rx);
        return ElectronicPrescriptionDtos.ElectronicPrescriptionResponse.from(
                prescriptionRepository.findDetailById(prescriptionId).orElse(rx));
    }
}
