CREATE TABLE doctor_time_slots (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT NOT NULL REFERENCES doctor(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL,
    appointment_id BIGINT REFERENCES appointment(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    booked_at TIMESTAMP,
    deleted_at TIMESTAMP,
    CHECK (end_time > start_time)
);

CREATE UNIQUE INDEX uq_doctor_time_slots_start ON doctor_time_slots(doctor_id, start_time);
CREATE INDEX idx_doctor_slots_lookup ON doctor_time_slots(doctor_id, start_time, status);
