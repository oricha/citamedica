package com.citamedica.backend.adapter.in.dto.availability;

import com.citamedica.backend.domain.model.TimeSlot;

import java.time.LocalDateTime;

public class TimeSlotResponse {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public static TimeSlotResponse from(TimeSlot s) {
        TimeSlotResponse r = new TimeSlotResponse();
        r.id = s.getId();
        r.startTime = s.getStartTime();
        r.endTime = s.getEndTime();
        r.status = s.getStatus().name();
        return r;
    }

    public Long getId() { return id; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getStatus() { return status; }
}
