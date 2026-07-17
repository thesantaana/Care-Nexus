# CareNexus Lite 开发规则

## 1. 项目范围

当前有效产品为 CareNexus Lite 护理培训平台，只包含两类业务角色：

- 管理员：维护培训分类、标签、资源、题库、考试和 AI 题目草稿。
- 护工：学习课程、记录笔记、参与讨论、完成作业和考试、查看成绩与错题，并使用 AI 学习辅助。

老人、家属、医生、健康档案、护理预约和订单不属于 Lite 当前交付范围。不得仅因历史文档存在而恢复这些模块。

## 2. 需求确认

涉及业务、页面、接口、数据库、权限或状态流转的修改，先遵循 `docs/workflows/REQUIREMENT_CLARIFICATION.md`。获得明确授权后再实施。

## 3. 技术与架构

- 后端：Java 8、Spring Boot 2.7.18、Spring Security、JWT、MyBatis-Plus、MySQL 8、Maven。
- 前端：Vue 3 管理端、Vue 3 护工端、React 18 门户端，均使用 Vite。
- 后端采用模块化单体，当前模块为 `auth`、`training`、`ai`、`file`、`audit`、`common`。
- Controller 只处理 HTTP 输入输出；业务逻辑位于 Service；Mapper 只负责数据访问。
- 模块之间通过 Service 协作，不得跨模块直接调用 Mapper。
- 不为满足目录模板创建空模块、空包或空类。

## 4. AI 边界

- AI 只辅助护理培训：资料问答、总结、学习建议、题目草稿和学习讲解。
- 默认使用稳定 Mock 模式；真实模型通过适配层切换，密钥仅从环境变量读取。
- AI 不提供诊断、处方、治疗方案或自动医疗决策。
- AI 题目草稿必须由管理员审核，不能自动进入正式题库或考试。

## 5. 代码规则

- 类使用大驼峰，变量和方法使用小驼峰，常量使用大写下划线。
- 删除无用导入、重复逻辑和无效注释；公共能力命名必须表达真实业务含义。
- 禁止空 `catch`、`System.out.println` 业务日志和向客户端返回堆栈。
- Service 文件明显超过 300 行、方法超过 50 行、依赖超过 6 个时优先拆分职责。
- 列表组装禁止逐条查询数据库，使用批量查询或预加载。

## 6. API、数据库与安全

- API 使用 `/api/v1`、统一 `ApiResponse`、统一错误码和分页结构。
- DTO 接收请求，VO 返回响应，不直接暴露 Entity。
- 数据库变更必须同步 SQL 脚本，使用 `utf8mb4`，状态值集中维护。
- 密码使用 BCrypt；Token、数据库密码和模型密钥不得提交到 Git。
- 日志不得记录密码、完整 Token 或敏感个人信息。
- 文件上传必须校验空文件、大小、扩展名、MIME 对应关系和安全文件名。

## 7. Git 规则

- `main`：历史稳定基线。
- `lite_develop`：当前 Lite 集成与演示分支。
- 新任务使用 `feature/*`、`fix/*` 或 `docs/*`，完成后合入 `lite_develop`。
- 提交前检查 diff；未经用户明确要求不 push、不合并、不删除分支。
- 提交信息使用 `feat:`、`fix:`、`refactor:`、`test:`、`docs:`、`style:`、`chore:`。

## 8. 验证与记录

代码任务完成前运行所有适用命令：

- 后端：`mvn verify`
- 管理端：`npm run lint`、`npm run build`
- 护工端：`npm run lint`、`npm run build`
- 门户端：`npm run build`
- 通用：`git diff --check`

未执行的检查不得写成通过。不适用项需注明原因。实际测试记录写入 `docs/test/TEST_LOG.md`，当前状态写入 `docs/management/PROJECT_STATUS.md`，任务写入 `docs/management/TASKS.md`。

## 9. 完成定义

任务只有满足全部适用条件才完成：达到验收标准、架构边界正确、权限和异常受控、相关接口和 SQL 同步、适用构建与测试真实执行、文档与状态更新、没有提交敏感信息或构建产物。
