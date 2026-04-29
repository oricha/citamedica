CREATE TABLE doctor_specialty (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL REFERENCES doctor(id) ON DELETE CASCADE,
    specialty_id BIGINT NOT NULL REFERENCES medical_specialty(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    primary_specialty BOOLEAN NOT NULL DEFAULT FALSE,
    override_duration_minutes INT CHECK (override_duration_minutes IS NULL OR (override_duration_minutes >= 15 AND override_duration_minutes <= 120)),
    UNIQUE(doctor_id, specialty_id)
);

CREATE INDEX idx_doctor_specialty_doctor ON doctor_specialty(doctor_id);
CREATE INDEX idx_doctor_specialty_specialty ON doctor_specialty(specialty_id);
