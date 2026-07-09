USE care_nexus;

INSERT INTO sys_role (role_code, role_name) VALUES
('ADMIN', '管理员'),
('OPERATOR', '运营人员'),
('TRAINING_ADMIN', '培训管理员'),
('CAREGIVER', '护工/护理人员'),
('ELDER', '老人'),
('FAMILY', '家属'),
('DOCTOR', '医生'),
('HEALTH_MANAGER', '健康管理人员');

INSERT INTO sys_permission (permission_code, permission_name, permission_type) VALUES
('training:resource:manage', '培训资源管理', 'ACTION'),
('care:order:assign', '护理订单分配', 'ACTION'),
('doctor:elder:authorize', '医生老人授权', 'ACTION'),
('system:user:manage', '用户管理', 'ACTION');

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'admin_demo', '{bcrypt}DEMO_HASH_REPLACE_BEFORE_REAL_USE', '演示管理员', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'ADMIN';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'trainer_demo', '{bcrypt}DEMO_HASH_REPLACE_BEFORE_REAL_USE', '演示培训管理员', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'TRAINING_ADMIN';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'doctor_demo', '{bcrypt}DEMO_HASH_REPLACE_BEFORE_REAL_USE', '演示医生', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'DOCTOR';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'caregiver_demo', '{bcrypt}DEMO_HASH_REPLACE_BEFORE_REAL_USE', '演示护工', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'CAREGIVER';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'elder_demo', '{bcrypt}DEMO_HASH_REPLACE_BEFORE_REAL_USE', '演示老人', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'ELDER';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'family_demo', '{bcrypt}DEMO_HASH_REPLACE_BEFORE_REAL_USE', '演示家属', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'FAMILY';

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r JOIN sys_permission p
WHERE r.role_code = 'ADMIN';

INSERT INTO sys_dict (dict_type, dict_code, dict_name, sort_no) VALUES
('ORDER_STATUS', 'PENDING_ASSIGN', '待分配', 1),
('ORDER_STATUS', 'PENDING_CONFIRM', '待确认', 2),
('ORDER_STATUS', 'CONFIRMED', '已确认', 3),
('ORDER_STATUS', 'IN_SERVICE', '服务中', 4),
('ORDER_STATUS', 'COMPLETED', '已完成', 5),
('ORDER_STATUS', 'CANCELLED', '已取消', 6),
('TRAINING_RESOURCE_TYPE', 'ARTICLE', '文章', 1),
('TRAINING_RESOURCE_TYPE', 'VIDEO', '视频', 2),
('TRAINING_RESOURCE_TYPE', 'PPT', 'PPT', 3),
('STORAGE_MODE', 'TEXT', '文本', 1),
('STORAGE_MODE', 'LOCAL_FILE', '本地文件', 2),
('STORAGE_MODE', 'EXTERNAL_LINK', '外部链接', 3);

INSERT INTO elder_profile (user_id, elder_name, gender, birth_date, mobile_cipher_text, mobile_last4, health_summary)
SELECT id, '演示老人', 'UNKNOWN', '1950-01-01', NULL, NULL, '仅用于演示的模拟健康摘要'
FROM sys_user WHERE username = 'elder_demo';

INSERT INTO elder_family_binding (elder_id, family_user_id, binding_status, verify_type)
SELECT e.id, u.id, 'ACTIVE', 'BINDING_CODE'
FROM elder_profile e JOIN sys_user u ON u.username = 'family_demo'
WHERE e.elder_name = '演示老人';

INSERT INTO doctor_elder_authorization (doctor_user_id, elder_id, auth_status, authorized_by)
SELECT d.id, e.id, 'ACTIVE', a.id
FROM sys_user d JOIN elder_profile e JOIN sys_user a
WHERE d.username = 'doctor_demo' AND a.username = 'admin_demo';

INSERT INTO training_category (category_name, sort_no) VALUES
('基础护理', 1),
('康复护理', 2);

INSERT INTO training_tag (tag_name) VALUES
('压疮预防'),
('跌倒预防'),
('安全规范');

INSERT INTO training_resource (resource_type, storage_mode, category_id, title, summary, content, external_url, duration_seconds, resource_status, published_at, created_by)
SELECT 'ARTICLE', 'TEXT', c.id, '压疮预防基础', '压疮预防基础培训文章', '演示文章内容，后续替换为正式培训资料。', NULL, 900, 'PUBLISHED', NOW(), u.id
FROM training_category c JOIN sys_user u
WHERE c.category_name = '基础护理' AND u.username = 'trainer_demo';

