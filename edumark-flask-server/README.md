# EduMark Flask Server

这是基于现有 `edumark-server` 路由结构新建的一套 Python Flask 兼容后端，目标是尽量适配：

- `edumark-admin`
- `edumark-app`

当前版本特性：

- 统一返回格式兼容前端：`{ code, message, data, timestamp }`
- 默认挂载在 `/api`
- 内置后台登录、App 登录、学校/考试/成绩/AI/答题卡等主要路由
- 使用内存数据仓储，便于快速联调前端
- 已覆盖后台与 App 的主要接口路径，低频或复杂算法接口先提供兼容返回

## 目录结构

```text
edumark-flask-server/
  app/
    api/
      admin_routes.py
      app_routes.py
      misc_routes.py
    core/
      auth.py
      result.py
      store.py
  requirements.txt
  run.py
```

## 启动方式

```bash
cd edumark-flask-server
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python run.py
```

默认地址：

- `http://localhost:8080/api`

## 默认账号

后台：

- 用户名：`admin`
- 密码：`admin123`

App 家长端：

- 手机号：`13900000099`
- 密码：`admin123`

App 学生端：

- 手机号：`13800000111`
- 密码：`admin123`

## 当前实现说明

这套 Flask 版本优先解决“前端可接、接口兼容、可快速联调”的问题：

- CRUD 类接口大多已落为可用的内存实现
- App 端登录、绑定学生、成绩、答题卡、AI 学情分析可直接跑通
- 文件上传、角点检测、气泡识别、阅卷流程等复杂能力当前是兼容桩实现

如果要继续推进，建议下一步按这个顺序做：

1. 把内存仓储替换成 SQLAlchemy + MySQL
2. 把鉴权替换成 JWT
3. 把文件、图像识别、阅卷任务迁移为真实服务实现
4. 对照 `edumark-admin/src/api` 和 `edumark-app/src/api` 逐个补齐字段细节
