# CareNexus 轻量版 PDM 说明

项目名称：CareNexus 颐联  
更新时间：2026-07-15  
目标数据库：MySQL 8  
状态：待生成 PowerDesigner PDM

## 1. 建模基线

PDM 必须从 `database/init/001` 至 `008` 执行后的最终结构生成。当前最终表数量为 28。

- `001_schema.sql`：22 张核心表。
- `004_course_interactions.sql`：4 张互动和作业表。
- `007_social_discussions.sql`：2 张点赞表，并为回复增加父回复外键。

## 2. 物理表分组

| 数据域 | 表数量 | 表 |
|---|---:|---|
| 账号权限 | 5 | `sys_role`、`sys_permission`、`sys_user`、`sys_role_permission`、`sys_dict` |
| 审计文件 | 2 | `operation_log`、`file_resource` |
| 培训学习 | 7 | `training_category`、`training_tag`、`training_resource`、`training_resource_tag`、`learning_record`、`learning_access_log`、`training_note` |
| 题库考核 | 6 | `training_exam`、`exam_question`、`exam_question_option`、`training_exam_question`、`exam_record`、`exam_answer` |
| AI | 2 | `ai_draft`、`ai_draft_source_resource` |
| 互动作业 | 6 | `training_discussion`、`training_discussion_reply`、两张点赞表、`training_assignment`、`training_assignment_submission` |

## 3. 类型与命名规则

- 主键：`BIGINT AUTO_INCREMENT`。
- 业务状态：`VARCHAR(32)`。
- 名称和标题：`VARCHAR(64–200)`。
- 摘要与审核意见：`VARCHAR(500)`。
- 普通正文：`TEXT`。
- 富文本笔记：`LONGTEXT`。
- 金额不在轻量版范围；分数使用 `DECIMAL(5,2)`。
- 时间使用 `DATETIME`。
- 逻辑删除使用 `TINYINT`。
- 表、字段、索引和约束统一 snake_case。

## 4. 关键唯一约束

- `sys_role.role_code`
- `sys_permission.permission_code`
- `sys_user.username`
- `sys_role_permission(role_id, permission_id)`
- `sys_dict(dict_type, dict_code)`
- `training_resource_tag(resource_id, tag_id)`
- `learning_record(user_id, training_scope)`
- `training_note(user_id, resource_id)`
- `training_exam.resource_id`
- `exam_question_option(question_id, option_label)`
- `training_exam_question(exam_id, question_id)`
- `exam_record(user_id, exam_id, attempt_no)`
- `exam_answer(exam_record_id, question_id)`
- `ai_draft_source_resource(draft_id, resource_id)`
- `training_discussion_like(discussion_id, user_id)`
- `training_discussion_reply_like(reply_id, user_id)`
- `training_assignment_submission(assignment_id, user_id)`

## 5. 关键外键

PDM 应完整表达：

- 用户到主角色。
- 角色权限关联。
- 培训资源到分类和文件。
- 资源标签关系。
- 学习访问和笔记到用户、课程。
- 考核、题目到课程。
- 题目选项、考核题目关系。
- 考试记录和答案。
- AI 草稿来源和正式题目来源。
- 讨论、回复、嵌套父回复、点赞。
- 作业和提交。

删除策略以最终 SQL 为准。点赞和父回复相关外键包含 `ON DELETE CASCADE` 的，应在 PDM 中保留。

## 6. 索引检查

重点检查：

- 用户角色、状态。
- 培训资源分类+状态、类型+状态。
- 文件类型+状态。
- 学习访问用户+时间。
- 笔记用户+更新时间。
- 考试记录用户+提交时间。
- AI 草稿状态。
- 讨论课程+时间、回复主题+时间、父回复。
- 所有唯一关系索引。

## 7. 生成步骤

1. 创建干净 MySQL 8 数据库。
2. 顺序执行 001–008。
3. 验证表数为 28，脚本无错误。
4. PowerDesigner 连接数据库反向工程，或从最终 SQL 导入。
5. 设置 MySQL 8 DBMS。
6. 按数据域分图排布。
7. 检查所有 PK、FK、AK、索引、默认值和字段注释。
8. 保存 `CareNexus-Lite.pdm`。
9. 导出 `CareNexus-Lite-PDM.png`。
10. 将验证结果追加到 `TEST_LOG.md`。

## 8. 验收标准

- 表数量 28。
- 表名和字段与最终数据库一致。
- 不出现 care/doctor 完整版表。
- 关键唯一约束和外键无遗漏。
- 自关联回复关系正确。
- 图片清晰、分区合理、无大面积重叠。
- PDM 可重新生成与 `001_schema.sql` 兼容的结构说明。

## 9. 当前限制

本文件只是生成说明，不是 PDM 成果。`.pdm` 和 PNG 尚未提交；完成前 `TASKS.md` 中数据模型任务保持 TODO。