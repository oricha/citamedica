package com.citamedica.backend.adapter.in.dto.availability;

import com.citamedica.backend.domain.model.DoctorAvailabilityBlock;

import java.time.LocalDateTime;

public class AvailabilityBlockResponse {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String blockType;
    private String recurrenceRule;

    public static AvailabilityBlockResponse from(DoctorAvailabilityBlock b) {
        AvailabilityBlockResponse r = new AvailabilityBlockResponse();
        r.id = b.getId();
        r.startTime = b.getStartTime();
        r.endTime = b.getEndTime();
        r.blockType = b.getBlockType().name();
        r.recurrenceRule = b.getRecurrenceRule();
        return r;
    }

    public Long getId() { return id; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getBlockType() { return blockType; }
    public String getRecurrenceRule() { return recurrenceRule; }
}
