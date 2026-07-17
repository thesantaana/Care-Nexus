-- Repair legacy/demo AI drafts whose former source course no longer exists.
-- Existing valid source relationships are preserved.
INSERT IGNORE INTO ai_draft_source_resource (draft_id, resource_id)
SELECT d.id, (
  SELECT MIN(r.id)
  FROM training_resource r
  WHERE r.resource_status = 'PUBLISHED' AND r.is_deleted = 0
)
FROM ai_draft d
WHERE d.draft_type = 'QUESTION'
  AND NOT EXISTS (
    SELECT 1 FROM ai_draft_source_resource s WHERE s.draft_id = d.id
  )
  AND EXISTS (
    SELECT 1 FROM training_resource r
    WHERE r.resource_status = 'PUBLISHED' AND r.is_deleted = 0
  );
