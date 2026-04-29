package com.citamedica.backend.exception.domain;

public class InvalidPhoneNumberException extends NotificationException {
    public InvalidPhoneNumberException(String message) {
        super(message);
    }
}
