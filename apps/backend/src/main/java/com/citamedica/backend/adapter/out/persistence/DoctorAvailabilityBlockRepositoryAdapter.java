package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.DoctorAvailabilityBlockJpaRepository;
import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;
import com.citamedica.backend.domain.repository.DoctorAvailabilityBlockRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DoctorAvailabilityBlockRepositoryAdapter implements DoctorAvailabilityBlockRepository {

    private final DoctorAvailabilityBlockJpaRepository jpa;

    public DoctorAvailabilityBlockRepositoryAdapter(DoctorAvailabilityBlockJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<DoctorAvailabilityBlock> findById(Long id) {
        return jpa.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public List<DoctorAvailabilityBlock> findActiveByDoctorId(Long doctorId) {
        return jpa.findByDoctorIdAndDeletedAtIsNull(doctorId);
    }

    @Override
    public DoctorAvailabilityBlock save(DoctorAvailabilityBlock entity) {
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
