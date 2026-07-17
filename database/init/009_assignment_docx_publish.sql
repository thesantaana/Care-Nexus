SET NAMES utf8mb4;

ALTER TABLE training_assignment
  ADD COLUMN answer_analysis TEXT NULL AFTER standard_answer;

-- Restore the themed covers for the two lightweight demonstration courses.
UPDATE training_resource
SET cover_url = '/assets/course-pressure-care.png'
WHERE id = 1 AND cover_url = '/assets/default-course-cover.png';

UPDATE training_resource
SET cover_url = '/assets/course-fall-prevention.png'
WHERE id = 2 AND cover_url = '/assets/default-course-cover.png';

-- 分类与标签已从 lite 版产品入口和资源编辑流程移除。
-- 旧表暂留用于兼容已有数据，后续归档版本统一执行物理清理。
