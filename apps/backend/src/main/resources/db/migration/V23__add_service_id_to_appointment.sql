ALTER TABLE appointment
    ADD COLUMN service_id BIGINT REFERENCES clinic_service(id);

CREATE INDEX idx_appointment_service ON appointment(service_id);
