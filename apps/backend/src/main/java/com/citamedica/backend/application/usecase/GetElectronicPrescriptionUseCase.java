package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.prescription.ElectronicPrescriptionDtos;
import com.citamedica.backend.domain.repository.ElectronicPrescriptionRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetElectronicPrescriptionUseCase {

    private final ElectronicPrescriptionRepository prescriptionRepository;

    public GetElectronicPrescriptionUseCase(ElectronicPrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional(readOnly = true)
    public ElectronicPrescriptionDtos.ElectronicPrescriptionResponse execute(Long prescriptionId) {
        return prescriptionRepository.findDetailById(prescriptionId)
                .map(ElectronicPrescriptionDtos.ElectronicPrescriptionResponse::from)
                .orElseThrow(() -> new EntityNotFoundDomainException("Prescription not found: " + prescriptionId));
    }
}
