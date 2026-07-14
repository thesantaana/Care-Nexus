CREATE TABLE IF NOT EXISTS training_discussion (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  resource_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  title VARCHAR(160) NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_training_discussion_resource_time (resource_id, created_at),
  CONSTRAINT fk_training_discussion_resource FOREIGN KEY (resource_id) REFERENCES training_resource(id),
  CONSTRAINT fk_training_discussion_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS training_discussion_reply (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  discussion_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_training_discussion_reply_topic_time (discussion_id, created_at),
  CONSTRAINT fk_training_discussion_reply_topic FOREIGN KEY (discussion_id) REFERENCES training_discussion(id),
  CONSTRAINT fk_training_discussion_reply_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS training_assignment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  resource_id BIGINT NOT NULL,
  title VARCHAR(160) NOT NULL,
  assignment_type VARCHAR(32) NOT NULL,
  content TEXT NOT NULL,
  options_json TEXT,
  standard_answer VARCHAR(500),
  assignment_status VARCHAR(32) NOT NULL DEFAULT 'PUBLISHED',
  created_by BIGINT NOT NULL,
  due_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_training_assignment_resource_status (resource_id, assignment_status),
  CONSTRAINT fk_training_assignment_resource FOREIGN KEY (resource_id) REFERENCES training_resource(id),
  CONSTRAINT fk_training_assignment_creator FOREIGN KEY (created_by) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS training_assignment_submission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  assignment_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  answer_content TEXT NOT NULL,
  score DECIMAL(5,2),
  submission_status VARCHAR(32) NOT NULL DEFAULT 'SUBMITTED',
  submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_training_assignment_submission (assignment_id, user_id),
  CONSTRAINT fk_training_assignment_submission_assignment FOREIGN KEY (assignment_id) REFERENCES training_assignment(id),
  CONSTRAINT fk_training_assignment_submission_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO training_assignment (resource_id, title, assignment_type, content, options_json,
  standard_answer, created_by)
SELECT r.id, CONCAT(r.title, '课后判断'), 'TRUE_FALSE',
  CONCAT('完成《', r.title, '》学习后，请判断：照护操作应遵循课程中的规范流程。'),
  '["正确","错误"]', '正确', u.id
FROM training_resource r
JOIN sys_user u ON u.username = 'admin_demo'
WHERE r.resource_status = 'PUBLISHED'
  AND NOT EXISTS (SELECT 1 FROM training_assignment a WHERE a.resource_id = r.id)
LIMIT 3;
