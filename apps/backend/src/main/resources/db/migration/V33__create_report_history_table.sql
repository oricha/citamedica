CREATE TABLE report_history (
    id BIGSERIAL PRIMARY KEY,
    clinic_id BIGINT NOT NULL REFERENCES clinic (id) ON DELETE CASCADE,
    scheduled_report_id BIGINT REFERENCES scheduled_report (id) ON DELETE SET NULL,
    report_type VARCHAR(32) NOT NULL,
    export_format VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL,
    filter_params TEXT,
    content BYTEA,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_report_history_clinic_created ON report_history (clinic_id, created_at);
