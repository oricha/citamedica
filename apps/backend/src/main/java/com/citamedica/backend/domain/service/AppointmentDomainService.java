package com.citamedica.backend.domain.service;

import com.citamedica.backend.exception.domain.InvalidDomainOperationException;

import java.time.LocalDateTime;

public class AppointmentDomainService {

    public void validateTimes(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null) {
            throw new InvalidDomainOperationException("startAt and endAt are required");
        }
        if (!endAt.isAfter(startAt)) {
            throw new InvalidDomainOperationException("endAt must be after startAt");
        }
    }
}
