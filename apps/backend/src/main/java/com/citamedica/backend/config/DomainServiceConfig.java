package com.citamedica.backend.config;

import com.citamedica.backend.domain.service.AppointmentDomainService;
import com.citamedica.backend.domain.service.DoctorDomainService;
import com.citamedica.backend.domain.service.PatientDomainService;
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
}
