package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.ClinicOffering;
import com.citamedica.backend.domain.repository.ClinicOfferingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListClinicOfferingsUseCase {

    private final ClinicOfferingRepository clinicOfferingRepository;

    public ListClinicOfferingsUseCase(ClinicOfferingRepository clinicOfferingRepository) {
        this.clinicOfferingRepository = clinicOfferingRepository;
    }

    @Transactional(readOnly = true)
    public List<ClinicOffering> execute(Long clinicId, boolean activeOnly) {
        if (activeOnly) {
            return clinicOfferingRepository.findByClinicIdAndActiveTrue(clinicId);
        }
        return clinicOfferingRepository.findByClinicId(clinicId);
    }
}
