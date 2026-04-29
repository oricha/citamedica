package com.citamedica.backend.adapter.out.persistence.jpa;

import com.citamedica.backend.domain.model.Notification;
import com.citamedica.backend.domain.model.NotificationStatus;
import com.citamedica.backend.domain.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<Notification> findByPatientIdAndNotificationTypeOrderByCreatedAtDesc(Long patientId, NotificationType notificationType);
    List<Notification> findByPatientIdAndStatusOrderByCreatedAtDesc(Long patientId, NotificationStatus status);
    List<Notification> findByStatusAndNextRetryAtBefore(NotificationStatus status, LocalDateTime at);
}
