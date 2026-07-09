# Module Ownership

更新时间：2026-07-07

## 模块负责人

| 模块/范围 | 负责人 | 当前状态 | 说明 |
|---|---|---|---|
| 后端架构与核心接口 | 隋咏轩 | 骨架已创建 | 当前已创建 Java 8、Spring Boot 2.7.x、MyBatis-Plus 后端骨架 |
| 前端与页面联调 | 孙洋 | 骨架已创建 | 当前已创建 PC 管理端和移动端 Vue 工程骨架 |
| 需求与文档 | 李亦航 | 已形成需求基线 | 需求规约、用例模型和业务流程已进入需求基线 v1.0 |
| 测试与交付 | 张远航 | 测试设计已完成 | 测试计划和详细测试用例已完成审核，尚未执行真实业务测试 |
| AI 模块 | 隋咏轩、孙洋 | 骨架已创建 | 当前已创建 AI 培训辅助适配接口和 Mock 实现，真实模型接入需后续任务确认 |

## AI 受保护范围

当前已发现并登记以下实际 AI 相关范围：

- `backend/src/main/java/com/carenexus/ai/`
- `docs/api/API设计规范.md` 中 AI 草稿接口规范部分
- `docs/design/系统架构设计.md` 中 AI 适配层设计部分
- `docs/design/数据库设计.md` 中 AI 草稿和审核相关表设计

以上范围由隋咏轩、孙洋负责。非 AI 负责人或非 AI 任务不得修改；AI负责人隋咏轩、孙洋在需求确认和用户授权后可在登记范围内开发。

后续如出现以下目录或文件，应在本文件登记为受保护范围。非 AI 负责人或非 AI 任务不得修改；AI负责人隋咏轩、孙洋在需求确认和用户授权后可在登记范围内开发：

- `backend/*/ai/`
- `backend/*/assistant/`
- `backend/*/llm/`
- `backend/*/rag/`
- `backend/*/vector/`
- `backend/*/prompt/`
- `frontend/*/ai/`
- `frontend/*/assistant/`
- AI 接口协议文档，如 `docs/api/*ai*.md`、`docs/api/*assistant*.md`
- Spring AI、向量数据库、RAG 相关配置或依赖

## 本轮登记结论

T-011 已创建 AI 培训辅助适配层骨架，但尚未接入真实 AI 模型，尚未实现 AI 题目正式发布逻辑，也未扩展到医生端、老人端或管理决策 AI。
