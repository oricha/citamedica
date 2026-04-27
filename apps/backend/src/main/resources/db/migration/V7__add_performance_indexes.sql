-- Additional indexes aligned with query patterns (entities may already declare some via JPA;
-- this migration ensures they exist at the database level for production databases created before @Index annotations).

CREATE INDEX IF NOT EXISTS idx_patient_email ON patient (email);
CREATE INDEX IF NOT EXISTS idx_doctor_clinic ON doctor (clinic_id);
CREATE INDEX IF NOT EXISTS idx_appointment_patient ON appointment (patient_id);
