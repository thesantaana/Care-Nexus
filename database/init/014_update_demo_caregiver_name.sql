-- Keep the demo caregiver's account name consistent across login and reports.
UPDATE sys_user
SET real_name = '隋咏轩',
    updated_at = CURRENT_TIMESTAMP
WHERE username = 'caregiver_demo'
  AND is_deleted = 0;
