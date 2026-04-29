package com.citamedica.backend;

import com.citamedica.backend.config.AvailabilityProperties;
import com.citamedica.backend.config.MedicalDocumentStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.citamedica.backend.adapter.out.persistence.jpa")
@EnableRetry
@EnableScheduling
@EnableConfigurationProperties({AvailabilityProperties.class, MedicalDocumentStorageProperties.class})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
