package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import com.citamedica.backend.domain.repository.MedicalSpecialtyRepository;
import com.citamedica.backend.domain.service.ServiceCatalogDomainService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidSpecialtyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UpdateClinicOfferingUseCase {

    private final ClinicOfferingRepository clinicOfferingRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;
    private final ServiceCatalogDomainService serviceCatalogDomainService;

    public UpdateClinicOfferingUseCase(
            ClinicOfferingRepository clinicOfferingRepository,
            MedicalSpecialtyRepository medicalSpecialtyRepository,
            ServiceCatalogDomainService serviceCatalogDomainService) {
        this.clinicOfferingRepository = clinicOfferingRepository;
        this.medicalSpecialtyRepository = medicalSpecialtyRepository;
        this.serviceCatalogDomainService = serviceCatalogDomainService;
    }

    @Transactional
    public ClinicOffering execute(
            Long clinicId,
            Long offeringId,
            String name,
            String description,
            Integer durationMinutes,
            BigDecimal basePrice,
            Boolean clearMinRequiredSpecialty,
            Long minRequiredSpecialtyId,
            Boolean active) {
        ClinicOffering offering = clinicOfferingRepository.findById(offeringId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Service not found: " + offeringId));
        if (offering.getClinic() == null || !offering.getClinic().getId().equals(clinicId)) {
            throw new EntityNotFoundDomainException("Service not found for clinic: " + offeringId);
        }

        if (name != null) {
            offering.setName(name);
        }
        if (description != null) {
            offering.setDescription(description);
        }
        if (durationMinutes != null) {
            offering.setDurationMinutes(durationMinutes);
        }
        if (basePrice != null) {
            offering.setBasePrice(basePrice);
        }
        if (Boolean.TRUE.equals(clearMinRequiredSpecialty)) {
            offering.setMinRequiredSpecialty(null);
        } else if (minRequiredSpecialtyId != null) {
            MedicalSpecialty spec = medicalSpecialtyRepository.findById(minRequiredSpecialtyId)
                    .orElseThrow(() -> new InvalidSpecialtyException("Unknown specialty id: " + minRequiredSpecialtyId));
            offering.setMinRequiredSpecialty(spec);
        }
        if (active != null) {
            offering.setActive(active);
        }
        offering.setUpdatedAt(LocalDateTime.now());

        serviceCatalogDomainService.validateClinicOffering(offering);
        return clinicOfferingRepository.save(offering);
    }
}
