package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.application.usecase.RetryFailedNotificationsUseCase;
import com.citamedica.backend.domain.model.NotificationStatus;
import com.citamedica.backend.domain.repository.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/notifications")
public class AdminNotificationController {

    private final NotificationRepository notificationRepository;
    private final RetryFailedNotificationsUseCase retryUseCase;

    public AdminNotificationController(NotificationRepository notificationRepository,
                                       RetryFailedNotificationsUseCase retryUseCase) {
        this.notificationRepository = notificationRepository;
        this.retryUseCase = retryUseCase;
    }

    @PostMapping("/{id}/resend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resend(@PathVariable Long id) {
        var notification = notificationRepository.findById(id).orElseThrow();
        notification.setStatus(NotificationStatus.RETRYING);
        notificationRepository.save(notification);
        int processed = retryUseCase.execute();

        Map<String, Object> response = new HashMap<>();
        response.put("notificationId", id);
        response.put("processed", processed);
        response.put("status", notification.getStatus().name());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> stats() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Stats available in notification_stats DB view");
        return ResponseEntity.ok(result);
    }
}
