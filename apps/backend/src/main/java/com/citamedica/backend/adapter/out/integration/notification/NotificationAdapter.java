package com.citamedica.backend.adapter.out.integration.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Adapter implementation for notification services.
 * In development mode (notifications.enabled=false), this logs notifications instead of sending them.
 * In production, this would integrate with real email/SMS providers (SendGrid, Twilio, etc.)
 */
@Service
@Slf4j
public class NotificationAdapter implements NotificationPort {
    
    @Value("${app.notifications.enabled:false}")
    private boolean notificationsEnabled;
    
    @Override
    public void sendEmail(EmailNotification notification) {
        if (!notificationsEnabled) {
            int attachLen = notification.getAttachmentContent() != null ? notification.getAttachmentContent().length : 0;
            log.info("Email notification (stub): to={}, subject={}, body={}, attachmentBytes={}, attachmentName={}",
                    notification.getTo(),
                    notification.getSubject(),
                    notification.getBody(),
                    attachLen,
                    notification.getAttachmentFileName());
            return;
        }
        
        // TODO: Implement real email sending with SendGrid or similar
        // Example:
        // sendGridClient.send(notification);
        log.info("Sending real email to: {}", notification.getTo());
    }
    
    @Override
    public void sendSMS(SMSNotification notification) {
        if (!notificationsEnabled) {
            log.info("SMS notification (stub): to={}, message={}", 
                notification.getTo(), 
                notification.getMessage());
            return;
        }
        
        // TODO: Implement real SMS sending with Twilio or similar
        // Example:
        // twilioClient.messages.create(
        //     new Message.Builder()
        //         .setTo(notification.getTo())
        //         .setFrom(notification.getFrom())
        //         .setBody(notification.getMessage())
        //         .build()
        // );
        log.info("Sending real SMS to: {}", notification.getTo());
    }
}