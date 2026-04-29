package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.in.dto.prescription.ElectronicPrescriptionDtos;
import com.citamedica.backend.domain.repository.ElectronicPrescriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ListMyElectronicPrescriptionsUseCase {

    private final ElectronicPrescriptionRepository prescriptionRepository;

    public ListMyElectronicPrescriptionsUseCase(ElectronicPrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @Transactional(readOnly = true)
    public Page<ElectronicPrescriptionDtos.ElectronicPrescriptionResponse> execute(Long patientId, Pageable pageable) {
        return prescriptionRepository.findPortalActiveForPatient(patientId, LocalDate.now(), pageable)
                .map(ElectronicPrescriptionDtos.ElectronicPrescriptionResponse::from);
    }
}
