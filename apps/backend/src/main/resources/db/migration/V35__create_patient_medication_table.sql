CREATE TABLE patient_medication (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    medication_name VARCHAR(512) NOT NULL,
    dosage VARCHAR(255),
    frequency VARCHAR(255),
    start_date DATE,
    end_date DATE,
    indication TEXT,
    contraindications TEXT,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_patient_medication_patient ON patient_medication (patient_id);
CREATE INDEX idx_patient_medication_patient_end ON patient_medication (patient_id, end_date, deleted_at);
