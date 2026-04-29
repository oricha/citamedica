CREATE TABLE patient_review (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient (id) ON DELETE CASCADE,
    doctor_id BIGINT NOT NULL REFERENCES doctor (id) ON DELETE CASCADE,
    appointment_id BIGINT NOT NULL REFERENCES appointment (id) ON DELETE CASCADE,
    clinic_id BIGINT REFERENCES clinic (id) ON DELETE SET NULL,
    rating INTEGER NOT NULL,
    title VARCHAR(255),
    comment TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_patient_review_rating CHECK (rating >= 1 AND rating <= 5)
);

CREATE UNIQUE INDEX uq_patient_review_appointment ON patient_review (appointment_id);

CREATE INDEX idx_patient_review_doctor_status ON patient_review (doctor_id, status);

CREATE INDEX idx_patient_review_patient ON patient_review (patient_id);
