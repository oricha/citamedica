package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;

import java.util.List;
import java.util.Optional;

public interface DoctorAvailabilityBlockRepository {

    Optional<DoctorAvailabilityBlock> findById(Long id);

    List<DoctorAvailabilityBlock> findActiveByDoctorId(Long doctorId);

    DoctorAvailabilityBlock save(DoctorAvailabilityBlock entity);

    void deleteById(Long id);

    void deleteAll();
}
