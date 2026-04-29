CREATE TABLE medical_procedure (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    procedure_name VARCHAR(512) NOT NULL,
    procedure_date DATE NOT NULL,
    outcome TEXT,
    related_condition_id BIGINT REFERENCES medical_condition (id) ON DELETE SET NULL,
    notes TEXT,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_medical_procedure_patient ON medical_procedure (patient_id);
CREATE INDEX idx_medical_procedure_patient_date ON medical_procedure (patient_id, procedure_date);
