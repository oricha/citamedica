package com.citamedica.backend.adapter.in.dto.notification;

import com.citamedica.backend.domain.model.Notification;

import java.time.LocalDateTime;

public class NotificationResponse {
    private Long id;
    private String type;
    private String channel;
    private String recipient;
    private String status;
    private String preview;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.id = n.getId();
        r.type = n.getNotificationType().name();
        r.channel = n.getChannel().name();
        r.recipient = n.getRecipient();
        r.status = n.getStatus().name();
        r.preview = n.getMessageContent() == null ? "" : n.getMessageContent().substring(0, Math.min(80, n.getMessageContent().length()));
        r.createdAt = n.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public String getChannel() { return channel; }
    public String getRecipient() { return recipient; }
    public String getStatus() { return status; }
    public String getPreview() { return preview; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
