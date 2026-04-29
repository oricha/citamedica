package com.citamedica.backend.exception.domain;

public class InvalidDomainOperationException extends DomainException {
    public InvalidDomainOperationException(String message) {
        super(message);
    }
}
