# Architecture Decision Records

本目录保存 CareNexus 的架构决策记录。ADR 一经接受原则上不删除；决策变化时新增 ADR 并说明替代关系。

当前现行决策：

- `ADR-001-总体架构与工程结构.md`：轻量版产品范围、模块化单体、三个前端、MySQL 28 表、Mock/DeepSeek AI 与安全边界。

适用基线：`lite_develop`。完整版 `develop` 中的订单、医生和健康模块不属于当前架构。

新增 ADR 应包含背景、决策、替代方案、正负影响、实施约束和后续工作。涉及角色、核心框架、数据库、医疗 AI、破坏性接口或大范围部署变化时必须记录 ADR。