CREATE OR REPLACE VIEW notification_stats AS
SELECT
    CAST(created_at AS DATE) AS stat_day,
    COUNT(*) AS total_notifications,
    SUM(CASE WHEN status = 'SENT' THEN 1 ELSE 0 END) AS sent_count,
    SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count,
    SUM(CASE WHEN status = 'RETRYING' THEN 1 ELSE 0 END) AS retrying_count,
    SUM(CASE WHEN status = 'PERMANENTLY_FAILED' THEN 1 ELSE 0 END) AS permanently_failed_count
FROM notification_log
GROUP BY CAST(created_at AS DATE)
ORDER BY stat_day DESC;
