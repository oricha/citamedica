CREATE TABLE appointment (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT REFERENCES clinic(id),
    doctor_id BIGINT NOT NULL REFERENCES doctor(id),
    patient_id BIGINT NOT NULL REFERENCES patient(id),
    cal_booking_id VARCHAR(255) UNIQUE,
    type VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_doctor_date ON appointment(doctor_id, start_at);
CREATE INDEX idx_cal_booking ON appointment(cal_booking_id);

