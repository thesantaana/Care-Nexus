# CareNexus Lite API设计规范

## 基础约定

- 前缀：`/api/v1`
- 请求/响应：`application/json; charset=UTF-8`，文件上传使用 `multipart/form-data`
- 鉴权：`Authorization: Bearer <token>`
- 分页：`pageNo` 从1开始，`pageSize` 为每页数量

统一响应：

```json
{"code":"SUCCESS","message":"操作成功","data":{}}
```

分页数据包含 `records`、`pageNo`、`pageSize`、`total`。客户端不得把非JSON错误直接当JSON解析。

## HTTP与错误

- 查询 `GET`，创建 `POST`，整体更新或状态动作 `PUT`，删除 `DELETE`。
- 参数错误400，未登录401，无权限403，不存在404，状态冲突409，系统异常500。
- 状态流转接口必须校验当前状态，重复审核、超出考试次数等返回409。

## 权限

- `training:resource:view`：查看已发布资源、学习和AI基础辅助。
- `training:resource:manage`：管理分类、标签、资源、题库、考试和AI草稿。
- 护工数据按当前用户隔离；管理员成绩查询按管理权限开放。

## 文件与AI

- 文件上传校验空文件、大小、扩展名、MIME对应关系和安全文件名。
- AI请求必须提供已入库资料ID；普通护工只能使用已发布资料。
- AI草稿审核通过只创建正式题库草稿，不自动发布或加入考试。

## 审计

登录、资源发布/下架、考试发布、AI草稿审核等关键操作记录操作日志，不记录密码、完整Token、模型密钥或敏感输入。
