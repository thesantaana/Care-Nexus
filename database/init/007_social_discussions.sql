ALTER TABLE training_discussion_reply
  ADD COLUMN parent_reply_id BIGINT NULL AFTER discussion_id,
  ADD KEY idx_training_reply_parent (parent_reply_id),
  ADD CONSTRAINT fk_training_reply_parent
    FOREIGN KEY (parent_reply_id) REFERENCES training_discussion_reply(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS training_discussion_like (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  discussion_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_training_discussion_like (discussion_id, user_id),
  CONSTRAINT fk_training_discussion_like_topic FOREIGN KEY (discussion_id)
    REFERENCES training_discussion(id) ON DELETE CASCADE,
  CONSTRAINT fk_training_discussion_like_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS training_discussion_reply_like (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reply_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_training_reply_like (reply_id, user_id),
  CONSTRAINT fk_training_reply_like_reply FOREIGN KEY (reply_id)
    REFERENCES training_discussion_reply(id) ON DELETE CASCADE,
  CONSTRAINT fk_training_reply_like_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
