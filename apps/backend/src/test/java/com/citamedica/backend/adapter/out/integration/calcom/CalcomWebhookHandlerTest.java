package com.citamedica.backend.adapter.out.integration.calcom;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.Clinic;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.application.AuditService;
import com.citamedica.backend.application.usecase.SyncCalComCalendarUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CalcomWebhookHandler.
 * Tests webhook event processing for booking creation, rescheduling, and cancellation.
 */
@ExtendWith(MockitoExtension.class)
class CalcomWebhookHandlerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private SyncCalComCalendarUseCase syncCalComCalendarUseCase;

    private CalcomWebhookHandler webhookHandler;

    private ObjectMapper objectMapper;
    private Doctor testDoctor;
    private Patient testPatient;
    private Clinic testClinic;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        webhookHandler = new CalcomWebhookHandler(
                objectMapper,
                appointmentRepository,
                doctorRepository,
                patientRepository,
                auditService,
                syncCalComCalendarUseCase
        );

        testClinic = new Clinic();
        testClinic.setId(1L);
        testClinic.setName("Test Clinic");

        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setFullName("Dr. Test");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setClinic(testClinic);

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFullName("Test Patient");
        testPatient.setEmail("patient@test.com");
        testPatient.setPhone("+34600000000");
    }

    @Test
    void handle_BookingCreated_CreatesNewAppointment() {
        // Arrange
        String payload = """
                {
                    "triggerEvent": "BOOKING_CREATED",
                    "payload": {
                        "uid": "booking-123",
                        "title": "Consultation",
                        "startTime": "2024-01-15T10:00:00Z",
                        "endTime": "2024-01-15T11:00:00Z",
                        "attendees": [{
                            "email": "patient@test.com",
                            "name": "Test Patient",
                            "phoneNumber": "+34600000000"
                        }],
                        "organizer": {
                            "email": "doctor@test.com"
                        }
                    }
                }
                """;

        when(patientRepository.findByEmail("patient@test.com"))
                .thenReturn(Optional.of(testPatient));
        when(doctorRepository.findByEmail("doctor@test.com"))
                .thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> {
                    Appointment apt = invocation.getArgument(0);
                    apt.setId(1L);
                    return apt;
                });

        // Act
        webhookHandler.handle(payload);

        // Assert
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(auditService, times(1)).logAction(
                eq("calcom"),
                eq("CREATE"),
                eq("Appointment"),
                eq(1L),
                anyString()
        );
    }

    @Test
    void handle_BookingCreated_CreatesNewPatientIfNotExists() {
        // Arrange
        String payload = """
                {
                    "triggerEvent": "BOOKING_CREATED",
                    "payload": {
                        "uid": "booking-123",
                        "title": "Consultation",
                        "startTime": "2024-01-15T10:00:00Z",
                        "endTime": "2024-01-15T11:00:00Z",
                        "attendees": [{
                            "email": "newpatient@test.com",
                            "name": "New Patient",
                            "phoneNumber": "+34600000001"
                        }],
                        "organizer": {
                            "email": "doctor@test.com"
                        }
                    }
                }
                """;

        when(patientRepository.findByEmail("newpatient@test.com"))
                .thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> {
                    Patient p = invocation.getArgument(0);
                    p.setId(2L);
                    return p;
                });
        when(doctorRepository.findByEmail("doctor@test.com"))
                .thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> {
                    Appointment apt = invocation.getArgument(0);
                    apt.setId(1L);
                    return apt;
                });

        // Act
        webhookHandler.handle(payload);

        // Assert
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void handle_BookingRescheduled_UpdatesAppointmentTimes() {
        // Arrange
        String calBookingId = "booking-123";
        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(1L);
        existingAppointment.setCalBookingId(calBookingId);
        existingAppointment.setDoctor(testDoctor);
        existingAppointment.setPatient(testPatient);
        existingAppointment.setStatus(AppointmentStatus.SCHEDULED);

        String payload = """
                {
                    "triggerEvent": "BOOKING_RESCHEDULED",
                    "payload": {
                        "uid": "booking-123",
                        "startTime": "2024-01-16T14:00:00Z",
                        "endTime": "2024-01-16T15:00:00Z"
                    }
                }
                """;

        when(appointmentRepository.findByCalBookingId(calBookingId))
                .thenReturn(Arrays.asList(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(existingAppointment);

        // Act
        webhookHandler.handle(payload);

        // Assert
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(auditService, times(1)).logAction(
                eq("calcom"),
                eq("UPDATE"),
                eq("Appointment"),
                eq(1L),
                anyString()
        );
        assertEquals(AppointmentStatus.RESCHEDULED, existingAppointment.getStatus());
        assertNotNull(existingAppointment.getUpdatedAt());
    }

    @Test
    void handle_BookingCanceled_UpdatesAppointmentStatus() {
        // Arrange
        String calBookingId = "booking-123";
        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(1L);
        existingAppointment.setCalBookingId(calBookingId);
        existingAppointment.setDoctor(testDoctor);
        existingAppointment.setPatient(testPatient);
        existingAppointment.setStatus(AppointmentStatus.SCHEDULED);

        String payload = """
                {
                    "triggerEvent": "BOOKING_CANCELLED",
                    "payload": {
                        "uid": "booking-123"
                    }
                }
                """;

        when(appointmentRepository.findByCalBookingId(calBookingId))
                .thenReturn(Arrays.asList(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(existingAppointment);

        // Act
        webhookHandler.handle(payload);

        // Assert
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(auditService, times(1)).logAction(
                eq("calcom"),
                eq("DELETE"),
                eq("Appointment"),
                eq(1L),
                anyString()
        );
        assertEquals(AppointmentStatus.CANCELED, existingAppointment.getStatus());
        assertNotNull(existingAppointment.getUpdatedAt());
    }

    @Test
    void handle_UnknownEvent_LogsWarning() {
        // Arrange
        String payload = """
                {
                    "triggerEvent": "UNKNOWN_EVENT",
                    "payload": {}
                }
                """;

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> webhookHandler.handle(payload));
        
        // Verify no repository interactions
        verify(appointmentRepository, never()).save(any(Appointment.class));
        verify(auditService, never()).logAction(anyString(), anyString(), anyString(), any(), anyString());
    }

    @Test
    void handle_BookingCreated_ThrowsExceptionWhenDoctorNotFound() {
        // Arrange
        String payload = """
                {
                    "triggerEvent": "BOOKING_CREATED",
                    "payload": {
                        "uid": "booking-123",
                        "title": "Consultation",
                        "startTime": "2024-01-15T10:00:00Z",
                        "endTime": "2024-01-15T11:00:00Z",
                        "attendees": [{
                            "email": "patient@test.com",
                            "name": "Test Patient"
                        }],
                        "organizer": {
                            "email": "nonexistent@test.com"
                        }
                    }
                }
                """;

        when(patientRepository.findByEmail("patient@test.com"))
                .thenReturn(Optional.of(testPatient));
        when(doctorRepository.findByEmail("nonexistent@test.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> webhookHandler.handle(payload));
        
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void handle_BookingRescheduled_HandlesNonExistentAppointment() {
        // Arrange
        String payload = """
                {
                    "triggerEvent": "BOOKING_RESCHEDULED",
                    "payload": {
                        "uid": "nonexistent-booking",
                        "startTime": "2024-01-16T14:00:00Z",
                        "endTime": "2024-01-16T15:00:00Z"
                    }
                }
                """;

        when(appointmentRepository.findByCalBookingId("nonexistent-booking"))
                .thenReturn(Arrays.asList());

        // Act - should not throw exception
        assertDoesNotThrow(() -> webhookHandler.handle(payload));
        
        // Verify no save operation
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void handle_InvalidJson_ThrowsException() {
        // Arrange
        String invalidPayload = "{ invalid json }";

        // Act & Assert
        assertThrows(RuntimeException.class, () -> webhookHandler.handle(invalidPayload));
    }
}