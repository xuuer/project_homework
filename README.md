# 智慧教学管理平台

一个基于 Spring Boot 3 + Vue 3 的在线教育管理系统，支持班课管理、作业测验、在线考试、AI 智能对话等功能。

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17+ | 编程语言 |
| Spring Boot | 3.2.2 | 主框架 |
| MyBatis Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| JWT | 0.12.6 | 身份认证 |
| MinIO | 8.5.6 | 对象存储 |
| EasyExcel | 4.0.3 | Excel 导入导出 |
| SpringDoc | 2.5.0 | API 文档 (Swagger) |
| Spring Cloud Alibaba | 2023.0.3.2 | Nacos 配置中心 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vite | 3.x | 构建工具 |
| Element Plus | 2.5.0 | UI 组件库 |
| Pinia | 2.0.27 | 状态管理 |
| Axios | 1.2.2 | HTTP 请求 |
| ECharts | 5.4.2 | 数据可视化 |
| TinyMCE / WangEditor | - | 富文本编辑器 |

## 功能模块

- **班课管理** — 创建/编辑班课、学生加入/退出、资源分组
- **作业管理** — 布置作业、学生提交、作业批改与统计
- **测验考试** — 在线测验、题库组卷、自动阅卷、成绩排名
- **题库管理** — 题目录入、分类管理、知识点关联
- **资源管理** — 学习资料上传下载、MinIO 对象存储、在线文件预览
- **知识点体系** — 知识点标签树、学生掌握情况追踪、针对性练习推荐
- **AI 智能对话** — 集成 Coze AI，提供智能答疑与辅导
- **数据统计** — 作业统计、测验分析、学习情况可视化

## 项目结构

```
project_two/
├── project/                      # 后端项目
│   ├── src/main/java/edu/fdzc/project/
│   │   ├── controller/           # 控制器层
│   │   ├── service/              # 业务逻辑层
│   │   ├── mapper/               # 数据访问层
│   │   ├── entity/               # 实体类
│   │   ├── config/               # 配置类
│   │   ├── utils/                # 工具类
│   │   └── common/               # 通用组件
│   ├── src/main/resources/
│   │   ├── application.yml       # 应用配置
│   │   └── mapper/               # XML 映射文件
│   ├── pom.xml                   # Maven 配置
│   └── docker-compose.yml        # Docker 部署配置
├── vue/net-student/              # 前端项目
│   ├── src/                      # 源码目录
│   ├── dist/                     # 构建产物
│   ├── package.json              # 依赖配置
│   └── vite.config.js            # Vite 配置
└── project_db.sql                # 数据库初始化脚本
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 16+
- Docker (可选，用于部署 MinIO 和 KKFileView)

### 1. 数据库初始化

```bash
# 登录 MySQL 并执行初始化脚本
mysql -u root -p < project_db.sql
```

### 2. 后端启动

```bash
cd project

# 方式一：直接运行
./mvnw spring-boot:run

# 方式二：打包后运行
./mvnw clean package
java -jar target/project-0.0.1-SNAPSHOT.jar
```

### 3. 前端启动

```bash
cd vue/net-student

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 生产构建
npm run build
```

### 4. 启动依赖服务 (可选)

```bash
cd project

# 启动 MinIO 和 KKFileView
docker-compose up -d
```

## 配置文件说明

修改 `project/src/main/resources/application.yml` 中的以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/net        # 数据库地址
    username: root                               # 数据库用户名
    password: 1234567                            # 数据库密码

minio:
  endpoint: http://localhost:9000                # MinIO 服务地址
  accessKey: minio                               # MinIO 访问密钥
  secretKey: minio123                            # MinIO 秘密密钥
  bucketName: kkfileview                         # 存储桶名称

kkFileViewServer: http://localhost:8012/onlinePreview?url=   # 文件预览服务

coze:
  token: your-coze-token                         # Coze API Token
  workflowId: your-workflow-id                   # Coze 工作流 ID
```

## API 文档

启动后端服务后，访问 Swagger UI：

```
http://localhost:8080/swagger-ui.html
```

## 主要接口

| 模块 | 基础路径 | 说明 |
|------|----------|------|
| 班课 | `/course` | 班课 CRUD、学生管理、资源分组 |
| 学生 | `/student` | 学生信息、登录注册 |
| 教师 | `/teacher` | 教师信息、登录注册 |
| 作业 | `/homework` | 作业发布、提交、批改 |
| 测验 | `/exam` | 测验创建、在线考试、成绩统计 |
| 题目 | `/question` | 题库管理、题目录入 |
| 资源 | `/resource` | 资源上传、下载、分类 |
| 对话 | `/chat` | AI 智能对话 |

## 部署说明

### Docker Compose 部署

项目已配置 `docker-compose.yml`，可一键启动 MinIO 和 KKFileView：

```bash
cd project
docker-compose up -d
```

| 服务 | 端口 | 说明 |
|------|------|------|
| MinIO API | 9000 | 对象存储服务 |
| MinIO Console | 9090 | MinIO 管理后台 |
| KKFileView | 8012 | 文件预览服务 |

### 生产环境部署

1. 修改 `application.yml` 中的数据库、MinIO、Coze 等配置为生产环境值
2. 使用 Maven 打包后端项目
3. 使用 Vite 构建前端项目，将 `dist` 目录部署到 Nginx
4. 配置 Nginx 反向代理到后端服务

## 项目截图
<img width="376" height="323" alt="image" src="https://github.com/user-attachments/assets/bdf36fdb-4929-4ff9-bd9d-fdc30b8335ce" />
<img width="468" height="256" alt="image" src="https://github.com/user-attachments/assets/e3c489dd-97bb-4ea8-abea-791b019bb20b" />
<img width="438" height="268" alt="image" src="https://github.com/user-attachments/assets/41acd962-71a7-4d0b-9109-57cb381cebc2" />
<img width="470" height="216" alt="image" src="https://github.com/user-attachments/assets/b6c29593-c2f9-4b71-9cd0-655bf6de7916" />
<img width="470" height="250" alt="image" src="https://github.com/user-attachments/assets/6e0b5a78-a0dd-4d87-bb22-b33827584557" />
<img width="470" height="312" alt="image" src="https://github.com/user-attachments/assets/8fc4857e-3f8c-4516-ba03-63fe12340db4" />
<img width="466" height="296" alt="image" src="https://github.com/user-attachments/assets/014ad766-3d08-4e2d-b317-f942faa332a4" />
<img width="469" height="216" alt="image" src="https://github.com/user-attachments/assets/5d2ab8fe-1e6e-4721-8efd-7ba0caf2a47e" />
<img width="470" height="122" alt="image" src="https://github.com/user-attachments/assets/7a4d09f8-f523-4764-8154-d45afc01953b" />
<img width="419" height="272" alt="image" src="https://github.com/user-attachments/assets/5d88c820-e2b7-4cd1-9aad-ce570e1907d9" />
<img width="469" height="101" alt="image" src="https://github.com/user-attachments/assets/7c2f5a0a-ed12-44c2-9bf3-1902e78073d3" />
<img width="469" height="150" alt="image" src="https://github.com/user-attachments/assets/f5ac2239-b16d-487f-88b6-1e6dfc14112f" />
<img width="470" height="306" alt="image" src="https://github.com/user-attachments/assets/8067c233-4db1-4fcc-a6fd-557700052b83" />
<img width="441" height="283" alt="image" src="https://github.com/user-attachments/assets/26466c45-9b67-4290-9c21-9194bbaa5684" />



## 开源协议

[MIT](LICENSE)
