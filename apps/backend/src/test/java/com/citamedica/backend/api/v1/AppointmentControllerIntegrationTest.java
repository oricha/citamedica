package com.citamedica.backend.api.v1;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AppointmentController.
 * Tests REST endpoints with real database interactions.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AppointmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
        // Clean up
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        clinicRepository.deleteAll();

        // Create test data
        testClinic = new Clinic();
        testClinic.setName("Test Clinic");
        testClinic.setSlug("test-clinic");
        testClinic.setAddress("Test Address");
        testClinic.setPhone("+34600000000");
        testClinic.setEmail("clinic@test.com");
        testClinic.setCreatedAt(LocalDateTime.now());
        testClinic = clinicRepository.save(testClinic);

        testDoctor = new Doctor();
        testDoctor.setClinic(testClinic);
        testDoctor.setFullName("Dr. Test");
        testDoctor.setSpecialty("General Medicine");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setPhone("+34600000001");
        testDoctor.setActive(true);
        testDoctor.setCreatedAt(LocalDateTime.now());
        testDoctor = doctorRepository.save(testDoctor);

        testPatient = new Patient();
        testPatient.setFullName("Test Patient");
        testPatient.setEmail("patient@test.com");
        testPatient.setPhone("+34600000002");
        testPatient.setCreatedAt(LocalDateTime.now());
        testPatient = patientRepository.save(testPatient);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAppointments_WithExistingAppointments_ReturnsAppointmentsList() throws Exception {
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

        // Act & Assert
        mockMvc.perform(get("/api/v1/appointments")
                        .param("doctorId", testDoctor.getId().toString())
                        .param("date", "2024-01-15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is("Consultation")))
                .andExpect(jsonPath("$[0].status", is("SCHEDULED")));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAppointments_WithNoAppointments_ReturnsEmptyList() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/appointments")
                        .param("doctorId", testDoctor.getId().toString())
                        .param("date", "2024-01-15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAppointments_WithMultipleAppointments_ReturnsSortedByStartTime() throws Exception {
        // Arrange - Create appointments in reverse order
        LocalDateTime date = LocalDateTime.of(2024, 1, 15, 0, 0);

        Appointment apt1 = new Appointment();
        apt1.setDoctor(testDoctor);
        apt1.setPatient(testPatient);
        apt1.setClinic(testClinic);
        apt1.setType("Consultation");
        apt1.setStatus(AppointmentStatus.SCHEDULED);
        apt1.setStartAt(date.withHour(14).withMinute(0));
        apt1.setEndAt(date.withHour(15).withMinute(0));
        apt1.setCreatedAt(LocalDateTime.now());
        appointmentRepository.save(apt1);

        Appointment apt2 = new Appointment();
        apt2.setDoctor(testDoctor);
        apt2.setPatient(testPatient);
        apt2.setClinic(testClinic);
        apt2.setType("Follow-up");
        apt2.setStatus(AppointmentStatus.SCHEDULED);
        apt2.setStartAt(date.withHour(10).withMinute(0));
        apt2.setEndAt(date.withHour(11).withMinute(0));
        apt2.setCreatedAt(LocalDateTime.now());
        appointmentRepository.save(apt2);

        // Act & Assert - Should be sorted by start time
        mockMvc.perform(get("/api/v1/appointments")
                        .param("doctorId", testDoctor.getId().toString())
                        .param("date", "2024-01-15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type", is("Follow-up")))  // 10:00 comes first
                .andExpect(jsonPath("$[1].type", is("Consultation"))); // 14:00 comes second
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void createAppointment_WithValidData_CreatesAppointment() throws Exception {
        // Arrange
        String requestBody = String.format("""
                {
                    "doctorId": %d,
                    "patientId": %d,
                    "type": "Consultation",
                    "startAt": "2024-01-15T10:00:00",
                    "endAt": "2024-01-15T11:00:00"
                }
                """, testDoctor.getId(), testPatient.getId());

        // Act & Assert
        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calBookingId", is("cal-booking-123")))
                .andExpect(jsonPath("$.notes", is("Test notes")));
    }

    @Test
    void getAppointments_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/appointments")
                        .param("doctorId", testDoctor.getId().toString())
                        .param("date", "2024-01-15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void getAppointments_WithInvalidDate_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/appointments")
                        .param("doctorId", testDoctor.getId().toString())
                        .param("date", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

        // Act & Assert
        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", is("Consultation")))
                .andExpect(jsonPath("$.status", is("SCHEDULED")));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void createAppointment_WithCalBookingId_SavesCalBookingId() throws Exception {
        // Arrange
        String requestBody = String.format("""
                {
                    "doctorId": %d,
                    "patientId": %d,
                    "type": "Consultation",
                    "startAt": "2024-01-15T10:00:00",
                    "endAt": "2024-01-15T11:00:00",
                    "calBookingId": "cal-booking-123",
                    "notes": "Test notes"
                }
                """, testDoctor.getId(), testPatient.getId());