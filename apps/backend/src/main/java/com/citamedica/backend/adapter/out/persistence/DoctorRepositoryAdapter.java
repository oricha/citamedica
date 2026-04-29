package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.DoctorJpaRepository;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.repository.DoctorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DoctorRepositoryAdapter implements DoctorRepository {

    private final DoctorJpaRepository jpa;

    public DoctorRepositoryAdapter(DoctorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<Doctor> findByClinicId(Long clinicId) {
        return jpa.findByClinicId(clinicId);
    }

    @Override
    public List<Doctor> findByActiveTrue() {
        return jpa.findByActiveTrue();
    }

    @Override
    public List<Doctor> searchActive(Long clinicId, Long specialtyId, Long serviceOfferingId, int page, int size) {
        if (size <= 0) {
            return List.of();
        }
        return jpa.searchActiveDoctors(clinicId, specialtyId, serviceOfferingId, PageRequest.of(page, size)).getContent();
    }

    @Override
    public Optional<Doctor> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public Optional<Doctor> findByCalUsername(String calUsername) {
        return jpa.findByCalUsername(calUsername);
    }

    @Override
    public Doctor save(Doctor entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
