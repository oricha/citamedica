package com.citamedica.backend.adapter.out.persistence;

import com.citamedica.backend.adapter.out.persistence.jpa.NotificationJpaRepository;
import com.citamedica.backend.domain.model.Notification;
import com.citamedica.backend.domain.model.NotificationStatus;
import com.citamedica.backend.domain.model.NotificationType;
import com.citamedica.backend.domain.repository.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpa;

    public NotificationRepositoryAdapter(NotificationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Notification save(Notification notification) { return jpa.save(notification); }

    @Override
    public Optional<Notification> findById(Long id) { return jpa.findById(id); }

    @Override
    public List<Notification> findByPatientIdOrderByCreatedAtDesc(Long patientId) {
        return jpa.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Override
    public List<Notification> findByPatientIdAndNotificationTypeOrderByCreatedAtDesc(Long patientId, NotificationType type) {
        return jpa.findByPatientIdAndNotificationTypeOrderByCreatedAtDesc(patientId, type);
    }

    @Override
    public List<Notification> findByPatientIdAndStatusOrderByCreatedAtDesc(Long patientId, NotificationStatus status) {
        return jpa.findByPatientIdAndStatusOrderByCreatedAtDesc(patientId, status);
    }

    @Override
    public List<Notification> findByStatusAndNextRetryAtBefore(NotificationStatus status, LocalDateTime at) {
        return jpa.findByStatusAndNextRetryAtBefore(status, at);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }
}
