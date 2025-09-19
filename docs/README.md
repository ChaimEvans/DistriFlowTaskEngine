# DistriFlowTaskEngine

一个基于Spring Boot 3.5.5 + MySQL 8的分布式任务处理系统，提供灵活的基于节点的工作流架构。

## 技术栈

- **后端框架**: Spring Boot 3.5.5
- **数据库**: MySQL 8
- **ORM**: Spring Data JPA
- **构建工具**: Gradle
- **Java版本**: Java 24
- **其他依赖**: Lombok, Validation, Redis, AMQP

## 项目结构

```
src/main/java/fun/chaim/DFTE/
├── common/           # 通用组件
│   └── ApiResponse.java    # 统一API响应格式
├── controller/       # 控制器层
│   ├── FileUploadController.java
│   ├── ProgramController.java
│   ├── WorkflowController.java
│   ├── ParamController.java
│   ├── ProjectController.java
│   ├── TaskController.java
│   └── RunningRecordController.java
├── dto/             # 数据传输对象
│   ├── ProgramDto.java
│   ├── WorkflowDto.java
│   ├── ParamDto.java
│   ├── ProjectDto.java
│   ├── TaskDto.java
│   └── RunningRecordDto.java
├── entity/          # 实体类
│   ├── Program.java
│   ├── Workflow.java
│   ├── Param.java
│   ├── Project.java
│   ├── Task.java
│   └── RunningRecord.java
├── exception/       # 异常处理
│   ├── BusinessException.java
│   ├── ResourceNotFoundException.java
│   ├── ValidationException.java
│   ├── ConflictException.java
│   ├── ForbiddenException.java
│   └── GlobalExceptionHandler.java
├── repository/      # 数据访问层
│   ├── ProgramRepository.java
│   ├── WorkflowRepository.java
│   ├── ParamRepository.java
│   ├── ProjectRepository.java
│   ├── TaskRepository.java
│   └── RunningRecordRepository.java
└── service/         # 业务逻辑层
    ├── FileUploadService.java
    ├── ProgramService.java
    ├── WorkflowService.java
    ├── ParamService.java
    ├── ProjectService.java
    ├── TaskService.java
    └── RunningRecordService.java
```

## 快速开始

### 1. 环境要求

- Java 24+
- MySQL 8+
- Gradle 7+

### 2. 数据库配置

1. 启动MySQL服务
2. 创建数据库：
   ```sql
   CREATE DATABASE distriflow_task_engine CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. 修改 `src/main/resources/application.properties` 中的数据库连接信息：
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/distriflow_task_engine?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### 3. 启动应用

#### Windows
```bash
start.bat
```

#### Linux/macOS
```bash
./start.sh
```

#### 手动启动
```bash
./gradlew bootRun
```

### 4. 访问应用

应用启动后，访问 `http://localhost:8080` 即可使用API接口。

## API文档

详细的API文档请参考 [API手册](docs/API手册.md)

## 主要功能

### 1. 文件管理
- 文件上传
- 文件列表查看

### 2. 处理程序管理
- 创建、查询、更新、删除处理程序
- 处理程序参数管理
- 支持文件路径和启动命令配置

### 3. 工作流管理
- 创建、查询、更新、删除工作流
- 工作流克隆
- 工作流锁定/解锁
- 工作流参数管理

### 4. 项目管理
- 创建、查询、更新项目
- 项目与工作流关联

### 5. 任务管理
- 任务查询（分页）
- 任务详情查看
- 任务状态跟踪

### 6. 运行记录管理
- 运行记录查询（分页、条件筛选）
- 执行历史查看

## 数据库表结构

系统包含以下主要数据表：

- **program**: 处理程序表
- **workflow**: 工作流表
- **param**: 参数表
- **project**: 项目表
- **task**: 任务表
- **running_record**: 运行记录表

详细的数据库设计请参考 [数据库设计文档](docs/prompt/数据库设计.md)

## 开发规范

### 1. 代码规范
- 使用Lombok减少样板代码
- 遵循Spring Boot开发规范
- 添加完整的JavaDoc注释
- 使用统一的异常处理机制

### 2. API设计规范
- 统一使用JSON格式
- 统一的响应格式
- RESTful API设计
- 完整的错误处理

### 3. 数据验证
- 使用Bean Validation进行参数验证
- 业务逻辑验证
- 数据完整性检查

## 配置说明

### 应用配置
```properties
# 应用名称
spring.application.name=DistriFlowTask Engine

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/distriflow_task_engine
spring.datasource.username=root
spring.datasource.password=123456

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# 文件上传配置
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# 服务器配置
server.port=8080
```

## 错误处理

系统提供完整的错误处理机制：

- **400**: 请求参数错误
- **403**: 禁止操作
- **404**: 资源未找到
- **409**: 资源冲突
- **413**: 文件大小超出限制
- **500**: 服务器内部错误

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 项目Issues: [GitHub Issues](https://github.com/your-repo/issues)
- 邮箱: your-email@example.com

---

**注意**: 这是一个开发版本，请在生产环境中使用前进行充分测试。
