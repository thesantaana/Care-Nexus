# CareNexus 轻量版数据字典

更新时间：2026-07-15  
数据库基线：MySQL 8，001–008 脚本，28 张表

## 1. 角色与权限

### 角色

| 代码 | 名称 | 说明 |
|---|---|---|
| `ADMIN` | 管理员 | 培训内容、题库考核、AI 草稿和成绩管理 |
| `CAREGIVER` | 护工/护理人员 | 课程学习、考核、笔记、互动和作业 |

### 权限码

| 权限码 | 说明 |
|---|---|
| `system:user:view` | 用户查看与基础 RBAC 验证 |
| `system:role:view` | 角色查看 |
| `system:user:manage` | 用户管理基础能力 |
| `training:resource:view` | 培训内容和个人学习能力 |
| `training:resource:manage` | 培训资源、题库、AI 草稿和成绩管理 |

## 2. 通用状态

| 字段/领域 | 代码 | 含义 |
|---|---|---|
| 角色、分类、标签 | `ENABLED` | 启用 |
| 角色、分类、标签 | `DISABLED` | 停用 |
| 账号 | `NORMAL` | 正常 |
| 账号 | `DISABLED` | 停用 |
| 逻辑删除 | `0` | 有效 |
| 逻辑删除 | `1` | 已删除 |

## 3. 培训资源字典

### 资源类型 `TRAINING_RESOURCE_TYPE`

| 代码 | 名称 |
|---|---|
| `ARTICLE` | 文章 |
| `VIDEO` | 视频 |
| `PPT` | PPT |

### 存储方式 `STORAGE_MODE`

| 代码 | 含义 | 主要字段 |
|---|---|---|
| `TEXT` | 正文直接保存在资源表 | `content` |
| `LOCAL_FILE` | 引用上传文件 | `file_resource_id` |
| `EXTERNAL_LINK` | 引用外部地址 | `external_url` |

### 资源状态

| 代码 | 含义 |
|---|---|
| `DRAFT` | 草稿，可编辑，护工不可见 |
| `PUBLISHED` | 已发布，护工可见 |
| `OFFLINE` | 已下架，护工不可见，可修改后重新发布 |

## 4. 题库与考核

### 题型

| 代码 | 含义 | 答案形式 |
|---|---|---|
| `SINGLE_CHOICE` | 单选题 | 选项标签，如 A/B/C/D |
| `TRUE_FALSE` | 判断题 | `true` / `false` 或业务层兼容值 |

不支持 `SHORT_ANSWER`。

### 题目状态

| 代码 | 含义 |
|---|---|
| `DRAFT` | 题库草稿 |
| `PUBLISHED` | 可用于正式考核的题目 |

### 考核状态

| 代码 | 含义 |
|---|---|
| `DRAFT` | 草稿考核 |
| `PUBLISHED` | 护工可参加 |

### 考试结果

| 代码 | 含义 |
|---|---|
| `PASSED` | 分数达到通过线 |
| `NOT_PASSED` | 未达到通过线 |

默认通过分：`60.00`。默认最大尝试次数：`3`，以具体考核配置为准。

## 5. 学习状态

学习状态由后端根据课程访问和考试记录计算。现行常量可能包含：

| 代码 | 含义 |
|---|---|
| `NOT_STARTED` | 未开始 |
| `LEARNING` / `IN_PROGRESS` | 学习中，具体值以代码常量为准 |
| `PASSED` | 培训通过 |

前端不得自行写入状态。接口调整状态码时应同步本字典。

## 6. AI 草稿

### 草稿类型

当前用于题目草稿，题型仅限单选和判断。

### 草稿状态

| 代码 | 含义 |
|---|---|
| `DRAFT` | 待审核 |
| `APPROVED` | 审核通过，已创建正式 DRAFT 题目 |
| `REJECTED` | 审核驳回，不创建题目 |

`draft_content` 保存结构化草稿内容，`ai_draft_source_resource` 保存来源课程。重复审核不允许。

## 7. 作业与互动

### 作业类型

演示脚本目前使用：

| 代码 | 含义 |
|---|---|
| `SINGLE_CHOICE` | 单选作业 |
| `TRUE_FALSE` | 判断作业 |

### 作业状态

| 代码 | 含义 |
|---|---|
| `PUBLISHED` | 已发布，可供护工查看 |

### 提交状态

| 代码 | 含义 |
|---|---|
| `SUBMITTED` | 已提交 |

### 讨论排序

