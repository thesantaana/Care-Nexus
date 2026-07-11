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
('system:user:view', '用户查看', 'ACTION'),
('system:role:view', '角色查看', 'ACTION'),
('training:resource:view', '培训资源查看', 'ACTION'),
('care:order:view', '护理订单查看', 'ACTION'),
('doctor:elder:view', '授权老人查看', 'ACTION'),
('training:resource:manage', '培训资源管理', 'ACTION'),
('care:order:assign', '护理订单分配', 'ACTION'),
('doctor:elder:authorize', '医生老人授权', 'ACTION'),
('doctor:health:manage', '健康数据管理', 'ACTION'),
('system:user:manage', '用户管理', 'ACTION');

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'admin_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示管理员', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'ADMIN';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'operator_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示运营人员', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'OPERATOR';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'trainer_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示培训管理员', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'TRAINING_ADMIN';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'doctor_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示医生', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'DOCTOR';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'health_manager_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示健康管理人员', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'HEALTH_MANAGER';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'caregiver_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示护工', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'CAREGIVER';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'elder_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示老人', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'ELDER';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'family_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示家属', NULL, NULL, id, 'NORMAL'
FROM sys_role WHERE role_code = 'FAMILY';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status)
SELECT 'disabled_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示停用账号', NULL, NULL, id, 'DISABLED'
FROM sys_role WHERE role_code = 'ELDER';

INSERT INTO sys_user (username, password_hash, real_name, mobile_cipher_text, mobile_last4, main_role_id, account_status, is_deleted)
SELECT 'deleted_demo', '$2a$12$e/jPGifBDKCTBu0Yenv2leiX7KQ18J5P7r48W0Zu4CCAWH0JVmP5u', '演示逻辑删除账号', NULL, NULL, id, 'NORMAL', 1
FROM sys_role WHERE role_code = 'ELDER';

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r JOIN sys_permission p
WHERE r.role_code = 'ADMIN';

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r JOIN sys_permission p
WHERE r.role_code = 'TRAINING_ADMIN' AND p.permission_code IN ('training:resource:view', 'training:resource:manage');

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r JOIN sys_permission p
WHERE r.role_code = 'OPERATOR' AND p.permission_code IN ('care:order:view', 'care:order:assign');

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r JOIN sys_permission p
WHERE r.role_code = 'CAREGIVER' AND p.permission_code IN ('care:order:view', 'training:resource:view');

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r JOIN sys_permission p
WHERE r.role_code = 'DOCTOR'
  AND p.permission_code IN ('doctor:elder:view', 'doctor:health:manage');

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r JOIN sys_permission p
WHERE r.role_code = 'HEALTH_MANAGER'
  AND p.permission_code IN ('doctor:elder:view', 'doctor:elder:authorize', 'doctor:health:manage');

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
('安全照护', 2),
('感染预防', 3);

INSERT INTO training_tag (tag_name) VALUES
('压疮预防'),
('皮肤观察'),
('跌倒预防'),
('环境安全'),
('手卫生'),
('感染防控'),
('安全规范');

INSERT INTO training_resource (resource_type, storage_mode, category_id, title, summary, content, external_url, duration_seconds, resource_status, published_at, created_by)
SELECT 'ARTICLE', 'TEXT', c.id, '老年人压疮风险观察与日常预防',
       '介绍压疮风险观察、变换体位、保持皮肤和床单位清洁干燥、使用减压器具和记录要求。',
       '压疮风险较高的老年人包括长期卧床、久坐、活动受限或感觉减退者。护工应观察受压部位皮肤颜色、干燥程度和有无破损，检查纸尿裤、衣物和被褥是否干燥平整。应按照护理计划协助变换体位，保持皮肤清洁干燥，整理床铺并清除碎屑，按要求使用减压器具。发现持续发红、破损、水疱、渗液、异味或疼痛，应立即报告护理负责人或医护人员，并做好记录。',
       NULL, 900, 'PUBLISHED', NOW(), u.id
FROM training_category c JOIN sys_user u
WHERE c.category_name = '基础护理' AND u.username = 'trainer_demo';

