# MVP接口清单

项目名称：CareNexus 颐联

任务编号：T-011

文档状态：已审核，T-011封板

更新时间：2026-07-09

本文档基于需求基线 v1.0 和 `docs/api/API设计规范.md`，列出 MVP 阶段需要覆盖的接口清单。除 T-012 已实现的认证基础接口外，其他接口仍为设计清单，不代表已经实现。

分页接口统一使用 `pageNo` 和 `pageSize`。AI 题目草稿审核统一使用 `review` 单接口，不再同时保留 approve/reject 双接口。

## 1. 账号和当前用户

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 登录 | POST | `/api/v1/auth/login` | 匿名 | 无 | username, password | token, tokenType, expiresIn, expiresAt, userId, username, displayName, mainRoleCode, mainRoleName, permissionCodes | 无 | AUTH_INVALID_CREDENTIALS, AUTH_ACCOUNT_DISABLED, UNAUTHORIZED | 是 |
| 当前用户 | GET | `/api/v1/auth/me` | 已登录用户 | 本人 | 无 | userId, username, displayName, mainRoleCode, mainRoleName, permissionCodes, accountStatus | 无 | AUTH_TOKEN_MISSING, AUTH_TOKEN_INVALID, AUTH_TOKEN_EXPIRED, AUTH_TOKEN_REVOKED, UNAUTHORIZED | 否 |
| 退出登录 | POST | `/api/v1/auth/logout` | 已登录用户 | 本人 | 无 | success | 当前 JWT jti 加入单机内存黑名单 | AUTH_TOKEN_MISSING, AUTH_TOKEN_INVALID, UNAUTHORIZED | 是 |
| RBAC验证接口 | GET | `/api/v1/auth/rbac-check` | 已登录用户 | 需要 `system:user:view` 权限 | 无 | permission, result | 无 | AUTH_FORBIDDEN, UNAUTHORIZED | 否 |

## 2. 用户、角色、权限、字典和日志

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 用户分页查询 | GET | `/api/v1/admin/users` | 管理员 | 全部用户 | keyword, roleId, status, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 新增用户 | POST | `/api/v1/admin/users` | 管理员 | 全部用户 | username, realName, mobile, mainRoleId | userId | 创建账号 | BAD_REQUEST, CONFLICT | 是 |
| 修改用户 | PUT | `/api/v1/admin/users/{id}` | 管理员 | 全部用户 | realName, mobile, mainRoleId | user | 修改账号 | NOT_FOUND, CONFLICT | 是 |
| 启停账号 | PUT | `/api/v1/admin/users/{id}/status` | 管理员 | 全部用户 | accountStatus | user | 正常/停用 | NOT_FOUND, CONFLICT | 是 |
| 角色查询 | GET | `/api/v1/admin/roles` | 管理员 | 全部角色 | keyword, status, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 新增角色 | POST | `/api/v1/admin/roles` | 管理员 | 全部角色 | roleCode, roleName | role | 创建角色 | BAD_REQUEST, CONFLICT | 是 |
| 修改角色 | PUT | `/api/v1/admin/roles/{id}` | 管理员 | 全部角色 | roleName, roleStatus | role | 修改角色 | NOT_FOUND, CONFLICT | 是 |
| 权限查询 | GET | `/api/v1/admin/permissions` | 管理员 | 全部权限 | permissionType | permissions | 无 | FORBIDDEN | 否 |
| 分配角色权限 | PUT | `/api/v1/admin/roles/{id}/permissions` | 管理员 | 全部角色 | permissionIds | rolePermissions | 权限配置变化 | BAD_REQUEST, NOT_FOUND | 是 |
| 服务项目分页 | GET | `/api/v1/admin/service-items` | 管理员、运营人员 | 全部服务项目 | keyword, category, status, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 新增服务项目 | POST | `/api/v1/admin/service-items` | 管理员、运营人员 | 全部服务项目 | serviceName, category, description | serviceItem | 新增服务项目 | BAD_REQUEST, CONFLICT | 是 |
| 修改服务项目 | PUT | `/api/v1/admin/service-items/{id}` | 管理员、运营人员 | 全部服务项目 | serviceName, category, description | serviceItem | 修改服务项目 | NOT_FOUND, CONFLICT | 是 |
| 启停服务项目 | PUT | `/api/v1/admin/service-items/{id}/status` | 管理员、运营人员 | 全部服务项目 | serviceStatus | serviceItem | 启用/停用 | NOT_FOUND, CONFLICT | 是 |
| 基础字典查询 | GET | `/api/v1/admin/dicts` | 管理员 | 全部字典 | dictType, keyword, status, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 新增基础字典 | POST | `/api/v1/admin/dicts` | 管理员 | 全部字典 | dictType, dictCode, dictName, sortNo | dict | 新增字典项 | BAD_REQUEST, CONFLICT | 是 |
| 修改基础字典 | PUT | `/api/v1/admin/dicts/{id}` | 管理员 | 全部字典 | dictName, sortNo, dictStatus | dict | 修改字典项 | NOT_FOUND, CONFLICT | 是 |
| 启停基础字典 | PUT | `/api/v1/admin/dicts/{id}/status` | 管理员 | 全部字典 | dictStatus | dict | 启用/停用 | NOT_FOUND, CONFLICT | 是 |
| 操作日志分页查询 | GET | `/api/v1/admin/operation-logs` | 管理员 | 全部操作日志 | pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 操作日志筛选查询 | GET | `/api/v1/admin/operation-logs/search` | 管理员 | 全部操作日志 | userId, startTime, endTime, actionType, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |

