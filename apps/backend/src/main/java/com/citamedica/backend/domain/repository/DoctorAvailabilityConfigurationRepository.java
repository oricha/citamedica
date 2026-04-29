package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.DoctorAvailabilityConfiguration;

import com.citamedica.backend.domain.model.ScheduleDayOfWeek;

import java.util.List;
import java.util.Optional;

public interface DoctorAvailabilityConfigurationRepository {

    Optional<DoctorAvailabilityConfiguration> findById(Long id);

    List<DoctorAvailabilityConfiguration> findByDoctorId(Long doctorId);

    Optional<DoctorAvailabilityConfiguration> findByDoctorIdAndDay(Long doctorId, ScheduleDayOfWeek day);

    DoctorAvailabilityConfiguration save(DoctorAvailabilityConfiguration entity);

    void deleteById(Long id);

    void deleteAll();
}
