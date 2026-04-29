CREATE TABLE medical_history_audit_log (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    record_type VARCHAR(64) NOT NULL,
    record_id BIGINT,
    action VARCHAR(32) NOT NULL,
    changed_data TEXT,
    actor_id VARCHAR(255),
    actor_ip VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medical_history_audit_patient_created ON medical_history_audit_log (patient_id, created_at DESC);
CREATE INDEX idx_medical_history_audit_record ON medical_history_audit_log (record_type, record_id);
CREATE INDEX idx_medical_history_audit_action_created ON medical_history_audit_log (action, created_at DESC);