## 3. 培训类别、标签、资源、题库和考核管理

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 培训类别列表 | GET | `/api/v1/training/categories` | 培训管理员、护工 | 按状态过滤 | status | categories | 无 | FORBIDDEN | 否 |
| 新增培训类别 | POST | `/api/v1/training/categories` | 培训管理员 | 培训模块 | categoryName, sortNo | category | 新增类别 | BAD_REQUEST, CONFLICT | 是 |
| 修改培训类别 | PUT | `/api/v1/training/categories/{id}` | 培训管理员 | 培训模块 | categoryName, sortNo | category | 修改类别 | NOT_FOUND, CONFLICT | 是 |
| 启停培训类别 | PUT | `/api/v1/training/categories/{id}/status` | 培训管理员 | 培训模块 | status | category | 启用/停用 | NOT_FOUND | 是 |
| 培训标签列表 | GET | `/api/v1/training/tags` | 培训管理员、护工 | 按状态过滤 | status | tags | 无 | FORBIDDEN | 否 |
| 标签新增 | POST | `/api/v1/training/tags` | 培训管理员 | 培训模块 | tagName | tag | 新增标签 | BAD_REQUEST, CONFLICT | 是 |
| 标签修改 | PUT | `/api/v1/training/tags/{id}` | 培训管理员 | 培训模块 | tagName | tag | 修改标签 | NOT_FOUND, CONFLICT | 是 |
| 标签启停 | PUT | `/api/v1/training/tags/{id}/status` | 培训管理员 | 培训模块 | status | tag | 启用/停用 | NOT_FOUND | 是 |
| 保存培训资源 | POST | `/api/v1/training/resources` | 培训管理员 | 培训模块 | resourceType, storageMode, categoryId, title, summary, content, fileResourceId, externalUrl, durationSeconds, tagIds | resource | 新建为DRAFT | BAD_REQUEST, NOT_FOUND, CONFLICT | 是 |
| 培训资源修改 | PUT | `/api/v1/training/resources/{id}` | 培训管理员 | 培训模块 | categoryId, title, summary, content, fileResourceId, externalUrl, durationSeconds, tagIds | resource | 允许修改DRAFT/OFFLINE，PUBLISHED需先下架 | NOT_FOUND, CONFLICT | 是 |
| 培训资源分页 | GET | `/api/v1/training/resources` | 培训管理员、护工 | 已发布或管理权限 | keyword, resourceType, categoryId, tagId, status, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 培训资源详情 | GET | `/api/v1/training/resources/{id}` | 培训管理员、护工 | 已发布或管理权限 | 无 | resource | 无 | NOT_FOUND, FORBIDDEN | 否 |
| 发布培训资源 | PUT | `/api/v1/training/resources/{id}/publish` | 培训管理员 | 培训模块 | 无 | resource | 草稿->已发布 | NOT_FOUND, CONFLICT | 是 |
| 下架培训资源 | PUT | `/api/v1/training/resources/{id}/offline` | 培训管理员 | 培训模块 | reason | resource | 已发布->已下架 | NOT_FOUND, CONFLICT | 是 |
| 考核新增 | POST | `/api/v1/training/exams` | 培训管理员 | 培训模块 | resourceId, examName, passScore, maxAttempts | exam | 草稿 | BAD_REQUEST, CONFLICT | 是 |
| 考核修改 | PUT | `/api/v1/training/exams/{id}` | 培训管理员 | 培训模块 | resourceId, examName, passScore, maxAttempts | exam | 修改考核 | NOT_FOUND, CONFLICT | 是 |
| 考核发布 | PUT | `/api/v1/training/exams/{id}/publish` | 培训管理员 | 培训模块 | 无 | exam | 草稿->已发布 | NOT_FOUND, CONFLICT | 是 |
| 题目新增 | POST | `/api/v1/training/questions` | 培训管理员 | 培训模块 | questionType, content, standardAnswer, analysis | question | 新增题目 | BAD_REQUEST | 是 |
| 题目修改 | PUT | `/api/v1/training/questions/{id}` | 培训管理员 | 培训模块 | content, standardAnswer, analysis, status | question | 修改题目 | NOT_FOUND, CONFLICT | 是 |
| 题目选项维护 | PUT | `/api/v1/training/questions/{id}/options` | 培训管理员 | 培训模块 | options | questionOptions | 选项变化 | BAD_REQUEST, NOT_FOUND | 是 |
| 考核题目关联管理 | PUT | `/api/v1/training/exams/{id}/questions` | 培训管理员 | 培训模块 | questionIds, scores, sortNo | examQuestions | 考核题目变化 | BAD_REQUEST, NOT_FOUND | 是 |

