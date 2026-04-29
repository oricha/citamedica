CREATE TABLE medical_condition (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    condition_name VARCHAR(512) NOT NULL,
    severity VARCHAR(32),
    onset_date DATE,
    resolution_date DATE,
    notes TEXT,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_medical_condition_patient ON medical_condition (patient_id);
CREATE INDEX idx_medical_condition_patient_active ON medical_condition (patient_id, resolution_date, deleted_at);
