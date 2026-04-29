CREATE TABLE notification_log (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient(id) ON DELETE CASCADE,
    appointment_id BIGINT REFERENCES appointment(id) ON DELETE SET NULL,
    notification_type VARCHAR(30) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    status VARCHAR(40) NOT NULL,
    message_content TEXT,
    provider_message_id VARCHAR(255),
    error_message TEXT,
    attempt_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP,
    next_retry_at TIMESTAMP,
    CONSTRAINT chk_notification_status CHECK (status IN ('SENT','FAILED','RETRYING','PERMANENTLY_FAILED'))
);

CREATE INDEX idx_notification_patient ON notification_log(patient_id);
CREATE INDEX idx_notification_status_created ON notification_log(status, created_at);
CREATE INDEX idx_notification_appointment ON notification_log(appointment_id);
