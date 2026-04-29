package com.citamedica.backend.application.usecase;

import com.citamedica.backend.adapter.out.integration.notification.EmailNotification;
import com.citamedica.backend.adapter.out.integration.notification.NotificationPort;
import com.citamedica.backend.adapter.out.integration.notification.SMSNotification;
import com.citamedica.backend.domain.model.*;
import com.citamedica.backend.domain.repository.NotificationPreferenceRepository;
import com.citamedica.backend.domain.repository.NotificationRepository;
import com.citamedica.backend.domain.service.AppointmentNotificationDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationOrchestrator {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final AppointmentNotificationDomainService domainService;
    private final NotificationPort notificationPort;

    public NotificationOrchestrator(
            NotificationRepository notificationRepository,
            NotificationPreferenceRepository preferenceRepository,
            AppointmentNotificationDomainService domainService,
            NotificationPort notificationPort) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.domainService = domainService;
        this.notificationPort = notificationPort;
    }

    @Transactional
    public void notifyAppointmentEvent(Appointment appointment, NotificationType type) {
        Patient patient = appointment.getPatient();
        NotificationPreference preference = preferenceRepository.findByPatientId(patient.getId())
                .orElseGet(() -> createDefaultPreference(patient));

        if (!domainService.canNotifyPatient(patient, preference, type)) {
            return;
        }

        List<NotificationChannel> channels = domainService.determineChannels(patient, preference);
        var message = domainService.composeMessage(type, appointment, patient.getLanguagePreference());

        for (NotificationChannel channel : channels) {
            Notification log = new Notification();
            log.setAppointment(appointment);
            log.setPatient(patient);
            log.setNotificationType(type);
            log.setChannel(channel);
            log.setMessageContent(channel == NotificationChannel.EMAIL ? message.emailBody() : message.smsBody());
            log.setAttemptCount(0);
            log.setCreatedAt(LocalDateTime.now());

            try {
                if (channel == NotificationChannel.EMAIL) {
                    log.setRecipient(patient.getEmail());
                    notificationPort.sendEmail(EmailNotification.builder()
                            .to(patient.getEmail())
                            .subject(message.subject())
                            .body(message.emailBody())
                            .from("no-reply@citamedica.com")
                            .build());
                } else {
                    log.setRecipient(preference.getPhone() != null ? preference.getPhone() : patient.getPhone());
                    notificationPort.sendSMS(SMSNotification.builder()
                            .to(log.getRecipient())
                            .message(message.smsBody())
                            .from("CitaMedica")
                            .build());
                }
                log.setStatus(NotificationStatus.SENT);
                log.setDeliveredAt(LocalDateTime.now());
                log.setProviderMessageId(UUID.randomUUID().toString());
            } catch (Exception ex) {
                log.setStatus(NotificationStatus.RETRYING);
                log.setErrorMessage(ex.getMessage());
                log.setAttemptCount(log.getAttemptCount() + 1);
                log.setNextRetryAt(domainService.computeNextRetryAt(log.getAttemptCount(), LocalDateTime.now()));
            }
            notificationRepository.save(log);
        }
    }

    private NotificationPreference createDefaultPreference(Patient patient) {
        NotificationPreference preference = new NotificationPreference();
        preference.setPatient(patient);
        preference.setEmailEnabled(true);
        preference.setSmsEnabled(false);
        preference.setPhone(patient.getPhone());
        preference.setConsentMethod("default");
        preference.setConsentTimestamp(LocalDateTime.now());
        return preferenceRepository.save(preference);
    }
}
