package com.citamedica.backend.application;

import com.citamedica.backend.domain.model.Appointment;
import com.citamedica.backend.domain.model.AppointmentStatus;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.adapter.out.integration.notification.NotificationPort;
import com.citamedica.backend.adapter.out.integration.notification.SMSNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for managing notifications and scheduled reminders.
 * Sends appointment reminders 24 hours before scheduled appointments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final AppointmentRepository appointmentRepository;
    private final NotificationPort notificationPort;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Scheduled task that runs daily at 9:00 AM to send reminders for appointments
     * happening in the next 24 hours.
     */
    @Scheduled(cron = "0 0 9 * * *") // Every day at 9:00 AM
    public void sendAppointmentReminders24Hours() {
        log.info("Starting scheduled task: sending 24-hour appointment reminders");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        
        // Find appointments between 23 and 25 hours from now (to account for scheduling variance)
        LocalDateTime startWindow = now.plusHours(23);
        LocalDateTime endWindow = now.plusHours(25);
        
        List<Appointment> upcomingAppointments = appointmentRepository
            .findByStartAtBetween(startWindow, endWindow)
            .stream()
            .filter(apt -> apt.getStatus() == AppointmentStatus.SCHEDULED)
            .toList();
        
        log.info("Found {} appointments for reminder in the next 24 hours", upcomingAppointments.size());
        
        upcomingAppointments.forEach(this::sendAppointmentReminder);
        
        log.info("Completed sending 24-hour appointment reminders");
    }
    
    /**
     * Send a reminder SMS for a specific appointment
     * @param appointment The appointment to send reminder for
     */
    public void sendAppointmentReminder(Appointment appointment) {
        try {
            String patientPhone = appointment.getPatient().getPhone();
            String doctorName = appointment.getDoctor().getFullName();
            String appointmentTime = appointment.getStartAt().format(TIME_FORMATTER);
            String appointmentDate = appointment.getStartAt().format(DATE_FORMATTER);
            
            String message = String.format(
                "Recordatorio: Tiene una cita médica mañana %s a las %s con Dr. %s. " +
                "Si necesita cancelar o reprogramar, por favor contacte a la clínica.",
                appointmentDate,
                appointmentTime,
                doctorName
            );
            
            SMSNotification sms = SMSNotification.builder()
                .to(patientPhone)
                .message(message)
                .build();
            
            notificationPort.sendSMS(sms);
            
            log.info("Sent appointment reminder for appointment ID: {}, patient: {}", 
                appointment.getId(), 
                appointment.getPatient().getFullName());
                
        } catch (Exception e) {
            log.error("Failed to send appointment reminder for appointment ID: {}", 
                appointment.getId(), e);
        }
    }
    
    /**
     * Send immediate notification for appointment confirmation
     * @param appointment The newly created appointment
     */
    public void sendAppointmentConfirmation(Appointment appointment) {
        try {
            String patientPhone = appointment.getPatient().getPhone();
            String doctorName = appointment.getDoctor().getFullName();
            String appointmentTime = appointment.getStartAt().format(TIME_FORMATTER);
            String appointmentDate = appointment.getStartAt().format(DATE_FORMATTER);
            
            String message = String.format(
                "Confirmación de cita: Su cita con Dr. %s está programada para el %s a las %s. " +
                "Gracias por confiar en nosotros.",
                doctorName,
                appointmentDate,
                appointmentTime
            );
            
            SMSNotification sms = SMSNotification.builder()
                .to(patientPhone)
                .message(message)
                .build();
            
            notificationPort.sendSMS(sms);
            
            log.info("Sent appointment confirmation for appointment ID: {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send appointment confirmation for appointment ID: {}", 
                appointment.getId(), e);
        }
    }
    
    /**
     * Send cancellation notification
     * @param appointment The cancelled appointment
     */
    public void sendAppointmentCancellation(Appointment appointment) {
        try {
            String patientPhone = appointment.getPatient().getPhone();
            String doctorName = appointment.getDoctor().getFullName();
            String appointmentTime = appointment.getStartAt().format(TIME_FORMATTER);
            String appointmentDate = appointment.getStartAt().format(DATE_FORMATTER);
            
            String message = String.format(
                "Su cita con Dr. %s programada para el %s a las %s ha sido cancelada. " +
                "Para reprogramar, por favor contacte a la clínica.",
                doctorName,
                appointmentDate,
                appointmentTime
            );
            
            SMSNotification sms = SMSNotification.builder()
                .to(patientPhone)
                .message(message)
                .build();
            
            notificationPort.sendSMS(sms);
            
            log.info("Sent appointment cancellation for appointment ID: {}", appointment.getId());
            
        } catch (Exception e) {
            log.error("Failed to send appointment cancellation for appointment ID: {}", 
                appointment.getId(), e);
        }
    }
}