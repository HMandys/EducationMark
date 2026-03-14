# EduMark 智能阅卷与家长查分平台 - 开发计划

## 一、项目概述

### 项目名称
EduMark 智能阅卷与家长查分平台

### 项目定位
用于学校组织考试后进行线上阅卷、成绩汇总、成绩分析，并向家长和学生提供查分服务的完整业务系统。

### 技术栈

| 层级 | 技术选型 |
|------|----------|
| 后端 | Java 17 + Spring Boot 3 + Spring Security + JWT + MyBatis Plus + MySQL 8 + Redis + MinIO |
| 管理端 | Vue 3 + TypeScript + Vite + Element Plus + Pinia + Vue Router + Axios + ECharts |
| 家长/学生端 | UniApp (兼容 H5 / 小程序) |
| AI 模块 | 独立 AI Service，支持 OpenAI / Claude / 通义 / DeepSeek 等模型 |
| 文档 | Knife4j / OpenAPI |

---

## 二、用户角色

| 角色 | 说明 |
|------|------|
| 超级管理员 | 系统最高权限，管理所有学校 |
| 学校管理员 | 管理本校所有数据 |
| 教务主任 | 组织考试、发布成绩、查看统计 |
| 阅卷组长 | 分配阅卷任务、监控阅卷进度 |
| 任课教师 | 执行阅卷任务 |
| 班主任 | 查看本班成绩、学情分析 |
| 家长 | 绑定学生、查询成绩、查看AI分析 |
| 学生 | 查询个人成绩、答题卡、学习建议 |

---

## 三、核心业务模块

### 3.1 基础管理
- 用户与权限管理
- 学校组织架构管理（学校、年级、班级）
- 教师管理
- 学生管理
- 家长绑定管理

### 3.2 考试管理
- 考试创建与配置
- 科目管理
- 试卷结构配置
- 题目管理（题型、满分、知识点）
- 考试状态流转

### 3.3 答题卡与判分
- 答题卡图片上传（MinIO 存储）
- 客观题自动判分
- 学生答案管理

### 3.4 阅卷管理
- 主观题任务生成与分配
- 教师阅卷工作台
- 双评机制
- 仲裁机制
- 阅卷进度监控

### 3.5 成绩管理
- 成绩汇总
- 班级/年级排名计算
- 统计分析（均分、优秀率、及格率、分数段）
- 成绩发布与撤回
- 导出 Excel

### 3.6 AI 学情分析
- 单科成绩分析
- 多次考试趋势分析
- 知识点薄弱项识别
- 错题原因归纳
- 家长沟通建议
- 个性化练习建议
- 班级学情报告

### 3.7 查分端
- 家长端登录与绑定
- 学生端登录
- 考试成绩查询
- 答题卡图片查看
- AI 分析报告查看

### 3.8 系统管理
- 操作日志
- 登录日志
- 字典管理
- 系统配置

---

## 四、数据库核心表设计

### 4.1 基础组织表
- `sys_user` - 系统用户
- `sys_role` - 角色
- `sys_user_role` - 用户角色关联
- `sys_permission` - 权限
- `sys_role_permission` - 角色权限关联
- `school` - 学校
- `grade` - 年级
- `class_info` - 班级
- `teacher` - 教师
- `student` - 学生
- `parent` - 家长
- `parent_student_bind` - 家长学生绑定

### 4.2 考试业务表
- `exam` - 考试
- `exam_subject` - 考试科目
- `paper` - 试卷
- `paper_question` - 试卷题目
- `knowledge_point` - 知识点
- `question_knowledge_point` - 题目知识点关联

### 4.3 答题与阅卷表
- `answer_sheet` - 答题卡
- `answer_sheet_image` - 答题卡图片
- `student_answer` - 学生答案
- `objective_answer_result` - 客观题判分结果
- `marking_task` - 阅卷任务
- `marking_task_detail` - 阅卷任务明细
- `marking_record` - 阅卷记录
- `marking_arbitration` - 仲裁记录

### 4.4 成绩表
- `exam_score` - 考试总成绩
- `subject_score` - 科目成绩
- `score_publish_record` - 成绩发布记录
- `ranking_snapshot` - 排名快照

### 4.5 AI 分析表
- `ai_analysis_task` - AI分析任务
- `ai_student_report` - 学生AI报告
- `ai_parent_advice` - 家长建议
- `ai_class_report` - 班级报告
- `ai_knowledge_weakness` - 知识点薄弱项

