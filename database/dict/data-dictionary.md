# 数据字典说明

本文档记录 T-011 MVP 数据库脚本中的主要字典值。所有内容均为演示规划，不包含真实隐私数据。

## 账号状态

| 值 | 说明 |
|---|---|
| NORMAL | 正常 |
| DISABLED | 停用 |

## 培训资源状态

| 值 | 说明 |
|---|---|
| DRAFT | 草稿 |
| PUBLISHED | 已发布 |
| OFFLINE | 已下架 |

## 培训学习状态

| 值 | 说明 |
|---|---|
| NOT_STARTED | 未开始 |
| LEARNING | 学习中 |
| EXAM_TAKEN | 已参加考核 |
| PASSED | 已通过 |
| NOT_PASSED | 未通过 |

## 护理订单主状态

| 值 | 说明 |
|---|---|
| PENDING_ASSIGN | 待分配 |
| PENDING_CONFIRM | 待确认 |
| CONFIRMED | 已确认 |
| IN_SERVICE | 服务中 |
| COMPLETED | 已完成 |
| CANCELLED | 已取消 |

## 评价和投诉状态

评价状态不在 `care_order` 中重复保存：不存在 `care_order_evaluation` 记录表示未评价，存在评价记录表示已评价。

投诉状态来源于 `care_order_complaint.complaint_status`。

| 值 | 说明 |
|---|---|
| PROCESSING | 投诉处理中 |
| PROCESSED | 投诉已处理 |

## 培训资源存储模式

| 值 | 说明 |
|---|---|
| TEXT | 文本内容 |
| LOCAL_FILE | 本地文件 |
| EXTERNAL_LINK | 外部链接 |

## 考核和题目状态

| 字段 | 值 | 说明 |
|---|---|---|
| exam_status | DRAFT | 考核草稿 |
| exam_status | PUBLISHED | 考核已发布 |
| question_status | DRAFT | 题目草稿 |
| question_status | PUBLISHED | 正式题目 |
| pass_status | PASSED | 已通过 |
| pass_status | NOT_PASSED | 未通过 |

## 用户角色与AI资料来源

- MVP 阶段用户主要业务角色以 `sys_user.main_role_id` 为唯一数据源。
- RBAC 权限通过 `sys_role_permission` 维护。
- 不设置 `sys_user_role` 多角色关系表，避免角色数据源重复。
- AI草稿通过 `ai_draft_source_resource` 关联一份或多份培训资源，`ai_draft` 本身不保存单一来源字段。

## 健康预警状态

| 值 | 说明 |
|---|---|
| PENDING | 待处理 |
| PROCESSING | 处理中 |
| CLOSED | 已关闭 |

## 文件上传限制

| 类型 | 扩展名 | 单文件大小 |
|---|---|---|
| 图片 | jpg、jpeg、png、webp | 10MB |
| 文档 | pdf、ppt、pptx、doc、docx | 50MB |
| 视频 | mp4、webm | 500MB |

文件存储名由服务端生成，原始文件名仅用于展示。
## T-012 演示账号和认证数据说明

- 演示账号统一密码为 `Demo@123456`，该密码仅用于虚构演示账号。
- 数据库只保存 BCrypt 哈希，不保存明文密码。
- `sys_user.account_status` 使用 `NORMAL` 和 `DISABLED` 表示正常和停用。
- `sys_user.is_deleted=1` 表示逻辑删除账号，旧 Token 和登录请求均不得继续使用该账号。
- T-012 可测试权限码至少包括：`system:user:view`、`system:role:view`、`training:resource:view`、`care:order:view`、`doctor:elder:view`。
