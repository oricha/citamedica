package com.citamedica.backend.adapter.out.integration.calcom;

public class CalcomApiException extends RuntimeException {
    public CalcomApiException(String message) {
        super(message);
    }

    public CalcomApiException(String message, Throwable cause) {
        super(message, cause);
    }
}