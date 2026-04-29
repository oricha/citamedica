CREATE TABLE consent (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient(id),
    type VARCHAR(50) NOT NULL,
    accepted BOOLEAN NOT NULL,
    accepted_at TIMESTAMP
);

