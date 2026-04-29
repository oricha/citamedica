package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.ServicePricingRuleJpaRepository;
import com.citamedica.backend.domain.model.ServicePricingRule;
import com.citamedica.backend.domain.repository.ServicePricingRuleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ServicePricingRuleRepositoryAdapter implements ServicePricingRuleRepository {

    private final ServicePricingRuleJpaRepository jpa;

    public ServicePricingRuleRepositoryAdapter(ServicePricingRuleJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<ServicePricingRule> findByClinicIdAndClinicServiceId(Long clinicId, Long clinicServiceId) {
        return jpa.findByClinicIdAndClinicServiceId(clinicId, clinicServiceId);
    }

    @Override
    public ServicePricingRule save(ServicePricingRule entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
