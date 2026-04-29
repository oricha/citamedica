CREATE TABLE clinic_service_pricing_rule (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinic(id) ON DELETE CASCADE,
    clinic_service_id BIGINT NOT NULL REFERENCES clinic_service(id) ON DELETE CASCADE,
    specialty_id BIGINT REFERENCES medical_specialty(id) ON DELETE CASCADE,
    override_price DECIMAL(12, 2) NOT NULL CHECK (override_price > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_pricing_rule_lookup ON clinic_service_pricing_rule(clinic_id, clinic_service_id, specialty_id);
