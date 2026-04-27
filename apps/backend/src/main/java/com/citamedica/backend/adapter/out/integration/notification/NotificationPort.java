package com.citamedica.backend.adapter.out.integration.notification;

/**
 * Port interface for notification services.
 * This follows the hexagonal architecture pattern, allowing different implementations
 * (email providers, SMS providers) to be plugged in without changing business logic.
 */
public interface NotificationPort {
    
    /**
     * Send an email notification
     * @param notification Email notification details
     */
    void sendEmail(EmailNotification notification);
    
    /**
     * Send an SMS notification
     * @param notification SMS notification details
     */
    void sendSMS(SMSNotification notification);
}