package com.citamedica.backend.adapter.in.dto.availability;

import com.citamedica.backend.domain.model.AvailabilitySyncLog;

import java.time.LocalDateTime;

public class AvailabilitySyncStatusResponse {

    private LocalDateTime lastSyncAt;
    private String status;
    private int eventsFetched;
    private int conflictsFound;
    private Integer syncDurationSeconds;
    private String errorMessage;

    public static AvailabilitySyncStatusResponse from(AvailabilitySyncLog log) {
        AvailabilitySyncStatusResponse r = new AvailabilitySyncStatusResponse();
        r.lastSyncAt = log.getSyncTimestamp();
        r.status = log.getStatus().name();
        r.eventsFetched = log.getEventsFetched();
        r.conflictsFound = log.getConflictsFound();
        r.syncDurationSeconds = log.getSyncDurationSeconds();
        r.errorMessage = log.getErrorMessage();
        return r;
    }

    public LocalDateTime getLastSyncAt() { return lastSyncAt; }
    public String getStatus() { return status; }
    public int getEventsFetched() { return eventsFetched; }
    public int getConflictsFound() { return conflictsFound; }
    public Integer getSyncDurationSeconds() { return syncDurationSeconds; }
    public String getErrorMessage() { return errorMessage; }
}
