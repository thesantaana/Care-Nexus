USE care_nexus;

DELETE submission
FROM training_assignment_submission submission
JOIN training_assignment assignment ON assignment.id = submission.assignment_id
WHERE assignment.title LIKE '%?%';

DELETE FROM training_assignment WHERE title LIKE '%?%';

INSERT INTO training_assignment (
  resource_id, title, assignment_type, content, options_json,
  standard_answer, assignment_status, created_by
)
SELECT
  r.id,
  CONCAT(r.title, '课后单选题'),
  'SINGLE_CHOICE',
  '开始本课程对应的照护操作前，首先应完成哪项准备？',
  '["完成手卫生并核对照护要求","直接开始操作","跳过风险确认","仅在结束后记录"]',
  '完成手卫生并核对照护要求',
  'PUBLISHED',
  u.id
FROM training_resource r
JOIN sys_user u ON u.username = 'admin_demo'
WHERE r.resource_status = 'PUBLISHED'
  AND NOT EXISTS (
    SELECT 1 FROM training_assignment a
    WHERE a.resource_id = r.id AND a.title = CONCAT(r.title, '课后单选题')
  );

INSERT INTO training_assignment (
  resource_id, title, assignment_type, content, options_json,
  standard_answer, assignment_status, created_by
)
SELECT
  r.id,
  CONCAT(r.title, '课后风险判断题'),
  'TRUE_FALSE',
  '照护过程中发现异常风险时，可以不记录并继续原操作。',
  '["正确","错误"]',
  '错误',
  'PUBLISHED',
  u.id
FROM training_resource r
JOIN sys_user u ON u.username = 'admin_demo'
WHERE r.resource_status = 'PUBLISHED'
  AND NOT EXISTS (
    SELECT 1 FROM training_assignment a
    WHERE a.resource_id = r.id AND a.title = CONCAT(r.title, '课后风险判断题')
  );
