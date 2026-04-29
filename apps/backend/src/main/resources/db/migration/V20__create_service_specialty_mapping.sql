CREATE TABLE service_specialty_mapping (
    id BIGSERIAL PRIMARY KEY,
    clinic_service_id BIGINT NOT NULL REFERENCES clinic_service(id) ON DELETE CASCADE,
    specialty_id BIGINT NOT NULL REFERENCES medical_specialty(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(clinic_service_id, specialty_id)
);

CREATE INDEX idx_ssm_service ON service_specialty_mapping(clinic_service_id);
CREATE INDEX idx_ssm_specialty ON service_specialty_mapping(specialty_id);
