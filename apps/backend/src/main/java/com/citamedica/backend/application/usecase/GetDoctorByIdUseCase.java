package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GetDoctorByIdUseCase {

    private final DoctorRepository doctorRepository;

    public GetDoctorByIdUseCase(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Doctor> execute(Long id) {
        return doctorRepository.findById(id);
    }
}
