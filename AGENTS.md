# Repository Guidelines

## Project Overview

EduMark（智能阅卷与家长查分平台）是一个面向 K12 教育场景的考试管理与成绩查询系统。系统支持学校管理员创建考试、设计答题卡、扫描上传答卷、分配阅卷任务、人工/AI 批改、成绩统计与发布，同时支持家长/学生通过移动端查询成绩和答题卡。

本仓库包含三个活跃模块与一个已弃用的实验模块：

- **`edumark-server/`**：Spring Boot 3 后端服务，提供 RESTful API，统一响应格式为 `Result<T>`（code / message / data / timestamp）。
- **`edumark-admin/`**：Vue 3 + TypeScript 管理后台，面向学校管理员和教师，使用 Element Plus 组件库。
- **`edumark-app/`**：UniApp 跨端应用（Vue 3），面向家长和学生，支持 H5、微信小程序、App 等多端发行。
- **`edumark-flask-server-弃用/`**：早期 Flask 实验后端，已不再维护，请勿修改或依赖。

外部依赖基础设施：

- **MySQL 8**：业务数据持久化
- **Redis**：缓存、会话、分布式锁
- **MinIO**：对象存储，用于保存答题卡图片、PDF、文件等

---

## Project Structure & Module Organization

### 后端（`edumark-server/`）

- **入口类**：`src/main/java/com/edumark/EduMarkApplication.java`
- **Java 源码**：`src/main/java/com/edumark`
  - 按业务域分包，每个域下再分 `controller`、`service`、`mapper`、`entity`、`dto`、`vo`
  - 主要业务域：
    - `app` —— 家长/学生端 API（查分、登录、绑定学生、AI 报告）
    - `exam` —— 考试、科目、试卷、知识点管理
    - `answersheet` —— 答题卡模板设计
    - `file` —— 文件上传、答题卡扫描、客观题填涂识别、图像裁切
    - `marking` —— 阅卷任务分配、阅卷记录、仲裁
    - `score` —— 成绩统计、排名、发布
    - `school` —— 学校、年级、班级、教师、学生、家长管理
    - `system` —— 系统用户、角色、权限、字典
    - `ai` —— AI 自动批改配置与审计记录
    - `security` —— Spring Security 配置、JWT 过滤器、认证逻辑
    - `common` —— 统一结果封装、全局异常、枚举、工具类
- **MyBatis XML**：`src/main/resources/mapper/**/*.xml`
- **SQL 脚本**：`src/main/resources/sql/` 和 `src/main/resources/db/`
- **配置文件**：`src/main/resources/application.yml`

### 管理后台（`edumark-admin/`）

- **入口**：`src/main.ts`
- **页面视图**：`src/views/`，按业务模块划分子目录
- **API 封装**：`src/api/`，每个模块一个 ts 文件
- **路由**：`src/router/index.ts`，使用 `createWebHistory`，基于权限 meta 做守卫
- **状态管理**：`src/store/`，使用 Pinia（`user.ts`、`app.ts`）
- **布局**：`src/layouts/`
- **全局样式**：`src/assets/styles/index.scss`
- **Vite 代理**：开发时 `/api` 代理到 `http://127.0.0.1:8080`（可通过环境变量 `EDUMARK_API_TARGET` 覆盖）

### 移动端（`edumark-app/`）

- **入口**：`src/main.ts`
- **页面**：`src/pages/`
- **路由配置**：`src/pages.json`（UniApp 原生页面路由 + tabBar）
- **API 封装**：`src/api/`
- **状态管理**：`src/stores/user.ts`（Pinia）
- **静态资源**：`static/`

---

## Technology Stack

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.3 (Java 17) |
| ORM / SQL | MyBatis Plus 3.5.5 |
| 安全 | Spring Security + JWT (jjwt 0.12.5) |
| 缓存 | Spring Data Redis (Lettuce + commons-pool2) |
| 数据库 | MySQL 8 |
| 文件存储 | MinIO 8.5.7 |
| API 文档 | Knife4j 4.4.0 (OpenAPI 3) |
| 工具库 | Hutool 5.8.25、Lombok |
| Excel / PDF | EasyExcel 3.3.3、iText 8.0.3 |
| 条码识别 | ZXing 3.5.3 |
| 前端框架 | Vue 3.4 + TypeScript 5.4 |
| 构建工具 | Vite 5 |
| UI 库（管理端） | Element Plus 2.6 |
| 图表 | ECharts 5 + vue-echarts |
| 移动端框架 | UniApp 3 (Vue 3) |
| 状态管理 | Pinia 2.1.7 |

---

## Build, Test, and Development Commands

### 后端

```bash
cd edumark-server

# 本地运行（使用 application.yml 中的 dev 配置）
mvn spring-boot:run

# 打包（跳过测试）
mvn clean package -DskipTests

# 运行测试
mvn test

# 生产启动示例
java -jar target/edumark-server-1.0.0.jar \
  --spring.profiles.active=prod \
  --spring.config.additional-location=/www/server/edumark/config/
```

### 管理后台

```bash
cd edumark-admin
npm install

# 开发服务（默认端口 3000）
npm run dev

# 类型检查并构建生产包
npm run build

# ESLint 自动修复
npm run lint
```

开发环境可通过环境变量指定后端地址：

```bash
EDUMARK_API_TARGET=http://192.168.1.100:8080 npm run dev
```

### 移动端

```bash
cd edumark-app
npm install

# H5 开发
npm run dev:h5

# H5 构建
npm run build:h5

# 微信小程序开发
npm run dev:mp-weixin

# App 构建
npm run build:app
```

