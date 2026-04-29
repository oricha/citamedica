package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JpaEntitiesIntegrationTest {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void persistsAndQueriesCoreJpaEntityRelationships() {
        Clinic clinic = new Clinic();
        clinic.setSlug("clinic-" + UUID.randomUUID());
        clinic.setName("Rel Clinic");
        clinic.setCreatedAt(LocalDateTime.now());
        clinic = clinicRepository.save(clinic);

        Doctor doctor = new Doctor();
        doctor.setClinic(clinic);
        doctor.setFullName("Dr. Rel");
        doctor.setSpecialty("Cardiology");
        doctor.setEmail("doc-" + UUID.randomUUID() + "@test.com");
        doctor.setActive(true);
        doctor.setCreatedAt(LocalDateTime.now());
        doctor = doctorRepository.save(doctor);

        Patient patient = new Patient();
        patient.setFullName("Patient Rel");
        patient.setEmail("pat-" + UUID.randomUUID() + "@test.com");
        patient.setPhone("+34111111111");
        patient.setBirthDate(LocalDate.of(1990, 5, 15));
        patient.setCreatedAt(LocalDateTime.now());
        patient = patientRepository.save(patient);

        Consent consent = new Consent();
        consent.setPatient(patient);
        consent.setType(ConsentType.PRIVACY_POLICY);
        consent.setAccepted(true);
        consent.setAcceptedAt(LocalDateTime.now());
        consent = consentRepository.save(consent);

        Appointment appointment = new Appointment();
        appointment.setClinic(clinic);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setType("Control");
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStartAt(LocalDateTime.of(2026, 5, 1, 9, 0));
        appointment.setEndAt(LocalDateTime.of(2026, 5, 1, 9, 30));
        appointment.setCreatedAt(LocalDateTime.now());
        appointment = appointmentRepository.save(appointment);

        List<Doctor> clinicDoctors = doctorRepository.findByClinicId(clinic.getId());
        List<Consent> patientConsents = consentRepository.findByPatientIdAndType(patient.getId(), ConsentType.PRIVACY_POLICY);
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorIdAndDate(doctor.getId(), LocalDate.of(2026, 5, 1));

        assertEquals(1, clinicDoctors.size());
        assertEquals(doctor.getId(), clinicDoctors.get(0).getId());

        assertEquals(1, patientConsents.size());
        assertEquals(consent.getId(), patientConsents.get(0).getId());

        assertEquals(1, doctorAppointments.size());
        assertEquals(appointment.getId(), doctorAppointments.get(0).getId());
        assertEquals(patient.getId(), doctorAppointments.get(0).getPatient().getId());
    }
}
