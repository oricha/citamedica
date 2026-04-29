package com.citamedica.backend.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JpaEntitiesUnitTest {

    @Test
    void clinicDoctorPatientConstructorsInitializeCoreFields() {
        Clinic clinic = new Clinic("clinic-main", "Main Clinic");
        Doctor doctor = new Doctor(clinic, "Dr. House", "Internal Medicine", "house@test.com");
        Patient patient = new Patient("John Doe", "john@test.com", "+34123456789");

        assertEquals("clinic-main", clinic.getSlug());
        assertEquals("Main Clinic", clinic.getName());
        assertNotNull(clinic.getCreatedAt());

        assertEquals(clinic, doctor.getClinic());
        assertEquals("Dr. House", doctor.getFullName());
        assertEquals("Internal Medicine", doctor.getSpecialty());
        assertEquals("house@test.com", doctor.getEmail());
        assertTrue(doctor.getActive());
        assertNotNull(doctor.getCreatedAt());

        assertEquals("John Doe", patient.getFullName());
        assertEquals("john@test.com", patient.getEmail());
        assertEquals("+34123456789", patient.getPhone());
        assertNotNull(patient.getCreatedAt());
    }

    @Test
    void appointmentAndConsentConstructorsSetDomainDefaults() {
        Clinic clinic = new Clinic("clinic-2", "Clinic 2");
        Doctor doctor = new Doctor(clinic, "Dr. A", "General", "a@test.com");
        Patient patient = new Patient("Jane", "jane@test.com", "+34999999999");

        Appointment appointment = new Appointment(
                doctor,
                patient,
                "First Visit",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusMinutes(30)
        );
        Consent acceptedConsent = new Consent(patient, ConsentType.PRIVACY_POLICY, true);
        Consent rejectedConsent = new Consent(patient, ConsentType.TERMS_OF_SERVICE, false);

        assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
        assertNotNull(appointment.getCreatedAt());

        assertTrue(acceptedConsent.getAccepted());
        assertNotNull(acceptedConsent.getAcceptedAt());

        assertFalse(rejectedConsent.getAccepted());
        assertNull(rejectedConsent.getAcceptedAt());
    }
}
