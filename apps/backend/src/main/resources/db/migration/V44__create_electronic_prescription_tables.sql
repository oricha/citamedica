CREATE TABLE electronic_prescription (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES doctor (id) ON DELETE RESTRICT,
    appointment_id BIGINT REFERENCES appointment (id) ON DELETE SET NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_until DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE electronic_prescription_line (
    id BIGSERIAL PRIMARY KEY,
    prescription_id BIGINT NOT NULL REFERENCES electronic_prescription (id) ON DELETE CASCADE,
    medication_name VARCHAR(512) NOT NULL,
    dosage VARCHAR(255),
    frequency VARCHAR(255),
    duration_days INTEGER,
    route VARCHAR(128),
    instructions TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_electronic_rx_patient_status ON electronic_prescription (patient_id, status);
CREATE INDEX idx_electronic_rx_doctor ON electronic_prescription (doctor_id);
CREATE INDEX idx_electronic_rx_line_rx ON electronic_prescription_line (prescription_id);
