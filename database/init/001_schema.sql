CREATE DATABASE IF NOT EXISTS care_nexus DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE care_nexus;

CREATE TABLE sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(64) NOT NULL,
  role_name VARCHAR(64) NOT NULL,
  role_status VARCHAR(32) NOT NULL DEFAULT 'ENABLED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_sys_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  permission_code VARCHAR(128) NOT NULL,
  permission_name VARCHAR(128) NOT NULL,
  permission_type VARCHAR(32) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_sys_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  real_name VARCHAR(64) NOT NULL,
  mobile_cipher_text VARCHAR(255),
  mobile_last4 VARCHAR(8),
  main_role_id BIGINT NOT NULL,
  account_status VARCHAR(32) NOT NULL DEFAULT 'NORMAL',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT,
  updated_by BIGINT,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_sys_user_username (username),
  KEY idx_sys_user_main_role (main_role_id),
  KEY idx_sys_user_status (account_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sys_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE sys_dict (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dict_type VARCHAR(64) NOT NULL,
  dict_code VARCHAR(64) NOT NULL,
  dict_name VARCHAR(128) NOT NULL,
  sort_no INT NOT NULL DEFAULT 0,
  dict_status VARCHAR(32) NOT NULL DEFAULT 'ENABLED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_sys_dict_type_code (dict_type, dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  operator_id BIGINT,
  operator_name VARCHAR(64),
  action VARCHAR(128) NOT NULL,
  target_type VARCHAR(64) NOT NULL,
  target_id BIGINT,
  result VARCHAR(32) NOT NULL,
  detail_summary VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_operation_log_operator_time (operator_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE file_resource (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  original_name VARCHAR(255) NOT NULL,
  storage_name VARCHAR(255) NOT NULL,
  file_type VARCHAR(64) NOT NULL,
  file_size BIGINT NOT NULL,
  relative_path VARCHAR(500) NOT NULL,
  uploaded_by BIGINT,
  file_status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  KEY idx_file_resource_type_status (file_type, file_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE training_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_name VARCHAR(128) NOT NULL,
  sort_no INT NOT NULL DEFAULT 0,
  category_status VARCHAR(32) NOT NULL DEFAULT 'ENABLED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE training_tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tag_name VARCHAR(128) NOT NULL,
  tag_status VARCHAR(32) NOT NULL DEFAULT 'ENABLED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE training_resource (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  resource_type VARCHAR(32) NOT NULL,
  storage_mode VARCHAR(32) NOT NULL DEFAULT 'TEXT',
  category_id BIGINT,
  title VARCHAR(200) NOT NULL,
  summary VARCHAR(500),
  content TEXT,
  file_resource_id BIGINT,
  external_url VARCHAR(500),
  duration_seconds INT,
  resource_status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  published_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by BIGINT,
  updated_by BIGINT,
  is_deleted TINYINT NOT NULL DEFAULT 0,
  KEY idx_training_resource_category_status (category_id, resource_status),
  KEY idx_training_resource_type_status (resource_type, resource_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE training_resource_tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  resource_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_training_resource_tag (resource_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE learning_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  training_scope VARCHAR(64) NOT NULL DEFAULT 'MVP',
  total_learning_seconds INT NOT NULL DEFAULT 0,
  latest_learning_time DATETIME,
  training_status VARCHAR(32) NOT NULL DEFAULT 'NOT_STARTED',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_learning_record_user_scope (user_id, training_scope)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE learning_access_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  access_seconds INT NOT NULL DEFAULT 0,
  accessed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_learning_access_user_time (user_id, accessed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE training_exam (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exam_name VARCHAR(128) NOT NULL,
  pass_score DECIMAL(5,2) NOT NULL DEFAULT 60.00,
  max_attempts INT NOT NULL DEFAULT 3,
  exam_status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE exam_question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  resource_id BIGINT NOT NULL,
  question_type VARCHAR(32) NOT NULL,
  question_content TEXT NOT NULL,
  standard_answer TEXT,
  analysis TEXT,
  question_status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  source_ai_draft_id BIGINT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE exam_question_option (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  question_id BIGINT NOT NULL,
  option_label VARCHAR(8) NOT NULL,
  option_content VARCHAR(500) NOT NULL,
  is_correct TINYINT NOT NULL DEFAULT 0,
  sort_no INT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_question_option_label (question_id, option_label)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE training_exam_question (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exam_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  score DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  sort_no INT NOT NULL DEFAULT 0,
  UNIQUE KEY uk_exam_question (exam_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE exam_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exam_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  attempt_no INT NOT NULL,
  score DECIMAL(5,2),
  pass_status VARCHAR(32) NOT NULL DEFAULT 'NOT_PASSED',
  submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_exam_record_attempt (user_id, exam_id, attempt_no),
  KEY idx_exam_record_user_time (user_id, submitted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE exam_answer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  exam_record_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  user_answer TEXT,
  score DECIMAL(5,2),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_exam_answer_record_question (exam_record_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ai_draft (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  draft_type VARCHAR(32) NOT NULL,
  prompt TEXT,
  draft_content TEXT NOT NULL,
  draft_status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  reviewed_by BIGINT,
  reviewed_at DATETIME,
  review_comment VARCHAR(500),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ai_draft_status (draft_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ai_draft_source_resource (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  draft_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_ai_draft_source_resource (draft_id, resource_id),
  KEY idx_ai_draft_source_resource_resource (resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE sys_user ADD CONSTRAINT fk_sys_user_main_role FOREIGN KEY (main_role_id) REFERENCES sys_role(id);
ALTER TABLE sys_role_permission ADD CONSTRAINT fk_sys_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role(id);
ALTER TABLE sys_role_permission ADD CONSTRAINT fk_sys_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission(id);
ALTER TABLE training_resource ADD CONSTRAINT fk_training_resource_category FOREIGN KEY (category_id) REFERENCES training_category(id);
ALTER TABLE training_resource ADD CONSTRAINT fk_training_resource_file FOREIGN KEY (file_resource_id) REFERENCES file_resource(id);
ALTER TABLE training_resource_tag ADD CONSTRAINT fk_training_resource_tag_resource FOREIGN KEY (resource_id) REFERENCES training_resource(id);
ALTER TABLE training_resource_tag ADD CONSTRAINT fk_training_resource_tag_tag FOREIGN KEY (tag_id) REFERENCES training_tag(id);
ALTER TABLE learning_record ADD CONSTRAINT fk_learning_record_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
ALTER TABLE learning_access_log ADD CONSTRAINT fk_learning_access_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
ALTER TABLE learning_access_log ADD CONSTRAINT fk_learning_access_resource FOREIGN KEY (resource_id) REFERENCES training_resource(id);
ALTER TABLE exam_question ADD CONSTRAINT fk_exam_question_resource FOREIGN KEY (resource_id) REFERENCES training_resource(id);
ALTER TABLE exam_question ADD CONSTRAINT fk_exam_question_ai_draft FOREIGN KEY (source_ai_draft_id) REFERENCES ai_draft(id);
ALTER TABLE exam_question_option ADD CONSTRAINT fk_question_option_question FOREIGN KEY (question_id) REFERENCES exam_question(id);
ALTER TABLE training_exam_question ADD CONSTRAINT fk_training_exam_question_exam FOREIGN KEY (exam_id) REFERENCES training_exam(id);
ALTER TABLE training_exam_question ADD CONSTRAINT fk_training_exam_question_question FOREIGN KEY (question_id) REFERENCES exam_question(id);
ALTER TABLE exam_record ADD CONSTRAINT fk_exam_record_exam FOREIGN KEY (exam_id) REFERENCES training_exam(id);
ALTER TABLE exam_record ADD CONSTRAINT fk_exam_record_user FOREIGN KEY (user_id) REFERENCES sys_user(id);
ALTER TABLE exam_answer ADD CONSTRAINT fk_exam_answer_record FOREIGN KEY (exam_record_id) REFERENCES exam_record(id);
ALTER TABLE exam_answer ADD CONSTRAINT fk_exam_answer_question FOREIGN KEY (question_id) REFERENCES exam_question(id);
ALTER TABLE ai_draft_source_resource ADD CONSTRAINT fk_ai_draft_source_draft FOREIGN KEY (draft_id) REFERENCES ai_draft(id);
ALTER TABLE ai_draft_source_resource ADD CONSTRAINT fk_ai_draft_source_resource FOREIGN KEY (resource_id) REFERENCES training_resource(id);
ALTER TABLE file_resource ADD CONSTRAINT fk_file_resource_uploader FOREIGN KEY (uploaded_by) REFERENCES sys_user(id);
