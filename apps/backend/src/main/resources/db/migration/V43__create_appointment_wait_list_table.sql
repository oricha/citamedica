CREATE TABLE appointment_wait_list (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES doctor (id) ON DELETE CASCADE,
    clinic_id BIGINT REFERENCES clinic (id) ON DELETE SET NULL,
    service_id BIGINT REFERENCES clinic_service (id) ON DELETE SET NULL,
    preferred_start_date DATE,
    preferred_end_date DATE,
    appointment_type VARCHAR(255),
    notes TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'WAITING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_wait_list_date_range CHECK (
        preferred_start_date IS NULL OR preferred_end_date IS NULL OR preferred_start_date <= preferred_end_date
    )
);

CREATE INDEX idx_wait_list_doctor_status ON appointment_wait_list (doctor_id, status);
CREATE INDEX idx_wait_list_patient_status ON appointment_wait_list (patient_id, status);
