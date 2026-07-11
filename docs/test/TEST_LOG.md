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

## 2026-07-09 T-014 培训资源管理后端自动化验证

本节记录 T-014 培训类别、标签和资源管理后端的实际自动化验证情况。

| 命令 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `cd backend; mvn test` | 后端单元测试、MockMvc接口测试 | 通过 | 累计执行 44 个测试，Failures 0，Errors 0；其中 T-014 新增 21 个测试，T-012 认证回归测试继续通过 |
| `cd backend; mvn verify` | 后端测试、打包、Checkstyle | 通过 | 累计执行 44 个测试，Failures 0，Errors 0；生成 jar；Checkstyle 0 violations |
| MySQL 8 临时库执行 `001_schema.sql` 和 `002_seed_data.sql` | 数据库真实执行验证 | 通过 | 使用 `care_nexus_t014` 临时库，不覆盖既有 `care_nexus`；表数量 36，`CAREGIVER` 已具备 `training:resource:view` |
| 后端连接 MySQL 8 临时库后 HTTP 联调 | T-014 真实接口联调 | 通过 | 覆盖 trainer/caregiver 登录、类别新增、标签新增、TEXT资源创建、发布、护工查询/详情、护工新增403、下架、下架后护工不可见、标签关联和操作日志 |

### T-014 自动化测试覆盖

- 未登录访问培训接口返回 HTTP 401。
- `training:resource:view` 可查询，不能新增、修改、发布或下架。
- `training:resource:manage` 可执行类别、标签和资源管理操作。
- 类别新增、重复名称、空名称、不存在和非法状态校验。
- 标签新增、重复名称、不存在和状态切换。
- TEXT、LOCAL_FILE、EXTERNAL_LINK 三类存储方式校验。
- 资源创建默认 `DRAFT`，`tagIds` 去重，`createdBy` 来自当前用户。
- 草稿资源可修改，已发布资源直接修改返回 `CONFLICT`。
- 普通查看用户无法读取草稿或下架资源详情。
- 资源分页结构包含 `records`、`pageNo`、`pageSize`、`total`、`pages`。
- 发布设置 `publishedAt` 并写操作日志，重复发布返回 `CONFLICT`。
- 下架写操作日志，重复下架返回 `CONFLICT`。

### MySQL真实联调结果

- `trainer_demo` 登录成功，主角色为 `TRAINING_ADMIN`。
- `caregiver_demo` 登录成功，主角色为 `CAREGIVER`。
- `trainer_demo` 新增培训类别、新增培训标签、创建 TEXT 培训资源成功。
- 新资源初始状态为 `DRAFT`。
- `trainer_demo` 发布资源成功，状态变为 `PUBLISHED`。
- `caregiver_demo` 可查询已发布培训资源，可查看已发布资源详情。
- `caregiver_demo` 尝试新增资源返回 HTTP 403。
- `trainer_demo` 下架资源成功，状态变为 `OFFLINE`。
- 下架后 `caregiver_demo` 无法查看该资源详情，返回 HTTP 404。
- `training_resource_tag` 关联去重后仅保留 1 条有效关联。
- `operation_log` 中存在发布和下架记录，共 2 条。

### 未执行事项

- 未执行前端 lint 和前端 build。原因：T-014 未修改 `frontend/admin-web` 和 `frontend/mobile-web`。

## 2026-07-10 T-016 题库、考核和学习记录后端自动化验证

本节记录 T-016 题库、考核和学习记录后端的实际验证情况。

| 命令 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `cd backend; mvn verify` | 后端测试、打包、Checkstyle | 通过 | 累计执行 52 个测试，Failures 0，Errors 0；生成 jar；Checkstyle 0 violations |
| MySQL 8.0.45 执行 `001_schema.sql` 和 `002_seed_data.sql` | 数据库真实执行验证 | 通过 | 使用同版本隔离实例端口 3307；实际 36 张表、51 个外键、17 个唯一约束；3 个培训资源、9 道题、24 个选项和 1 套考核 |
| 后端连接 MySQL 8.0.45 后执行最小 HTTP 联调 | T-016 主链路 | 通过 | 覆盖护工登录、资源学习、学习门槛、考核获取、提交、自动评分、培训通过状态和数据库落库 |

### T-016 自动化测试覆盖

- `training:resource:view` 无法创建考核。
- 培训管理员可以创建考核和客观题。
- `SHORT_ANSWER` 题型被拒绝。
- 单选题选项必须只有一个正确答案。
- 学习访问记录会累计整体学习时长并更新学习中状态。
- 学习时长或资源访问未达标时拒绝提交考核。
- 客观题提交后立即自动评分，并更新考核通过状态。

### MySQL 最小真实联调结果

