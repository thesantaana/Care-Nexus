-- Repeatable showcase data. Existing user-created records are preserved.
SET NAMES utf8mb4;

INSERT INTO sys_user (username, password_hash, real_name, avatar_url, main_role_id, account_status)
SELECT 'caregiver_chen', u.password_hash, '陈护工', '/assets/default-avatar.png', u.main_role_id, 'NORMAL'
FROM sys_user u WHERE u.username = 'caregiver_demo'
AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'caregiver_chen');
INSERT INTO sys_user (username, password_hash, real_name, avatar_url, main_role_id, account_status)
SELECT 'caregiver_li', u.password_hash, '李护工', '/assets/default-avatar.png', u.main_role_id, 'NORMAL'
FROM sys_user u WHERE u.username = 'caregiver_demo'
AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'caregiver_li');
INSERT INTO sys_user (username, password_hash, real_name, avatar_url, main_role_id, account_status)
SELECT 'caregiver_wang', u.password_hash, '王护工', '/assets/default-avatar.png', u.main_role_id, 'NORMAL'
FROM sys_user u WHERE u.username = 'caregiver_demo'
AND NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'caregiver_wang');

UPDATE sys_user SET real_name = '陈护工' WHERE username = 'caregiver_chen';
UPDATE sys_user SET real_name = '李护工' WHERE username = 'caregiver_li';
UPDATE sys_user SET real_name = '王护工' WHERE username = 'caregiver_wang';

INSERT INTO training_note (user_id, resource_id, note_title, note_content)
SELECT u.id, r.id, CONCAT(r.title, ' - 学习笔记'),
       CONCAT('<h2>', r.title, '</h2><p>复习服务准备、风险核对、规范操作和记录要求。</p>')
FROM sys_user u CROSS JOIN training_resource r
WHERE u.username = 'caregiver_demo' AND r.resource_status = 'PUBLISHED' AND r.is_deleted = 0
ON DUPLICATE KEY UPDATE note_title = VALUES(note_title);

DELETE d FROM training_discussion d
JOIN sys_user u ON u.id = d.user_id
WHERE u.username = 'caregiver_demo' AND d.content REGEXP '^[?]+$';

UPDATE training_discussion
SET title = REPLACE(title, ' - key steps', ' - 学习要点讨论'),
    content = '我把本课程整理为服务准备、过程观察和服务后记录三个环节，大家认为还需要补充哪些要点？'
WHERE title LIKE '% - key steps';
UPDATE training_discussion
SET title = REPLACE(title, ' - practice reflection', ' - 实践心得'),
    content = '课程中的风险提示很实用。实际照护时还应结合老人的当前状况，并及时、客观地记录服务过程。'
WHERE title LIKE '% - practice reflection';
UPDATE training_discussion_reply
SET content = '这个结构很清晰。我还会在每次服务后，对照课程检查清单复盘自己的操作。'
WHERE content = 'This structure is clear. I also compare my work with the course checklist after each service.';

INSERT INTO training_discussion (resource_id, user_id, title, content)
SELECT r.id, u.id, CONCAT(r.title, ' - 学习要点讨论'),
       '我把本课程整理为服务准备、过程观察和服务后记录三个环节，大家认为还需要补充哪些要点？'
FROM training_resource r JOIN sys_user u ON u.username = 'caregiver_demo'
WHERE r.resource_status = 'PUBLISHED' AND r.is_deleted = 0
AND NOT EXISTS (SELECT 1 FROM training_discussion d WHERE d.resource_id = r.id AND d.title = CONCAT(r.title, ' - 学习要点讨论'));
INSERT INTO training_discussion (resource_id, user_id, title, content)
SELECT r.id, u.id, CONCAT(r.title, ' - 实践心得'),
       '课程中的风险提示很实用。实际照护时还应结合老人的当前状况，并及时、客观地记录服务过程。'
