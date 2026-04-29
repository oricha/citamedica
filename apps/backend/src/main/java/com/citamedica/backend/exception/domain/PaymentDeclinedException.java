package com.citamedica.backend.exception.domain;

public class PaymentDeclinedException extends DomainException {
    public PaymentDeclinedException(String message) {
        super(message);
    }
}
