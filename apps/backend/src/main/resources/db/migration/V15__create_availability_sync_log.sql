CREATE TABLE availability_sync_log (
    id BIGSERIAL PRIMARY KEY,
    doctor_id BIGINT REFERENCES doctor(id) ON DELETE SET NULL,
    sync_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    events_fetched INT NOT NULL DEFAULT 0,
    conflicts_found INT NOT NULL DEFAULT 0,
    sync_duration_seconds INT,
    status VARCHAR(32) NOT NULL,
    error_message TEXT
);

CREATE INDEX idx_availability_sync_doctor_time ON availability_sync_log(doctor_id, sync_timestamp DESC);
