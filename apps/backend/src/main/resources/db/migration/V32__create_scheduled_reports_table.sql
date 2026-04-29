CREATE TABLE scheduled_report (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinic (id) ON DELETE CASCADE,
    report_type VARCHAR(32) NOT NULL,
    frequency VARCHAR(16) NOT NULL,
    recipients TEXT NOT NULL,
    next_run_at TIMESTAMP NOT NULL,
    last_run_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_scheduled_report_clinic_next ON scheduled_report (clinic_id, next_run_at);
