package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Notification;
import com.citamedica.backend.domain.model.NotificationStatus;
import com.citamedica.backend.domain.model.NotificationType;
import com.citamedica.backend.domain.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetNotificationHistoryUseCase {

    private final NotificationRepository notificationRepository;

    public GetNotificationHistoryUseCase(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> execute(Long patientId, NotificationType type, NotificationStatus status, int limit, int offset) {
        List<Notification> base;
        if (type != null) {
            base = notificationRepository.findByPatientIdAndNotificationTypeOrderByCreatedAtDesc(patientId, type);
        } else if (status != null) {
            base = notificationRepository.findByPatientIdAndStatusOrderByCreatedAtDesc(patientId, status);
        } else {
            base = notificationRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        }

        int from = Math.min(offset, base.size());
        int to = Math.min(from + limit, base.size());
        return base.subList(from, to);
    }
}
