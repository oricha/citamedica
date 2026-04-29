package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.DoctorSpecialty;

import java.util.List;
import java.util.Optional;

public interface DoctorSpecialtyRepository {

    List<DoctorSpecialty> findByDoctorId(Long doctorId);

    Optional<DoctorSpecialty> findByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);

    boolean existsByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);

    long countByDoctorIdAndPrimarySpecialtyTrue(Long doctorId);

    DoctorSpecialty save(DoctorSpecialty entity);

    void delete(DoctorSpecialty entity);

    void deleteByDoctorIdAndSpecialtyId(Long doctorId, Long specialtyId);

    void deleteAllByDoctorId(Long doctorId);

    void deleteAll();
}