接口默认使用：

| 代码 | 含义 |
|---|---|
| `LATEST` | 按最新创建或更新时间排序 |

如增加热门排序，应在接口、Service、前端和本字典中同步定义。

## 8. 文件字典

### 允许扩展名

- 图片：`jpg`、`jpeg`、`png`、`webp`
- 文档：`pdf`、`ppt`、`pptx`、`doc`、`docx`
- 视频：`mp4`、`webm`

### 禁止扩展名

`exe`、`bat`、`cmd`、`sh`、`js`、`jsp`、`php`

### 默认大小限制

| 类型 | 默认限制 |
|---|---:|
| 图片 | 10 MB |
| 文档 | 50 MB |
| 视频 | 500 MB |
| Spring 单请求 | 12 MB（当前主配置，和大文件业务限制需联调核对） |

注意：Spring multipart 全局 `max-request-size` 当前为 12 MB，可能限制大于 12 MB 的文档或视频上传。若正式支持 50 MB/500 MB 文件，应同步调整全局 multipart 配置并增加测试。

## 9. 主要表字段说明

### `sys_user`

| 字段 | 说明 |
|---|---|
| `username` | 登录名，唯一 |
| `password_hash` | BCrypt 密码哈希 |
| `real_name` | 显示姓名 |
| `avatar_url` | 头像地址 |
| `mobile_cipher_text` | 可恢复密文手机号，现行培训主线非必需 |
| `mobile_last4` | 脱敏展示尾号 |
| `main_role_id` | 主角色 |
| `account_status` | NORMAL / DISABLED |
| `is_deleted` | 逻辑删除 |

### `training_resource`

| 字段 | 说明 |
|---|---|
| `resource_type` | ARTICLE / VIDEO / PPT |
| `storage_mode` | TEXT / LOCAL_FILE / EXTERNAL_LINK |
| `category_id` | 分类 |
| `title` | 课程标题 |
| `summary` | 摘要 |
| `cover_url` | 封面 |
| `content` | 文本正文 |
| `file_resource_id` | 本地文件引用 |
| `external_url` | 外部链接 |
| `duration_seconds` | 建议学习时长 |
| `resource_status` | DRAFT / PUBLISHED / OFFLINE |

### `training_note`

| 字段 | 说明 |
|---|---|
| `user_id` | 笔记所有者 |
| `resource_id` | 课程 |
| `note_title` | 标题 |
| `note_content` | 富文本 HTML |

唯一键：`(user_id, resource_id)`。

### `training_exam`

| 字段 | 说明 |
|---|---|
| `resource_id` | 对应课程，唯一 |
| `exam_name` | 考核名称 |
| `pass_score` | 通过线 |
| `max_attempts` | 最大尝试次数 |
| `exam_status` | DRAFT / PUBLISHED |

### `exam_record`

| 字段 | 说明 |
|---|---|
| `exam_id` | 考核 |
| `user_id` | 当前护工 |
| `attempt_no` | 尝试序号 |
| `score` | 得分 |
| `pass_status` | PASSED / NOT_PASSED |

### `ai_draft`

| 字段 | 说明 |
|---|---|
| `draft_type` | 草稿类型 |
| `prompt` | 生成请求摘要 |
| `draft_content` | 草稿结构化内容 |
| `draft_status` | DRAFT / APPROVED / REJECTED |
| `reviewed_by` | 审核人 |
| `reviewed_at` | 审核时间 |
| `review_comment` | 审核意见 |

### 讨论和作业

- `training_discussion`：课程主题。
- `training_discussion_reply`：回复，`parent_reply_id` 支持嵌套。
- 两张点赞表：内容与用户唯一。
- `training_assignment`：作业题目、类型、内容、选项、答案和截止时间。
- `training_assignment_submission`：用户答案、得分和提交状态，作业+用户唯一。

## 10. 演示账号

| 用户名 | 角色/状态 |
|---|---|
| `admin_demo` | 管理员，正常 |
| `caregiver_demo` | 护工，正常 |
| `disabled_demo` | 护工，停用 |
| `deleted_demo` | 护工，逻辑删除 |

所有演示密码仅用于本地和答辩，生产环境必须删除或更换。

## 11. 维护规则

- 字典值应由后端常量统一维护。
- 新增状态或枚举时同步更新 SQL、Java、前端显示、API 和测试。
- 不在前端自行翻译未知状态为“正常”。
- 当前文档不包含护理订单或医生健康领域字典；历史值属于完整版。