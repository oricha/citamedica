CREATE TABLE patient_allergy (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    allergen_name VARCHAR(512) NOT NULL,
    severity VARCHAR(32) NOT NULL,
    reaction_type VARCHAR(255),
    notes TEXT,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_patient_allergy_patient ON patient_allergy (patient_id);
CREATE INDEX idx_patient_allergy_severity ON patient_allergy (patient_id, severity);
