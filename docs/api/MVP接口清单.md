# MVP 接口清单

项目名称：CareNexus 颐联  
版本：轻量版 2.0  
更新时间：2026-07-15  
接口前缀：`/api/v1`

本清单依据 `lite_develop` 当前 Controller 路由整理。已删除护理订单、医生健康管理和对应接口。

## 1. 通用与认证

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/health` | 匿名 | 服务健康检查 |
| POST | `/auth/login` | 匿名 | 用户名密码登录 |
| GET | `/auth/me` | 已登录 | 当前用户、角色与权限 |
| POST | `/auth/logout` | 已登录 | 当前 Token 退出 |
| GET | `/auth/rbac-check` | `system:user:view` | RBAC 最小验证 |

## 2. 培训分类

基础路径：`/training`

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/training/categories` | view/manage | 分类列表，可按状态筛选 |
| POST | `/training/categories` | manage | 新增分类 |
| PUT | `/training/categories/{id}` | manage | 修改分类 |
| PUT | `/training/categories/{id}/status` | manage | 启用或停用分类 |

## 3. 培训标签

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/training/tags` | view/manage | 标签列表，可按状态筛选 |
| POST | `/training/tags` | manage | 新增标签 |
| PUT | `/training/tags/{id}` | manage | 修改标签 |
| PUT | `/training/tags/{id}/status` | manage | 启用或停用标签 |

## 4. 培训资源

基础路径：`/training/resources`

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/training/resources/covers` | manage | 上传课程封面，字段 `file` |
| POST | `/training/resources` | manage | 创建草稿资源 |
| PUT | `/training/resources/{id}` | manage | 修改草稿或下架资源 |
| GET | `/training/resources` | view/manage | 分页查询资源 |
| GET | `/training/resources/{id}` | view/manage | 资源详情 |
| PUT | `/training/resources/{id}/publish` | manage | 发布或重新发布 |
| PUT | `/training/resources/{id}/offline` | manage | 下架资源 |

### 资源查询参数

| 参数 | 类型 | 说明 |
|---|---|---|
| `keyword` | string | 标题等关键字 |
| `resourceType` | string | ARTICLE / VIDEO / PPT |
| `categoryId` | long | 分类 |
| `tagId` | long | 标签 |
| `status` | string | DRAFT / PUBLISHED / OFFLINE |
| `pageNo` | int | 默认 1 |
| `pageSize` | int | 默认 10，最大 100 |

普通查看权限只可见已发布资源，管理权限可查看全部状态。

## 5. 题库管理

基础路径：`/training`

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/training/questions` | manage | 创建单选题或判断题 |
| PUT | `/training/questions/{id}` | manage | 修改题干、答案和解析 |
| PUT | `/training/questions/{id}/options` | manage | 替换单选题选项 |

题型仅支持：

- `SINGLE_CHOICE`
- `TRUE_FALSE`

## 6. 考核管理

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/training/exams` | manage | 创建课程考核 |
| PUT | `/training/exams/{id}` | manage | 修改考核 |
| PUT | `/training/exams/{id}/questions` | manage | 替换考核题目和分值 |
| PUT | `/training/exams/{id}/publish` | manage | 发布考核 |
| GET | `/training/exams` | 已登录，按角色范围 | 获取考核列表 |

每门课程只允许一份考核。管理员查看管理数据，护工只应读取可参加的已发布考核。

## 7. 学习记录与成绩

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/training/learning/access` | view | 记录当前用户课程访问秒数 |
| GET | `/training/learning/me` | view | 当前用户整体学习记录 |
| GET | `/training/learning/resources/{resourceId}` | view | 当前用户某课程学习状态 |
| GET | `/training/learning/scores` | view | 当前用户逐课成绩汇总 |
| GET | `/training/learning/scores/users` | manage | 管理员查看护工成绩汇总 |
| GET | `/training/learning/resources/{resourceId}/mistakes` | view | 当前用户课程错题 |

## 8. 参加考核

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/training/exams/{examId}` | view | 获取已发布考核，不泄露答案 |
| POST | `/training/exams/{examId}/records` | view | 提交客观题并自动评分 |

