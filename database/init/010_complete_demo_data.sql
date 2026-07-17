-- Complete the local showcase without overwriting user-created or submitted data.
-- Run after 008_demo_showcase_data.sql and 009_assignment_docx_publish.sql.
USE care_nexus;
SET NAMES utf8mb4;

-- Give every showcase caregiver visible learning activity for every published course.
INSERT INTO learning_access_log (user_id, resource_id, access_seconds, accessed_at)
SELECT
  u.id,
  r.id,
  CASE u.username
    WHEN 'caregiver_demo' THEN 1500 + (r.id * 180)
    WHEN 'caregiver_chen' THEN 1200 + (r.id * 120)
    WHEN 'caregiver_li' THEN 900 + (r.id * 90)
    ELSE 600 + (r.id * 60)
  END,
  DATE_SUB(NOW(), INTERVAL (r.id + u.id) HOUR)
FROM sys_user u
CROSS JOIN training_resource r
WHERE u.username IN ('caregiver_demo', 'caregiver_chen', 'caregiver_li', 'caregiver_wang')
  AND r.resource_status = 'PUBLISHED'
  AND r.is_deleted = 0
  AND NOT EXISTS (
    SELECT 1
    FROM learning_access_log access_log
    WHERE access_log.user_id = u.id
      AND access_log.resource_id = r.id
  );

INSERT INTO learning_record (
  user_id, training_scope, total_learning_seconds,
  latest_learning_time, training_status
)
SELECT
  u.id,
  'MVP',
  COALESCE(SUM(access_log.access_seconds), 0),
  COALESCE(MAX(access_log.accessed_at), NOW()),
  'LEARNING'
FROM sys_user u
LEFT JOIN learning_access_log access_log ON access_log.user_id = u.id
WHERE u.username IN ('caregiver_demo', 'caregiver_chen', 'caregiver_li', 'caregiver_wang')
  AND NOT EXISTS (
    SELECT 1
    FROM learning_record record
    WHERE record.user_id = u.id
      AND record.training_scope = 'MVP'
  )
GROUP BY u.id;

-- Show completed assignments while preserving any answer already submitted by a user.
INSERT IGNORE INTO training_assignment_submission (
  assignment_id, user_id, answer_content, score,
  submission_status, submitted_at
)
SELECT
  assignment.id,
  user_account.id,
  COALESCE(assignment.standard_answer, '已完成课程要点复习'),
  100.00,
  'GRADED',
  DATE_SUB(NOW(), INTERVAL assignment.id HOUR)
FROM training_assignment assignment
JOIN sys_user user_account ON user_account.username = 'caregiver_demo'
WHERE assignment.assignment_status = 'PUBLISHED';

-- Add visible social feedback to seeded discussions without changing existing likes.
INSERT IGNORE INTO training_discussion_like (discussion_id, user_id)
SELECT discussion.id, user_account.id
FROM training_discussion discussion
JOIN sys_user user_account ON user_account.username IN ('caregiver_chen', 'caregiver_li')
WHERE discussion.title LIKE '% - 学习要点讨论';

INSERT IGNORE INTO training_discussion_reply_like (reply_id, user_id)
SELECT reply.id, user_account.id
FROM training_discussion_reply reply
JOIN sys_user user_account ON user_account.username = 'caregiver_demo';
