package com.citamedica.backend.adapter.out.integration.calcom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.model.Doctor;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.DoctorRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.application.AuditService;
import com.citamedica.backend.application.usecase.SyncCalComCalendarUseCase;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling Cal.com webhook events.
 * Processes booking creation, rescheduling, and cancellation events.
 */
@Service
@Transactional
public class CalcomWebhookHandler {

    private static final Logger logger = LoggerFactory.getLogger(CalcomWebhookHandler.class);

    private final ObjectMapper objectMapper;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AuditService auditService;
    private final SyncCalComCalendarUseCase syncCalComCalendarUseCase;

    public CalcomWebhookHandler(
            ObjectMapper objectMapper,
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            AuditService auditService,
            @Lazy SyncCalComCalendarUseCase syncCalComCalendarUseCase) {
        this.objectMapper = objectMapper;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.auditService = auditService;
        this.syncCalComCalendarUseCase = syncCalComCalendarUseCase;
    }

    /**
     * Main handler for webhook events
     */
    public void handle(String payload) {
        try {
            WebhookEvent event = objectMapper.readValue(payload, WebhookEvent.class);
            String triggerEvent = event.getTriggerEvent();
            
            String correlationId = MDC.get("correlationId");
            logger.info("Processing webhook event: {} - correlationId: {}", triggerEvent, correlationId);

            switch (triggerEvent) {
                case "booking.created":
                case "BOOKING_CREATED":
                    handleBookingCreated(event.getPayload());
                    break;
                case "booking.rescheduled":
                case "BOOKING_RESCHEDULED":
                    handleBookingRescheduled(event.getPayload());
                    break;
                case "booking.cancelled":
                case "booking.canceled":
                case "BOOKING_CANCELLED":
                    handleBookingCanceled(event.getPayload());
                    break;
                default:
                    logger.warn("Unknown trigger event: {} - correlationId: {}", triggerEvent, correlationId);
            }
        } catch (Exception e) {
            logger.error("Error handling webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing webhook", e);
        }
    }

    /**
     * Handles booking.created event
     * Creates a new appointment and patient if needed
     */
    private void handleBookingCreated(Object payloadObj) {
        try {
            JsonNode payload = objectMapper.valueToTree(payloadObj);
            String calBookingId = extractString(payload, "uid");
            
            // Add calBookingId to MDC for logging
            MDC.put("calBookingId", calBookingId);
            logger.info("Handling booking.created for calBookingId: {}", calBookingId);

            // Extract booking details
            String startTime = extractString(payload, "startTime");
            String endTime = extractString(payload, "endTime");
            String title = extractString(payload, "title");
            
            // Extract attendee (patient) information
            JsonNode attendees = payload.get("attendees");
            if (attendees == null || !attendees.isArray() || attendees.size() == 0) {
                logger.error("No attendees found in booking");
                return;
            }
            
            JsonNode attendee = attendees.get(0);
            String patientEmail = extractString(attendee, "email");
            String patientName = extractString(attendee, "name");
            String patientPhone = extractString(attendee, "phoneNumber");
            
            // Extract organizer (doctor) information
            JsonNode organizer = payload.get("organizer");
            String doctorEmail = extractString(organizer, "email");
            
            // Find or create patient
            Patient patient = findOrCreatePatient(patientName, patientEmail, patientPhone);
            
            // Find doctor by email
            Doctor doctor = doctorRepository.findByEmail(doctorEmail)
                    .orElseThrow(() -> new RuntimeException("Doctor not found with email: " + doctorEmail));
            
            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setCalBookingId(calBookingId);
            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            appointment.setClinic(doctor.getClinic());
            appointment.setType(title != null ? title : "Consultation");
            appointment.setStatus(AppointmentStatus.SCHEDULED);
            appointment.setStartAt(parseDateTime(startTime));
            appointment.setEndAt(parseDateTime(endTime));
            appointment.setCreatedAt(LocalDateTime.now());
            appointment.setUpdatedAt(LocalDateTime.now());
            
            appointmentRepository.save(appointment);

            triggerAvailabilitySync(doctor.getId());

            // Audit log
            auditService.logAction("calcom", "CREATE", "Appointment", appointment.getId(),
                    String.format("Created from Cal.com booking %s", calBookingId));
            
            logger.info("Successfully created appointment from Cal.com booking: {}", calBookingId);
            
        } catch (Exception e) {
            logger.error("Error handling booking.created: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating appointment", e);
        } finally {
            MDC.remove("calBookingId");
        }
    }

