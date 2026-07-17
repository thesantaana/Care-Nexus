# CareNexus Lite 数据库

数据库名：`care_nexus`，要求 MySQL 8 和 `utf8mb4`。

按编号执行：

1. `001_schema.sql`：基础账号、培训、考核、AI、文件和审计结构。
2. `002_seed_data.sql`：角色、权限、演示账号和基础培训数据。
3. `003_add_account_course_images.sql`：用户头像与课程封面。
4. `004_course_interactions.sql`：笔记、讨论与作业基础。
5. `005_demo_learning_progress.sql`：学习进度演示数据。
6. `006_multi_question_assignments.sql`：多题作业。
7. `007_social_discussions.sql`：社交式讨论、回复和点赞。
8. `008_demo_showcase_data.sql`：答辩展示数据与中文修复。

脚本用于本地演示，生产环境需改用正式迁移工具、独立账号、备份和密钥管理。
