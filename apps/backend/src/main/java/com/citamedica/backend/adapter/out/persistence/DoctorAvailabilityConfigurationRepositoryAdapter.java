package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.DoctorAvailabilityConfigurationJpaRepository;
import com.citamedica.backend.domain.model.DoctorAvailabilityConfiguration;
import com.citamedica.backend.domain.model.ScheduleDayOfWeek;
import com.citamedica.backend.domain.repository.DoctorAvailabilityConfigurationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DoctorAvailabilityConfigurationRepositoryAdapter implements DoctorAvailabilityConfigurationRepository {

    private final DoctorAvailabilityConfigurationJpaRepository jpa;

    public DoctorAvailabilityConfigurationRepositoryAdapter(DoctorAvailabilityConfigurationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<DoctorAvailabilityConfiguration> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<DoctorAvailabilityConfiguration> findByDoctorId(Long doctorId) {
        return jpa.findByDoctorId(doctorId);
    }

    @Override
    public Optional<DoctorAvailabilityConfiguration> findByDoctorIdAndDay(Long doctorId, ScheduleDayOfWeek day) {
        return jpa.findByDoctorIdAndDayOfWeek(doctorId, day);
    }

    @Override
    public DoctorAvailabilityConfiguration save(DoctorAvailabilityConfiguration entity) {
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
