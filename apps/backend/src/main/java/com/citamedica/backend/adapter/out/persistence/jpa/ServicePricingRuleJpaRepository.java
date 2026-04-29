package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.ServicePricingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePricingRuleJpaRepository extends JpaRepository<ServicePricingRule, Long> {

    @Query("SELECT r FROM ServicePricingRule r WHERE r.clinic.id = :clinicId AND r.clinicService.id = :clinicServiceId")
    List<ServicePricingRule> findByClinicIdAndClinicServiceId(
            @Param("clinicId") Long clinicId,
            @Param("clinicServiceId") Long clinicServiceId);
}