考试次数、分数、通过状态和逐题答案由后端保存。`attemptNo` 不由客户端指定。

## 9. 富文本笔记

基础路径：`/training/notes`

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/training/notes` | view | 当前用户全部课程笔记 |
| GET | `/training/notes/resource/{resourceId}` | view | 当前用户指定课程笔记 |
| PUT | `/training/notes/resource/{resourceId}` | view | 保存当前用户课程笔记 |
| POST | `/training/notes/images` | view | 上传笔记图片，字段 `file` |

笔记按当前用户和课程隔离，不接受其他用户 ID。

## 10. 课程讨论

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/training/resources/{resourceId}/discussions` | view | 课程讨论列表，支持 `sort` |
| POST | `/training/resources/{resourceId}/discussions` | view | 创建讨论主题 |
| GET | `/training/discussions/{discussionId}/replies` | view | 主题回复列表 |
| POST | `/training/discussions/{discussionId}/replies` | view | 创建回复，可指定父回复 |
| PUT | `/training/discussions/{discussionId}/like` | view | 点赞/取消点赞主题 |
| PUT | `/training/discussion-replies/{replyId}/like` | view | 点赞/取消点赞回复 |
| DELETE | `/training/discussions/{discussionId}` | view + 本人 | 删除本人主题 |
| DELETE | `/training/discussion-replies/{replyId}` | view + 本人 | 删除本人回复 |

课程互动 Controller 可通过 `care-nexus.course-interactions.enabled` 配置启停，默认启用。

## 11. 课后作业

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | `/training/resources/{resourceId}/assignments` | view | 课程作业列表 |
| POST | `/training/assignments/{assignmentId}/submission` | view | 当前用户提交作业 |

## 12. AI 培训辅助

基础路径：`/training/ai`

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/training/ai/qa` | view/manage | 基于文本资料问答 |
| POST | `/training/ai/summary` | view/manage | 资料总结 |
| POST | `/training/ai/suggestions` | view/manage | 学习建议 |
| POST | `/training/ai/question-drafts` | manage | 生成单选/判断题草稿 |
| GET | `/training/ai/question-drafts` | manage | 草稿分页，可按状态筛选 |
| GET | `/training/ai/question-drafts/{id}` | manage | 草稿详情及来源 |
| PUT | `/training/ai/question-drafts/{id}/review` | manage | 审核通过或驳回 |

AI 请求必须引用当前用户有权访问的 `TEXT` 培训资源。审核通过仅创建 `DRAFT` 正式题目。

## 13. 主要请求示例

### 登录

```json
{
  "username": "admin_demo",
  "password": "Demo@123456"
}
```

### 记录学习访问

```json
{
  "resourceId": 1,
  "accessSeconds": 300
}
```

### 创建讨论

```json
{
  "title": "课程重点整理",
  "content": "我把本课程分成准备、执行和记录三个阶段。"
}
```

### 保存笔记

```json
{
  "noteTitle": "压疮预防学习笔记",
  "noteContent": "<h2>观察重点</h2><p>注意受压部位皮肤变化。</p>"
}
```

### AI 问答

```json
{
  "resourceIds": [1],
  "prompt": "发现持续发红时应该怎么处理？"
}
```

### AI 草稿审核

```json
{
  "reviewStatus": "APPROVED",
  "reviewComment": "内容与课程一致"
}
```

实际字段名以 DTO 为准，前端接入时应通过接口测试再次核对。

## 14. 已删除接口域

轻量版不提供：

- `/care/**`
- `/doctor/**`
- 老人、家属、地址、订单、评价、投诉接口
- 健康记录、预警、随访、干预和评估接口

如历史文档或旧客户端仍调用这些路径，应删除调用，不得在轻量版后端添加空返回或假数据兼容。

## 15. 联调检查

- 所有管理写接口用管理员账号验证。
- 所有学习接口用护工账号验证。
- 未登录访问返回 401。
- 护工调用管理接口返回 403。
- 无权访问草稿或下架资源返回 404。
- 重复发布、下架或审核返回 409。
- 三个前端均使用真实 API，不使用业务 Mock 数据。