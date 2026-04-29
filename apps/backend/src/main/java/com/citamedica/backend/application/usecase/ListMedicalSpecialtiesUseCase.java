package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.repository.MedicalSpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListMedicalSpecialtiesUseCase {

    private final MedicalSpecialtyRepository medicalSpecialtyRepository;

    public ListMedicalSpecialtiesUseCase(MedicalSpecialtyRepository medicalSpecialtyRepository) {
        this.medicalSpecialtyRepository = medicalSpecialtyRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicalSpecialty> execute() {
        return medicalSpecialtyRepository.findAllOrderByName();
    }
}
