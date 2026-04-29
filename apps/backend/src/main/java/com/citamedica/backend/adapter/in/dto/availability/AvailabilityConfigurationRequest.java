package com.citamedica.backend.adapter.in.dto.availability;

import com.citamedica.backend.domain.model.ScheduleDayOfWeek;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class AvailabilityConfigurationRequest {

    @NotNull
    private ScheduleDayOfWeek dayOfWeek;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    @Min(15)
    @Max(60)
    private Integer slotDurationMinutes;

    @Min(1)
    @Max(10)
    private Integer maxConcurrentAppointments = 1;

    public ScheduleDayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(ScheduleDayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(Integer slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public Integer getMaxConcurrentAppointments() {
        return maxConcurrentAppointments;
    }

    public void setMaxConcurrentAppointments(Integer maxConcurrentAppointments) {
        this.maxConcurrentAppointments = maxConcurrentAppointments;
    }
}
