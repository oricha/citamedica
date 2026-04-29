package com.citamedica.backend.domain.repository;

import com.citamedica.backend.domain.model.Notification;
import com.citamedica.backend.domain.model.NotificationStatus;
import com.citamedica.backend.domain.model.NotificationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(Long id);
    List<Notification> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<Notification> findByPatientIdAndNotificationTypeOrderByCreatedAtDesc(Long patientId, NotificationType type);
    List<Notification> findByPatientIdAndStatusOrderByCreatedAtDesc(Long patientId, NotificationStatus status);
    List<Notification> findByStatusAndNextRetryAtBefore(NotificationStatus status, LocalDateTime at);
    void deleteAll();
}
