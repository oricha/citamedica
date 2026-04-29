package com.citamedica.backend.application;

import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor createDoctor(Long clinicId, String fullName, String specialty, String email, String phone) {
        Doctor doctor = new Doctor();
        if (clinicId != null) {
            // Assuming Clinic is fetched or set later
        }
        doctor.setFullName(fullName);
        doctor.setSpecialty(specialty);
        doctor.setEmail(email);
        doctor.setPhone(phone);
        return doctorRepository.save(doctor);
    }

    public List<Doctor> findByClinicId(Long clinicId) {
        return doctorRepository.findByClinicId(clinicId);
    }

    public List<Doctor> findActiveDoctors() {
        return doctorRepository.findByActiveTrue();
    }

    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor updateDoctor(Long id, String fullName, String specialty, String email, String phone, Boolean active) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow();
        doctor.setFullName(fullName);
        doctor.setSpecialty(specialty);
        doctor.setEmail(email);
        doctor.setPhone(phone);
        doctor.setActive(active);
        return doctorRepository.save(doctor);
    }
}