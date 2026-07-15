# CareNexus 轻量版数据库模型成果

更新时间：2026-07-15  
目标基线：`lite_develop` 最终 28 表结构

本目录用于保存 PowerDesigner 概念数据模型、物理数据模型和导出图片。

## 计划成果

```text
CareNexus-Lite.cdm
CareNexus-Lite.pdm
CareNexus-Lite-CDM.png
CareNexus-Lite-PDM.png
```

当前仓库只有模型说明，尚未生成上述 PowerDesigner 成果。不得把 README、CDM说明或 PDM说明写成“模型已经完成”。

## 模型来源

模型必须基于以下脚本顺序生成：

```text
001_schema.sql
002_seed_data.sql
003_add_account_course_images.sql
004_course_interactions.sql
005_demo_learning_progress.sql
006_multi_question_assignments.sql
007_social_discussions.sql
008_demo_showcase_data.sql
```

结构建模以 001、003、004、007 的最终表和字段为主，数据脚本用于核对枚举、演示关系和业务语义。

## 当前范围

最终模型共 28 张表，包含：

- 账号与权限。
- 文件和审计。
- 培训资源与学习。
- 题库、考核和成绩。
- AI 题目草稿。
- 笔记、讨论、点赞、作业和提交。

不包含护理订单、老人家属、医生健康记录、预警、随访和评估。

## 生成要求

1. 在干净 MySQL 8 环境验证 001–008。
2. 确认最终表数 28。
3. 从数据库或最终 SQL 反向生成 PDM。
4. 整理逻辑实体和关系生成 CDM。
5. 检查主键、唯一约束、外键、索引、字段类型和默认值。
6. 导出清晰 PNG，避免文字重叠。
7. 将模型版本、生成工具和日期写入本目录。
8. 更新 `PROJECT_STATUS.md`、`TASKS.md` 和 `TEST_LOG.md`。

## 历史模型说明

早期完整版曾记录 36 张表。该结构属于 `develop`，不得用作轻量版最终模型。早期轻量范围说明中的 21/22 张也已经被笔记、讨论、作业和点赞迁移扩展，当前统一为 28 张。