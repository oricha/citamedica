CREATE TABLE medical_document (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    document_type VARCHAR(64) NOT NULL,
    file_path VARCHAR(2048) NOT NULL,
    file_hash VARCHAR(128) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(128),
    uploaded_by VARCHAR(255),
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version_id INT NOT NULL DEFAULT 1,
    notes TEXT,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medical_document_patient ON medical_document (patient_id);
CREATE INDEX idx_medical_document_patient_type ON medical_document (patient_id, document_type, deleted_at);
