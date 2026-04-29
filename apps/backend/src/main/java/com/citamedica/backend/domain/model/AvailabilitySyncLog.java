package com.citamedica.backend.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "availability_sync_log")
public class AvailabilitySyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column(name = "sync_timestamp", nullable = false)
    private LocalDateTime syncTimestamp;

    @Column(name = "events_fetched", nullable = false)
    private int eventsFetched;

    @Column(name = "conflicts_found", nullable = false)
    private int conflictsFound;

    @Column(name = "sync_duration_seconds")
    private Integer syncDurationSeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AvailabilitySyncStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    public AvailabilitySyncLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public LocalDateTime getSyncTimestamp() { return syncTimestamp; }
    public void setSyncTimestamp(LocalDateTime syncTimestamp) { this.syncTimestamp = syncTimestamp; }
    public int getEventsFetched() { return eventsFetched; }
    public void setEventsFetched(int eventsFetched) { this.eventsFetched = eventsFetched; }
    public int getConflictsFound() { return conflictsFound; }
    public void setConflictsFound(int conflictsFound) { this.conflictsFound = conflictsFound; }
    public Integer getSyncDurationSeconds() { return syncDurationSeconds; }
    public void setSyncDurationSeconds(Integer syncDurationSeconds) { this.syncDurationSeconds = syncDurationSeconds; }
    public AvailabilitySyncStatus getStatus() { return status; }
    public void setStatus(AvailabilitySyncStatus status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
