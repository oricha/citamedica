package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DeactivateClinicOfferingUseCase {

    private final ClinicOfferingRepository clinicOfferingRepository;

    public DeactivateClinicOfferingUseCase(ClinicOfferingRepository clinicOfferingRepository) {
        this.clinicOfferingRepository = clinicOfferingRepository;
    }

    @Transactional
    public ClinicOffering execute(Long clinicId, Long offeringId) {
        ClinicOffering offering = clinicOfferingRepository.findById(offeringId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Service not found: " + offeringId));
        if (offering.getClinic() == null || !offering.getClinic().getId().equals(clinicId)) {
            throw new EntityNotFoundDomainException("Service not found for clinic: " + offeringId);
        }
        offering.setActive(false);
        offering.setUpdatedAt(LocalDateTime.now());
        return clinicOfferingRepository.save(offering);
    }
}