FROM training_resource r JOIN sys_user u ON u.username = 'caregiver_chen'
WHERE r.resource_status = 'PUBLISHED' AND r.is_deleted = 0
AND NOT EXISTS (SELECT 1 FROM training_discussion d WHERE d.resource_id = r.id AND d.title = CONCAT(r.title, ' - 实践心得'));

INSERT INTO training_discussion_reply (discussion_id, user_id, content)
SELECT d.id, u.id, '这个结构很清晰。我还会在每次服务后，对照课程检查清单复盘自己的操作。'
FROM training_discussion d JOIN sys_user u ON u.username = 'caregiver_li'
WHERE d.title LIKE '% - 学习要点讨论'
AND NOT EXISTS (SELECT 1 FROM training_discussion_reply r WHERE r.discussion_id = d.id AND r.content = '这个结构很清晰。我还会在每次服务后，对照课程检查清单复盘自己的操作。');

UPDATE ai_draft
SET draft_content = JSON_OBJECT('questionType','TRUE_FALSE','questionContent','Identity and risk checks should be completed before care work.','standardAnswer','true','analysis','Pre-operation checks reduce process errors.','options',JSON_ARRAY())
WHERE JSON_VALID(draft_content) = 0;

INSERT INTO ai_draft (draft_type, prompt, draft_content, draft_status)
SELECT 'QUESTION', 'DEMO-SHOWCASE-01', JSON_OBJECT('questionType','SINGLE_CHOICE','questionContent',CONVERT(0xE8BF9BE8A18CE68AA4E79086E6938DE4BD9CE5898DEFBC8CE9A696E58588E5BA94E5AE8CE68890E593AAE9A1B9E58786E5A487EFBC9F USING utf8mb4),'standardAnswer','A','analysis',CONVERT(0xE6938DE4BD9CE5898DE6A0B8E5AFB9E883BDE5A49FE5878FE5B091E5AFB9E8B1A1E38081E9A1B9E79BAEE5928CE6B581E7A88BE5B7AEE99499E38082 USING utf8mb4),'options',JSON_ARRAY(JSON_OBJECT('label','A','content',CONVERT(0xE6A0B8E5AFB9E69C8DE58AA1E5AFB9E8B1A1E38081E6938DE4BD9CE8A681E6B182E5B9B6E8AF84E4BCB0E9A38EE999A9 USING utf8mb4),'correct',TRUE),JSON_OBJECT('label','B','content',CONVERT(0xE79BB4E68EA5E5BC80E5A78BE6938DE4BD9C USING utf8mb4),'correct',FALSE),JSON_OBJECT('label','C','content',CONVERT(0xE8B7B3E8BF87E58786E5A487E78EAFE88A82 USING utf8mb4),'correct',FALSE))), 'DRAFT'
WHERE NOT EXISTS (SELECT 1 FROM ai_draft WHERE prompt = 'DEMO-SHOWCASE-01');
INSERT INTO ai_draft (draft_type, prompt, draft_content, draft_status)
SELECT 'QUESTION', 'DEMO-SHOWCASE-02', JSON_OBJECT('questionType','TRUE_FALSE','questionContent',CONVERT(0xE58F91E78EB0E5BC82E5B8B8E9A38EE999A9E697B6EFBC8CE5BA94E69A82E5819CE6938DE4BD9CE5B9B6E58F8AE697B6E68AA5E5918AE38082 USING utf8mb4),'standardAnswer','true','analysis',CONVERT(0xE9A38EE999A9E5A484E7BDAEE4BC98E58588E4BA8EE7BBA7E7BBADE5AE8CE68890E6938DE4BD9CE38082 USING utf8mb4),'options',JSON_ARRAY()), 'DRAFT'
WHERE NOT EXISTS (SELECT 1 FROM ai_draft WHERE prompt = 'DEMO-SHOWCASE-02');
INSERT INTO ai_draft (draft_type, prompt, draft_content, draft_status)
SELECT 'QUESTION', 'DEMO-SHOWCASE-05', JSON_OBJECT('questionType','SINGLE_CHOICE','questionContent',CONVERT(0xE4B88BE58897E593AAE9A1B9E5819AE6B395E69C80E7ACA6E59088E5AE89E585A8E785A7E68AA4E8A681E6B182EFBC9F USING utf8mb4),'standardAnswer','B','analysis',CONVERT(0xE8A784E88C83E6938DE4BD9CE5928CE58F8AE697B6E8AEB0E5BD95E698AFE5AE89E585A8E785A7E68AA4E79A84E9878DE8A681E59FBAE7A180E38082 USING utf8mb4),'options',JSON_ARRAY(JSON_OBJECT('label','A','content',CONVERT(0xE5BFBDE795A5E8BDBBE5BEAEE58F98E58C96 USING utf8mb4),'correct',FALSE),JSON_OBJECT('label','B','content',CONVERT(0xE981B5E5BEAAE8A784E88C83E5B9B6E58F8AE697B6E8AEB0E5BD95E5BC82E5B8B8 USING utf8mb4),'correct',TRUE),JSON_OBJECT('label','C','content',CONVERT(0xE69CAAE7BB8FE68E88E69D83E887AAE8A18CE694B9E58F98E785A7E68AA4E696B9E6A188 USING utf8mb4),'correct',FALSE))), 'DRAFT'
WHERE NOT EXISTS (SELECT 1 FROM ai_draft WHERE prompt = 'DEMO-SHOWCASE-05');

