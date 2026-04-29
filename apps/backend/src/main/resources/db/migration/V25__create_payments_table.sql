CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patient(id),
    appointment_id BIGINT REFERENCES appointment(id) ON DELETE SET NULL,
    invoice_id BIGINT,
    amount DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(32) NOT NULL,
    payment_provider VARCHAR(16) NOT NULL,
    stripe_token TEXT,
    stripe_transaction_id VARCHAR(255),
    paypal_order_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_payment_patient ON payment(patient_id);
CREATE INDEX idx_payment_appointment ON payment(appointment_id);
CREATE INDEX idx_payment_invoice ON payment(invoice_id);
