CREATE TABLE doctor_availability_configuration (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL REFERENCES doctor(id) ON DELETE CASCADE,
    day_of_week VARCHAR(16) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_duration_minutes INT NOT NULL CHECK (slot_duration_minutes IN (15, 30, 45, 60)),
    max_concurrent_appointments INT NOT NULL DEFAULT 1 CHECK (max_concurrent_appointments >= 1),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE UNIQUE INDEX uq_doctor_config_day ON doctor_availability_configuration(doctor_id, day_of_week);
CREATE INDEX idx_doctor_availability_day ON doctor_availability_configuration(doctor_id, day_of_week);
