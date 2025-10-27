package com.citamedica.backend.integration.notification;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for SMS notifications
 */
@Data
@Builder
public class SMSNotification {
    private String to;
    private String message;
    private String from;
}