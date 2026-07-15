# CareNexus 轻量版数据库

更新时间：2026-07-15  
数据库：MySQL 8  
最终表数量：28

## 1. 目录结构

```text
database/
├── init/
│   ├── 001_schema.sql
│   ├── 002_seed_data.sql
│   ├── 003_add_account_course_images.sql
│   ├── 004_course_interactions.sql
│   ├── 005_demo_learning_progress.sql
│   ├── 006_multi_question_assignments.sql
│   ├── 007_social_discussions.sql
│   └── 008_demo_showcase_data.sql
├── dict/data-dictionary.md
└── model/
    ├── README.md
    ├── CDM说明.md
    └── PDM说明.md
```

## 2. 脚本说明

| 顺序 | 文件 | 作用 |
|---:|---|---|
| 1 | `001_schema.sql` | 创建数据库、22 张核心表、索引和外键 |
| 2 | `002_seed_data.sql` | 两角色、权限、演示账号、课程、题库和逐课考核 |
| 3 | `003_add_account_course_images.sql` | 账号头像与课程封面兼容调整 |
| 4 | `004_course_interactions.sql` | 新增讨论、回复、作业和提交 4 张表 |
| 5 | `005_demo_learning_progress.sql` | 演示学习访问、成绩和进度 |
| 6 | `006_multi_question_assignments.sql` | 多题型作业演示数据 |
| 7 | `007_social_discussions.sql` | 嵌套回复及主题/回复点赞 2 张表 |
| 8 | `008_demo_showcase_data.sql` | 答辩展示账号、笔记、讨论、作业和 AI 数据 |

最终表数：22 + 4 + 2 = 28。

## 3. 初始化

使用 MySQL 8 客户端按顺序执行：

```sql
SOURCE database/init/001_schema.sql;
SOURCE database/init/002_seed_data.sql;
SOURCE database/init/003_add_account_course_images.sql;
SOURCE database/init/004_course_interactions.sql;
SOURCE database/init/005_demo_learning_progress.sql;
SOURCE database/init/006_multi_question_assignments.sql;
SOURCE database/init/007_social_discussions.sql;
SOURCE database/init/008_demo_showcase_data.sql;
```

Windows 中文路径可能导致 `SOURCE` 失败，可将脚本复制到纯 ASCII 临时路径后执行。中文种子数据建议使用：

```text
mysql --default-character-set=utf8mb4
```

## 4. 环境变量

后端读取：

```text
DB_URL=jdbc:mysql://localhost:3306/care_nexus?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
DB_USERNAME=...
DB_PASSWORD=...
```

不要把真实数据库密码提交到仓库。

## 5. 验证

### 表数量

```sql
SELECT COUNT(*) AS table_count
FROM information_schema.tables
WHERE table_schema = 'care_nexus';
```

预期：`28`。

### 角色与账号

```sql
SELECT role_code, role_name FROM sys_role;
SELECT username, account_status, is_deleted FROM sys_user;
```

应至少存在：

- `ADMIN`
- `CAREGIVER`
- `admin_demo`
- `caregiver_demo`
- `disabled_demo`
- `deleted_demo`

### 课程和考核

```sql
SELECT id, title, resource_status FROM training_resource;
SELECT resource_id, exam_name, pass_score, exam_status FROM training_exam;
```

### 互动与作业

```sql
SELECT COUNT(*) FROM training_discussion;
SELECT COUNT(*) FROM training_discussion_reply;
SELECT COUNT(*) FROM training_assignment;
SELECT COUNT(*) FROM training_assignment_submission;
```

## 6. 数据域

- 账号与权限：5 张。
- 审计与文件：2 张。
- 培训资源与学习：7 张。
- 题库与考核：6 张。
- AI：2 张。
- 互动与作业：6 张。

详细字段和字典值见 `dict/data-dictionary.md`；领域关系见 `docs/design/数据库设计.md`。

## 7. 演示数据

演示密码统一为 `Demo@123456`，数据库仅保存 BCrypt 哈希。演示数据包含：

- 三门养老护理培训课程。
- 每门课程对应考核和客观题。
- 护工学习、考试和成绩数据。
- 富文本笔记。
- 讨论、回复、点赞和课后作业。
- AI 题目草稿展示数据。

这些数据仅用于本地开发和答辩，生产环境必须删除或更换。

## 8. 迁移规则

- 全新环境必须按 001–008 顺序执行。
- 新结构优先通过新增编号脚本迁移，不随意改写已交付脚本语义。
- 修改表结构时同步更新 Entity、Mapper、数据字典、数据库设计、API 和测试。
- 必须在干净 MySQL 8 环境验证后才能记录为通过。
- `TEST_LOG.md` 中应记录版本、命令、表数、约束和错误处理。

## 9. 范围说明

轻量版数据库不包含护理订单、老人家属、医生健康档案、预警、随访和评估表。早期文档中的 36 表结构属于完整版 `develop`，不适用于当前分支。