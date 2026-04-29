CREATE TABLE invoice (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinic(id),
    patient_id BIGINT NOT NULL REFERENCES patient(id),
    appointment_id BIGINT REFERENCES appointment(id) ON DELETE SET NULL,
    invoice_number VARCHAR(64) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL CHECK (amount >= 0),
    due_date DATE,
    status VARCHAR(32) NOT NULL,
    pdf_url VARCHAR(1024),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uq_invoice_clinic_number UNIQUE (clinic_id, invoice_number)
);

CREATE INDEX idx_invoice_clinic_created ON invoice(clinic_id, created_at);
CREATE INDEX idx_invoice_patient ON invoice(patient_id);
CREATE INDEX idx_invoice_appointment ON invoice(appointment_id);
