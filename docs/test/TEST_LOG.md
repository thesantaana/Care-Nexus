# Test Log

更新时间：2026-07-09

## 2026-07-07 审计阶段测试基础

当时仓库尚未创建后端工程、前端工程、测试目录、构建脚本或测试命令配置，因此没有可执行的单元测试、后端构建、前端 lint 或前端生产构建命令。

## 2026-07-07 审计检查执行情况

以下命令属于仓库审计检查，不属于软件测试，不得记录为功能测试、单元测试或构建通过。

| 命令 | 结果 | 说明 |
|---|---|---|
| `rg --files` | 已执行 | 审计检查：用于确认仓库当前文件结构 |
| `rg -n "AI|ai|Spring AI|RAG|vector|向量|人工智能|智能问答|gpt|model|llm" .` | 已执行 | 审计检查：用于识别 AI 相关目录、文件、接口和依赖 |
| 后端单元测试 | 未执行 | 仓库不存在后端工程和测试命令 |
| 后端构建 | 未执行 | 仓库不存在 Maven/Gradle 配置 |
| 前端 lint | 未执行 | 仓库不存在前端工程和 lint 命令 |
| 前端生产构建 | 未执行 | 仓库不存在前端工程和构建命令 |

## 2026-07-07 测试结论

当时仓库尚无可执行测试。未运行任何单元测试、接口测试、前端 lint 或生产构建。

## 注意

没有实际运行的测试不得记录为“通过”。后续创建工程后，应将每次测试命令、结果、错误摘要和处理方式追加到本文件。

## 2026-07-09 T-011 工程骨架验证

以下命令用于验证 T-011 工程骨架可构建、可启动和基础依赖安全情况，不代表 `docs/test/测试用例.md` 中 95 条详细业务测试用例已经执行。

| 命令 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `mvn test` | 后端骨架单元测试 | 通过 | 执行 1 个 Spring Boot 上下文加载测试，Failures 0，Errors 0 |
| `mvn package` | 后端构建 | 通过 | 成功生成后端 Spring Boot jar |
| `java -jar backend/target/care-nexus-backend-0.1.0-SNAPSHOT.jar --server.port=18080` + `GET /api/v1/health` | 后端启动和健康检查 | 通过 | 健康检查返回 HTTP 200，响应包含 `status=UP` |
| `npm install` | PC 管理端和移动端依赖 | 已执行 | 安装项目依赖并生成/更新 `package-lock.json` |
| `npm run build` | PC 管理端 | 通过 | Vite 生产构建成功 |
| `npm run build` | 移动端 | 通过 | Vite 生产构建成功 |
| `npm audit --omit=dev` | PC 管理端和移动端生产依赖 | 通过 | 生产依赖审计结果为 0 vulnerabilities |
| `mysql --version` | MySQL 客户端检查 | 通过 | 本机存在 MySQL 8.0.45 客户端 |
| `Start-Service MySQL80` / `Start-Service mysql` | MySQL 服务启动尝试 | 未通过 | 当前终端权限不足，无法打开并启动本机 MySQL 服务 |
| `mysql --no-defaults --comments --force --execute="SOURCE database/init/001_schema.sql"` | SQL 脚本执行验证 | 未完成 | 因 MySQL 服务未启动，连接 `localhost:3306` 失败 |
| `docker --version` | Docker 环境检查 | 未安装 | 当前本机未识别 `docker` 命令；T-011 仅将 Docker 作为后续增强规划 |

### 本轮测试结论

- T-011 后端骨架构建、基础测试、启动和健康检查已通过。
- T-011 两个前端骨架生产构建已通过。
- 详细业务测试用例尚未执行，因为本轮没有实现完整业务功能。
- 数据库 SQL 脚本已创建，但本机 MySQL 服务因权限限制未能启动，因此未完成真实连接执行验证。

## 2026-07-09 T-011 修改后复审验证

以下命令用于验证 T-011 修改后复审范围，仍不代表 `docs/test/测试用例.md` 中 95 条详细业务测试用例已经执行。

| 命令 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `mvn verify` | 后端测试、打包、Checkstyle | 通过 | 执行 Spring Boot 上下文测试、生成 jar、Checkstyle 0 violations |
| `npm run lint` | PC 管理端 | 通过 | ESLint 无错误、无警告 |
| `npm run build` | PC 管理端 | 通过 | Vite 生产构建成功 |
| `npm run lint` | 移动端 | 通过 | ESLint 无错误、无警告 |
| `npm run build` | 移动端 | 通过 | Vite 生产构建成功 |
| `npm audit --omit=dev` | PC 管理端和移动端生产依赖 | 通过 | 生产依赖审计结果为 0 vulnerabilities |
| 临时 MySQL 8 实例执行 `001_schema.sql` 和 `002_seed_data.sql` | 数据库真实执行验证 | 通过 | 表数量 36，外键 50，唯一约束 17 |
| 后端启动后访问 `GET /api/v1/health` | 后端健康检查 | 通过 | HTTP 200，响应包含 `status=UP` |

