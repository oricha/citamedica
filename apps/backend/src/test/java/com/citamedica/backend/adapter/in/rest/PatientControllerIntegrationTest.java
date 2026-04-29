package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.NotificationPreferenceRepository;
import com.citamedica.backend.domain.repository.NotificationRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @BeforeEach
    void setUp() {
        notificationPreferenceRepository.deleteAll();
        notificationRepository.deleteAll();
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void createPatient_withValidPayload_returns201() throws Exception {
        String payload = """
                {
                  "fullName": "Jane Doe",
                  "email": "jane.%s@test.com",
                  "phone": "+34600111222",
                  "birthDate": "1992-04-10",
                  "insurancePlan": "Premium"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.fullName", is("Jane Doe")))
                .andExpect(jsonPath("$.insurancePlan", is("Premium")));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getPatient_withExistingId_returns200() throws Exception {
        Patient patient = new Patient();
        patient.setFullName("Patient One");
        patient.setEmail("patient." + UUID.randomUUID() + "@test.com");
        patient.setPhone("+34600333444");
        patient.setCreatedAt(LocalDateTime.now());
        patient = patientRepository.save(patient);

        mockMvc.perform(get("/api/v1/patients/{id}", patient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(patient.getId().intValue())))
                .andExpect(jsonPath("$.email", is(patient.getEmail())));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getPatient_withUnknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/patients/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void updatePatient_withValidPayload_returns200() throws Exception {
        Patient patient = new Patient();
        patient.setFullName("Old Name");
        patient.setEmail("old." + UUID.randomUUID() + "@test.com");
        patient.setPhone("+34600555666");
        patient.setCreatedAt(LocalDateTime.now());
        patient = patientRepository.save(patient);

        String payload = """
                {
                  "fullName": "New Name",
                  "email": "%s",
                  "phone": "+34600999888",
                  "birthDate": "1990-01-01",
                  "insurancePlan": "Basic"
                }
                """.formatted(patient.getEmail());

        mockMvc.perform(put("/api/v1/patients/{id}", patient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("New Name")))
                .andExpect(jsonPath("$.insurancePlan", is("Basic")));
    }
}
