-- Analytics views (plain views for PostgreSQL + H2 in PostgreSQL mode).
-- Underlying indexes support dashboard filters on clinic and date.

CREATE INDEX IF NOT EXISTS idx_appointment_clinic_start_at ON appointment (clinic_id, start_at);
CREATE INDEX IF NOT EXISTS idx_payment_completed_at ON payment (completed_at);

CREATE OR REPLACE VIEW clinic_occupancy_view AS
SELECT
    d.clinic_id,
    dts.doctor_id,
    CAST(dts.start_time AS DATE) AS slot_date,
    COUNT(*) AS total_slots,
    SUM(CASE WHEN dts.appointment_id IS NOT NULL THEN 1 ELSE 0 END) AS booked_slots,
    CASE
        WHEN COUNT(*) > 0 THEN CAST(SUM(CASE WHEN dts.appointment_id IS NOT NULL THEN 1 ELSE 0 END) AS DECIMAL(12, 4)) / COUNT(*)
        ELSE CAST(0 AS DECIMAL(12, 4))
    END AS occupancy_rate
FROM doctor_time_slots dts
         INNER JOIN doctor d ON d.id = dts.doctor_id
WHERE dts.deleted_at IS NULL
GROUP BY d.clinic_id, dts.doctor_id, CAST(dts.start_time AS DATE);

CREATE OR REPLACE VIEW clinic_revenue_view AS
SELECT
    COALESCE(a.clinic_id, inv.clinic_id) AS clinic_id,
    a.doctor_id,
    ms.name AS specialty_name,
    cs.name AS service_name,
    CAST(COALESCE(p.completed_at, p.created_at) AS DATE) AS revenue_date,
    SUM(p.amount) AS revenue
FROM payment p
         LEFT JOIN appointment a ON p.appointment_id = a.id
         LEFT JOIN invoice inv ON p.invoice_id = inv.id
         LEFT JOIN clinic_service cs ON a.service_id = cs.id
         LEFT JOIN doctor_specialty ds ON ds.doctor_id = a.doctor_id AND ds.primary_specialty = TRUE
         LEFT JOIN medical_specialty ms ON ds.specialty_id = ms.id
WHERE p.status = 'COMPLETED'
  AND COALESCE(a.clinic_id, inv.clinic_id) IS NOT NULL
GROUP BY COALESCE(a.clinic_id, inv.clinic_id), a.doctor_id, ms.name, cs.name,
         CAST(COALESCE(p.completed_at, p.created_at) AS DATE);

CREATE OR REPLACE VIEW clinic_collections_view AS
SELECT
    i.clinic_id,
    COALESCE(SUM(CASE WHEN i.status IN ('SENT', 'OVERDUE', 'DRAFT') THEN i.amount ELSE 0 END), 0) AS outstanding_balance,
    COALESCE(SUM(CASE WHEN i.status = 'OVERDUE' THEN i.amount ELSE 0 END), 0)              AS overdue_balance,
    COUNT(DISTINCT CASE WHEN i.status IN ('SENT', 'OVERDUE', 'DRAFT') THEN i.patient_id END) AS patient_count
FROM invoice i
GROUP BY i.clinic_id;

CREATE OR REPLACE VIEW patient_retention_view AS
WITH patient_last AS (
    SELECT
        a.clinic_id,
        a.patient_id,
        MAX(a.start_at) AS last_visit
    FROM appointment a
    WHERE a.clinic_id IS NOT NULL
    GROUP BY a.clinic_id, a.patient_id
)
SELECT
    clinic_id,
    COUNT(*) AS total_patients,
    SUM(CASE WHEN last_visit >= CURRENT_TIMESTAMP - INTERVAL '365' DAY THEN 1 ELSE 0 END) AS active_patients,
    CASE
        WHEN COUNT(*) > 0 THEN CAST(
                COUNT(*) - SUM(CASE WHEN last_visit >= CURRENT_TIMESTAMP - INTERVAL '365' DAY THEN 1 ELSE 0 END)
                AS DECIMAL(12, 4)) / COUNT(*)
        ELSE CAST(0 AS DECIMAL(12, 4))
    END AS churn_rate
FROM patient_last
GROUP BY clinic_id;
