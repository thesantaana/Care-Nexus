ALTER TABLE sys_user
    ADD COLUMN avatar_url VARCHAR(500) NOT NULL DEFAULT '/assets/default-avatar.png' AFTER real_name;

ALTER TABLE training_resource
    ADD COLUMN cover_url VARCHAR(500) NOT NULL DEFAULT '/assets/default-course-cover.png' AFTER summary;
