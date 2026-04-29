package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.catalog.EffectivePriceResponse;
import com.citamedica.backend.adapter.in.dto.catalog.ServicePricingRuleRequest;
import com.citamedica.backend.adapter.in.dto.catalog.SpecialtySurchargeRequest;
import com.citamedica.backend.application.usecase.CalculateOfferingPriceUseCase;
import com.citamedica.backend.application.usecase.CreateServicePricingRuleUseCase;
import com.citamedica.backend.application.usecase.CreateSpecialtySurchargeUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ServiceCatalogPricingController {

    private final CreateSpecialtySurchargeUseCase createSpecialtySurchargeUseCase;
    private final CreateServicePricingRuleUseCase createServicePricingRuleUseCase;
    private final CalculateOfferingPriceUseCase calculateOfferingPriceUseCase;

    public ServiceCatalogPricingController(
            CreateSpecialtySurchargeUseCase createSpecialtySurchargeUseCase,
            CreateServicePricingRuleUseCase createServicePricingRuleUseCase,
            CalculateOfferingPriceUseCase calculateOfferingPriceUseCase) {
        this.createSpecialtySurchargeUseCase = createSpecialtySurchargeUseCase;
        this.createServicePricingRuleUseCase = createServicePricingRuleUseCase;
        this.calculateOfferingPriceUseCase = calculateOfferingPriceUseCase;
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @PostMapping("/api/v1/specialty-surcharges")
    public ResponseEntity<Void> createSurcharge(@Valid @RequestBody SpecialtySurchargeRequest request) {
        createSpecialtySurchargeUseCase.execute(
                request.getSpecialtyId(),
                request.getSurchargeAmount(),
                request.getClinicId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @PostMapping("/api/v1/clinics/{clinicId}/pricing-rules")
    public ResponseEntity<Void> createPricingRule(
            @PathVariable Long clinicId,
            @Valid @RequestBody ServicePricingRuleRequest request) {
        createServicePricingRuleUseCase.execute(
                clinicId,
                request.getServiceId(),
                request.getSpecialtyId(),
                request.getOverridePrice());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/v1/doctors/{doctorId}/services/{serviceId}/price")
    public ResponseEntity<EffectivePriceResponse> getEffectivePrice(
            @PathVariable Long doctorId,
            @PathVariable Long serviceId) {
        CalculateOfferingPriceUseCase.OfferingPriceResult result =
                calculateOfferingPriceUseCase.execute(doctorId, serviceId);
        return ResponseEntity.ok(new EffectivePriceResponse(
                result.clinicId(),
                result.doctorId(),
                result.serviceId(),
                result.effectivePrice()));
    }
}
