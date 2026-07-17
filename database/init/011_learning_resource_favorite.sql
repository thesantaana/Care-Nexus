-- Persist caregiver course favorites in MySQL.
CREATE TABLE IF NOT EXISTS learning_resource_favorite (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_learning_resource_favorite_user_resource (user_id, resource_id),
  KEY idx_learning_resource_favorite_user_created (user_id, created_at),
  CONSTRAINT fk_learning_resource_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_learning_resource_favorite_resource FOREIGN KEY (resource_id) REFERENCES training_resource(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Keep the demo caregiver's library visibly populated without changing any
-- existing user's choices.
INSERT IGNORE INTO learning_resource_favorite (user_id, resource_id)
SELECT u.id, r.id
FROM sys_user u
JOIN training_resource r ON r.resource_status = 'PUBLISHED' AND r.is_deleted = 0
WHERE u.username = 'caregiver_demo'
ORDER BY r.id
LIMIT 1;