### 4.6 系统表
- `sys_operate_log` - 操作日志
- `sys_login_log` - 登录日志
- `sys_file` - 文件记录
- `sys_dict` - 字典

---

## 五、开发阶段计划

### 第一阶段：后端基础骨架 ✅
**目标：** 搭建后端项目结构，完成登录认证和权限基础

**输出：**
- [x] Maven 项目结构（模块化单体）
- [x] pom.xml 完整配置
- [x] application.yml 配置
- [x] 统一返回结果类 Result
- [x] 全局异常处理器
- [x] JWT 工具类与认证过滤器
- [x] Spring Security 配置
- [x] MyBatis Plus 配置
- [x] Redis 配置
- [x] MinIO 配置
- [x] Knife4j 配置
- [x] 用户/角色/权限实体与基础 CRUD
- [x] 登录接口

### 第二阶段：管理端基础骨架 ✅
**目标：** 搭建 Vue3 管理端项目结构

**输出：**
- [x] Vue3 + Vite 项目结构
- [x] package.json 依赖配置
- [x] vite.config.ts
- [x] 路由配置
- [x] Axios 请求封装
- [x] Pinia 用户状态管理
- [x] 全局 Layout 组件
- [x] 登录页
- [x] 首页仪表盘

### 第三阶段：基础组织管理模块 ✅
**目标：** 完成学校组织架构管理

**输出：**
- [x] 学校管理（后端 + 前端）
- [x] 年级管理（后端 + 前端）
- [x] 班级管理（后端 + 前端）
- [x] 教师管理（后端 + 前端）
- [x] 学生管理（后端 + 前端）
- [x] 家长管理与绑定

### 第四阶段：考试管理模块 ✅
**目标：** 完成考试配置功能

**输出：**
- [x] 考试 CRUD（后端 + 前端）
- [x] 科目配置
- [x] 题目结构配置
- [x] 知识点管理
- [x] 考试状态流转

### 第五阶段：答题卡与上传模块 ✅
**目标：** 完成文件上传和答题卡管理

**输出：**
- [x] MinIO 文件服务封装
- [x] 答题卡上传接口
- [x] 答题卡图片管理
- [x] 图片预览组件

### 第五.五阶段：答题卡模板制作 ✅
**目标：** 支持可视化配置答题卡模板，关联试卷题目，生成可打印 PDF

**输出：**
- [x] 数据库设计（answer_sheet_template、answer_sheet_region 表）
- [x] 后端 answersheet 模块（Entity/DTO/VO/Mapper/Service/Controller）
- [x] iText 7 PDF 生成服务
- [x] 自动根据试卷题目生成模板
- [x] 前端模板列表页
- [x] 前端可视化编辑器（页面设置、学生信息区、区域配置、预览面板）
- [x] 区域类型支持：选择题、填空题、解答题、作文题
- [x] 拖拽排序功能
- [x] PDF 发布、预览、下载

### 第六阶段：阅卷模块 ✅
**目标：** 核心阅卷业务实现

**输出：**
- [x] 客观题自动判分服务（后端）
- [x] 主观题阅卷任务生成（后端）
- [x] 阅卷任务分配（后端）
- [x] 教师阅卷工作台（前端）
- [x] 评分提交（后端）
- [x] 双评逻辑（后端）
- [x] 仲裁机制（后端）

### 第七阶段：成绩汇总与分析模块 ✅
**目标：** 成绩统计与发布

**输出：**
- [x] 成绩汇总服务
- [x] 排名计算
- [x] 统计分析接口（均分、优秀率、分数段等）
- [x] ECharts 图表展示
- [x] 成绩发布功能
- [x] Excel 导出

### 第八阶段：家长端与学生端 ✅
**目标：** UniApp 移动端开发

**输出：**
- [x] UniApp 项目结构
- [x] 登录页
- [x] 家长绑定页
- [x] 首页
- [x] 考试列表页
- [x] 成绩详情页
- [x] 答题卡查看页
- [x] 个人中心
- [x] 后端 App 模块接口（Mock 实现）
- [ ] AI 报告页（待第九阶段实现）

### 第九阶段：AI 分析模块
**目标：** AI 学情分析功能

**输出：**
- [ ] AI Provider 接口设计
- [ ] Mock Provider 实现
- [ ] Prompt Builder
- [ ] AI 分析任务调度
- [ ] 学生报告生成
- [ ] 家长建议生成
- [ ] 班级报告生成
- [ ] 前端报告展示页

