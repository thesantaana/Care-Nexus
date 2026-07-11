USE care_nexus;

-- T-020 doctor health management schema additions.
-- This patch is executed after 001_schema.sql and is consolidated during T-023 final database import.

ALTER TABLE health_alert
  ADD COLUMN handled_at DATETIME AFTER handled_comment;

ALTER TABLE doctor_elder_authorization
  ADD CONSTRAINT fk_doctor_elder_authorized_by
    FOREIGN KEY (authorized_by) REFERENCES sys_user(id),
  ADD CONSTRAINT fk_doctor_elder_cancelled_by
    FOREIGN KEY (cancelled_by) REFERENCES sys_user(id);

ALTER TABLE health_alert
  ADD CONSTRAINT fk_health_alert_handler
    FOREIGN KEY (handled_by) REFERENCES sys_user(id);
