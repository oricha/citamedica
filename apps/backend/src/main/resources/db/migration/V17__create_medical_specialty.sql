CREATE TABLE medical_specialty (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medical_specialty_code ON medical_specialty(code);

INSERT INTO medical_specialty (code, name, description) VALUES
    ('GENERAL_PRACTICE', 'General Practice', 'Primary care'),
    ('CARDIOLOGY', 'Cardiology', 'Heart and cardiovascular'),
    ('PEDIATRICS', 'Pediatrics', 'Child health'),
    ('DERMATOLOGY', 'Dermatology', 'Skin conditions'),
    ('ORTHOPEDICS', 'Orthopedics', 'Bones and joints'),
    ('PSYCHIATRY', 'Psychiatry', 'Mental health'),
    ('NEUROLOGY', 'Neurology', 'Brain and nervous system'),
    ('ONCOLOGY', 'Oncology', 'Cancer care'),
    ('ENDOCRINOLOGY', 'Endocrinology', 'Hormones and metabolism'),
    ('GASTROENTEROLOGY', 'Gastroenterology', 'Digestive system'),
    ('PULMONOLOGY', 'Pulmonology', 'Respiratory system'),
    ('NEPHROLOGY', 'Nephrology', 'Kidney care'),
    ('RHEUMATOLOGY', 'Rheumatology', 'Autoimmune and joints'),
    ('UROLOGY', 'Urology', 'Urinary tract'),
    ('OPHTHALMOLOGY', 'Ophthalmology', 'Eye care'),
    ('OTOLARYNGOLOGY', 'Otolaryngology', 'ENT'),
    ('OBSTETRICS_GYNECOLOGY', 'Obstetrics & Gynecology', 'Women''s health'),
    ('ANESTHESIOLOGY', 'Anesthesiology', 'Anesthesia'),
    ('RADIOLOGY', 'Radiology', 'Medical imaging'),
    ('PATHOLOGY', 'Pathology', 'Laboratory diagnostics'),
    ('EMERGENCY_MEDICINE', 'Emergency Medicine', 'Emergency care'),
    ('INTERNAL_MEDICINE', 'Internal Medicine', 'Adult internal medicine'),
    ('SPORTS_MEDICINE', 'Sports Medicine', 'Athletic injuries'),
    ('GERIATRICS', 'Geriatrics', 'Elderly care');
