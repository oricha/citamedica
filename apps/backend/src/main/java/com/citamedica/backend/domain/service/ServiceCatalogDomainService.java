package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.exception.domain.ServiceCatalogException;

public class ServiceCatalogDomainService {

    public void validateClinicOffering(ClinicOffering offering) {
        try {
            offering.validateBusinessRules();
        } catch (IllegalArgumentException e) {
            throw new ServiceCatalogException(e.getMessage());
        }
    }
}
