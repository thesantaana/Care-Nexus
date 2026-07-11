USE care_nexus;

-- T-020 doctor health management permission additions.
-- This patch is executed after 002_seed_data.sql and is consolidated during T-023.

INSERT IGNORE INTO sys_permission (permission_code, permission_name, permission_type)
VALUES ('doctor:health:manage', '健康数据管理', 'ACTION');

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p
WHERE r.role_code = 'DOCTOR'
  AND p.permission_code = 'doctor:health:manage';

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p
WHERE r.role_code = 'HEALTH_MANAGER'
  AND p.permission_code = 'doctor:health:manage';
