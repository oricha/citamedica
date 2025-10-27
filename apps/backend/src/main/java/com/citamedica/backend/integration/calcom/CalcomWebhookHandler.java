package com.citamedica.backend.integration.calcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.service.AuditService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Service
public class CalcomWebhookHandler {

    private static final Logger logger = LoggerFactory.getLogger(CalcomWebhookHandler.class);

    private final ObjectMapper objectMapper;
    private final AppointmentRepository appointmentRepository;
    private final AuditService auditService;

    public CalcomWebhookHandler(ObjectMapper objectMapper, AppointmentRepository appointmentRepository, AuditService auditService) {
        this.objectMapper = objectMapper;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
    }

    public void handle(String payload) {
        try {
            WebhookEvent event = objectMapper.readValue(payload, WebhookEvent.class);
            String triggerEvent = event.getTriggerEvent();

            switch (triggerEvent) {
                case "booking.created":
                    handleBookingCreated(event.getPayload());
                    break;
                case "booking.rescheduled":
                    handleBookingRescheduled(event.getPayload());
                    break;
                case "booking.canceled":
                    handleBookingCanceled(event.getPayload());
                    break;
                default:
                    logger.warn("Unknown trigger event: {}", triggerEvent);
            }
        } catch (Exception e) {
            logger.error("Error handling webhook: {}", e.getMessage());
            throw new RuntimeException("Error processing webhook", e);
        }
    }

    private void handleBookingCreated(Object payload) {
        // Parse payload and create appointment
        logger.info("Handling booking.created");
        // TODO: Extract details and save to DB
        auditService.logAction("calcom", "CREATE", "appointment", null, payload.toString());
    }

    private void handleBookingRescheduled(Object payload) {
        logger.info("Handling booking.rescheduled");
        // TODO: Update appointment status
        auditService.logAction("calcom", "UPDATE", "appointment", null, payload.toString());
    }

    private void handleBookingCanceled(Object payload) {
        logger.info("Handling booking.canceled");
        // TODO: Update appointment status
        auditService.logAction("calcom", "DELETE", "appointment", null, payload.toString());
    }
}