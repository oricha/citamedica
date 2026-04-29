package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.DoctorSpecialty;
import com.citamedica.backend.domain.repository.DoctorSpecialtyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetDoctorSpecialtiesUseCase {

    private final DoctorSpecialtyRepository doctorSpecialtyRepository;

    public GetDoctorSpecialtiesUseCase(DoctorSpecialtyRepository doctorSpecialtyRepository) {
        this.doctorSpecialtyRepository = doctorSpecialtyRepository;
    }

    @Transactional(readOnly = true)
    public List<DoctorSpecialty> execute(Long doctorId) {
        return doctorSpecialtyRepository.findByDoctorId(doctorId);
    }
}
