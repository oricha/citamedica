package com.citamedica.backend.application;

import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.repository.ClinicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public Clinic createClinic(String slug, String name, String address, String phone) {
        Clinic clinic = new Clinic(slug, name);
        clinic.setAddress(address);
        clinic.setPhone(phone);
        return clinicRepository.save(clinic);
    }

    public Optional<Clinic> findBySlug(String slug) {
        return clinicRepository.findBySlug(slug);
    }

    public List<Clinic> findAll() {
        return clinicRepository.findAll();
    }

    public Clinic updateClinic(Long id, String name, String address, String phone) {
        Clinic clinic = clinicRepository.findById(id).orElseThrow();
        clinic.setName(name);
        clinic.setAddress(address);
        clinic.setPhone(phone);
        return clinicRepository.save(clinic);
    }

    public void deleteClinic(Long id) {
        clinicRepository.deleteById(id);
    }
}