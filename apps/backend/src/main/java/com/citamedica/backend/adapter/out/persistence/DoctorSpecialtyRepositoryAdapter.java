package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.DoctorSpecialtyJpaRepository;
import com.citamedica.backend.domain.model.DoctorSpecialty;
import com.citamedica.backend.domain.repository.DoctorSpecialtyRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DoctorSpecialtyRepositoryAdapter implements DoctorSpecialtyRepository {

    private final DoctorSpecialtyJpaRepository jpa;

    public DoctorSpecialtyRepositoryAdapter(DoctorSpecialtyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<DoctorSpecialty> findByDoctorId(Long doctorId) {
        return jpa.findByDoctorIdOrderByPrimarySpecialtyDesc(doctorId);
    }

    @Override
    public Optional<DoctorSpecialty> findByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId) {
        return jpa.findByDoctorIdAndSpecialtyId(doctorId, specialtyId);
    }

    @Override
    public boolean existsByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId) {
        return jpa.existsByDoctorIdAndSpecialtyId(doctorId, specialtyId);
    }

    @Override
    public long countByDoctorIdAndPrimarySpecialtyTrue(Long doctorId) {
        return jpa.countByDoctorIdAndPrimarySpecialtyTrue(doctorId);
    }

    @Override
    public DoctorSpecialty save(DoctorSpecialty entity) {
        return jpa.save(entity);
    }

    @Override
    public void delete(DoctorSpecialty entity) {
        jpa.delete(entity);
    }

    @Override
    public void deleteByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId) {
        jpa.deleteByDoctorIdAndSpecialtyId(doctorId, specialtyId);
    }

    @Override
    public void deleteAllByDoctorId(Long doctorId) {
        jpa.deleteAllByDoctorId(doctorId);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