UPDATE ai_draft SET draft_content = JSON_SET(draft_content,
  '$.questionContent', CONVERT(0xE8BF9BE8A18CE68AA4E79086E6938DE4BD9CE5898DEFBC8CE9A696E58588E5BA94E5AE8CE68890E593AAE9A1B9E58786E5A487EFBC9F USING utf8mb4),
  '$.analysis', CONVERT(0xE6938DE4BD9CE5898DE6A0B8E5AFB9E883BDE5A49FE5878FE5B091E5AFB9E8B1A1E38081E9A1B9E79BAEE5928CE6B581E7A88BE5B7AEE99499E38082 USING utf8mb4),
  '$.options', JSON_ARRAY(JSON_OBJECT('label','A','content',CONVERT(0xE6A0B8E5AFB9E69C8DE58AA1E5AFB9E8B1A1E38081E6938DE4BD9CE8A681E6B182E5B9B6E8AF84E4BCB0E9A38EE999A9 USING utf8mb4),'correct',TRUE),JSON_OBJECT('label','B','content',CONVERT(0xE79BB4E68EA5E5BC80E5A78BE6938DE4BD9C USING utf8mb4),'correct',FALSE),JSON_OBJECT('label','C','content',CONVERT(0xE8B7B3E8BF87E58786E5A487E78EAFE88A82 USING utf8mb4),'correct',FALSE)))
WHERE prompt = 'DEMO-SHOWCASE-01';
UPDATE ai_draft SET draft_content = JSON_SET(draft_content,
  '$.questionContent', CONVERT(0xE58F91E78EB0E5BC82E5B8B8E9A38EE999A9E697B6EFBC8CE5BA94E69A82E5819CE6938DE4BD9CE5B9B6E58F8AE697B6E68AA5E5918AE38082 USING utf8mb4),
  '$.analysis', CONVERT(0xE9A38EE999A9E5A484E7BDAEE4BC98E58588E4BA8EE7BBA7E7BBADE5AE8CE68890E6938DE4BD9CE38082 USING utf8mb4))