## 4. 学习记录和考核执行

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 记录学习访问 | POST | `/api/v1/training/learning/access` | 护工 | 本人 | resourceId, accessSeconds | learningRecord | 未开始->学习中 | BAD_REQUEST, NOT_FOUND | 否 |
| 查看学习记录 | GET | `/api/v1/training/learning/me` | 护工 | 本人 | 无 | learningRecord, accessLogs | 无 | UNAUTHORIZED | 否 |
| 查看课程成绩汇总 | GET | `/api/v1/training/learning/scores` | 护工 | 本人 | 无 | 每课最高分、平均分、通过课程数、整体培训状态 | 无 | UNAUTHORIZED | 否 |
| 获取考核 | GET | `/api/v1/training/exams/{examId}` | 护工 | 已发布考核 | 无 | exam, questions | 无 | NOT_FOUND | 否 |
| 提交考核 | POST | `/api/v1/training/exams/{examId}/records` | 护工 | 本人 | answers | examRecord | 已参加考核/已通过/未通过 | BAD_REQUEST, CONFLICT | 是 |

## 5. AI培训辅助和题目草稿审核

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 培训资料问答 | POST | `/api/v1/training/ai/qa` | 护工、培训管理员 | 已发布或管理权限资料 | sourceResourceIds, question | answer, references | 无 | BAD_REQUEST | 否 |
| 生成知识点总结 | POST | `/api/v1/training/ai/summary` | 护工、培训管理员 | 已发布或管理权限资料 | sourceResourceIds | summary | 无 | BAD_REQUEST | 否 |
| 生成学习建议 | POST | `/api/v1/training/ai/suggestions` | 护工、培训管理员 | 本人学习记录 | sourceResourceIds | suggestions | 无 | BAD_REQUEST | 否 |
| 生成题目草稿 | POST | `/api/v1/training/ai/question-drafts` | 培训管理员 | 培训模块 | sourceResourceIds, questionType, count | draftIds | 创建草稿 | BAD_REQUEST | 是 |
| AI草稿列表 | GET | `/api/v1/training/ai/question-drafts` | 培训管理员 | 培训模块 | draftStatus, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| AI草稿详情 | GET | `/api/v1/training/ai/question-drafts/{id}` | 培训管理员 | 培训模块 | 无 | draft, sourceResources | 无 | NOT_FOUND | 否 |
| 审核题目草稿 | PUT | `/api/v1/training/ai/question-drafts/{id}/review` | 培训管理员 | 培训模块 | reviewResult, comment | draft, questionId | 草稿->通过/驳回 | NOT_FOUND, CONFLICT | 是 |

