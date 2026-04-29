package com.citamedica.backend.adapter.in.dto.availability;

import com.citamedica.backend.domain.model.DoctorAvailabilityConfiguration;

import java.time.LocalTime;

public class AvailabilityConfigurationResponse {

    private Long id;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDurationMinutes;
    private int maxConcurrentAppointments;

    public static AvailabilityConfigurationResponse from(DoctorAvailabilityConfiguration c) {
        AvailabilityConfigurationResponse r = new AvailabilityConfigurationResponse();
        r.id = c.getId();
        r.dayOfWeek = c.getDayOfWeek().name();
        r.startTime = c.getStartTime();
        r.endTime = c.getEndTime();
        r.slotDurationMinutes = c.getSlotDurationMinutes();
        r.maxConcurrentAppointments = c.getMaxConcurrentAppointments();
        return r;
    }

    public Long getId() { return id; }
    public String getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public int getMaxConcurrentAppointments() { return maxConcurrentAppointments; }
}
