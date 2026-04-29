ALTER TABLE clinic_service
    ADD COLUMN min_required_specialty_id BIGINT REFERENCES medical_specialty(id);

CREATE INDEX idx_clinic_service_min_specialty ON clinic_service(min_required_specialty_id);