INSERT INTO training_resource (resource_type, storage_mode, category_id, title, summary, content, external_url, duration_seconds, resource_status, published_at, created_by)
SELECT 'ARTICLE', 'TEXT', c.id, '老年人跌倒预防与安全陪护',
       '介绍跌倒风险、环境整理、起身行走协助、药物反应观察和跌倒后应急处理原则。',
       '老年人跌倒可能导致骨折、头部损伤和活动能力下降。居室、厕所、走廊和活动区域应保持地面干燥、通道无障碍物。清洁地面前及过程中应放置明显安全警示标志。常用物品应放在容易拿到的位置。高风险老人起床、行走和如厕时，应使用合适助行器具或由工作人员协助，不催促老人快速起身。跌倒发生后不要立即强行扶起，应先观察意识、呼吸、疼痛、出血和肢体异常，立即呼叫专业人员并按流程记录。',
       NULL, 900, 'PUBLISHED', NOW(), u.id
FROM training_category c JOIN sys_user u
WHERE c.category_name = '安全照护' AND u.username = 'trainer_demo';

INSERT INTO training_resource (resource_type, storage_mode, category_id, title, summary, content, external_url, duration_seconds, resource_status, published_at, created_by)
SELECT 'ARTICLE', 'TEXT', c.id, '照护工作中的手卫生五个关键时刻',
       '讲解照护过程中必须执行手卫生的五个关键时刻，以及洗手、手消毒和手套使用原则。',
       '手卫生是阻断病原体通过双手传播的重要措施。五个关键时刻包括接触老年人之前、清洁或无菌相关操作之前、可能接触血液或排泄物等体液之后、接触老年人之后、接触老年人周围环境和物品之后。双手有可见污物时应使用流动水和洗手液洗手；双手无可见污物且机构允许时可使用速干手消毒剂。摘除手套后仍应进行手卫生，手套不能代替手卫生。',
       NULL, 720, 'PUBLISHED', NOW(), u.id
FROM training_category c JOIN sys_user u
WHERE c.category_name = '感染预防' AND u.username = 'trainer_demo';

INSERT INTO training_exam (exam_name, pass_score, max_attempts, exam_status)
VALUES ('护工基础安全照护 MVP 考核', 48.00, 3, 'PUBLISHED');

INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'SINGLE_CHOICE', '下列哪一组措施最符合压疮日常预防要求？', 'B',
       '压疮预防要求变换体位、保持皮肤清洁、整理床铺并清除碎屑。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '老年人压疮风险观察与日常预防';
INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'TRUE_FALSE', '只要皮肤还没有破损，就不需要观察颜色变化或记录。', 'false',
       '压疮预防强调早期观察和记录。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '老年人压疮风险观察与日常预防';
INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'SINGLE_CHOICE', '下列哪项属于压疮日常预防措施？', 'D',
       '变换体位、皮肤清洁干燥、整理床铺和异常记录均属于预防措施。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '老年人压疮风险观察与日常预防';
INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'SINGLE_CHOICE', '清洁养老机构走廊地面时，正确做法是？', 'C',
       '清洁前及清洁过程中应放置安全警示标志，并及时处理湿滑区域。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '老年人跌倒预防与安全陪护';
INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'TRUE_FALSE', '有跌倒风险的老年人只要身边有助行器，就可以无人协助起床、行走和如厕。', 'false',
       '应根据风险评估提供助行器具或工作人员协助。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '老年人跌倒预防与安全陪护';
INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'SINGLE_CHOICE', '跌倒发生后，护工首先应当怎么做？', 'B',
       '跌倒发生后不要立即强行扶起，应先观察并呼叫专业人员。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '老年人跌倒预防与安全陪护';
INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'SINGLE_CHOICE', '下列哪一种情况不应省略手卫生？', 'D',
       '摘除手套后、接触老年人前后、接触周围环境后均需手卫生。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '照护工作中的手卫生五个关键时刻';
INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'TRUE_FALSE', '佩戴一次性手套后，可以完全代替洗手或手消毒。', 'false',
       '手套不能代替手卫生，摘除手套后仍需手卫生。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '照护工作中的手卫生五个关键时刻';
INSERT INTO exam_question (resource_id, question_type, question_content, standard_answer, analysis, question_status)
SELECT r.id, 'SINGLE_CHOICE', '下列哪项属于手卫生五个关键时刻？', 'A',
       '接触老年人前、清洁操作前、体液暴露后、接触老年人后和接触周围环境后都是关键时刻。', 'PUBLISHED'
FROM training_resource r WHERE r.title = '照护工作中的手卫生五个关键时刻';

INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'A', '长时间保持同一体位，减少翻动', 0, 1 FROM exam_question WHERE question_content = '下列哪一组措施最符合压疮日常预防要求？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'B', '定时变换体位、保持皮肤清洁、整理床铺并清除碎屑', 1, 2 FROM exam_question WHERE question_content = '下列哪一组措施最符合压疮日常预防要求？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'C', '皮肤发红时用力按摩受压部位', 0, 3 FROM exam_question WHERE question_content = '下列哪一组措施最符合压疮日常预防要求？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'D', '只有皮肤破损后才需要记录', 0, 4 FROM exam_question WHERE question_content = '下列哪一组措施最符合压疮日常预防要求？';

INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'A', '清洁后自然晾干，不需要提示', 0, 1 FROM exam_question WHERE question_content = '清洁养老机构走廊地面时，正确做法是？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'B', '只口头提醒一位老年人', 0, 2 FROM exam_question WHERE question_content = '清洁养老机构走廊地面时，正确做法是？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'C', '清洁前及清洁过程中放置安全警示标志，并及时处理湿滑区域', 1, 3 FROM exam_question WHERE question_content = '清洁养老机构走廊地面时，正确做法是？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'D', '让行动不便的老年人快速通过', 0, 4 FROM exam_question WHERE question_content = '清洁养老机构走廊地面时，正确做法是？';

INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'A', '保持皮肤清洁干燥', 0, 1 FROM exam_question WHERE question_content = '下列哪项属于压疮日常预防措施？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'B', '按护理计划变换体位', 0, 2 FROM exam_question WHERE question_content = '下列哪项属于压疮日常预防措施？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'C', '观察受压部位并做好记录', 0, 3 FROM exam_question WHERE question_content = '下列哪项属于压疮日常预防措施？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'D', '以上都属于', 1, 4 FROM exam_question WHERE question_content = '下列哪项属于压疮日常预防措施？';

INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'A', '立即强行扶起老人', 0, 1 FROM exam_question WHERE question_content = '跌倒发生后，护工首先应当怎么做？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'B', '观察意识、呼吸、疼痛和出血情况，并呼叫专业人员', 1, 2 FROM exam_question WHERE question_content = '跌倒发生后，护工首先应当怎么做？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'C', '让老人自行回房休息', 0, 3 FROM exam_question WHERE question_content = '跌倒发生后，护工首先应当怎么做？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'D', '不记录，等待老人自行恢复', 0, 4 FROM exam_question WHERE question_content = '跌倒发生后，护工首先应当怎么做？';

INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'A', '摘除接触排泄物时使用的手套后', 0, 1 FROM exam_question WHERE question_content = '下列哪一种情况不应省略手卫生？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'B', '接触老年人床栏后', 0, 2 FROM exam_question WHERE question_content = '下列哪一种情况不应省略手卫生？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'C', '接触老年人之前', 0, 3 FROM exam_question WHERE question_content = '下列哪一种情况不应省略手卫生？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'D', '以上都不应省略', 1, 4 FROM exam_question WHERE question_content = '下列哪一种情况不应省略手卫生？';

INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'A', '接触老年人之前', 1, 1 FROM exam_question WHERE question_content = '下列哪项属于手卫生五个关键时刻？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'B', '只在下班前洗手', 0, 2 FROM exam_question WHERE question_content = '下列哪项属于手卫生五个关键时刻？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'C', '只有双手明显很脏时才需要手卫生', 0, 3 FROM exam_question WHERE question_content = '下列哪项属于手卫生五个关键时刻？';
INSERT INTO exam_question_option (question_id, option_label, option_content, is_correct, sort_no)
SELECT id, 'D', '戴手套后永远不用洗手', 0, 4 FROM exam_question WHERE question_content = '下列哪项属于手卫生五个关键时刻？';

INSERT INTO training_exam_question (exam_id, question_id, score, sort_no)
SELECT e.id, q.id, CASE WHEN q.question_type = 'TRUE_FALSE' THEN 5.00 ELSE 7.50 END,
       ROW_NUMBER() OVER (ORDER BY q.id)
FROM training_exam e JOIN exam_question q
WHERE e.exam_name = '护工基础安全照护 MVP 考核';

INSERT INTO ai_draft (draft_type, prompt, draft_content, draft_status)
SELECT 'QUESTION_DRAFT', '基于资料生成单选题草稿', '【草稿】压疮预防单选题示例', 'DRAFT'
FROM training_resource WHERE title = '老年人压疮风险观察与日常预防';

INSERT INTO ai_draft_source_resource (draft_id, resource_id)
SELECT d.id, r.id
FROM ai_draft d JOIN training_resource r
WHERE d.draft_type = 'QUESTION_DRAFT' AND r.title = '老年人压疮风险观察与日常预防';

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
