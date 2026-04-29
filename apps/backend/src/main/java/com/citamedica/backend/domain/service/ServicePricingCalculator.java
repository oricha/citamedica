package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.model.ServicePricingRule;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Computes list price from base, optional specialty surcharge, and optional pricing rule overrides.
 * When a matching {@link ServicePricingRule} exists, its {@code overridePrice} replaces base-plus-surcharge.
 */
public final class ServicePricingCalculator {

    private ServicePricingCalculator() {}

    public static BigDecimal computeEffectivePrice(
            ClinicOffering offering,
            List<ServicePricingRule> rules,
            Optional<BigDecimal> surchargeAmount) {

        BigDecimal basePlusSurcharge = offering.getBasePrice().add(surchargeAmount.orElse(BigDecimal.ZERO));

        MedicalSpecialty min = offering.getMinRequiredSpecialty();
        Optional<ServicePricingRule> rule = rules.stream()
                .filter(r -> ruleApplies(r, min))
                .max(Comparator
                        .comparing((ServicePricingRule r) -> r.getSpecialty() != null)
                        .thenComparing(
                                ServicePricingRule::getUpdatedAt,
                                Comparator.nullsLast(Comparator.naturalOrder())));

        return rule.map(ServicePricingRule::getOverridePrice).orElse(basePlusSurcharge);
    }

    private static boolean ruleApplies(ServicePricingRule rule, MedicalSpecialty minRequired) {
        if (rule.getSpecialty() == null) {
            return true;
        }
        if (minRequired == null) {
            return false;
        }
        return rule.getSpecialty().getId().equals(minRequired.getId());
    }
}
