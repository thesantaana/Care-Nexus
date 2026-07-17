# CareNexus Lite 已实现接口清单

## 认证

| 方法 | URL | 说明 |
| --- | --- | --- |
| POST | `/api/v1/auth/login` | 登录 |
| GET | `/api/v1/auth/me` | 当前用户、角色和权限 |
| POST | `/api/v1/auth/logout` | 退出并使Token失效 |
| GET | `/api/v1/auth/rbac-check` | RBAC演示检查 |
| GET | `/api/v1/health` | 匿名健康检查 |

## 分类、标签与资源

- `GET/POST /api/v1/training/categories`
- `PUT /api/v1/training/categories/{id}`
- `PUT /api/v1/training/categories/{id}/status`
- `GET/POST /api/v1/training/tags`
- `PUT /api/v1/training/tags/{id}`
- `PUT /api/v1/training/tags/{id}/status`
- `GET/POST /api/v1/training/resources`
- `GET/PUT /api/v1/training/resources/{id}`
- `PUT /api/v1/training/resources/{id}/publish`
- `PUT /api/v1/training/resources/{id}/offline`
- `POST /api/v1/training/resources/covers`

## 学习、题库与考试

- `POST /api/v1/training/learning/access`
- `GET /api/v1/training/learning/me`
- `GET /api/v1/training/learning/resources/{resourceId}`
- `GET /api/v1/training/learning/scores`
- `GET /api/v1/training/learning/scores/users`
- `GET /api/v1/training/learning/resources/{resourceId}/mistakes`
- `POST/GET /api/v1/training/exams`
- `GET/PUT /api/v1/training/exams/{id}`
- `PUT /api/v1/training/exams/{id}/publish`
- `PUT /api/v1/training/exams/{id}/questions`
- `POST /api/v1/training/exams/{examId}/records`
- `POST /api/v1/training/questions`
- `PUT /api/v1/training/questions/{id}`
- `PUT /api/v1/training/questions/{id}/options`

## 笔记、讨论与作业

- `GET/PUT /api/v1/training/notes/resource/{resourceId}`
- `GET /api/v1/training/notes`
- `POST /api/v1/training/notes/images`
- `GET/POST /api/v1/training/resources/{resourceId}/discussions`
- `GET/POST /api/v1/training/discussions/{discussionId}/replies`
- `PUT /api/v1/training/discussions/{discussionId}/like`
- `PUT /api/v1/training/discussion-replies/{replyId}/like`
- `DELETE /api/v1/training/discussions/{discussionId}`
- `DELETE /api/v1/training/discussion-replies/{replyId}`
- `GET /api/v1/training/resources/{resourceId}/assignments`
- `POST /api/v1/training/assignments/{assignmentId}/submission`

## AI培训辅助

- `POST /api/v1/training/ai/qa`
- `POST /api/v1/training/ai/summary`
- `POST /api/v1/training/ai/suggestions`
- `POST /api/v1/training/ai/practice`
- `POST /api/v1/training/ai/assignment-explanation`
- `POST /api/v1/training/ai/question-drafts`
- `GET /api/v1/training/ai/question-drafts`
- `GET /api/v1/training/ai/question-drafts/{id}`
- `PUT /api/v1/training/ai/question-drafts/{id}/review`

### AI接口实现状态

| AI能力 | 后端AI接口 | 前端已调用 | 当前实现 |
| --- | --- | --- | --- |
| 培训资料问答 | 是 | 是 | 默认Mock，可切DeepSeek |
| 培训资料重点总结 | 是 | 是 | 默认Mock，可切DeepSeek |
| 个性化学习建议 | 是 | 是 | 默认Mock，可切DeepSeek |
| 课程模拟陪练 | 是 | 是 | 默认Mock，可切DeepSeek |
| 作业答案讲解 | 是 | 是 | 默认Mock，可切DeepSeek |
| AI题目草稿生成 | 是 | 是 | 默认Mock，可切DeepSeek |

所有生成能力统一通过 `AiTrainingService` 适配层调用。未配置 `AI_MODE` 时启用Mock；设置
`AI_MODE=deepseek` 并提供 `DEEPSEEK_API_KEY`、`DEEPSEEK_BASE_URL` 和 `DEEPSEEK_MODEL`
后切换为DeepSeek。题目草稿列表、详情和审核属于数据库工作流，不直接调用模型，但前后端接口均已接通。

接口请求字段、权限和响应对象以对应 Controller、DTO 和 VO 为最终事实源。
