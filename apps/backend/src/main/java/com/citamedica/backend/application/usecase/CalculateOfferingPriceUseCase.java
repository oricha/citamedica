package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.model.ServicePricingRule;
import com.citamedica.backend.domain.model.SpecialtySurcharge;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.ServicePricingRuleRepository;
import com.citamedica.backend.domain.repository.SpecialtySurchargeRepository;
import com.citamedica.backend.domain.service.ServicePricingCalculator;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidDomainOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CalculateOfferingPriceUseCase {

    public record OfferingPriceResult(Long clinicId, Long doctorId, Long serviceId, BigDecimal effectivePrice) {}

    private final DoctorRepository doctorRepository;
    private final ClinicOfferingRepository clinicOfferingRepository;
    private final ServicePricingRuleRepository servicePricingRuleRepository;
    private final SpecialtySurchargeRepository specialtySurchargeRepository;

    public CalculateOfferingPriceUseCase(
            DoctorRepository doctorRepository,
            ClinicOfferingRepository clinicOfferingRepository,
            ServicePricingRuleRepository servicePricingRuleRepository,
            SpecialtySurchargeRepository specialtySurchargeRepository) {
        this.doctorRepository = doctorRepository;
        this.clinicOfferingRepository = clinicOfferingRepository;
        this.servicePricingRuleRepository = servicePricingRuleRepository;
        this.specialtySurchargeRepository = specialtySurchargeRepository;
    }

    @Transactional(readOnly = true)
    public OfferingPriceResult execute(Long doctorId, Long serviceId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
        ClinicOffering offering = clinicOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Service not found: " + serviceId));

        if (doctor.getClinic() == null || offering.getClinic() == null
                || !doctor.getClinic().getId().equals(offering.getClinic().getId())) {
            throw new InvalidDomainOperationException("Doctor and service must belong to the same clinic");
        }

        Long clinicId = doctor.getClinic().getId();
        Optional<BigDecimal> surcharge = resolveSurcharge(offering.getMinRequiredSpecialty(), clinicId);
        List<ServicePricingRule> rules =
                servicePricingRuleRepository.findByClinicIdAndClinicServiceId(clinicId, serviceId);
        BigDecimal price = ServicePricingCalculator.computeEffectivePrice(offering, rules, surcharge);
        return new OfferingPriceResult(clinicId, doctorId, serviceId, price);
    }

    private Optional<BigDecimal> resolveSurcharge(MedicalSpecialty minRequired, Long clinicId) {
        if (minRequired == null) {
            return Optional.empty();
        }
        Long specialtyId = minRequired.getId();
        List<SpecialtySurcharge> clinicSpecific = specialtySurchargeRepository.findBySpecialtyIdAndClinicId(specialtyId, clinicId);
        if (!clinicSpecific.isEmpty()) {
            return Optional.of(clinicSpecific.get(0).getSurchargeAmount());
        }
        List<SpecialtySurcharge> global = specialtySurchargeRepository.findBySpecialtyIdAndClinicIdIsNull(specialtyId);
        if (!global.isEmpty()) {
            return Optional.of(global.get(0).getSurchargeAmount());
        }
        return Optional.empty();
    }
}
