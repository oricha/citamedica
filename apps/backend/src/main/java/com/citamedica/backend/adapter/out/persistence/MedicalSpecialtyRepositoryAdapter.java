package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.MedicalSpecialtyJpaRepository;
import com.citamedica.backend.domain.model.MedicalSpecialty;
import com.citamedica.backend.domain.repository.MedicalSpecialtyRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MedicalSpecialtyRepositoryAdapter implements MedicalSpecialtyRepository {

    private final MedicalSpecialtyJpaRepository jpa;

    public MedicalSpecialtyRepositoryAdapter(MedicalSpecialtyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<MedicalSpecialty> findAllOrderByName() {
        return jpa.findAllByOrderByNameAsc();
    }

    @Override
    public Optional<MedicalSpecialty> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<MedicalSpecialty> findByCode(String code) {
        return jpa.findByCode(code);
    }

    @Override
    public MedicalSpecialty save(MedicalSpecialty entity) {
        return jpa.save(entity);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
