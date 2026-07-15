# CareNexus 文档总览

更新时间：2026-07-15  
现行开发基线：`lite_develop`

本目录保存 CareNexus 轻量版的需求、设计、接口、测试、过程和交付材料。阅读文档时应先区分“现行基线文档”和“历史过程记录”，避免把完整版阶段已经删除的护理订单或医生健康管理能力误认为当前功能。

## 1. 推荐阅读顺序

1. [轻量版范围](management/LITE_SCOPE.md)
2. [项目当前状态](management/PROJECT_STATUS.md)
3. [软件需求规约](requirements/软件需求规约.md)
4. [用例模型](requirements/用例模型.md)
5. [业务流程](requirements/业务流程.md)
6. [系统架构设计](design/系统架构设计.md)
7. [数据库设计](design/数据库设计.md)
8. [API 设计规范](api/API设计规范.md)
9. [MVP 接口清单](api/MVP接口清单.md)
10. [测试计划](test/测试计划.md)

## 2. 现行基线文档

| 目录 | 内容 | 维护原则 |
|---|---|---|
| `management/` | 范围、状态、任务和负责人 | 随当前实现更新 |
| `plans/` | 开发、进度和配置管理 | 面向轻量版交付 |
| `requirements/` | 需求、用例和业务流程 | 只描述管理员与护工培训主线 |
| `design/` | 架构、模型、原型、数据库和认证 | 与当前代码和 28 表结构一致 |
| `api/` | API 规则和接口清单 | 以 Controller 实际路由为准 |
| `decisions/` | 架构决策记录 | 重大技术选择只追加 ADR |
| `test/测试计划.md` | 当前测试策略 | 覆盖轻量版范围 |
| `test/测试用例.md` | 当前核心用例 | 与现行接口和角色一致 |

## 3. 历史过程记录

以下文件保留项目从完整版需求基线、功能开发到轻量版收敛的真实过程，可能包含当前已经删除的角色、模块、表数量和测试结论：

- `management/CHANGELOG.md`
- `test/TEST_LOG.md`
- `logs/`
- `management/tasks/`
- 早期需求基线评审、会议和个人工作记录

这些文件原则上只追加，不回写历史内容。需要判断当前状态时，以 `LITE_SCOPE.md`、`PROJECT_STATUS.md` 和当前代码为准。

## 4. 当前系统口径

- 角色：管理员、护工。
- 产品：培训资源、学习、逐课考核、成绩、错题、笔记、讨论、作业和 AI 培训辅助。
- 前端：React 统一门户、Vue 管理端、Vue 护工端。
- 后端：Java 8 + Spring Boot 2.7.18 + MyBatis-Plus。
- 数据库：MySQL 8，最终 28 张表。
- AI：默认 Mock，可选 DeepSeek，只处理可访问的文本培训资料。
- 排除：护理订单、医生健康管理和医疗决策。

## 5. 文档更新检查

功能、接口或数据模型变化时至少检查：

- `README.md`
- `management/LITE_SCOPE.md`
- `management/PROJECT_STATUS.md`
- `management/TASKS.md`
- `requirements/`
- `design/`
- `api/MVP接口清单.md`
- `database/README.md` 与数据字典
- `test/测试计划.md` 与 `TEST_LOG.md`

没有实际执行的测试不得写成通过。文档中的角色、路由、端口、表数量和状态必须能够从当前分支的代码或 SQL 中验证。