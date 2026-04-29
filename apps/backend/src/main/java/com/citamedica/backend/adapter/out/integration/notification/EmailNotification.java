package com.citamedica.backend.adapter.out.integration.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for email notifications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotification {
    private String to;
    private String subject;
    private String body;
    private String from;
    /** Optional binary attachment (e.g. PDF report). */
    private byte[] attachmentContent;
    /** Suggested filename for attachment when non-null. */
    private String attachmentFileName;
}