    /**
     * Handles booking.rescheduled event
     * Updates the appointment times
     */
    private void handleBookingRescheduled(Object payloadObj) {
        try {
            JsonNode payload = objectMapper.valueToTree(payloadObj);
            String calBookingId = extractString(payload, "uid");
            
            MDC.put("calBookingId", calBookingId);
            logger.info("Handling booking.rescheduled for calBookingId: {}", calBookingId);

            // Find existing appointment
            List<Appointment> appointments = appointmentRepository.findByCalBookingId(calBookingId);
            if (appointments.isEmpty()) {
                logger.warn("Appointment not found for calBookingId: {}", calBookingId);
                return;
            }
            
            Appointment appointment = appointments.get(0);
            
            // Update times
            String startTime = extractString(payload, "startTime");
            String endTime = extractString(payload, "endTime");
            
            appointment.setStartAt(parseDateTime(startTime));
            appointment.setEndAt(parseDateTime(endTime));
            appointment.setStatus(AppointmentStatus.RESCHEDULED);
            appointment.setUpdatedAt(LocalDateTime.now());
            
            appointmentRepository.save(appointment);

            triggerAvailabilitySync(appointment.getDoctor().getId());

            // Audit log
            auditService.logAction("calcom", "UPDATE", "Appointment", appointment.getId(),
                    String.format("Rescheduled Cal.com booking %s", calBookingId));
            
            logger.info("Successfully rescheduled appointment: {}", calBookingId);
            
        } catch (Exception e) {
            logger.error("Error handling booking.rescheduled: {}", e.getMessage(), e);
            throw new RuntimeException("Error rescheduling appointment", e);
        } finally {
            MDC.remove("calBookingId");
        }
    }

    /**
     * Handles booking.canceled event
     * Updates the appointment status to CANCELED
     */
    private void handleBookingCanceled(Object payloadObj) {
        try {
            JsonNode payload = objectMapper.valueToTree(payloadObj);
            String calBookingId = extractString(payload, "uid");
            
            MDC.put("calBookingId", calBookingId);
            logger.info("Handling booking.canceled for calBookingId: {}", calBookingId);

            // Find existing appointment
            List<Appointment> appointments = appointmentRepository.findByCalBookingId(calBookingId);
            if (appointments.isEmpty()) {
                logger.warn("Appointment not found for calBookingId: {}", calBookingId);
                return;
            }
            
            Appointment appointment = appointments.get(0);
            appointment.setStatus(AppointmentStatus.CANCELED);
            appointment.setUpdatedAt(LocalDateTime.now());
            
            appointmentRepository.save(appointment);

            triggerAvailabilitySync(appointment.getDoctor().getId());

            // Audit log
            auditService.logAction("calcom", "DELETE", "Appointment", appointment.getId(),
                    String.format("Canceled Cal.com booking %s", calBookingId));
            
            logger.info("Successfully canceled appointment: {}", calBookingId);
            
        } catch (Exception e) {
            logger.error("Error handling booking.canceled: {}", e.getMessage(), e);
            throw new RuntimeException("Error canceling appointment", e);
        } finally {
            MDC.remove("calBookingId");
        }
    }

    /**
     * Finds an existing patient or creates a new one
     */
    private Patient findOrCreatePatient(String name, String email, String phone) {
        Optional<Patient> existingPatient = patientRepository.findByEmail(email);
        
        if (existingPatient.isPresent()) {
            logger.debug("Found existing patient with email: {}", email);
            return existingPatient.get();
        }
        
        // Create new patient
        Patient patient = new Patient();
        patient.setFullName(name != null ? name : "Unknown");
        patient.setEmail(email);
        patient.setPhone(phone != null ? phone : "N/A");
        patient.setCreatedAt(LocalDateTime.now());
        
        patient = patientRepository.save(patient);
        logger.info("Created new patient with email: {}", email);
        
        return patient;
    }

    private void triggerAvailabilitySync(Long doctorId) {
        try {
            syncCalComCalendarUseCase.execute(doctorId);
        } catch (Exception ex) {
            logger.warn("Availability sync after Cal.com webhook failed: {}", ex.getMessage());
        }
    }

    /**
     * Extracts a string value from JSON node
     */
    private String extractString(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        JsonNode field = node.get(fieldName);
        return field.isNull() ? null : field.asText();
    }

    /**
     * Parses ISO 8601 datetime string to LocalDateTime
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null) {
            return LocalDateTime.now();
        }
        try {
            Instant instant = Instant.parse(dateTimeStr);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (Exception e) {
            logger.warn("Error parsing datetime: {}, using current time", dateTimeStr);
            return LocalDateTime.now();
        }
    }
}