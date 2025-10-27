package com.citamedica.backend.service;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppointmentService.
 * Tests appointment creation, retrieval, and status updates.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor testDoctor;
    private Patient testPatient;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setFullName("Dr. Test");
        testDoctor.setEmail("doctor@test.com");

        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setFullName("Test Patient");
        testPatient.setEmail("patient@test.com");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
        testAppointment.setType("Consultation");
        testAppointment.setStatus(AppointmentStatus.SCHEDULED);
        testAppointment.setStartAt(LocalDateTime.of(2024, 1, 15, 10, 0));
        testAppointment.setEndAt(LocalDateTime.of(2024, 1, 15, 11, 0));
    }

    @Test
    void findByDoctorIdAndDate_WithExistingAppointments_ReturnsAppointments() {
        // Arrange
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2024, 1, 15);
        List<Appointment> expectedAppointments = Arrays.asList(testAppointment);
        
        when(appointmentRepository.findByDoctorIdAndDate(doctorId, date))
                .thenReturn(expectedAppointments);

        // Act
        List<Appointment> result = appointmentService.findByDoctorIdAndDate(doctorId, date);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointment.getId(), result.get(0).getId());
        verify(appointmentRepository, times(1)).findByDoctorIdAndDate(doctorId, date);
    }

    @Test
    void findByDoctorIdAndDate_WithNoAppointments_ReturnsEmptyList() {
        // Arrange
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2024, 1, 15);
        
        when(appointmentRepository.findByDoctorIdAndDate(doctorId, date))
                .thenReturn(Arrays.asList());

        // Act
        List<Appointment> result = appointmentService.findByDoctorIdAndDate(doctorId, date);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentRepository, times(1)).findByDoctorIdAndDate(doctorId, date);
    }

    @Test
    void createAppointment_WithValidData_CreatesAndReturnsAppointment() {
        // Arrange
        Long doctorId = 1L;
        Long patientId = 1L;
        String type = "Consultation";
        LocalDateTime startAt = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime endAt = LocalDateTime.of(2024, 1, 15, 11, 0);
        
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        // Act
        Appointment result = appointmentService.createAppointment(doctorId, patientId, type, startAt, endAt);

        // Assert
        assertNotNull(result);
        assertEquals(type, result.getType());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
        assertEquals(startAt, result.getStartAt());
        assertEquals(endAt, result.getEndAt());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void createAppointment_SetsScheduledStatus() {
        // Arrange
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Appointment result = appointmentService.createAppointment(
                1L, 1L, "Consultation",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1)
        );

        // Assert
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());
    }

    @Test
    void updateAppointmentStatus_WithExistingAppointment_UpdatesStatus() {
        // Arrange
        Long appointmentId = 1L;
        AppointmentStatus newStatus = AppointmentStatus.COMPLETED;
        
        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(testAppointment);

        // Act
        Appointment result = appointmentService.updateAppointmentStatus(appointmentId, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        assertNotNull(result.getUpdatedAt());
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, times(1)).save(testAppointment);
    }

    @Test
    void updateAppointmentStatus_WithNonExistingAppointment_ThrowsException() {
        // Arrange
        Long appointmentId = 999L;
        AppointmentStatus newStatus = AppointmentStatus.COMPLETED;
        
        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            appointmentService.updateAppointmentStatus(appointmentId, newStatus);
        });
        
        verify(appointmentRepository, times(1)).findById(appointmentId);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void findByCalBookingId_WithExistingBooking_ReturnsAppointments() {
        // Arrange
        String calBookingId = "cal-booking-123";
        testAppointment.setCalBookingId(calBookingId);
        List<Appointment> expectedAppointments = Arrays.asList(testAppointment);
        
        when(appointmentRepository.findByCalBookingId(calBookingId))
                .thenReturn(expectedAppointments);

        // Act
        List<Appointment> result = appointmentService.findByCalBookingId(calBookingId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(calBookingId, result.get(0).getCalBookingId());
        verify(appointmentRepository, times(1)).findByCalBookingId(calBookingId);
    }

    @Test
    void findByCalBookingId_WithNonExistingBooking_ReturnsEmptyList() {
        // Arrange
        String calBookingId = "non-existing-booking";
        
        when(appointmentRepository.findByCalBookingId(calBookingId))
                .thenReturn(Arrays.asList());

        // Act
        List<Appointment> result = appointmentService.findByCalBookingId(calBookingId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentRepository, times(1)).findByCalBookingId(calBookingId);
    }

    @Test
    void updateAppointmentStatus_UpdatesTimestamp() {
        // Arrange
        Long appointmentId = 1L;
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        when(appointmentRepository.findById(appointmentId))
                .thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Appointment result = appointmentService.updateAppointmentStatus(appointmentId, AppointmentStatus.COMPLETED);

        // Assert
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(beforeUpdate) || result.getUpdatedAt().isEqual(beforeUpdate));
    }
}