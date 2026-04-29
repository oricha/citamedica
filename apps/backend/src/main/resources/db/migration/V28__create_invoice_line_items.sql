CREATE TABLE invoice_line_item (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL REFERENCES invoice(id) ON DELETE CASCADE,
    description VARCHAR(512) NOT NULL,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    unit_price DECIMAL(12, 2) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL CHECK (amount >= 0)
);

CREATE INDEX idx_invoice_line_invoice ON invoice_line_item(invoice_id);
