package com.citamedica.backend.integration.notification;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for email notifications
 */
@Data
@Builder
public class EmailNotification {
    private String to;
    private String subject;
    private String body;
    private String from;
}