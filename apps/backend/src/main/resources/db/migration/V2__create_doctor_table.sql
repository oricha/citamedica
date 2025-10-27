CREATE TABLE doctor (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT REFERENCES clinic(id),
    full_name VARCHAR(255) NOT NULL,
    specialty VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    cal_username VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doctor_clinic ON doctor(clinic_id);
CREATE INDEX idx_doctor_active ON doctor(active);