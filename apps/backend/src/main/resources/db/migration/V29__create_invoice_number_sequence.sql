CREATE TABLE invoice_number_sequence (
    clinic_id BIGINT NOT NULL REFERENCES clinic(id) ON DELETE CASCADE,
    fiscal_year INTEGER NOT NULL,
    last_value INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (clinic_id, fiscal_year)
);