INSERT INTO training_resource (resource_type, storage_mode, category_id, title, summary, content, external_url, duration_seconds, resource_status, published_at, created_by)
SELECT 'VIDEO', 'EXTERNAL_LINK', c.id, '跌倒预防视频', '跌倒预防演示视频链接', NULL, 'https://example.invalid/training/fall-prevention', 1200, 'PUBLISHED', NOW(), u.id
FROM training_category c JOIN sys_user u
WHERE c.category_name = '康复护理' AND u.username = 'trainer_demo';

INSERT INTO training_exam (exam_name, pass_score, max_attempts, exam_status)
VALUES ('护理培训MVP考核', 60.00, 3, 'PUBLISHED');

INSERT INTO exam_question (question_type, question_content, standard_answer, analysis, question_status)
VALUES
('SINGLE_CHOICE', '压疮预防应优先关注哪类风险？', 'A', '长期卧床和局部受压是核心风险。', 'PUBLISHED'),
('TRUE_FALSE', '跌倒预防只需要在老人已经跌倒后处理。', 'false', '应提前识别风险并预防。', 'PUBLISHED'),
('SHORT_ANSWER', '简述护理服务记录应包含的关键内容。', '服务时间、服务内容、异常情况和处理说明。', '简答题由培训管理员人工评分。', 'PUBLISHED');

INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'A', '长期卧床和局部受压', 1, 1 FROM exam_question WHERE question_type = 'SINGLE_CHOICE';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'B', '只关注饮食偏好', 0, 2 FROM exam_question WHERE question_type = 'SINGLE_CHOICE';

INSERT INTO training_exam_question (exam_id, question_id, score, sort_no)
SELECT e.id, q.id, 30.00, 1 FROM training_exam e JOIN exam_question q WHERE e.exam_name = '护理培训MVP考核' AND q.question_type = 'SINGLE_CHOICE';
INSERT INTO training_exam_question (exam_id, question_id, score, sort_no)
SELECT e.id, q.id, 30.00, 2 FROM training_exam e JOIN exam_question q WHERE e.exam_name = '护理培训MVP考核' AND q.question_type = 'TRUE_FALSE';
INSERT INTO training_exam_question (exam_id, question_id, score, sort_no)
SELECT e.id, q.id, 40.00, 3 FROM training_exam e JOIN exam_question q WHERE e.exam_name = '护理培训MVP考核' AND q.question_type = 'SHORT_ANSWER';

INSERT INTO ai_draft (draft_type, prompt, draft_content, draft_status)
SELECT 'QUESTION_DRAFT', '基于资料生成单选题草稿', '【草稿】压疮预防单选题示例', 'DRAFT'
FROM training_resource WHERE title = '压疮预防基础';

INSERT INTO ai_draft_source_resource (draft_id, resource_id)
SELECT d.id, r.id
FROM ai_draft d JOIN training_resource r
WHERE d.draft_type = 'QUESTION_DRAFT' AND r.title = '压疮预防基础';

INSERT INTO care_service_item (service_name, service_category, description, service_status)
VALUES ('上门基础护理', '基础护理', '演示服务项目，后续由运营人员维护。', 'ENABLED');

INSERT INTO care_address (owner_user_id, elder_id, contact_name, contact_mobile_cipher_text, contact_mobile_last4, address_detail, is_default)
SELECT f.id, e.id, '演示联系人', NULL, '0000', '演示地址，不包含真实住址', 1
FROM sys_user f JOIN elder_profile e
WHERE f.username = 'family_demo';

INSERT INTO care_order (elder_id, order_user_id, service_item_id, address_id, appointment_time, assigned_caregiver_id, order_status)
SELECT e.id, f.id, s.id, a.id, DATE_ADD(NOW(), INTERVAL 1 DAY), c.id, 'PENDING_CONFIRM'
FROM elder_profile e JOIN sys_user f JOIN care_service_item s JOIN care_address a JOIN sys_user c
WHERE f.username = 'family_demo' AND c.username = 'caregiver_demo' AND s.service_name = '上门基础护理';

INSERT INTO health_record (elder_id, recorder_id, systolic_pressure, diastolic_pressure, heart_rate, body_temperature, record_time, remark)
SELECT e.id, d.id, 125, 80, 76, 36.6, NOW(), '演示健康记录'
FROM elder_profile e JOIN sys_user d
WHERE d.username = 'doctor_demo';