### 本轮测试结论

- 后端骨架、静态检查和健康检查已通过。
- 两个前端骨架 lint 和生产构建已通过。
- MySQL 8 建表脚本和演示数据脚本已在临时 MySQL 实例中真实执行通过；本轮确认已删除 `sys_user_role` 表并新增 `ai_draft_source_resource` 表。
- 详细业务功能尚未实现，因此详细业务测试用例尚未执行。
- PowerDesigner 模型文件和截图尚未生成；该成果已拆分为 T-030 / Issue #2，最终交付前基于最终版 SQL 手动生成。
## 2026-07-09 T-012 后端登录权限自动化验证

本节记录 T-012 后端账号登录、当前用户与 RBAC 权限基础的实际验证情况。

| 命令 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `cd backend; mvn verify` | 后端单元测试、MockMvc接口测试、打包、Checkstyle | 通过 | 执行 23 个测试，Failures 0，Errors 0；Checkstyle 0 violations |
| `Get-Service -Name MySQL80,mysql` | MySQL服务状态检查 | 未通过 | `MySQL80` 与 `mysql` 当前均显示 Stopped |
| `Start-Service -Name MySQL80` | MySQL80服务启动尝试 | 未通过 | 当前终端无法打开并启动本机 MySQL80 服务 |

### 自动化测试覆盖

- 登录成功返回 Bearer Token、主角色和权限码。
- 密码错误和不存在账号均返回 HTTP 401 且使用相同对外提示。
- 停用账号和逻辑删除账号登录返回 HTTP 401。
- 有效 Token 访问 `GET /api/v1/auth/me` 成功。
- 无 Token、错误 Bearer 格式、伪造 Token、签名错误 Token、过期 Token 和黑名单 Token 返回 HTTP 401。
- `POST /api/v1/auth/logout` 后原 Token 失效，重复 logout 不返回 500。
- 有 `system:user:view` 权限访问 RBAC 验证接口返回 HTTP 200，无权限返回 HTTP 403。
- `me` 返回主要业务角色和权限码。
- BCrypt 哈希与演示密码匹配。
- 账号停用后旧 Token 不可继续使用。
- 权限变更后旧 Token 请求读取最新权限集合。

### 未执行事项

- 未执行 MySQL 真实联调。原因：本机 `MySQL80` 服务当前停止，且当前终端启动服务失败。
- 未重新执行前端 lint 和前端 build。原因：T-012 为后端独立任务，本轮未修改 `frontend/admin-web` 和 `frontend/mobile-web`。

## 2026-07-09 T-012 MySQL真实联调补跑

用户启动 MySQL80 服务并提供本机 root 登录信息后，已完成 T-012 真实数据库和 HTTP 联调。数据库密码仅用于本机命令执行，未写入仓库。

| 命令或操作 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `mysql --version` | MySQL客户端版本 | 通过 | MySQL 8.0.45 |
| `Get-Service -Name MySQL80` | MySQL服务状态 | 通过 | `MySQL80` 为 Running |
| `SOURCE 001_schema.sql` | 建库建表 | 通过 | 使用临时 ASCII 路径执行仓库 SQL，避免中文路径导致 MySQL `SOURCE` 失败 |
| `SOURCE 002_seed_data.sql` | 演示数据 | 通过 | 使用 `--default-character-set=utf8mb4` 执行，避免中文种子数据乱码 |
| 查询表数量 | 数据库结构 | 通过 | `care_nexus` 实际表数量 36 |
| 查询演示账号 | 数据库数据 | 通过 | 演示用户 10 个，权限码 9 个；包含正常、停用、逻辑删除账号 |
| 启动后端 `java -jar ... --server.port=18080` | 后端真实启动 | 通过 | 使用本机 MySQL 数据库和环境变量配置 |
| `GET /api/v1/health` | 健康检查 | 通过 | HTTP 200，`SUCCESS/UP` |
| `POST /api/v1/auth/login` | 管理员登录 | 通过 | HTTP 200，返回 Bearer Token、主角色 `ADMIN` 和权限码；Token 已脱敏记录 |
| `GET /api/v1/auth/me` | 当前用户 | 通过 | HTTP 200，返回 `admin_demo` 和 `NORMAL` |
| `GET /api/v1/auth/rbac-check` | RBAC验证 | 通过 | HTTP 200，返回 `allowed` |
| `POST /api/v1/auth/logout` | 退出登录 | 通过 | HTTP 200，返回 `success=true` |
| 旧 Token 再访问 `GET /api/v1/auth/me` | 黑名单验证 | 通过 | HTTP 401 |
| 停用账号登录 | 账号状态校验 | 通过 | HTTP 401 |
| 错误密码登录 | 凭证校验 | 通过 | HTTP 401 |

### 本轮补跑结论

- T-012 自动化测试和 MySQL 真实联调均已通过。
- 前端目录未修改，因此前端 lint 和 build 未重新执行，未记录为通过。
