package com.citamedica.backend.exception.domain;

public class ConflictingAppointmentException extends AvailabilityException {

    public ConflictingAppointmentException(String message) {
        super(message);
    }
}
