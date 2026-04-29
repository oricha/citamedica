package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.ServicePricingRule;

import java.util.List;

public interface ServicePricingRuleRepository {

    List<ServicePricingRule> findByClinicIdAndClinicServiceId(Long clinicId, Long clinicServiceId);

    ServicePricingRule save(ServicePricingRule entity);

    void deleteAll();
}