- `caregiver_demo` 登录成功，主角色为 `CAREGIVER`。
- 查询到 3 个已发布培训资源，逐个记录 600 秒学习访问。
- 整体学习时长为 1800 秒，已访问资源 3/3，`examAllowed=true`。
- 已发布考核返回 9 道客观题；提交正确答案后得分 60.00，考核状态和整体培训状态均为 `PASSED`。
- 数据库实际写入 3 条学习访问、1 条学习汇总、1 条考核记录、9 条答案和 2 条操作日志。
- 真实联调发现并修复判断题无选项时的 NPE，以及登录只读事务中操作日志无法写入的问题。

### 未执行事项

- 未执行前端 lint 和前端 build。原因：T-016 未修改 `frontend/admin-web` 和 `frontend/mobile-web`。
## 2026-07-10 后端复杂度整理验证

本节记录 T-014 和 T-016 后端复杂度整理后的实际验证结果。

| 命令或操作 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `cd backend; mvn verify` | 后端测试、打包、Checkstyle | 通过 | 累计执行 52 个测试，Failures 0，Errors 0；生成 jar；Checkstyle 0 violations |
| `rg TrainingStatuses` | 常量拆分检查 | 通过 | 主代码和测试中不再引用 `TrainingStatuses` |
| `rg FileResourceMapper backend/src/main/java/com/carenexus/training` | 模块边界检查 | 通过 | training 主代码不再直接引用 file 模块 Mapper |
| `Get-Service -Name MySQL80,mysql` | MySQL 服务状态 | 未通过 | 两个服务均为 Stopped |
| `Start-Service -Name MySQL80` / `Start-Service -Name mysql` | 尝试启动 MySQL 服务 | 未通过 | 当前终端无法打开并启动服务 |
| `Get-NetTCPConnection -LocalPort 3306` | MySQL 端口检查 | 未通过 | 3306 未监听 |

结论：后端自动化测试、构建和静态检查通过；随后使用 MySQL 8.0.45 同版本隔离实例完成真实导入和 T-016 最小 HTTP 联调，结果通过。3306 服务的 Windows I/O 崩溃属于本机服务环境问题，不作为项目 SQL 或业务联调失败记录。

## 2026-07-10 T-018 护理预约与订单闭环后端验证

| 命令 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `cd backend; mvn -DskipTests compile` | 后端编译 | 通过 | T-018 主代码编译成功 |
| `cd backend; mvn test` | 后端自动化测试 | 通过 | 累计执行 59 个测试，Failures 0，Errors 0 |
| `cd backend; mvn verify` | 测试、打包、Checkstyle | 通过 | 59 个测试全部通过，生成 jar，Checkstyle 0 violations |

### T-018 聚焦测试覆盖

- 待分配订单可以进入待确认，非法旧状态不能执行同一流转。
- 创建预约时校验未来预约时间和可用服务项目。
- 取消、确认、开始和完成操作使用预期旧状态进行原子更新。
- 完成服务要求填写服务记录，重复状态操作被拒绝。
- 已完成订单只允许评价一次，评分限制为 1 至 5。
- 已完成订单只允许投诉一次，评价和投诉独立于订单主状态。

### 未执行事项

- 未执行前端 lint/build，原因是 T-018 未修改前端代码。
- 未执行 T-018 调整后 SQL 的正式 MySQL 全量导入、完整真实 HTTP 联调和跨模块回归测试。该范围由项目负责人明确归入 T-023，当前不得记录为通过。

## 2026-07-11 T-013 / T-015 前端实现验证

| 命令或操作 | 范围 | 结果 | 说明 |
|---|---|---|---|
| `cd frontend/admin-web && npm run lint` | PC 前端静态检查 | 通过 | ESLint 无报错 |
| `cd frontend/admin-web && npm run build` | PC 前端生产构建 | 通过 | Vite 生产构建完成 |
| `cd frontend/mobile-web && npm run lint` | 移动前端静态检查 | 通过 | ESLint 无报错 |
| `cd frontend/mobile-web && npm run build` | 移动前端生产构建 | 通过 | Vite 生产构建完成；已消除父目录 PostCSS 配置造成的 Tailwind 告警 |
| `cd frontend/portal-web && npm run build` | React 门户前端类型检查与生产构建 | 通过 | TypeScript 编译和 Vite 生产构建完成，图片与视频静态资源均被纳入工程 |
| 两个 Vite 开发服务器 + 本机 HTTP 请求 | 前端入口冒烟检查 | 通过 | `5173` 与 `5174` 均返回应用入口 HTML |
| `cd backend && mvn verify` | 后端依赖与测试尝试 | 未通过 | Maven Central TLS 握手失败，缺失依赖未能下载；不是业务测试失败 |
| 本机真实 login/me/logout 联调 | 前后端认证接口 | 未执行 | 本机 MySQL 服务未运行，后端无法启动；待环境恢复后执行 |

### 本轮结论

- 已验证前端代码可通过静态检查和生产构建，未使用业务 Mock 数据。
- 真实认证与培训 HTTP 联调尚未执行，因此 T-013、T-015 保持 `DOING`，不得标记为完成。
