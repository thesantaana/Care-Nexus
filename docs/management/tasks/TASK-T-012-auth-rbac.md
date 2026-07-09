# TASK-T-012 后端账号登录、当前用户与 RBAC 权限基础

任务状态：REVIEW

负责人：隋咏轩

分支：`feature/T-012-auth-rbac`

## 1. 任务目标

本任务实现后端账号登录、当前用户查询、退出登录、JWT 认证过滤器、账号状态校验和 RBAC 功能权限加载基础。

## 2. 实现范围

- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`
- `POST /api/v1/auth/logout`
- JWT 生成、解析、过期校验、签名校验和黑名单校验。
- Bearer Token 认证过滤器。
- 每次请求根据 JWT 中的用户标识重新加载账号、主角色和权限。
- BCrypt 密码校验。
- 账号停用和逻辑删除校验。
- 最小权限验证接口：`GET /api/v1/auth/rbac-check`。
- 单元测试和 MockMvc 接口测试。

## 3. 不实现范围

- 不实现用户、角色和权限完整 CRUD。
- 不实现注册、找回密码、修改密码、验证码和刷新 Token。
- 不实现 Redis、分布式会话和多设备登录管理。
- 不实现前端登录页、前端路由守卫、前端菜单权限和前端联调。
- 不实现医生老人、家属老人和护工订单等领域数据权限。

## 4. 权限规则

MVP 阶段一个账号只有一个主要业务角色，来源为 `sys_user.main_role_id`。

RBAC 权限加载路径：

```text
sys_user.main_role_id -> sys_role -> sys_role_permission -> sys_permission.permission_code
```

功能权限判断使用 `permission_code`，不使用中文角色名或中文权限名判断。

## 5. 数据库变更

本任务不新增认证相关表结构。

本任务更新 `database/init/002_seed_data.sql`：

- 将演示账号密码改为 BCrypt 哈希。
- 统一演示密码为 `Demo@123456`。
- 增加运营人员、健康管理人员、停用账号和逻辑删除账号演示数据。
- 补充 `system:user:view`、`system:role:view`、`training:resource:view`、`care:order:view`、`doctor:elder:view` 等可测试权限码。

## 6. 接口变更

认证接口遵循统一 `ApiResponse`。

登录成功返回 `token`、`tokenType`、`expiresIn`、`expiresAt`、当前用户、主角色和权限码。

登录失败使用泛化提示，不区分账号不存在和密码错误。

## 7. 测试范围

自动化测试覆盖登录成功、密码错误、不存在账号、停用账号、逻辑删除账号、当前用户、Token 缺失、Bearer 格式错误、伪造 Token、签名错误、过期 Token、黑名单 Token、退出登录、重复退出、RBAC 200/403、主角色和权限返回、BCrypt 哈希、账号停用后旧 Token 失效、权限变更后旧 Token 读取最新权限。

自动化测试使用 MockMvc 和 MockBean，不依赖个人 MySQL 账号密码。

## 8. 当前限制

退出登录黑名单当前为单机内存实现：

- 应用重启后黑名单丢失。
- 已退出但尚未过期的旧 JWT 在应用重启后可能重新有效。
- 当前仅用于本地单机 MVP 演示。
- 后续可替换为 Redis 黑名单。

## 9. 验收标准

- `mvn verify` 通过。
- 401 和 403 使用统一 JSON 响应。
- 不提交真实密码、真实密钥、完整 Token 或数据库密码。
- 不修改前端目录。
- MySQL 真实联调已完成，等待项目负责人审核。
