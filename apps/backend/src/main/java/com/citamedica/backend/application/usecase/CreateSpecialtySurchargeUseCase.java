package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.model.SpecialtySurcharge;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.repository.MedicalSpecialtyRepository;
import com.citamedica.backend.domain.repository.SpecialtySurchargeRepository;
import com.citamedica.backend.exception.domain.DuplicateEntityException;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.InvalidSpecialtyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CreateSpecialtySurchargeUseCase {

    private final SpecialtySurchargeRepository specialtySurchargeRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;
    private final ClinicRepository clinicRepository;

    public CreateSpecialtySurchargeUseCase(
            SpecialtySurchargeRepository specialtySurchargeRepository,
            MedicalSpecialtyRepository medicalSpecialtyRepository,
            ClinicRepository clinicRepository) {
        this.specialtySurchargeRepository = specialtySurchargeRepository;
        this.medicalSpecialtyRepository = medicalSpecialtyRepository;
        this.clinicRepository = clinicRepository;
    }

    @Transactional
    public SpecialtySurcharge execute(Long specialtyId, BigDecimal amount, Long clinicId) {
        MedicalSpecialty specialty = medicalSpecialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new InvalidSpecialtyException("Unknown specialty id: " + specialtyId));

        Clinic clinic = null;
        if (clinicId != null) {
            clinic = clinicRepository.findById(clinicId)
                    .orElseThrow(() -> new EntityNotFoundDomainException("Clinic not found: " + clinicId));
        }

        if (clinic == null) {
            List<SpecialtySurcharge> globals = specialtySurchargeRepository.findBySpecialtyIdAndClinicIdIsNull(specialtyId);
            if (!globals.isEmpty()) {
                throw new DuplicateEntityException("A global surcharge already exists for specialty " + specialtyId);
            }
        }

        SpecialtySurcharge s = new SpecialtySurcharge();
        s.setSpecialty(specialty);
        s.setSurchargeAmount(amount);
        s.setClinic(clinic);
        s.setCreatedAt(LocalDateTime.now());
        return specialtySurchargeRepository.save(s);
    }
}
