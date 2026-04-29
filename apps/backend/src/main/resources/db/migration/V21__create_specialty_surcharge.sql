CREATE TABLE specialty_surcharge (
    id BIGSERIAL PRIMARY KEY,
    specialty_id BIGINT NOT NULL REFERENCES medical_specialty(id) ON DELETE CASCADE,
    surcharge_amount DECIMAL(12, 2) NOT NULL CHECK (surcharge_amount >= 0),
    clinic_id BIGINT REFERENCES clinic(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_specialty_surcharge_specialty ON specialty_surcharge(specialty_id);
CREATE INDEX idx_specialty_surcharge_clinic ON specialty_surcharge(clinic_id);
-- Partial unique index (one global surcharge per specialty) is enforced in application code for H2/Flyway test compatibility.
