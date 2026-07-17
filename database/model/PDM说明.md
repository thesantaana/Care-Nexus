# CareNexus Lite PDM说明

PDM以 `database/init/001_schema.sql` 及 `003` 至 `007` 增量结构脚本为输入，数据库为 MySQL 8、字符集为 `utf8mb4`。

生成模型时核对主键、角色权限关系、资源分类标签、用户资源学习唯一性、考试题目关系、考试记录与答案、讨论回复点赞、AI草稿来源关系和文件资源外键。

正式 `.pdm` 与图片按 T-030 由PowerDesigner反向工程生成，本文不替代模型文件。