T-022 当前仅实现稳定 Mock 模式：输入只能来自 `sourceResourceIds` 对应且可访问的 TEXT 培训资料，输出附带资源引用。题目草稿仅支持单选题和判断题，审核通过后正式题目仍为 DRAFT，不自动发布或加入考核。非 TEXT 资源没有正文时返回明确参数错误；本期不接真实模型，不读取健康数据。

## 6. 老人家属绑定、服务、地址和移动订单

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 发起绑定 | POST | `/api/v1/mobile/elder-bindings` | 家属 | 本人 | elderIdentifier, verifyCode | binding | 创建或更新绑定 | BAD_REQUEST, CONFLICT | 是 |
| 解除绑定 | PUT | `/api/v1/mobile/elder-bindings/{id}/cancel` | 家属 | 本人绑定关系 | reason | binding | ACTIVE->CANCELLED | NOT_FOUND, CONFLICT | 是 |
| 我的老人列表 | GET | `/api/v1/mobile/elders` | 老人、家属 | 本人或绑定老人 | 无 | elders | 无 | UNAUTHORIZED | 否 |
| 服务项目列表 | GET | `/api/v1/mobile/service-items` | 老人、家属 | 已启用服务 | keyword, category, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 服务项目详情 | GET | `/api/v1/mobile/service-items/{id}` | 老人、家属 | 已启用服务 | 无 | serviceItem | 无 | NOT_FOUND | 否 |
| 地址列表 | GET | `/api/v1/mobile/addresses` | 老人、家属 | 本人 | elderId | addresses | 无 | FORBIDDEN | 否 |
| 新增地址 | POST | `/api/v1/mobile/addresses` | 老人、家属 | 本人或绑定老人 | elderId, contactName, mobile, addressDetail, isDefault | address | 新增地址 | BAD_REQUEST | 是 |
| 地址修改 | PUT | `/api/v1/mobile/addresses/{id}` | 老人、家属 | 本人或绑定老人 | contactName, mobile, addressDetail | address | 修改地址 | NOT_FOUND, FORBIDDEN | 是 |
| 地址停用 | PUT | `/api/v1/mobile/addresses/{id}/disable` | 老人、家属 | 本人或绑定老人 | reason | address | ACTIVE->DISABLED | NOT_FOUND, CONFLICT | 是 |
| 设置默认地址 | PUT | `/api/v1/mobile/addresses/{id}/default` | 老人、家属 | 本人或绑定老人 | elderId | address | 默认地址变化 | NOT_FOUND, FORBIDDEN | 是 |
| 提交护理预约 | POST | `/api/v1/mobile/orders` | 老人、家属 | 本人或绑定老人 | elderId, serviceItemId, addressId, appointmentTime | order | 新订单=待分配 | BAD_REQUEST, FORBIDDEN | 是 |
| 我的订单 | GET | `/api/v1/mobile/orders` | 老人、家属 | 本人或绑定老人 | status, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 订单详情 | GET | `/api/v1/mobile/orders/{id}` | 老人、家属 | 本人或绑定老人 | 无 | orderDetail | 无 | NOT_FOUND, FORBIDDEN | 否 |
| 取消订单 | PUT | `/api/v1/mobile/orders/{id}/cancel` | 老人、家属 | 本人或绑定老人 | cancelReason | order | 待分配/待确认/已确认->已取消 | NOT_FOUND, CONFLICT | 是 |
| 投诉详情 | GET | `/api/v1/mobile/complaints/{id}` | 老人、家属 | 本人或绑定老人 | 无 | complaint | 无 | NOT_FOUND, FORBIDDEN | 否 |
| 投诉处理状态查询 | GET | `/api/v1/mobile/orders/{id}/complaints/status` | 老人、家属 | 本人或绑定老人 | 无 | complaintStatus | 无 | NOT_FOUND, FORBIDDEN | 否 |

