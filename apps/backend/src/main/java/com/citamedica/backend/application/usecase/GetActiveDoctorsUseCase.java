package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetActiveDoctorsUseCase {

    private final DoctorRepository doctorRepository;

    public GetActiveDoctorsUseCase(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<Doctor> execute() {
        return doctorRepository.findByActiveTrue();
    }
}
