package com.citamedica.backend.domain.service;

import com.citamedica.backend.domain.model.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentNotificationDomainService {

    public record ComposedMessage(String subject, String emailBody, String smsBody) {}

    public boolean canNotifyPatient(Patient patient, NotificationPreference preference, NotificationType type) {
        if (type == NotificationType.REMINDER && appointmentInPastGuard(patient)) {
            return false;
        }
        return (patient.canReceiveNotification(NotificationChannel.EMAIL, preference)
                || patient.canReceiveNotification(NotificationChannel.SMS, preference));
    }

    private boolean appointmentInPastGuard(Patient patient) {
        return patient == null;
    }

    public List<NotificationChannel> determineChannels(Patient patient, NotificationPreference preference) {
        List<NotificationChannel> channels = new ArrayList<>();
        if (patient.canReceiveNotification(NotificationChannel.EMAIL, preference)) {
            channels.add(NotificationChannel.EMAIL);
        }
        if (patient.canReceiveNotification(NotificationChannel.SMS, preference)) {
            channels.add(NotificationChannel.SMS);
        }
        return channels;
    }

    public ComposedMessage composeMessage(NotificationType type, Appointment appointment, String language) {
        String lang = (language == null || language.isBlank()) ? "es" : language;
        String date = appointment.getStartAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String doctor = appointment.getDoctor().getFullName();
        String clinic = appointment.getClinic() != null ? appointment.getClinic().getName() : "CitaMedica";

        return switch (type) {
            case CONFIRMATION -> lang.startsWith("pt")
                    ? new ComposedMessage("Confirmacao de consulta",
                    "Sua consulta foi confirmada para " + date + " com " + doctor + " em " + clinic,
                    "Consulta confirmada: " + date + " com " + doctor)
                    : new ComposedMessage("Confirmacion de cita",
                    "Tu cita fue confirmada para " + date + " con " + doctor + " en " + clinic,
                    "Cita confirmada: " + date + " con " + doctor);
            case REMINDER -> lang.startsWith("pt")
                    ? new ComposedMessage("Lembrete de consulta",
                    "Lembrete: sua consulta e amanha " + date + " com " + doctor,
                    "Lembrete: consulta amanha " + date)
                    : new ComposedMessage("Recordatorio de cita",
                    "Recordatorio: tu cita es en 24h, " + date + " con " + doctor,
                    "Recordatorio: cita en 24h " + date);
            case CHANGE -> lang.startsWith("pt")
                    ? new ComposedMessage("Atualizacao da consulta",
                    "Sua consulta foi alterada. Novo horario: " + date + " com " + doctor,
                    "Consulta alterada: " + date + " con " + doctor)
                    : new ComposedMessage("Actualizacion de cita",
                    "Tu cita fue actualizada. Nuevo horario: " + date + " con " + doctor,
                    "Cita actualizada: " + date + " con " + doctor);
            case OTHER -> new ComposedMessage("Notificacion", "Tienes una notificacion", "Notificacion");
        };
    }

    public java.time.LocalDateTime computeNextRetryAt(int attemptCount, java.time.LocalDateTime now) {
        if (attemptCount <= 0) return now.plusMinutes(1);
        if (attemptCount == 1) return now.plusMinutes(10);
        return now.plusHours(2);
    }
}