## 7. 订单分配、护工执行、评价和投诉处理

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 待分配订单查询 | GET | `/api/v1/care/orders/pending-assignment` | 运营人员 | 全部待分配订单 | pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 人工分配订单 | PUT | `/api/v1/care/orders/{id}/assign` | 运营人员 | 全部订单 | caregiverId | order | 待分配->待确认 | NOT_FOUND, CONFLICT | 是 |
| 护工订单列表 | GET | `/api/v1/care/my-orders` | 护工 | 分配给本人 | status, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 护工订单详情 | GET | `/api/v1/care/my-orders/{id}` | 护工 | 分配给本人 | 无 | orderDetail | 无 | NOT_FOUND, FORBIDDEN | 否 |
| 确认服务 | PUT | `/api/v1/care/orders/{id}/confirm` | 护工 | 分配给本人 | 无 | order | 待确认->已确认 | NOT_FOUND, CONFLICT | 是 |
| 开始服务 | PUT | `/api/v1/care/orders/{id}/start` | 护工 | 分配给本人 | 无 | order | 已确认->服务中 | NOT_FOUND, CONFLICT | 是 |
| 完成服务 | PUT | `/api/v1/care/orders/{id}/complete` | 护工 | 分配给本人 | serviceContent, completedAt | order | 服务中->已完成 | BAD_REQUEST, CONFLICT | 是 |
| 评价订单 | POST | `/api/v1/mobile/orders/{id}/evaluation` | 老人、家属 | 本人或绑定老人 | rating, content | evaluation | 未评价->已评价 | NOT_FOUND, CONFLICT | 是 |
| 提交投诉 | POST | `/api/v1/mobile/orders/{id}/complaints` | 老人、家属 | 本人或绑定老人 | complaintContent | complaint | 未投诉->处理中 | NOT_FOUND, CONFLICT | 是 |
| 投诉分页查询 | GET | `/api/v1/care/complaints` | 运营人员 | 全部投诉 | complaintStatus, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 投诉详情 | GET | `/api/v1/care/complaints/{id}` | 运营人员 | 全部投诉 | 无 | complaint | 无 | NOT_FOUND | 否 |
| 处理投诉 | PUT | `/api/v1/care/complaints/{id}/handle` | 运营人员 | 全部投诉 | handledResult | complaint | 处理中->已处理 | NOT_FOUND, CONFLICT | 是 |

