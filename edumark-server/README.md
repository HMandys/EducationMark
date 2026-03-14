# EduMark 智能阅卷与家长查分平台 - 后端服务

## 技术栈

- Java 17
- Spring Boot 3.2.3
- Spring Security + JWT
- MyBatis Plus 3.5.5
- MySQL 8
- Redis
- MinIO
- Knife4j (OpenAPI 3)

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- MinIO (可选，用于文件存储)

## 快速开始

### 1. 创建数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行建表脚本
source /path/to/edumark-server/src/main/resources/sql/schema.sql

# 执行初始化数据脚本
source /path/to/edumark-server/src/main/resources/sql/init-data.sql
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/edumark?...
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password

minio:
  endpoint: http://localhost:9000
  access-key: your_access_key
  secret-key: your_secret_key
```

### 3. 启动服务

```bash
# 进入项目目录
cd edumark-server

# 编译项目
mvn clean package -DskipTests

# 运行
java -jar target/edumark-server-1.0.0.jar
```

或使用 Maven 直接运行：

```bash
mvn spring-boot:run
```

### 4. 访问接口文档

启动成功后，访问：

- Knife4j 文档: http://localhost:8080/api/doc.html
- OpenAPI JSON: http://localhost:8080/api/v3/api-docs

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |

## 项目结构

```
edumark-server/
├── src/main/java/com/edumark/
│   ├── EduMarkApplication.java     # 启动类
│   ├── common/                      # 通用模块
│   │   ├── config/                 # 配置类
│   │   ├── constant/               # 常量
│   │   ├── dto/                    # 通用DTO
│   │   ├── entity/                 # 基础实体
│   │   ├── enums/                  # 枚举
│   │   ├── exception/              # 异常处理
│   │   ├── result/                 # 统一返回
│   │   └── utils/                  # 工具类
│   ├── security/                    # 安全模块
│   │   ├── config/                 # 安全配置
│   │   ├── filter/                 # 过滤器
│   │   ├── handler/                # 处理器
│   │   ├── service/                # 认证服务
│   │   └── utils/                  # 安全工具
│   └── system/                      # 系统模块
│       ├── controller/             # 控制器
│       ├── service/                # 服务层
│       ├── mapper/                 # 数据访问层
│       ├── entity/                 # 实体类
│       ├── dto/                    # 请求DTO
│       └── vo/                     # 响应VO
├── src/main/resources/
│   ├── application.yml             # 配置文件
│   ├── mapper/                     # MyBatis XML
│   └── sql/                        # SQL脚本
└── pom.xml                         # Maven配置
```

## API 接口

### 认证接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/auth/login | POST | 用户登录 |
| /api/auth/info | GET | 获取当前用户信息 |
| /api/auth/logout | POST | 退出登录 |

### 用户管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/system/user/page | GET | 分页查询用户 |
| /api/system/user/{id} | GET | 获取用户详情 |
| /api/system/user | POST | 创建用户 |
| /api/system/user | PUT | 更新用户 |
| /api/system/user/{id} | DELETE | 删除用户 |

### 角色管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/system/role/list | GET | 获取角色列表 |

## 开发说明

### 添加新模块

1. 在 `com.edumark` 下创建模块目录
2. 创建 entity、dto、vo、mapper、service、controller 子目录
3. 在 `resources/mapper` 下创建对应的 XML 目录

### 权限控制

使用 `@PreAuthorize` 注解进行方法级权限控制：

```java
@PreAuthorize("hasAuthority('system:user:add')")
public Result<Long> createUser(@RequestBody SysUserDTO dto) {
    // ...
}
```

## 下一步开发

第一阶段已完成：
- [x] 项目骨架搭建
- [x] 基础配置（MyBatis Plus、Redis、MinIO、Knife4j）
- [x] 统一响应结果
- [x] 全局异常处理
- [x] JWT 认证
- [x] Spring Security 配置
- [x] 用户/角色/权限基础CRUD
- [x] 登录接口

下一阶段（第二阶段）：管理端前端骨架
