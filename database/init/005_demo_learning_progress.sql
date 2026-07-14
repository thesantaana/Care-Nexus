-- Demo-only learning progress: one completed course for caregiver presentation.
INSERT INTO learning_access_log (user_id, resource_id, access_seconds, accessed_at)
SELECT u.id, r.id, 1800, NOW()
FROM sys_user u
JOIN training_resource r ON r.id = 3
WHERE u.username = 'caregiver_demo'
  AND NOT EXISTS (
    SELECT 1 FROM learning_access_log l
    WHERE l.user_id = u.id AND l.resource_id = r.id
  );

INSERT INTO learning_record (user_id, training_scope, total_learning_seconds,
  latest_learning_time, training_status)
SELECT u.id, 'MVP', 1800, NOW(), 'LEARNING'
FROM sys_user u
WHERE u.username = 'caregiver_demo'
  AND NOT EXISTS (
    SELECT 1 FROM learning_record lr
    WHERE lr.user_id = u.id AND lr.training_scope = 'MVP'
  );

UPDATE learning_record lr
JOIN sys_user u ON u.id = lr.user_id
SET lr.total_learning_seconds = GREATEST(lr.total_learning_seconds, 1800),
    lr.latest_learning_time = COALESCE(lr.latest_learning_time, NOW()),
    lr.training_status = CASE
      WHEN lr.training_status = 'NOT_STARTED' THEN 'LEARNING'
      ELSE lr.training_status
    END
WHERE u.username = 'caregiver_demo'
  AND lr.training_scope = 'MVP';