WHERE prompt = 'DEMO-SHOWCASE-02';
UPDATE ai_draft SET draft_content = JSON_SET(draft_content,
  '$.questionContent', CONVERT(0xE4B88BE58897E593AAE9A1B9E5819AE6B395E69C80E7ACA6E59088E5AE89E585A8E785A7E68AA4E8A681E6B182EFBC9F USING utf8mb4),
  '$.analysis', CONVERT(0xE8A784E88C83E6938DE4BD9CE5928CE58F8AE697B6E8AEB0E5BD95E698AFE5AE89E585A8E785A7E68AA4E79A84E9878DE8A681E59FBAE7A180E38082 USING utf8mb4),
  '$.options', JSON_ARRAY(JSON_OBJECT('label','A','content',CONVERT(0xE5BFBDE795A5E8BDBBE5BEAEE58F98E58C96 USING utf8mb4),'correct',FALSE),JSON_OBJECT('label','B','content',CONVERT(0xE981B5E5BEAAE8A784E88C83E5B9B6E58F8AE697B6E8AEB0E5BD95E5BC82E5B8B8 USING utf8mb4),'correct',TRUE),JSON_OBJECT('label','C','content',CONVERT(0xE69CAAE7BB8FE68E88E69D83E887AAE8A18CE694B9E58F98E785A7E68AA4E696B9E6A188 USING utf8mb4),'correct',FALSE)))
WHERE prompt = 'DEMO-SHOWCASE-05';
UPDATE ai_draft SET draft_content = JSON_SET(draft_content,
  '$.questionContent', CONVERT(0xE68AA4E79086E8AEB0E5BD95E58FAFE4BBA5E59CA8E5A49AE697A5E5908EE7BB9FE4B880E8A1A5E58699E38082 USING utf8mb4),
  '$.analysis', CONVERT(0xE68AA4E79086E8AEB0E5BD95E5BA94E5BD93E58F8AE697B6E38081E5AEA2E8A782E38081E5AE8CE695B4E38082 USING utf8mb4)),
  review_comment = CONVERT(0xE58685E5AEB9E58786E7A1AEEFBC8CE58FAFE8BF9BE585A5E6ADA3E5BC8FE9A298E5BA93E88D89E7A8BF USING utf8mb4)
WHERE prompt = 'DEMO-SHOWCASE-03';
UPDATE ai_draft SET draft_content = JSON_SET(draft_content,
  '$.questionContent', CONVERT(0xE69CAAE7BB8FE58CBBE79697E68E88E69D83E58FAFE4BBA5E887AAE8A18CE98089E68BA9E88DAFE789A9E38082 USING utf8mb4),
  '$.analysis', CONVERT(0xE8AFA5E58685E5AEB9E8B685E587BAE68AA4E79086E59FB9E8AEADE8BE85E58AA9E8BEB9E7958CE38082 USING utf8mb4)),
  review_comment = CONVERT(0xE6B689E58F8AE58CBBE79697E586B3E7AD96EFBC8CE8AFB7E9878DE696B0E7949FE68890 USING utf8mb4)
WHERE prompt = 'DEMO-SHOWCASE-04';
INSERT INTO ai_draft (draft_type, prompt, draft_content, draft_status, reviewed_by, reviewed_at, review_comment)
SELECT 'QUESTION', 'DEMO-SHOWCASE-03', JSON_OBJECT('questionType','TRUE_FALSE','questionContent','Care records may be written several days later.','standardAnswer','false','analysis','Care records should be timely and objective.','options',JSON_ARRAY()), 'APPROVED', u.id, NOW(), 'Approved for the formal question bank draft'
FROM sys_user u WHERE u.username = 'admin_demo' AND NOT EXISTS (SELECT 1 FROM ai_draft WHERE prompt = 'DEMO-SHOWCASE-03');
INSERT INTO ai_draft (draft_type, prompt, draft_content, draft_status, reviewed_by, reviewed_at, review_comment)
SELECT 'QUESTION', 'DEMO-SHOWCASE-04', JSON_OBJECT('questionType','SINGLE_CHOICE','questionContent','Medication may be selected without medical authorization.','standardAnswer','A','analysis','This content exceeds the training assistant boundary.','options',JSON_ARRAY()), 'REJECTED', u.id, NOW(), 'Regenerate without medical decision content'
FROM sys_user u WHERE u.username = 'admin_demo' AND NOT EXISTS (SELECT 1 FROM ai_draft WHERE prompt = 'DEMO-SHOWCASE-04');

