package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.DoctorAvailabilityConfiguration;
import com.citamedica.backend.domain.model.ScheduleDayOfWeek;
import com.citamedica.backend.domain.repository.DoctorAvailabilityConfigurationRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.service.AvailabilityConfigurationService;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ConfigureDoctorAvailabilityUseCase {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityConfigurationRepository configurationRepository;
    private final AvailabilityConfigurationService availabilityConfigurationService;

    public ConfigureDoctorAvailabilityUseCase(
            DoctorRepository doctorRepository,
            DoctorAvailabilityConfigurationRepository configurationRepository,
            AvailabilityConfigurationService availabilityConfigurationService) {
        this.doctorRepository = doctorRepository;
        this.configurationRepository = configurationRepository;
        this.availabilityConfigurationService = availabilityConfigurationService;
    }

    public List<DoctorAvailabilityConfiguration> list(Long doctorId) {
        ensureDoctor(doctorId);
        return configurationRepository.findByDoctorId(doctorId);
    }

    @Transactional
    public DoctorAvailabilityConfiguration upsert(
            Long doctorId,
            ScheduleDayOfWeek dayOfWeek,
            LocalTime start,
            LocalTime end,
            int slotDurationMinutes,
            int maxConcurrentAppointments) {
        availabilityConfigurationService.validateWorkingWindow(start, end);
        availabilityConfigurationService.validateSlotDurationMinutes(slotDurationMinutes);

        Doctor doctor = ensureDoctor(doctorId);
        DoctorAvailabilityConfiguration config = configurationRepository
                .findByDoctorIdAndDay(doctorId, dayOfWeek)
                .orElseGet(DoctorAvailabilityConfiguration::new);

        if (config.getId() == null) {
            config.setDoctor(doctor);
            config.setCreatedAt(LocalDateTime.now());
        }
        config.setDayOfWeek(dayOfWeek);
        config.setStartTime(start);
        config.setEndTime(end);
        config.setSlotDurationMinutes(slotDurationMinutes);
        config.setMaxConcurrentAppointments(maxConcurrentAppointments);
        config.setUpdatedAt(LocalDateTime.now());
        return configurationRepository.save(config);
    }

    @Transactional
    public DoctorAvailabilityConfiguration update(
            Long doctorId,
            Long configId,
            LocalTime start,
            LocalTime end,
            Integer slotDurationMinutes,
            Integer maxConcurrentAppointments) {
        DoctorAvailabilityConfiguration config = configurationRepository.findById(configId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Configuration not found: " + configId));
        if (!config.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Configuration does not belong to doctor");
        }
        if ((start != null) != (end != null)) {
            throw new IllegalArgumentException("startTime and endTime must both be provided when updating times");
        }
        if (start != null) {
            availabilityConfigurationService.validateWorkingWindow(start, end);
            config.setStartTime(start);
            config.setEndTime(end);
        }
        if (slotDurationMinutes != null) {
            availabilityConfigurationService.validateSlotDurationMinutes(slotDurationMinutes);
            config.setSlotDurationMinutes(slotDurationMinutes);
        }
        if (maxConcurrentAppointments != null) {
            config.setMaxConcurrentAppointments(Math.max(1, maxConcurrentAppointments));
        }
        config.setUpdatedAt(LocalDateTime.now());
        return configurationRepository.save(config);
    }

    @Transactional
    public void delete(Long doctorId, Long configId) {
        DoctorAvailabilityConfiguration config = configurationRepository.findById(configId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Configuration not found: " + configId));
        if (!config.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("Configuration does not belong to doctor");
        }
        configurationRepository.deleteById(configId);
    }

    private Doctor ensureDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Doctor not found: " + doctorId));
    }
}
