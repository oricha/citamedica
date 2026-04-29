package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.DoctorAvailabilityConfiguration;
import com.citamedica.backend.domain.model.ScheduleDayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityConfigurationJpaRepository extends JpaRepository<DoctorAvailabilityConfiguration, Long> {

    List<DoctorAvailabilityConfiguration> findByDoctorId(Long doctorId);

    Optional<DoctorAvailabilityConfiguration> findByDoctorIdAndDayOfWeek(Long doctorId, ScheduleDayOfWeek dayOfWeek);
}
