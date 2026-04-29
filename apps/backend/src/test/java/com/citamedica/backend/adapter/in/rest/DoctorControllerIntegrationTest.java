package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.ClinicRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.NotificationPreferenceRepository;
import com.citamedica.backend.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    private Clinic clinic;

    @BeforeEach
    void setUp() {
        notificationPreferenceRepository.deleteAll();
        notificationRepository.deleteAll();
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        clinicRepository.deleteAll();

        clinic = new Clinic();
        clinic.setSlug("clinic-" + UUID.randomUUID());
        clinic.setName("Clinic REST");
        clinic.setCreatedAt(LocalDateTime.now());
        clinic = clinicRepository.save(clinic);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getDoctors_withClinicFilter_returnsFilteredDoctors() throws Exception {
        Doctor inClinic = new Doctor();
        inClinic.setClinic(clinic);
        inClinic.setFullName("Dr In");
        inClinic.setSpecialty("General");
        inClinic.setEmail("in." + UUID.randomUUID() + "@test.com");
        inClinic.setActive(true);
        inClinic.setCreatedAt(LocalDateTime.now());
        doctorRepository.save(inClinic);

        Clinic otherClinic = new Clinic();
        otherClinic.setSlug("clinic-" + UUID.randomUUID());
        otherClinic.setName("Other");
        otherClinic.setCreatedAt(LocalDateTime.now());
        otherClinic = clinicRepository.save(otherClinic);

        Doctor outClinic = new Doctor();
        outClinic.setClinic(otherClinic);
        outClinic.setFullName("Dr Out");
        outClinic.setSpecialty("Dermatology");
        outClinic.setEmail("out." + UUID.randomUUID() + "@test.com");
        outClinic.setActive(true);
        outClinic.setCreatedAt(LocalDateTime.now());
        doctorRepository.save(outClinic);

        mockMvc.perform(get("/api/v1/doctors").param("clinic", clinic.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is("Dr In")));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void getDoctor_withUnknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/doctors/{id}", 999999L))
                .andExpect(status().isNotFound());
    }
}
