CREATE TABLE medical_history_event (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    event_type VARCHAR(64) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    event_title VARCHAR(512) NOT NULL,
    event_description TEXT,
    source_record_id BIGINT,
    source_record_type VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medical_history_event_patient_date ON medical_history_event (patient_id, event_date DESC);
CREATE INDEX idx_medical_history_event_patient_type ON medical_history_event (patient_id, event_type);
CREATE INDEX idx_medical_history_event_patient_created ON medical_history_event (patient_id, created_at DESC);
