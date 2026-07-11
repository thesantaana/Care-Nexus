# TASK-T-013 前端登录与权限接入

## 基本信息

| 项目 | 内容 |
|---|---|
| 任务编号 | T-013 |
| 任务名称 | 前端登录与权限接入 |
| 状态 | DOING |
| 负责人 | 孙洋 |
| 审核人 | 隋咏轩 |
| 建议分支 | `feature/T-013-frontend-auth` |
| 前置依赖 | T-012 DONE |

## 修改范围

- `frontend/admin-web/`
- `frontend/mobile-web/`
- 必要的前端和联调文档

本任务不修改后端业务代码。

## 实现范围

1. PC端和移动端登录页面。
2. 调用 `POST /api/v1/auth/login`。
3. 调用 `GET /api/v1/auth/me`。
4. 调用 `POST /api/v1/auth/logout`。
5. Token本地保存。
6. Axios请求自动携带Bearer Token。
7. HTTP 401时清理登录状态并跳转登录页。
8. 路由守卫。
9. 根据角色和`permissionCodes`控制菜单或入口。
10. PC端和移动端分别完成真实后端联调。

## 当前执行记录

- 已在 `feature/frontend-integration` 实现两个 Vue 应用的登录、Token 本地保存、Bearer 请求头、`me` 会话恢复、退出登录、401 清理、路由守卫和按角色/权限的菜单控制。
- 两个前端的 lint 和生产构建已通过。
- 真实 `login`、`me`、`logout` HTTP 联调尚未完成：本机 MySQL 服务未运行，且本轮 `mvn verify` 受 Maven Central TLS 握手失败阻塞。该状态不视为功能验收通过。

## 不实现范围

- 不修改登录、JWT和RBAC后端实现。
- 不实现用户、角色和权限CRUD。
- 不实现培训、订单、医生等业务页面。
- 不在前端代码中写死完整Token或真实密码。

## 验收标准

- 两个前端`npm run lint`通过。
- 两个前端`npm run build`通过。
- `login`、`me`、`logout`真实联调通过。
- 未登录不能进入受保护页面。
- 退出后本地Token被清理。
- 401能够返回登录页。
- 不同角色显示合理的工作台入口。
- 完成后状态改为REVIEW，不直接改为DONE。
