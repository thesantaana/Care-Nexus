# Module Ownership

| 范围 | 路径 | 负责人 | 规则 |
| --- | --- | --- | --- |
| 后端基础与认证 | `backend/.../auth`、`common` | 隋咏轩 | 安全配置变更需回归认证测试 |
| 培训业务 | `backend/.../training` | 隋咏轩 | 保持资源、学习、考试边界清晰 |
| AI培训辅助 | `backend/.../ai` | 隋咏轩、孙洋 | 仅授权AI任务可修改 |
| 管理员端 | `frontend/admin-web` | 孙洋 | 管理员权限入口 |
| 护工端 | `frontend/mobile-web` | 孙洋 | 护工学习入口 |
| 门户端 | `frontend/portal-web` | 隋咏轩、孙洋 | 品牌与统一入口 |
| 数据库 | `database` | 隋咏轩 | 所有结构变化提交SQL |
| 测试与交付 | `docs/test`、`docs/delivery` | 张远航、李亦航 | 记录真实结果 |

## AI保护范围

- `backend/src/main/java/com/carenexus/ai/`
- `frontend/portal-web` 和护工端中的 AI 助手交互代码
- `database` 中 `ai_draft`、`ai_draft_source_resource` 相关结构

AI负责人在需求确认后可修改；其他任务不得顺手改变AI协议、审核边界或真实模型配置。