## 8. 医生老人授权和健康管理

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 维护医生老人授权 | PUT | `/api/v1/doctor/authorizations` | 管理员、健康管理人员 | 全部医生和老人 | doctorUserId, elderId, authStatus | authorization | ACTIVE/CANCELLED | BAD_REQUEST, CONFLICT | 是 |
| 查询医生已授权老人 | GET | `/api/v1/doctor/authorizations/doctors/{doctorUserId}/elders` | 管理员、健康管理人员、医生 | 医生仅本人授权老人 | pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 查询老人对应授权医生 | GET | `/api/v1/doctor/authorizations/elders/{elderId}/doctors` | 管理员、健康管理人员 | 全部授权关系 | pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 授权老人列表 | GET | `/api/v1/doctor/elders` | 医生、健康管理人员 | 医生仅授权老人 | keyword, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 健康档案详情 | GET | `/api/v1/doctor/elders/{elderId}` | 医生、健康管理人员 | 授权老人 | 无 | elderProfile | 无 | FORBIDDEN | 否 |
| 健康记录列表 | GET | `/api/v1/doctor/elders/{elderId}/health-records` | 医生、健康管理人员 | 授权老人 | startTime, endTime, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 新增健康记录 | POST | `/api/v1/doctor/elders/{elderId}/health-records` | 医生、健康管理人员 | 授权老人 | recordTime, indicators, remark | record, alert | 可触发预警 | BAD_REQUEST, FORBIDDEN | 是 |
| 健康预警列表 | GET | `/api/v1/doctor/elders/{elderId}/alerts` | 医生、健康管理人员 | 授权老人 | status, pageNo, pageSize | pageResult | 无 | FORBIDDEN | 否 |
| 人工创建健康预警 | POST | `/api/v1/doctor/elders/{elderId}/alerts` | 医生、健康管理人员 | 授权老人 | alertLevel, alertContent | alert | 新建为待处理 | BAD_REQUEST, FORBIDDEN | 是 |
| 处理健康预警 | PUT | `/api/v1/doctor/alerts/{id}/status` | 医生、健康管理人员 | 授权老人 | alertStatus, comment | alert | 待处理->处理中->已关闭 | CONFLICT, FORBIDDEN | 是 |
| 保存随访记录 | POST | `/api/v1/doctor/elders/{elderId}/follow-ups` | 医生 | 授权老人 | method, result, recordStatus | followUp | 草稿/已确认 | BAD_REQUEST | 是 |
| 确认随访记录 | PUT | `/api/v1/doctor/follow-ups/{id}/confirm` | 医生 | 本人创建且仍有老人授权 | 无 | followUp | 草稿->已确认 | CONFLICT, FORBIDDEN | 是 |
| 保存干预记录 | POST | `/api/v1/doctor/elders/{elderId}/interventions` | 医生 | 授权老人 | content, recordStatus | intervention | 草稿/已确认 | BAD_REQUEST | 是 |
| 确认干预记录 | PUT | `/api/v1/doctor/interventions/{id}/confirm` | 医生 | 本人创建且仍有老人授权 | 无 | intervention | 草稿->已确认 | CONFLICT, FORBIDDEN | 是 |
| 保存健康评估 | POST | `/api/v1/doctor/elders/{elderId}/assessments` | 医生 | 授权老人 | riskLevel, conclusion, suggestion, status | assessment | 草稿->已确认 | BAD_REQUEST | 是 |
| 确认健康评估 | PUT | `/api/v1/doctor/assessments/{id}/confirm` | 医生 | 本人创建且仍有老人授权 | 无 | assessment | 草稿->已确认 | CONFLICT, FORBIDDEN | 是 |

## 9. 文件上传

| 接口名称 | HTTP方法 | URL | 允许角色 | 数据权限 | 请求字段 | 响应字段 | 状态变化 | 错误码 | 操作日志 |
|---|---|---|---|---|---|---|---|---|---|
| 上传文件 | POST | `/api/v1/files` | 培训管理员、管理员 | 按业务用途校验 | file, purpose | fileId, url, originalName, fileSize | 创建文件资源 | BAD_REQUEST, FORBIDDEN | 是 |
| 文件信息 | GET | `/api/v1/files/{id}` | 已登录用户 | 关联业务权限 | 无 | fileMetadata | 无 | NOT_FOUND, FORBIDDEN | 否 |

## 10. 数量汇总

- 账号和当前用户：3 个 MVP 接口；另有 1 个 T-012 后端 RBAC 验证接口，不计入正式业务 MVP 合计。
- 用户、角色、权限、字典和日志：19 个。
- 培训类别、标签、资源、题库和考核管理：21 个。
- 学习记录和考核执行：5 个。
- AI培训辅助和题目草稿审核：7 个。
- 老人家属绑定、服务、地址和移动订单：16 个。
- 订单分配、护工执行、评价和投诉处理：12 个。
- 医生老人授权和健康管理：16 个。
- 文件上传：2 个。
- 合计：101 个 MVP 接口。

## 11. 实现状态补充

- T-014 已实际实现培训类别、标签和资源管理后端接口。
- T-014 仅实现后端接口和权限校验；前端页面、题库、考核、学习记录和 AI 辅助仍属于后续任务。
