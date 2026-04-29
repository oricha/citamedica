CREATE TABLE doctor_availability_block (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL REFERENCES doctor(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    block_type VARCHAR(32) NOT NULL,
    recurrence_rule VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CHECK (end_time > start_time)
);

CREATE INDEX idx_doctor_block_range ON doctor_availability_block(doctor_id, start_time, end_time);
