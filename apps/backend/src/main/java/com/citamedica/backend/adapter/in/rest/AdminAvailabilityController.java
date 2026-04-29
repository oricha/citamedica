package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.availability.AvailabilitySyncStatusResponse;
import com.citamedica.backend.application.usecase.SyncCalComCalendarUseCase;
import com.citamedica.backend.domain.repository.AvailabilitySyncLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/availability")
public class AdminAvailabilityController {

    private final AvailabilitySyncLogRepository availabilitySyncLogRepository;
    private final SyncCalComCalendarUseCase syncCalComCalendarUseCase;

    public AdminAvailabilityController(
            AvailabilitySyncLogRepository availabilitySyncLogRepository,
            SyncCalComCalendarUseCase syncCalComCalendarUseCase) {
        this.availabilitySyncLogRepository = availabilitySyncLogRepository;
        this.syncCalComCalendarUseCase = syncCalComCalendarUseCase;
    }

    @GetMapping("/sync-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AvailabilitySyncStatusResponse> syncStatus() {
        return availabilitySyncLogRepository.findFirstByOrderBySyncTimestampDesc()
                .map(AvailabilitySyncStatusResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> stats() {
        Map<String, Object> m = new HashMap<>();
        availabilitySyncLogRepository.findFirstByOrderBySyncTimestampDesc()
                .ifPresent(log -> m.put("latest", AvailabilitySyncStatusResponse.from(log)));
        m.put("recent", availabilitySyncLogRepository.findTop10ByOrderBySyncTimestampDesc().stream()
                .map(AvailabilitySyncStatusResponse::from)
                .collect(Collectors.toList()));
        return ResponseEntity.ok(m);
    }

    @PostMapping("/sync-now")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> syncNow(@RequestParam(required = false) Long doctorId) {
        syncCalComCalendarUseCase.execute(doctorId);
        return ResponseEntity.accepted().build();
    }
}
