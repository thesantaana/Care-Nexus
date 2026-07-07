# Module Ownership

更新时间：2026-07-07

## 模块负责人

| 模块/范围 | 负责人 | 当前状态 | 说明 |
|---|---|---|---|
| 后端架构与核心接口 | 隋咏轩 | 尚未创建 | 计划采用 Java、Spring Boot、Spring MVC、MyBatis 或 MyBatisPlus |
| 前端与页面联调 | 孙洋 | 尚未创建 | 计划采用 JavaScript、Vue，PC 端和移动端分开组织 |
| 需求与文档 | 李亦航 | 部分准备 | 当前仅有目录说明，正式需求文档尚未创建 |
| 测试与交付 | 张远航 | 部分准备 | 当前仅有目录说明，正式测试文档尚未创建 |
| AI 模块 | 隋咏轩、孙洋 | 尚未创建 | 当前仓库没有实际 AI 目录、AI 接口或 AI 依赖；后续出现 AI 目录后再登记具体负责范围 |

## AI 受保护范围

当前未发现实际 AI 代码目录、AI 接口文件或 AI 依赖配置。

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

## 本轮审计结论

AI 相关内容目前仅存在于 `README.md`、`AGENTS.md` 和部分 docs 说明文字中，属于项目规划描述，不属于可执行 AI 模块。
