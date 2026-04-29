package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.notification.NotificationResponse;
import com.citamedica.backend.application.usecase.GetNotificationHistoryUseCase;
import com.citamedica.backend.domain.model.NotificationStatus;
import com.citamedica.backend.domain.model.NotificationType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients/{id}/notifications")
public class NotificationHistoryController {

    private final GetNotificationHistoryUseCase getNotificationHistoryUseCase;

    public NotificationHistoryController(GetNotificationHistoryUseCase getNotificationHistoryUseCase) {
        this.getNotificationHistoryUseCase = getNotificationHistoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> get(@PathVariable Long id,
                                                           @RequestParam(defaultValue = "50") int limit,
                                                           @RequestParam(defaultValue = "0") int offset,
                                                           @RequestParam(required = false) NotificationType type,
                                                           @RequestParam(required = false) NotificationStatus status) {
        var list = getNotificationHistoryUseCase.execute(id, type, status, limit, offset)
                .stream().map(NotificationResponse::from).toList();
        return ResponseEntity.ok(list);
    }
}
