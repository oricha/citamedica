package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Notification;
import com.citamedica.backend.domain.model.NotificationStatus;
import com.citamedica.backend.domain.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RetryFailedNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    public RetryFailedNotificationsUseCase(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public int execute() {
        var retryables = notificationRepository.findByStatusAndNextRetryAtBefore(NotificationStatus.RETRYING, LocalDateTime.now());
        int processed = 0;
        for (Notification item : retryables) {
            if (item.getAttemptCount() >= 3) {
                item.setStatus(NotificationStatus.PERMANENTLY_FAILED);
            } else {
                item.setAttemptCount(item.getAttemptCount() + 1);
                item.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
            }
            notificationRepository.save(item);
            processed++;
        }
        return processed;
    }
}
