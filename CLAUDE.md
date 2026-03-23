# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

EduMark 是一个智能阅卷与家长查分平台，采用前后端分离架构，包含三个模块：

- `edumark-server/`: Spring Boot 3 后端服务（Java 17）
- `edumark-admin/`: Vue 3 + TypeScript 管理后台
- `edumark-app/`: UniApp 家长学生端（支持 H5、微信小程序、App）

## 常用命令

### 后端服务
```bash
cd edumark-server && mvn spring-boot:run          # 启动后端（端口 8080，上下文路径 /api）
cd edumark-server && mvn clean package -DskipTests # 构建 JAR
cd edumark-server && mvn test                      # 运行测试
```

### 管理后台
```bash
cd edumark-admin && npm install && npm run dev    # 启动开发服务器（端口 3000）
cd edumark-admin && npm run build                 # 类型检查并构建
cd edumark-admin && npm run lint                  # ESLint 检查并自动修复
```

### 移动端
```bash
cd edumark-app && npm install && npm run dev:h5   # H5 模式（端口 8081）
cd edumark-app && npm run dev:mp-weixin           # 微信小程序模式
cd edumark-app && npm run build:h5                # 构建 H5
```

## 技术栈

### 后端
- Spring Boot 3.2.3 + Spring Security + JWT
- MyBatis Plus 3.5.5
- MySQL 8 + Redis
- MinIO（文件存储）
- Knife4j（API 文档：http://localhost:8080/api/doc.html）
- 关键依赖：Hutool、EasyExcel、ZXing（条码识别）、iText 7（PDF）

### 管理后台
- Vue 3.4 + Vue Router 4 + Pinia
- Element Plus 2.6 + ECharts 5.5
- Vite 5.2 + TypeScript 5.4
- 路径别名：`@` -> `src/`

### 移动端
- UniApp 3.0 + Vue 3 + Pinia
- 支持 H5、微信小程序、App 多端部署

## 架构要点

### 后端领域模块（com.edumark）
```
├── answersheet/   # 答题卡管理
├── app/           # 家长学生端接口
├── exam/          # 考试管理
├── file/          # 文件处理和识别（答题卡扫描、条码识别）
├── marking/       # 阅卷管理（双评、复评、仲裁）
├── score/         # 成绩管理
├── school/        # 学校管理
├── security/      # 安全认证（JWT、Spring Security）
└── system/        # 系统管理（用户、角色、权限）
```

每个领域模块遵循分层结构：`controller/` -> `service/` -> `mapper/`，实体类在 `entity/`，DTO 在 `dto/`，VO 在 `vo/`。

### 前端目录结构
```
edumark-admin/src/
├── api/           # API 客户端（按模块组织）
├── views/         # 页面视图（考试、阅卷、成绩、系统管理等）
├── layouts/       # 布局组件
├── stores/        # Pinia 状态管理
├── router/        # 路由配置
└── assets/styles/ # 全局样式

edumark-app/src/
├── pages/         # 页面（首页、考试、成绩、个人中心）
├── stores/        # Pinia 状态
└── static/        # 静态资源
```

## 编码规范

- **Java**: 4 空格缩进，类名 PascalCase，方法/变量 camelCase，包名 `com.edumark.<domain>`
- **Vue/TypeScript**: 2 空格缩进，组件名 PascalCase（如 `RegionConfigDialog.vue`），变量/函数 camelCase
- **注释语言**: 中文

## 权限控制

后端使用 `@PreAuthorize` 注解：
```java
@PreAuthorize("hasAuthority('system:user:add')")
```

## 提交规范

提交信息使用简短中文描述，例如：`考试模块优化`、`阅卷任务修复`。避免跨模块混提交。