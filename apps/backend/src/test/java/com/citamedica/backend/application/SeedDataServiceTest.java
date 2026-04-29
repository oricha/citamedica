package com.citamedica.backend.application;

import com.citamedica.backend.domain.model.*;
import com.citamedica.backend.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeedDataServiceTest {

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private SeedDataService seedDataService;

    private Clinic mockClinic;
    private Doctor mockDoctor;
    private Patient mockPatient;
    private Appointment mockAppointment;

    @BeforeEach
    void setUp() {
        mockClinic = new Clinic("clinica-demo", "Clínica Demo CitaMedica");
        mockClinic.setId(1L);

        mockDoctor = new Doctor(mockClinic, "Dr. Test", "Test Specialty", "test@test.com");
        mockDoctor.setId(1L);

        mockPatient = new Patient("Test Patient", "patient@test.com", "+34 600000000");
        mockPatient.setId(1L);

        mockAppointment = new Appointment();
        mockAppointment.setId(1L);
    }

    @Test
    void testRun_WhenNoDataExists_ShouldCreateSeedData() throws Exception {
        // Given
        when(clinicRepository.findBySlug("clinica-demo")).thenReturn(Optional.empty());
        when(clinicRepository.save(any(Clinic.class))).thenReturn(mockClinic);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockDoctor);
        when(patientRepository.save(any(Patient.class))).thenReturn(mockPatient);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(mockAppointment);

        // When
        seedDataService.run();

        // Then
        verify(clinicRepository, times(1)).findBySlug("clinica-demo");
        verify(clinicRepository, times(1)).save(any(Clinic.class));
        verify(doctorRepository, times(2)).save(any(Doctor.class)); // 2 doctors
        verify(patientRepository, times(3)).save(any(Patient.class)); // 3 patients
        verify(appointmentRepository, times(4)).save(any(Appointment.class)); // 4 appointments
    }

    @Test
    void testRun_WhenDataAlreadyExists_ShouldSkipSeeding() throws Exception {
        // Given
        when(clinicRepository.findBySlug("clinica-demo")).thenReturn(Optional.of(mockClinic));

        // When
        seedDataService.run();

        // Then
        verify(clinicRepository, times(1)).findBySlug("clinica-demo");
        verify(clinicRepository, never()).save(any(Clinic.class));
        verify(doctorRepository, never()).save(any(Doctor.class));
        verify(patientRepository, never()).save(any(Patient.class));
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testRun_WhenExceptionOccurs_ShouldThrowRuntimeException() {
        // Given
        when(clinicRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        when(clinicRepository.save(any(Clinic.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            seedDataService.run();
        } catch (RuntimeException e) {
            verify(clinicRepository, times(1)).findBySlug("clinica-demo");
            verify(clinicRepository, times(1)).save(any(Clinic.class));
            // Verify that no other saves were attempted after the error
            verify(doctorRepository, never()).save(any(Doctor.class));
            verify(patientRepository, never()).save(any(Patient.class));
            verify(appointmentRepository, never()).save(any(Appointment.class));
        }
    }
}