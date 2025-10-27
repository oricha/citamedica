package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AppointmentRepository.
 * Tests JPA repository methods with H2 database.
 */
@DataJpaTest
@ActiveProfiles("test")
class AppointmentRepositoryIntegrationTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    private Doctor testDoctor;
    private Patient testPatient;
    private Clinic testClinic;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        clinicRepository.deleteAll();

        testClinic = new Clinic();
        testClinic.setName("Test Clinic");
        testClinic.setSlug("test-clinic");
        testClinic.setAddress("Test Address");
        testClinic.setPhone("+34600000000");
        testClinic.setCreatedAt(LocalDateTime.now());
        testClinic = clinicRepository.save(testClinic);

        testDoctor = new Doctor();
        testDoctor.setClinic(testClinic);
        testDoctor.setFullName("Dr. Test");
        testDoctor.setSpecialty("General Medicine");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setActive(true);
        testDoctor.setCreatedAt(LocalDateTime.now());
        testDoctor = doctorRepository.save(testDoctor);

        testPatient = new Patient();
        testPatient.setFullName("Test Patient");
        testPatient.setEmail("patient@test.com");
        testPatient.setPhone("+34600000000");
        testPatient.setCreatedAt(LocalDateTime.now());
        testPatient = patientRepository.save(testPatient);
    }

    @Test
    void findByDoctorIdAndDate_WithMatchingAppointments_ReturnsAppointments() {
        // Arrange
        LocalDate targetDate = LocalDate.of(2024, 1, 15);
        LocalDateTime startAt = targetDate.atTime(10, 0);
        LocalDateTime endAt = targetDate.atTime(11, 0);

        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setClinic(testClinic);
        appointment.setType("Consultation");
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStartAt(startAt);
        appointment.setEndAt(endAt);
        appointment.setCreatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        // Act
        List<Appointment> result = appointmentRepository.findByDoctorIdAndDate(testDoctor.getId(), targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Consultation", result.get(0).getType());
    }

    @Test
    void findByDoctorIdAndDate_WithDifferentDate_ReturnsEmpty() {
        // Arrange
        LocalDateTime startAt = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime endAt = LocalDateTime.of(2024, 1, 15, 11, 0);

        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setClinic(testClinic);
        appointment.setType("Consultation");
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStartAt(startAt);
        appointment.setEndAt(endAt);
        appointment.setCreatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        // Act - Query for different date
        List<Appointment> result = appointmentRepository.findByDoctorIdAndDate(
                testDoctor.getId(),
                LocalDate.of(2024, 1, 16)
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByCalBookingId_WithExistingBooking_ReturnsAppointment() {
        // Arrange
        String calBookingId = "cal-booking-123";

        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setClinic(testClinic);
        appointment.setCalBookingId(calBookingId);
        appointment.setType("Consultation");
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStartAt(LocalDateTime.now());
        appointment.setEndAt(LocalDateTime.now().plusHours(1));
        appointment.setCreatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        // Act
        List<Appointment> result = appointmentRepository.findByCalBookingId(calBookingId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(calBookingId, result.get(0).getCalBookingId());
    }

    @Test
    void findByCalBookingId_WithNonExistingBooking_ReturnsEmpty() {
        // Act
        List<Appointment> result = appointmentRepository.findByCalBookingId("non-existing");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_WithAllFields_PersistsCorrectly() {
        // Arrange
        Appointment appointment = new Appointment();
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setClinic(testClinic);
        appointment.setCalBookingId("cal-123");
        appointment.setType("Follow-up");
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setStartAt(LocalDateTime.of(2024, 1, 15, 10, 0));
        appointment.setEndAt(LocalDateTime.of(2024, 1, 15, 11, 0));
        appointment.setNotes("Test notes");
        appointment.setCreatedAt(LocalDateTime.now());

        // Act
        Appointment saved = appointmentRepository.save(appointment);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("Follow-up", saved.getType());
        assertEquals("cal-123", saved.getCalBookingId());
        assertEquals("Test notes", saved.getNotes());
        assertEquals(AppointmentStatus.SCHEDULED, saved.getStatus());
    }

    @Test
    void findByDoctorIdAndDate_WithMultipleAppointments_ReturnsAll() {
        // Arrange
        LocalDate targetDate = LocalDate.of(2024, 1, 15);

        Appointment apt1 = new Appointment();
        apt1.setDoctor(testDoctor);
        apt1.setPatient(testPatient);
        apt1.setClinic(testClinic);
        apt1.setType("Consultation");
        apt1.setStatus(AppointmentStatus.SCHEDULED);
        apt1.setStartAt(targetDate.atTime(10, 0));
        apt1.setEndAt(targetDate.atTime(11, 0));
        apt1.setCreatedAt(LocalDateTime.now());
        appointmentRepository.save(apt1);

        Appointment apt2 = new Appointment();
        apt2.setDoctor(testDoctor);
        apt2.setPatient(testPatient);
        apt2.setClinic(testClinic);
        apt2.setType("Follow-up");
        apt2.setStatus(AppointmentStatus.SCHEDULED);
        apt2.setStartAt(targetDate.atTime(14, 0));
        apt2.setEndAt(targetDate.atTime(15, 0));
        apt2.setCreatedAt(LocalDateTime.now());
        appointmentRepository.save(apt2);

        // Act
        List<Appointment> result = appointmentRepository.findByDoctorIdAndDate(testDoctor.getId(), targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}