### 第十阶段：测试与部署
**目标：** 完成测试和部署配置

**输出：**
- [ ] 测试用例文档
- [ ] 单元测试
- [ ] 接口测试
- [ ] Dockerfile
- [ ] docker-compose.yml
- [ ] Nginx 配置
- [ ] 部署文档

---

## 六、项目目录结构预览

### 后端结构
```
edumark-server/
├── pom.xml
├── src/main/java/com/edumark/
│   ├── EduMarkApplication.java
│   ├── common/                    # 通用模块
│   │   ├── config/               # 配置类
│   │   ├── constant/             # 常量
│   │   ├── enums/                # 枚举
│   │   ├── exception/            # 异常
│   │   ├── result/               # 统一返回
│   │   └── utils/                # 工具类
│   ├── security/                  # 安全模块
│   │   ├── config/
│   │   ├── filter/
│   │   └── handler/
│   ├── system/                    # 系统管理
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── vo/
│   ├── school/                    # 学校组织
│   ├── exam/                      # 考试管理
│   ├── marking/                   # 阅卷管理
│   ├── score/                     # 成绩管理
│   ├── file/                      # 文件服务
│   └── ai/                        # AI分析
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    └── mapper/
```

### 管理端前端结构
```
edumark-admin/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── src/
│   ├── main.ts
│   ├── App.vue
│   ├── api/                       # 接口定义
│   ├── assets/                    # 静态资源
│   ├── components/                # 公共组件
│   ├── layout/                    # 布局组件
│   ├── router/                    # 路由配置
│   ├── stores/                    # Pinia 状态
│   ├── styles/                    # 样式文件
│   ├── utils/                     # 工具函数
│   └── views/                     # 页面视图
│       ├── login/
│       ├── dashboard/
│       ├── system/
│       ├── school/
│       ├── exam/
│       ├── marking/
│       ├── score/
│       └── ai/
└── public/
```

### 家长/学生端结构
```
edumark-app/
├── package.json
├── manifest.json
├── pages.json
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── api/
│   ├── components/
│   ├── pages/
│   │   ├── login/
│   │   ├── bind/
│   │   ├── home/
│   │   ├── exam/
│   │   ├── score/
│   │   ├── answer-sheet/
│   │   ├── ai-report/
│   │   └── mine/
│   ├── stores/
│   ├── styles/
│   └── utils/
└── static/
```

---

## 七、关键业务规则

### 7.1 阅卷规则
- 客观题由系统自动判分，支持单选、多选、判断、填空
- 主观题按题号分配给教师进行流水阅卷
- 支持双评机制，两位教师独立评分
- 双评分差超过阈值（默认 5 分）进入仲裁
- 仲裁由阅卷组长或第三位教师完成

### 7.2 成绩发布规则
- 全部阅卷完成后方可发布成绩
- 成绩未发布时家长/学生端不可见
- 支持按考试整体发布
- 支持撤回发布

### 7.3 家长绑定规则
- 家长通过学生姓名 + 学号 + 绑定码进行绑定
- 一个家长可绑定多个学生
- 家长只能查看已绑定学生的成绩

### 7.4 AI 分析规则
- 成绩发布后自动触发 AI 分析任务
- AI 输出需遵循教育场景规范，避免绝对化评价
- 分析结果仅供参考，需标注免责声明

---

## 八、安全设计要点

- JWT Token 认证，支持过期刷新
- 密码 BCrypt 加密存储
- RBAC 权限模型，接口级权限控制
- 数据隔离：school_id 字段隔离不同学校数据
- 防越权：家长只能查看自己孩子的数据
- 文件访问控制：MinIO 私有 bucket + 签名 URL
- 操作日志审计
- 敏感数据脱敏（学生姓名、成绩等）

---

## 九、MVP 版本规划

### 第一阶段 MVP（核心功能）
- 用户登录与权限
- 学校组织架构管理
- 考试创建与配置
- 答题卡上传
- 主观题阅卷（单评）
- 成绩汇总与发布
- 家长查分

### 第二阶段增强
- 客观题自动判分
- 双评与仲裁
- 成绩统计分析
- Excel 导出
- 学生端

### 第三阶段 AI 深化
- AI 学情分析
- 知识点薄弱项识别
- 个性化学习建议
- 班级学情报告
- 多次考试趋势分析

---

## 十、下一步行动

准备开始 **第一阶段：后端基础骨架** 开发。

请确认是否开始？