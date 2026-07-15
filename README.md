# CareNexus 颐联

> 面向养老护理人员的轻量化培训、考核与 AI 辅助学习平台。

CareNexus 颐联通过统一门户连接管理员工作台与护工学习端，覆盖培训资源管理、课程学习、逐课考核、成绩追踪、富文本笔记、课程讨论、课后作业和 AI 培训辅助。当前轻量版以 `lite_develop` 为开发基线，仅保留管理员与护工两类角色，不包含护理订单、医生健康管理或医疗决策功能。

## 当前状态

文档核对基线：`lite_develop`，提交 `319838d`（2026-07-15 核对）。

- 后端核心业务已经落地：认证与 RBAC、培训资源、题库考核、学习成绩、笔记、讨论、作业、AI 草稿和审计。
- 管理端已经具备资源、分类标签、题库考核、AI 草稿审核和培训成绩页面。
- 护工端已经具备课程浏览、课程详情、学习进度、错题、笔记、讨论、作业和个人账号页面。
- 统一门户使用 React，管理员端和护工端使用 Vue。
- AI 默认运行在稳定 Mock 模式，可通过配置切换 DeepSeek；所有 AI 输出仅基于有权访问的文本培训资料。
- 数据库当前由 8 个顺序脚本组成，最终结构共 28 张表。

## 角色与权限

| 角色 | 主要能力 |
|---|---|
| 管理员 `ADMIN` | 管理培训资源、分类、标签、题库、考核、AI 题目草稿和护工成绩 |
| 护工 `CAREGIVER` | 浏览课程、记录学习、参加考核、查看成绩与错题、维护笔记、参与讨论和提交作业 |

轻量版已删除医生、健康管理人员、老人、家属、运营人员和独立培训管理员角色。管理员吸收培训管理职责。

## 核心功能

### 培训内容

- 类别和标签启停管理。
- 文章、视频、PPT 三类培训资源。
- 文本、本地文件、外部链接三种存储方式。
- 资源草稿、发布、下架状态流转。
- 课程封面、学习时长和访问记录。

### 题库、考核与成绩

- 单选题和判断题。
- 每门已发布课程绑定一份考核。
- 客观题自动评分，默认 60 分通过。
- 记录考试次数、最高分、平均分、通过状态和错题。
- 全部课程通过后，整体培训状态更新为通过。

### 学习互动

- 账号级富文本课程笔记，支持图片和常用排版。
- 课程讨论、回复、嵌套回复、点赞与本人内容删除。
- 课程课后作业及个人提交记录。

### AI 培训辅助

- 培训资料问答。
- 培训内容总结。
- 个人学习建议。
- 单选题、判断题草稿生成与管理员审核。
- 草稿审核通过后仅创建正式题库中的 `DRAFT` 题目，不自动发布或加入考核。

AI 不提供疾病诊断、药品处方、个体化治疗方案或自动医疗决策。资料不足时必须明确说明，不得编造来源。

## 技术栈

| 层级 | 技术 |
|---|---|
| 后端 | Java 8、Spring Boot 2.7.18、Spring Security、JWT、MyBatis-Plus 3.5.5 |
| 数据库 | MySQL 8、顺序 SQL 迁移脚本 |
| 管理端 | Vue 3.5、Vue Router 4、Vite 5 |
| 护工端 | Vue 3.5、Vue Router 4、Vite 5 |
| 统一门户 | React 18、TypeScript、Vite 6、Tiptap、GSAP |
| AI | Mock / DeepSeek 可切换适配层 |
| 质量检查 | JUnit 5、MockMvc、Maven Verify、Checkstyle、ESLint、Vite Build |

## 仓库结构

```text
Care-Nexus/
├── backend/                 # Spring Boot 后端
├── database/                # 建表、种子和演示数据脚本
├── docs/                    # 需求、设计、接口、测试和管理文档
├── frontend/
│   ├── portal-web/          # React 统一门户
│   ├── admin-web/           # Vue 管理端
│   └── mobile-web/          # Vue 护工端
├── scripts/
│   ├── start-lite.ps1       # Windows 本地一键启动
│   └── stop-lite.ps1        # Windows 本地停止脚本
└── AGENTS.md                # 项目协作与开发约束
```

## 本地启动

### 环境要求

- JDK 8
- Maven 3.8+
- Node.js 18+
- MySQL 8
- Windows PowerShell（使用一键脚本时）

### 1. 初始化数据库

按顺序执行：

```text
database/init/001_schema.sql
database/init/002_seed_data.sql
database/init/003_add_account_course_images.sql
database/init/004_course_interactions.sql
database/init/005_demo_learning_progress.sql
database/init/006_multi_question_assignments.sql
database/init/007_social_discussions.sql
database/init/008_demo_showcase_data.sql
```

### 2. 安装前端依赖

分别进入三个前端目录执行：

```bash
npm install
```

### 3. 一键启动

```powershell
$env:DB_PASSWORD = "你的本机 MySQL 密码"
./scripts/start-lite.ps1
```

默认地址：

| 服务 | 地址 |
|---|---|
| 统一门户 | `http://localhost:5175` |
| 管理端 | `http://localhost:5173` |
| 护工端 | `http://localhost:5174` |
| 后端健康检查 | `http://localhost:8080/api/v1/health` |

停止服务：

```powershell
./scripts/stop-lite.ps1
```

### 演示账号

演示密码统一为：`Demo@123456`

| 用户名 | 角色 |
|---|---|
| `admin_demo` | 管理员 |
| `caregiver_demo` | 护工 |

演示账号仅用于本地开发和答辩环境，生产环境必须删除或更换。

## AI 配置

默认使用 Mock：

```text
AI_MODE=mock
```

切换 DeepSeek：

```text
AI_MODE=deepseek
DEEPSEEK_API_KEY=你的密钥
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-chat
```

不要把密钥、数据库密码或生产 JWT Secret 提交到仓库。

## 文档入口

- [文档总览](docs/README.md)
- [轻量版范围](docs/management/LITE_SCOPE.md)
- [项目状态](docs/management/PROJECT_STATUS.md)
- [软件需求规约](docs/requirements/软件需求规约.md)
- [系统架构设计](docs/design/系统架构设计.md)
- [MVP 接口清单](docs/api/MVP接口清单.md)
- [测试计划](docs/test/测试计划.md)
- [数据库说明](database/README.md)

## 分支策略

- `main`：稳定基线。
- `develop`：原完整版业务分支，保留历史完整系统实现。
- `lite_develop`：轻量版当前开发基线。
- `feature/*`：功能分支。
- `agent/*`：自动化文档或代码整理分支。

所有面向轻量版的新功能和文档应以 `lite_develop` 为目标分支，不再向轻量版重新引入护理订单或医生健康管理模块。

## 项目边界

CareNexus 是培训与学习管理软件，不是医疗信息系统，也不是临床决策工具。任何课程内容、AI 输出和考核结果都不能代替专业医疗判断。