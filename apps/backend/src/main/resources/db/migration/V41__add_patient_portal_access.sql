ALTER TABLE patient ADD COLUMN portal_password_hash VARCHAR(255);

ALTER TABLE patient ADD COLUMN portal_access_enabled BOOLEAN NOT NULL DEFAULT FALSE;