---

## Coding Style & Naming Conventions

- **注释语言**：中文，与现有代码保持一致。
- **Java**：
  - 缩进 4 个空格。
  - 类名 `PascalCase`，如 `ExamServiceImpl`、`AnswerSheetController`。
  - 方法/变量名 `camelCase`。
  - 包名 `com.edumark.<domain>`。
  - 每个业务域按 `controller / service / mapper / entity / dto / vo` 分包。
- **Vue / TypeScript**：
  - 缩进 2 个空格。
  - 组件文件名 `PascalCase`，如 `RegionConfigDialog.vue`。
  - 变量/函数 `camelCase`。
  - 组合式 API 风格优先。
- **SQL / 数据库**：
  - 表名使用下划线命名，如 `answer_sheet`、`marking_record`。
  - 逻辑删除字段统一为 `deleted`（0 未删除，1 已删除）。
  - 时间戳字段统一为 `create_time`、`update_time`。

---

## Testing Guidelines

### 后端测试

- 测试代码位于 `edumark-server/src/test/java`，目录结构与生产代码镜像对应。
- 使用 **JUnit 5** + **Mockito** 编写单元测试。
- 对 Service 层优先使用 Mockito 做依赖隔离测试，避免启动完整 Spring 上下文。
- 权限敏感端点建议补充 Spring Security 相关测试（`spring-security-test` 已引入）。
- 测试运行时会设置 `java.awt.headless=true`（`maven-surefire-plugin` 配置），以支持图像处理相关测试在无头环境运行。

### 前端测试

- 管理后台与移动端当前未配置自动化测试套件。
- 变更页面后，至少执行 `npm run build`（管理后台）或 `npm run build:h5`（移动端），确保无类型错误和编译错误。
- 管理后台同步执行 `npm run lint` 检查代码规范。

---

## Database & Migration

首次初始化或重建数据库时，需要按顺序执行以下 SQL（**生产环境请勿直接执行 `schema.sql`，因为它包含删表重建逻辑**）：

1. `edumark-server/src/main/resources/sql/schema.sql` —— 系统基础表（用户、角色、权限、字典、日志、文件、AI 配置等）
2. `edumark-server/src/main/resources/sql/patch-business-tables.sql` —— 考试、答题卡、阅卷、成绩等业务表
3. `edumark-server/src/main/resources/sql/init-data.sql` —— 初始基础数据（默认管理员账号 `admin` / `admin123`、初始字典、权限菜单等）
4. 其余 `patch-*.sql` 按文件名时间顺序执行 —— 补字段、补索引、补权限
5. `edumark-server/src/main/resources/db/migration/*.sql` —— Flyway 风格迁移脚本

详细执行顺序可参考部署文档 `docs/宝塔面板部署后台保姆级教程.md` 第 8.3 节。

---

## Deployment Architecture

推荐部署结构（已验证通过宝塔面板）：

- `https://admin.example.com` —— 管理后台前端静态文件（`dist`）
- `https://admin.example.com/api` —— Nginx 反向代理到后端 Java 服务（`127.0.0.1:8080`）
- `https://file.example.com` —— MinIO 文件访问（代理到 `127.0.0.1:9000`）
- `https://minio.example.com` —— MinIO 管理控制台（代理到 `127.0.0.1:9001`）

关键配置约束：

- 后端 `server.servlet.context-path` 固定为 `/api`。
- 前端生产环境请求基路径固定为 `/api`，不单独配置跨域域名。
- 管理后台使用 `history` 路由模式，Nginx 必须配置 `try_files $uri $uri/ /index.html;`。
- MinIO 的 `endpoint` 必须配置为前端可访问的外网域名（不能是 `127.0.0.1:9000`），且建议使用 HTTPS。
- MinIO 的 `edumark` 桶需设置为公开可读，因为当前实现直接返回文件 URL 供前端访问。
- 生产环境应在服务器外部新建 `application-prod.yml` 覆盖数据库、Redis、JWT、MinIO 等敏感配置，并通过 `--spring.config.additional-location` 指定加载路径。

更详细的部署清单和 Nginx 配置模板见 `docs/宝塔面板部署后台保姆级教程.md`。

---

## Security Considerations

- 认证方式：JWT（`Authorization: Bearer <token>`），24 小时过期。
- 管理端与 App 端共用同一套用户表（`sys_user`），通过 `user_type` 区分（1-管理端用户，2-家长，3-学生）。
- 权限模型：RBAC（用户-角色-权限），后端使用 Spring Security 拦截，前端路由使用 `meta.permission` 做菜单级控制。
- 生产环境务必：
  - 修改默认管理员密码（`admin` / `admin123`）。
  - 修改 JWT `secret` 为强随机字符串。
  - 关闭或限制公网访问 Knife4j 接口文档（生产配置 `knife4j.enable: false`）。
  - 不将 `3306`、`6379`、`8080`、`9000`、`9001` 暴露到公网。

---

## Commit & Pull Request Guidelines

- 提交信息使用中文短摘要，例如 `优化`、`答题卡优化`、`仲裁`、`考试模块优化`、`阅卷任务修复`。
- 尽量按模块拆分提交，不要将后端 SQL、管理端 UI、App 逻辑混在一个提交里，除非该功能确实需要多端联动。
- Pull Request 应包含：
  - 变更模块说明
  - 业务影响描述
  - 数据库 / 配置变更说明
  - 验证命令或步骤
  - 管理端 / App 的 UI 变更需附截图
  - 如需执行 SQL，明确列出脚本路径和顺序
