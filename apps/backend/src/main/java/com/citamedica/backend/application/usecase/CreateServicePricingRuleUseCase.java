package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.model.ServicePricingRule;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.repository.MedicalSpecialtyRepository;
import com.citamedica.backend.domain.repository.ServicePricingRuleRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidSpecialtyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CreateServicePricingRuleUseCase {

    private final ClinicRepository clinicRepository;
    private final ClinicOfferingRepository clinicOfferingRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;
    private final ServicePricingRuleRepository servicePricingRuleRepository;

    public CreateServicePricingRuleUseCase(
            ClinicRepository clinicRepository,
            ClinicOfferingRepository clinicOfferingRepository,
            MedicalSpecialtyRepository medicalSpecialtyRepository,
            ServicePricingRuleRepository servicePricingRuleRepository) {
        this.clinicRepository = clinicRepository;
        this.clinicOfferingRepository = clinicOfferingRepository;
        this.medicalSpecialtyRepository = medicalSpecialtyRepository;
        this.servicePricingRuleRepository = servicePricingRuleRepository;
    }

    @Transactional
    public ServicePricingRule execute(Long clinicId, Long serviceId, Long specialtyId, BigDecimal overridePrice) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        ClinicOffering offering = clinicOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Service not found: " + serviceId));
        if (offering.getClinic() == null || !offering.getClinic().getId().equals(clinicId)) {
            throw new EntityNotFoundDomainException("Service not found for clinic: " + serviceId);
        }

        MedicalSpecialty specialty = null;
        if (specialtyId != null) {
            specialty = medicalSpecialtyRepository.findById(specialtyId)
                    .orElseThrow(() -> new InvalidSpecialtyException("Unknown specialty id: " + specialtyId));
        }

        ServicePricingRule rule = new ServicePricingRule();
        rule.setClinic(clinic);
        rule.setClinicService(offering);
        rule.setSpecialty(specialty);
        rule.setOverridePrice(overridePrice);
        rule.setCreatedAt(LocalDateTime.now());
        return servicePricingRuleRepository.save(rule);
    }
}
