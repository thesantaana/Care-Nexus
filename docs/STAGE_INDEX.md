# 轻量版阶段工件索引

更新时间：2026-07-15

本文件说明 CareNexus 轻量版各阶段工件的唯一存放位置。早期完整版阶段材料仍保留在仓库历史中，但当前开发、测试和交付均以 `lite_develop` 为基线。

## 先启与范围收敛

| 工件 | 路径 |
|---|---|
| 项目简介 | `README.md` |
| 轻量版范围 | `docs/management/LITE_SCOPE.md` |
| 当前项目状态 | `docs/management/PROJECT_STATUS.md` |
| 模块负责人 | `docs/management/MODULE_OWNERSHIP.md` |
| 架构决策 | `docs/decisions/ADR-001-总体架构与工程结构.md` |

## 需求阶段

| 工件 | 路径 |
|---|---|
| 软件需求规约 | `docs/requirements/软件需求规约.md` |
| 用例模型 | `docs/requirements/用例模型.md` |
| 业务流程 | `docs/requirements/业务流程.md` |

## 设计与实现阶段

| 工件 | 路径 |
|---|---|
| 系统架构 | `docs/design/系统架构设计.md` |
| 分析设计模型 | `docs/design/系统分析与设计模型.md` |
| 原型说明 | `docs/design/原型设计.md` |
| 数据库设计 | `docs/design/数据库设计.md` |
| 认证设计 | `docs/design/认证设计说明.md` |
| 工程结构 | `docs/design/工程骨架说明.md` |
| API 规范 | `docs/api/API设计规范.md` |
| 接口清单 | `docs/api/MVP接口清单.md` |
| 数据库脚本 | `database/init/` |
| 数据字典 | `database/dict/data-dictionary.md` |

## 测试与交付阶段

| 工件 | 路径 |
|---|---|
| 测试计划 | `docs/test/测试计划.md` |
| 测试用例 | `docs/test/测试用例.md` |
| 实际测试日志 | `docs/test/TEST_LOG.md` |
| 任务路线 | `docs/management/TASKS.md` |
| 变更记录 | `docs/management/CHANGELOG.md` |
| 项目进度 | `docs/plans/项目进度计划.md` |

## 历史记录

`docs/logs/`、`docs/meetings/`、`docs/management/tasks/`、需求基线评审记录、Changelog 和 Test Log 可以描述完整版开发过程。它们是审计证据，不是当前轻量版功能清单。

## 当前阶段

当前处于轻量版联调完善、文档归一和交付准备阶段。后续主要工件为全链路回归记录、部署说明、用户手册、最终测试报告、CDM/PDM、答辩材料和版本发布记录。