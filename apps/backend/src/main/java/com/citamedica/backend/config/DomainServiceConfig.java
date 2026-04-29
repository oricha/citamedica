package com.citamedica.backend.config;

import com.citamedica.backend.domain.service.AppointmentAvailabilityValidator;
import com.citamedica.backend.domain.service.AppointmentDomainService;
import com.citamedica.backend.domain.service.AppointmentNotificationDomainService;
import com.citamedica.backend.domain.service.AvailabilityConfigurationService;
import com.citamedica.backend.domain.service.AvailabilityConflictDetectionService;
import com.citamedica.backend.domain.service.DoctorDomainService;
import com.citamedica.backend.domain.service.NotificationPreferenceDomainService;
import com.citamedica.backend.domain.service.PatientDomainService;
import com.citamedica.backend.domain.service.TimeSlotGenerationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public AppointmentDomainService appointmentDomainService() {
        return new AppointmentDomainService();
    }

    @Bean
    public PatientDomainService patientDomainService() {
        return new PatientDomainService();
    }

    @Bean
    public DoctorDomainService doctorDomainService() {
        return new DoctorDomainService();
    }

    @Bean
    public AppointmentNotificationDomainService appointmentNotificationDomainService() {
        return new AppointmentNotificationDomainService();
    }

    @Bean
    public NotificationPreferenceDomainService notificationPreferenceDomainService() {
        return new NotificationPreferenceDomainService();
    }

    @Bean
    public AvailabilityConfigurationService availabilityConfigurationService() {
        return new AvailabilityConfigurationService();
    }

    @Bean
    public TimeSlotGenerationService timeSlotGenerationService() {
        return new TimeSlotGenerationService();
    }

    @Bean
    public AvailabilityConflictDetectionService availabilityConflictDetectionService() {
        return new AvailabilityConflictDetectionService();
    }

    @Bean
    public AppointmentAvailabilityValidator appointmentAvailabilityValidator(
            AvailabilityConflictDetectionService availabilityConflictDetectionService) {
        return new AppointmentAvailabilityValidator(availabilityConflictDetectionService);
    }
}
