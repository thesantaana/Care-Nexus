# API设计规范

项目名称：CareNexus 颐联

任务编号：T-011

文档状态：已审核，T-011封板

更新时间：2026-07-09

## 1. 设计目标

本文档定义 CareNexus MVP 阶段 REST API 的统一命名、请求响应、错误码、分页、鉴权、权限校验、状态流转、文件上传、AI 草稿和审计要求。

本文档为接口规范。MVP 业务接口清单见 `docs/api/MVP接口清单.md`，后续具体接口应在本规范基础上实现。

## 2. URL 版本策略

API 统一使用版本前缀：

```text
/api/v1
```

示例：

```text
GET /api/v1/training/resources
POST /api/v1/care/orders
PUT /api/v1/care/orders/{id}/confirm
```

## 3. REST 资源命名

- 使用名词复数。
- 使用短横线分隔多词。
- 状态动作接口放在资源下。

| 类型 | 示例 |
|---|---|
| 列表 | `GET /api/v1/users` |
| 详情 | `GET /api/v1/users/{id}` |
| 新增 | `POST /api/v1/users` |
| 修改 | `PUT /api/v1/users/{id}` |
| 状态操作 | `PUT /api/v1/care/orders/{id}/confirm` |

## 4. HTTP 方法

| 方法 | 含义 |
|---|---|
| `GET` | 查询 |
| `POST` | 新增或提交业务动作 |
| `PUT` | 全量或主要字段更新、状态变更 |
| `PATCH` | 局部更新，MVP 尽量少用 |
| `DELETE` | 删除或逻辑删除 |

## 5. 统一响应结构

```json
{
  "code": "SUCCESS",
  "message": "ok",
  "data": {}
}
```

字段说明：

| 字段 | 说明 |
|---|---|
| `code` | 业务错误码 |
| `message` | 可读提示 |
| `data` | 响应数据 |

## 6. 分页结构

请求参数：

```text
pageNo=1&pageSize=10
```

响应数据：

```json
{
  "records": [],
  "pageNo": 1,
  "pageSize": 10,
  "total": 0,
  "pages": 0
}
```

## 7. 错误码

| 错误码 | 含义 |
|---|---|
| `SUCCESS` | 成功 |
| `BAD_REQUEST` | 请求参数错误 |
| `UNAUTHORIZED` | 未登录或认证失败 |
| `FORBIDDEN` | 无权限 |
| `NOT_FOUND` | 资源不存在 |
| `CONFLICT` | 状态冲突或重复操作 |
| `VALIDATION_ERROR` | 参数校验失败 |
| `BUSINESS_ERROR` | 业务规则错误 |
| `SYSTEM_ERROR` | 系统异常 |

## 8. 参数校验

- Controller 使用 Bean Validation 校验基础字段。
- Service 校验业务规则、状态流转和数据权限。
- 外部输入不得直接拼接 SQL。
- 请求 DTO 与响应 VO 分离，不直接返回 Entity。

## 9. 鉴权头

```text
Authorization: Bearer <token>
```

规则：

- 未登录访问业务接口返回 `UNAUTHORIZED`。
- 权限不足返回 `FORBIDDEN`。
- 账号停用后不得继续访问业务接口。

## 10. 数据权限校验位置

数据权限必须在业务模块 Service 层校验，不由 `auth` 模块跨领域查询业务表：

| 场景 | 校验 |
|---|---|
| 医生访问老人 | `doctor` 模块通过 `DoctorAuthorizationService.canAccessElder(currentUser, elderId)` 校验医生老人授权关系 |
| 家属代老人操作 | `care` 模块通过 `FamilyElderAccessService` 校验老人家属绑定关系 |
| 护工处理订单 | `care` 模块通过 `CareOrderAccessService` 校验订单是否分配给本人 |
| AI 草稿审核 | 培训管理员权限 |

## 11. 幂等性

需要防重复的接口：

- 订单取消。
- 护工确认、开始、完成服务。
- 评价提交。
- 投诉处理。
- AI 草稿审核通过或驳回。

MVP 通过状态校验和唯一约束保证基础幂等。

## 12. 状态流转接口

状态变更接口必须：

1. 校验当前状态。
2. 校验操作角色。
3. 校验数据权限。
4. 写入状态变更记录或操作日志。

示例：

```text
PUT /api/v1/care/orders/{id}/assign
PUT /api/v1/care/orders/{id}/confirm
PUT /api/v1/care/orders/{id}/start
PUT /api/v1/care/orders/{id}/complete
PUT /api/v1/care/orders/{id}/cancel
```

## 13. 文件上传接口

建议接口：

```text
POST /api/v1/files
```

要求：

- 使用 `multipart/form-data`。
- 校验文件格式白名单。
- 校验文件大小。
- 服务端生成存储文件名。
- 原始文件名仅用于展示。
- 返回文件资源 ID 和访问路径。

## 14. AI 草稿接口

AI 仅用于护理培训辅助。

建议接口：

```text
POST /api/v1/training/ai/question-drafts
PUT /api/v1/training/ai/question-drafts/{id}/review
```

要求：

- 生成草稿必须选择已入库培训资料。
- AI 结果保存为草稿。
- 未审核草稿不得进入正式题库。
- 草稿审核统一使用 `review` 接口，通过 `reviewResult=APPROVED/REJECTED` 表示通过或驳回。
- AI 接口不得在医生端、老人端或管理决策场景提供医疗 AI 能力。

## 15. 日志和审计

必须记录操作日志的接口类型：

- 账号启停。
- 权限变更。
- 培训资源发布和下架。
- AI 草稿审核。
- 订单分配。
- 订单取消。
- 投诉处理。
- 医生老人授权变更。

日志不得记录密码、Token、完整手机号、身份证号或完整健康隐私。

## 16. 接口文档维护规则

后续新增具体接口时，应记录：

- 接口名称。
- URL。
- 方法。
- 请求参数。
- 响应结构。
- 权限要求。
- 数据权限要求。
- 错误码。
- 审计要求。
