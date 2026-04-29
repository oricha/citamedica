package com.citamedica.backend.adapter.in.dto.availability;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalTime;

public class AvailabilityConfigurationPatchRequest {

    private LocalTime startTime;
    private LocalTime endTime;

    @Min(15)
    @Max(60)
    private Integer slotDurationMinutes;

    @Min(1)
    private Integer maxConcurrentAppointments;

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Integer getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(Integer slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }
    public Integer getMaxConcurrentAppointments() { return maxConcurrentAppointments; }
    public void setMaxConcurrentAppointments(Integer maxConcurrentAppointments) {
        this.maxConcurrentAppointments = maxConcurrentAppointments;
    }
}
