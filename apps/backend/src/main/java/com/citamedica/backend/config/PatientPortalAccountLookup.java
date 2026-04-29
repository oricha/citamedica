package com.citamedica.backend.config;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.PatientRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PatientPortalAccountLookup {

    private final PatientRepository patientRepository;

    public PatientPortalAccountLookup(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Optional<PatientUserDetails> findActivePortalUser(String rawUsername) {
        if (rawUsername == null || rawUsername.isBlank()) {
            return Optional.empty();
        }
        return patientRepository.findByEmailIgnoreCase(rawUsername.trim())
                .filter(Patient::isPortalAccessEnabled)
                .filter(p -> p.getPortalPasswordHash() != null && !p.getPortalPasswordHash().isBlank())
                .map(p -> new PatientUserDetails(p.getId(), p.getEmail(), p.getPortalPasswordHash()));
    }
}
