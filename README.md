# CareNexus 颐联

CareNexus 颐联是一套面向智慧护理场景的四端互联管理系统，以护理培训系统为核心，连接老人及家属、护理人员、医生和后台管理员，支持培训资源管理、AI 辅助学习、护理服务预约、健康档案、预警随访和统一权限管理。

## Project Scope

本项目以“互联网+智慧护理护理培训系统”为主线，扩展移动护理平台、医生服务系统和综合管理系统，通过统一账号、统一角色权限、统一老人档案、统一订单/服务数据实现跨端互联互通。

## Core Modules

- 护理培训系统：类别、标签、文章、视频、PPT、学习记录、AI 辅助培训。
- 移动护理平台：注册登录、服务浏览、预约下单、地址管理、订单、评价、投诉。
- 医生服务系统：老人健康档案、健康预警、重点人群随访、干预记录、健康评估。
- 综合管理系统：用户、角色、权限、基础字典、文件资源、系统配置和操作日志。

## Tech Stack

- Backend: Java, Spring Boot, Spring MVC, MyBatis / MyBatis-Plus
- Frontend: JavaScript, Vue or H5 / Mini Program
- Database: MySQL 8.0
- Middleware: Redis, nginx
- DevOps: Git, Docker
- AI: Spring AI or independent AI adapter service

## MVP Boundary

MVP 阶段仅在护理培训系统中接入 AI 能力，聚焦培训资料问答、知识点总结、学习路径建议和练习题生成。医生端 AI 诊断、老人端 AI 问诊、第三方医院系统接口、真实支付和真实短信网关暂不纳入首期交付。

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

- Main branch: stable integration branch.
- Feature branches: use `feature/<module-name>` for daily development.
- Commit messages: keep them clear and module-oriented, such as `feat(training): add category API`.
- Pull requests: merge after review and basic local verification.
