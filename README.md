# CareNexus 颐联

CareNexus 颐联轻量版是一套面向护理人员的智慧培训平台，连接管理员与护工，支持培训资源管理、学习记录、护理知识考核和 AI 辅助学习。

## Project Scope

`lite_develop` 分支聚焦护理培训主线，仅保留管理员和护工两种角色。管理员维护培训资料、分类标签、题库和考核，护工通过移动端学习、查看进度和参加考核。

## Core Modules

- 培训内容管理：类别、标签、文章、视频和PPT资源。
- 护工学习中心：资源浏览、学习记录、整体学习时长和培训状态。
- 题库与考核：单选题、判断题、自动评分和考试记录。
- AI培训辅助：资料问答、知识总结、学习建议和题目草稿审核。

## Tech Stack

- Backend: Java, Spring Boot, Spring MVC, MyBatis / MyBatis-Plus
- Frontend: JavaScript, Vue or H5 / Mini Program
- Database: MySQL 8.0
- DevOps: Git
- AI: independent adapter service with stable Mock implementation

## MVP Boundary

轻量版 AI 仅用于护理培训资料问答、知识点总结、学习建议和题目草稿生成。AI 不提供医疗诊断、处方或自动医疗决策，生成的题目草稿须经管理员审核。

## Repository Plan

```text
care-nexus/
├── backend/        # Spring Boot backend service
├── frontend/       # Web / H5 frontend application
├── docs/           # Requirements, design, test and delivery documents
├── deploy/         # Docker, nginx and deployment scripts
└── database/       # MySQL schema and seed data
```

## Team Workflow

- `main`: stable releases and reviewed stage baselines.
- `develop`: planned daily integration branch. If it has not been created yet, do not create or switch to it without explicit approval.
- `feature/*`: feature or documentation task branches.
- Commit messages: keep them clear and module-oriented, such as `feat(training): add category API`.
- Pull requests: merge after review and basic local verification.
