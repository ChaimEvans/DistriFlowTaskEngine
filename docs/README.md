# DistriFlowTaskEngine

一个基于 Spring Boot 3.5.5 + MySQL 8 + RabbitMQ + Redis 的分布式任务处理系统，提供灵活的基于节点的工作流架构。

## 技术栈

- **后端框架**: Spring Boot 3.5.5
- **数据库**: MySQL 8
- **ORM**: Spring Data JPA
- **构建工具**: Gradle
- **Java版本**: Java 24
- **消息队列**: RabbitMQ
- **缓存**: Redis
- **其他依赖**: Lombok, Validation, AMQP

## 项目结构

```
src/main/java/fun/chaim/DFTE/
├── common/           # 通用组件
├── component/          # 特殊功能组件
├── config/          # 配置类
├── controller/       # 控制器层
├── dto/             # 数据传输对象
├── entity/          # 实体类
├── exception/       # 异常处理
├── repository/      # 数据访问层
└── service/         # 业务逻辑层
```

## API文档
[https://0cviksrddz.apifox.cn](https://0cviksrddz.apifox.cn)

## 前端
[https://github.com/ChaimEvans/DFTE-Frontend](https://github.com/ChaimEvans/DFTE-Frontend)