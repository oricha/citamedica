CREATE TABLE patient_notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE REFERENCES patient(id) ON DELETE CASCADE,
    email_enabled BOOLEAN NOT NULL DEFAULT true,
    sms_enabled BOOLEAN NOT NULL DEFAULT false,
    phone VARCHAR(20),
    consent_timestamp TIMESTAMP,
    consent_method VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_sms_phone_required CHECK (sms_enabled = false OR (phone IS NOT NULL AND length(trim(phone)) > 0))
);

INSERT INTO patient_notification_preferences (patient_id, email_enabled, sms_enabled, phone)
SELECT p.id, true, false, p.phone
FROM patient p
WHERE NOT EXISTS (
    SELECT 1
    FROM patient_notification_preferences pref
    WHERE pref.patient_id = p.id
);
