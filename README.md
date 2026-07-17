# CareNexus Lite 颐联护理培训平台

CareNexus Lite 是面向护理培训场景的前后端分离系统。当前 `lite_develop` 分支聚焦管理员与护工两类角色，形成“资料发布 - 在线学习 - 作业考试 - 成绩错题 - AI辅助”的可演示闭环。

## 已实现能力

### 管理员端

- 培训分类、标签和资源管理，支持文章、视频、PPT及课程封面上传。
- 资源草稿、发布和下架状态管理。
- 单选题、判断题、考试和题目关联管理。
- AI题目草稿列表、详情、审核通过和驳回。
- 护工课程成绩和整体培训结果查看。

### 护工端

- 课程列表、章节资料、学习访问记录和进度统计。
- 富文本学习笔记、图片插入和笔记卡片管理。
- 课程讨论、回复、点赞和本人内容删除。
- 多题作业、答题卡、自动批改、错题反馈和 AI 讲解。
- 考试列表、次数限制、自动评分、课程成绩、平均分和错题集。
- AI资料问答、知识总结、学习建议与练习生成。

### 门户端

- 品牌首页、产品理念和 AI 助手能力介绍。
- 管理员/护工身份入口，跳转到对应工作台。

## 技术栈

- 后端：Java 8、Spring Boot 2.7.18、Spring Security、JWT、MyBatis-Plus 3.5.5、MySQL 8。
- 管理端：Vue 3 + Vue Router + Vite。
- 护工端：Vue 3 + Vue Router + Vite。
- 门户端：React 18 + TypeScript + Vite + GSAP + Tiptap。
- AI：适配器结构，默认 Mock，可通过环境变量切换 DeepSeek。

## 目录

```text
backend/                 Spring Boot 后端
frontend/admin-web/      管理员端，默认端口 5173
frontend/mobile-web/     护工学习端，默认端口 5174
frontend/portal-web/     门户首页，默认端口 5175
database/init/           MySQL 建表、种子和演示数据脚本
docs/                    当前产品、设计、API、测试和过程文档
scripts/                 本地一键启动/停止脚本
```

## 本地运行

前置环境：JDK 8+、Maven 3.8+、Node.js 18+、npm、MySQL 8。

1. 创建数据库并按编号执行 `database/init/001_schema.sql` 至 `008_demo_showcase_data.sql`。
2. 在 PowerShell 中启动：

```powershell
.\scripts\start-lite.ps1 -DatabaseUsername root -DatabasePassword "你的本机MySQL密码"
```

3. 访问：

- 门户首页：http://localhost:5175
- 管理员端：http://localhost:5173
- 护工端：http://localhost:5174
- 后端健康检查：http://localhost:8080/api/v1/health

停止服务：

```powershell
.\scripts\stop-lite.ps1
```

## 演示账户

- 管理员：`admin_demo` / `1`
- 护工：`caregiver_demo` / `1`

账户仅用于本地演示。生产环境必须修改密码和 JWT 密钥。

## AI 模式

默认 `AI_MODE=mock`，无需密钥。真实模型模式需要自行配置 `AI_MODE=deepseek` 和 `DEEPSEEK_API_KEY`，密钥不得写入仓库。

## Git 分支

- `main`：历史稳定基线。
- `lite_develop`：当前 Lite 产品的最新集成分支。
- 新功能或修复从 `lite_develop` 创建任务分支，审核后合回 `lite_develop`。

更详细内容见 [docs/README.md](docs/README.md)。
