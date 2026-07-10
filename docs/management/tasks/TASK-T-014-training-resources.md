# TASK-T-014 培训类别、标签和资源管理后端

## 基本信息

| 项目 | 内容 |
|---|---|
| 任务编号 | T-014 |
| 任务名称 | 培训类别、标签和资源管理后端 |
| 状态 | DONE |
| 负责人 | 隋咏轩 |
| 审核人 | 隋咏轩 |
| 分支 | `feature/T-014-training-resources` |
| 前置依赖 | T-012 DONE |
| 并行任务 | T-013 前端登录与权限接入由孙洋独立推进 |

## 实现范围

- 培训类别列表、新增、修改和启停。
- 培训标签列表、新增、修改和启停。
- 培训资源新增、修改、分页查询、详情、发布和下架。
- 资源支持 `ARTICLE`、`VIDEO`、`PPT`。
- 存储方式支持 `TEXT`、`LOCAL_FILE`、`EXTERNAL_LINK`。
- 复用 T-012 的当前用户、JWT 和 RBAC 权限基础。
- 发布和下架写入操作日志。
- 使用现有 `training_category`、`training_tag`、`training_resource`、`training_resource_tag` 和 `file_resource` 表。

## 不实现范围

- 不修改前端登录页面或培训页面。
- 不实现题库、考核、学习记录。
- 不实现 AI 问答、总结、建议或出题。
- 不实现护理订单、医生健康管理或用户角色权限 CRUD。
- 不扩展完整文件管理模块。
- 不引入 Redis。
- 不开始 T-015 及后续业务任务。

## 接口清单

| 方法 | URL | 说明 |
|---|---|---|
| GET | `/api/v1/training/categories` | 培训类别列表 |
| POST | `/api/v1/training/categories` | 新增培训类别 |
| PUT | `/api/v1/training/categories/{id}` | 修改培训类别 |
| PUT | `/api/v1/training/categories/{id}/status` | 启停培训类别 |
| GET | `/api/v1/training/tags` | 培训标签列表 |
| POST | `/api/v1/training/tags` | 新增培训标签 |
| PUT | `/api/v1/training/tags/{id}` | 修改培训标签 |
| PUT | `/api/v1/training/tags/{id}/status` | 启停培训标签 |
| POST | `/api/v1/training/resources` | 新增培训资源，初始状态为草稿 |
| PUT | `/api/v1/training/resources/{id}` | 修改草稿或已下架培训资源 |
| GET | `/api/v1/training/resources` | 培训资源分页查询 |
| GET | `/api/v1/training/resources/{id}` | 培训资源详情 |
| PUT | `/api/v1/training/resources/{id}/publish` | 发布培训资源 |
| PUT | `/api/v1/training/resources/{id}/offline` | 下架培训资源 |

## 权限规则

- `training:resource:manage`：可以查询全部状态，可以新增、修改、启停、发布和下架。
- `training:resource:view`：只能查询启用类别、启用标签和已发布资源。
- 查询接口允许 `training:resource:view` 或 `training:resource:manage`。
- 管理接口必须具备 `training:resource:manage`。
- Controller 不判断中文角色名，不以角色码替代权限码。

## 状态流转

- 类别和标签：`ENABLED`、`DISABLED`。
- 培训资源：`DRAFT -> PUBLISHED -> OFFLINE`，`OFFLINE -> PUBLISHED`。
- 已发布资源不可直接修改核心内容，必须先下架再修改。
- 重复发布和重复下架返回 `CONFLICT`。
- 下架后保留历史 `publishedAt`。

## 测试和验收标准

- 后端 `mvn test` 已通过，执行 44 个测试，Failures 0，Errors 0。
- 后端 `mvn verify` 已通过，生成 jar，Checkstyle 0 violations。
- MySQL 8 真实联调已通过，使用临时库 `care_nexus_t014`，未覆盖既有项目库。
- 新增 T-014 MockMvc 测试 21 个，覆盖权限、类别、标签、资源创建、资源修改、资源查询、状态流转和操作日志。
- T-012 认证回归测试继续通过。
- 已完成 `git diff --check`、PR 创建、项目负责人审核和 PR 合并。
- 前端 lint/build 不执行，原因是 T-014 未修改前端目录。

## 审核和合并记录

- 项目负责人已审核通过 T-014。
- PR #5 已通过 Squash and merge 合并到 `develop`。
- 合并提交：`ababa9b96e00965821de9202df18a269c2950cbc`。
- T-014 任务分支完成合并后不再继续用于开发。
