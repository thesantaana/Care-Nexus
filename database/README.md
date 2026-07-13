# Database

本目录存放 CareNexus 颐联数据库初始化脚本和数据字典。

当前 T-011 仅提供 MVP 工程骨架所需的结构设计和演示数据规划，不包含真实隐私数据、真实密码、Token 或密钥。

```text
database/
├── init/
│   ├── 001_schema.sql
│   └── 002_seed_data.sql
├── dict/
│   └── data-dictionary.md
└── model/
    ├── README.md
    ├── CDM说明.md
    └── PDM说明.md
```

目标数据库：MySQL 8.0。

当前轻量版物理表数量：22 张（原21张核心表及新增的 `training_note`）。

PowerDesigner `.pdm` 文件和 CDM/PDM 截图需在安装 PowerDesigner 的环境中生成；当前仓库不得包含伪造模型文件。
