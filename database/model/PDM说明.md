# PDM说明

项目名称：CareNexus 颐联

任务编号：T-011

文档状态：已审核，T-011封板；PowerDesigner成果转T-030

更新时间：2026-07-09

## 1. PDM来源

PDM 以 `database/init/001_schema.sql` 为准。当前 SQL 目标数据库为 MySQL 8，字符集为 `utf8mb4`。

## 2. 表数量

当前物理表数量为 36 张。

| 领域 | 表数量 |
|---|---:|
| 用户权限、授权和审计 | 9 |
| 文件资源 | 1 |
| 护理培训、考核和 AI 草稿 | 14 |
| 护理服务和订单 | 7 |
| 医生健康管理 | 5 |
| 合计 | 36 |

## 3. 关键物理约束

- `sys_user.main_role_id` 外键关联 `sys_role.id`，作为 MVP 阶段用户主要业务角色的唯一数据源。
- MVP 阶段不保留 `sys_user_role` 多角色关系表，RBAC 权限通过 `sys_role_permission` 维护。
- `elder_profile.user_id` 可为空；非空时唯一关联老人登录账号。
- `elder_family_binding` 对 `(elder_id, family_user_id)` 建唯一约束，不将状态纳入唯一键。
- `doctor_elder_authorization` 对 `(doctor_user_id, elder_id)` 建唯一约束，不将状态纳入唯一键。
- `training_exam`、`exam_question`、`exam_question_option`、`training_exam_question`、`exam_record` 和 `exam_answer` 构成考核物理模型。
- `exam_record` 对 `(user_id, exam_id, attempt_no)` 建唯一约束，支持最多 3 次重考规则。
- `training_resource` 增加 `storage_mode` 和 `external_url`，支持文本、本地文件和外部链接。
- AI草稿资料来源通过 `ai_draft_source_resource(draft_id, resource_id)` 支持多资料关联。
- 评价状态以 `care_order_evaluation` 是否存在为准。
- 投诉状态以 `care_order_complaint.complaint_status` 为准。
- 联系电话使用受控保存字段 `mobile_cipher_text` / `contact_mobile_cipher_text` 和展示辅助字段 `mobile_last4` / `contact_mobile_last4`。

## 4. 待生成模型

可由 PowerDesigner 打开的模型文件和截图已拆分为 T-030 / Issue #2，最终交付前由项目负责人基于最终版 SQL 手动生成：

- `database/model/CareNexus.cdm`
- `database/model/CareNexus.pdm`
- `database/model/CareNexus-CDM.png`
- `database/model/CareNexus-PDM.png`
