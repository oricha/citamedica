package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.repository.MedicalSpecialtyRepository;
import com.citamedica.backend.domain.service.ServiceCatalogDomainService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidSpecialtyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CreateClinicOfferingUseCase {

    private final ClinicRepository clinicRepository;
    private final ClinicOfferingRepository clinicOfferingRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;
    private final ServiceCatalogDomainService serviceCatalogDomainService;

    public CreateClinicOfferingUseCase(
            ClinicRepository clinicRepository,
            ClinicOfferingRepository clinicOfferingRepository,
            MedicalSpecialtyRepository medicalSpecialtyRepository,
            ServiceCatalogDomainService serviceCatalogDomainService) {
        this.clinicRepository = clinicRepository;
        this.clinicOfferingRepository = clinicOfferingRepository;
        this.medicalSpecialtyRepository = medicalSpecialtyRepository;
        this.serviceCatalogDomainService = serviceCatalogDomainService;
    }

    @Transactional
    public ClinicOffering execute(
            Long clinicId,
            String name,
            String description,
            int durationMinutes,
            java.math.BigDecimal basePrice,
            Long minRequiredSpecialtyId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));

        ClinicOffering offering = new ClinicOffering();
        offering.setClinic(clinic);
        offering.setName(name);
        offering.setDescription(description);
        offering.setDurationMinutes(durationMinutes);
        offering.setBasePrice(basePrice);
        offering.setActive(true);
        offering.setCreatedAt(LocalDateTime.now());

        if (minRequiredSpecialtyId != null) {
            MedicalSpecialty spec = medicalSpecialtyRepository.findById(minRequiredSpecialtyId)
                    .orElseThrow(() -> new InvalidSpecialtyException("Unknown specialty id: " + minRequiredSpecialtyId));
            offering.setMinRequiredSpecialty(spec);
        }

        serviceCatalogDomainService.validateClinicOffering(offering);
        return clinicOfferingRepository.save(offering);
    }
}