INSERT IGNORE INTO ai_draft_source_resource (draft_id, resource_id)
SELECT d.id, r.id FROM ai_draft d JOIN training_resource r
ON r.id = CASE d.prompt WHEN 'DEMO-SHOWCASE-01' THEN 3 WHEN 'DEMO-SHOWCASE-02' THEN 2 WHEN 'DEMO-SHOWCASE-03' THEN 1 WHEN 'DEMO-SHOWCASE-05' THEN 1 ELSE 3 END
WHERE d.prompt LIKE 'DEMO-SHOWCASE-%';

INSERT INTO exam_record (exam_id, user_id, attempt_no, score, pass_status, submitted_at)
SELECT e.id, u.id, 1,
       CASE u.username WHEN 'caregiver_chen' THEN 80.00 WHEN 'caregiver_wang' THEN 100.00 ELSE 60.00 END,
       'PASSED', DATE_SUB(NOW(), INTERVAL (e.id + u.id) DAY)
FROM training_exam e JOIN sys_user u ON u.username IN ('caregiver_demo','caregiver_chen','caregiver_li','caregiver_wang')
WHERE e.exam_status = 'PUBLISHED' AND e.is_deleted = 0
ON DUPLICATE KEY UPDATE score = VALUES(score), pass_status = VALUES(pass_status);

INSERT INTO exam_answer (exam_record_id, question_id, user_answer, score)
SELECT er.id, q.id,
       CASE
         WHEN u.username IN ('caregiver_demo','caregiver_li') AND teq.id = (SELECT MIN(x.id) FROM training_exam_question x WHERE x.exam_id = e.id)
           THEN CASE q.question_type WHEN 'TRUE_FALSE' THEN IF(q.standard_answer = 'true','false','true') ELSE IF(q.standard_answer = 'A','B','A') END
         WHEN u.username = 'caregiver_chen' AND q.question_type = 'TRUE_FALSE'
           THEN IF(q.standard_answer = 'true','false','true')
         ELSE q.standard_answer
       END,
       CASE
         WHEN u.username IN ('caregiver_demo','caregiver_li') AND teq.id = (SELECT MIN(x.id) FROM training_exam_question x WHERE x.exam_id = e.id) THEN 0
         WHEN u.username = 'caregiver_chen' AND q.question_type = 'TRUE_FALSE' THEN 0
         ELSE teq.score
       END
FROM exam_record er JOIN sys_user u ON u.id = er.user_id
JOIN training_exam e ON e.id = er.exam_id
JOIN training_exam_question teq ON teq.exam_id = e.id
JOIN exam_question q ON q.id = teq.question_id
WHERE u.username IN ('caregiver_demo','caregiver_chen','caregiver_li','caregiver_wang') AND er.attempt_no = 1
ON DUPLICATE KEY UPDATE user_answer = VALUES(user_answer), score = VALUES(score);

-- A second, unsuccessful attempt gives every course three reviewable mistakes,
-- while the score dashboard still keeps the first attempt's best score.
INSERT INTO exam_record (exam_id, user_id, attempt_no, score, pass_status, submitted_at)
SELECT e.id, u.id, 2, 0.00, 'NOT_PASSED', DATE_SUB(NOW(), INTERVAL e.id HOUR)
FROM training_exam e JOIN sys_user u ON u.username = 'caregiver_demo'
WHERE e.exam_status = 'PUBLISHED' AND e.is_deleted = 0
ON DUPLICATE KEY UPDATE score = 0.00, pass_status = 'NOT_PASSED';

INSERT INTO exam_answer (exam_record_id, question_id, user_answer, score)
SELECT er.id, q.id,
       CASE q.question_type WHEN 'TRUE_FALSE' THEN IF(q.standard_answer = 'true','false','true') ELSE IF(q.standard_answer = 'A','B','A') END,
       0.00
FROM exam_record er JOIN sys_user u ON u.id = er.user_id
JOIN training_exam_question teq ON teq.exam_id = er.exam_id
JOIN exam_question q ON q.id = teq.question_id
WHERE u.username = 'caregiver_demo' AND er.attempt_no = 2
ON DUPLICATE KEY UPDATE user_answer = VALUES(user_answer), score = 0.00;
