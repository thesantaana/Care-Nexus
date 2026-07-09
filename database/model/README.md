# 数据库模型成果目录

本目录用于保存 CareNexus 数据库概念数据模型（CDM）、物理数据模型（PDM）和 PowerDesigner 导出成果。

## 当前状态

- T-011 已完成 MySQL 物理建表脚本：`database/init/001_schema.sql`。
- T-011 已完成 CDM/PDM 文字说明：`CDM说明.md`、`PDM说明.md`。
- PowerDesigner 16.5 已确认可在本机打开；模型文件和截图不在 T-011 当前提交中生成，已拆分为 T-030 / Issue #2。
- 不得提交伪造或无法打开的 `.pdm` / `.cdm` 文件。

## T-030 最终应补充的文件

| 文件 | 说明 | 状态 |
|---|---|---|
| `CareNexus.cdm` | 由项目负责人使用 PowerDesigner 整理或生成的概念数据模型 | 待生成 |
| `CareNexus.pdm` | 使用 PowerDesigner 从 MySQL 或 SQL 反向工程生成的物理模型 | 待生成 |
| `CareNexus-CDM.png` | CDM 模型截图 | 待导出 |
| `CareNexus-PDM.png` | PDM 模型截图 | 待导出 |

上述模型文件和截图必须由项目负责人在 PowerDesigner 中基于最终版 SQL 手动生成并确认可打开、可查看。Codex 不得伪造 `.cdm`、`.pdm` 或截图文件。T-030 标记为“最终交付前完成，不阻塞当前业务开发”。

## 推荐生成流程

1. 在可用 MySQL 8 环境中执行 `database/init/001_schema.sql`。
2. 使用 PowerDesigner 连接 MySQL 或导入 SQL，反向工程生成 PDM。
3. 检查表、主键、外键、唯一约束和索引是否完整。
4. 根据 PDM 整理或生成 CDM。
5. 保存可打开的模型文件并导出截图。
6. 将模型文件和截图放入本目录。
7. 在 `docs/design/数据库设计.md` 中更新模型引用状态。
