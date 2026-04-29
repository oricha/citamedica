ALTER TABLE payment
    ADD CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(id) ON DELETE SET NULL;

ALTER TABLE appointment ADD COLUMN total_amount DECIMAL(12, 2);
