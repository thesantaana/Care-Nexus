# CareNexus Lite 部署与演示

## 本地启动

```powershell
.\scripts\start-lite.ps1 -DatabaseUsername root -DatabasePassword "本机MySQL密码"
```

访问门户 `http://localhost:5175`，管理员端 `http://localhost:5173`，护工端 `http://localhost:5174`，后端健康检查 `http://localhost:8080/api/v1/health`。

演示账号：管理员 `admin_demo / 1`，护工 `caregiver_demo / 1`。

## 建议演示顺序

1. 门户介绍和角色入口。
2. 管理员编辑课程封面、内容与学习时长，展示分类标签。
3. 管理员查看题库、考试和AI草稿审核。
4. 护工学习课程章节，展示进度、笔记和讨论。
5. 完成作业与考试，展示自动评分、错题和平均成绩。
6. 展示AI资料问答、总结和学习建议，并说明安全边界。

## 停止

```powershell
.\scripts\stop-lite.ps1
```

当前是本地演示部署，不代表公网可访问。公网发布需要HTTPS、反向代理、生产密钥、数据库备份和安全验收。
