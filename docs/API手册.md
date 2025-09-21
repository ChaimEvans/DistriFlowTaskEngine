# DistriFlowTaskEngine API 手册

## 概述

DistriFlowTaskEngine 是一个基于Spring Boot 3.5.5 + MySQL 8的分布式任务处理系统，提供完整的RESTful API接口。

## 基础信息

- **基础URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **响应格式**: 统一JSON格式

## 统一响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {...},
  "stacktrace": null
}
```

### 失败响应
```json
{
  "code": 400,
  "message": "错误信息",
  "data": null,
  "stacktrace": "堆栈跟踪信息（可选）"
}
```

## API接口

### 1. 文件上传接口

#### 1.1 上传文件
**POST** `/api/files/upload`

**请求参数**:
- `file`: 上传的文件（multipart/form-data）

**cURL示例**:
```bash
curl -X POST "http://localhost:8080/api/files/upload" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/your/file.txt"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "downloadPath": "/uploads/550e8400-e29b-41d4-a716-446655440000.txt",
    "originalFilename": "test.txt",
    "fileSize": 1024
}
}
```

#### 1.2 列出上传文件
**GET** `/api/files/list`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/files/list"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "downloadPath": "/uploads/550e8400-e29b-41d4-a716-446655440000.txt",
      "originalFilename": "test.txt",
      "fileSize": 1024
  }
  ]
}
```

### 2. 处理程序接口

#### 2.1 新建处理程序
**POST** `/api/programs`

**请求体示例**:
```json
{
  "name": "test_program",
  "title": "测试程序",
  "description": "这是一个测试程序",
  "file": "/uploads/test_program.py",
  "cmd": "python test_program.py"
}
```

**cURL示例**:
```bash
curl -X POST "http://localhost:8080/api/programs" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test_program",
    "title": "测试程序",
    "description": "这是一个测试程序",
    "file": "/uploads/test_program.py",
    "cmd": "python test_program.py"
}'
```

**响应示例**：
```json
{
    "code": 200,
    "message": "处理程序创建成功",
    "data": {
        "id": 1,
        "name": "test_program",
        "title": "测试程序",
        "description": "这是一个测试程序",
        "buildin": false,
        "file": "/uploads/test_program.py",
        "cmd": "python test_program.py",
        "lock": false,
        "inputParams": null,
        "outputParams": null,
        "updatedAt": "2025-09-20T11:03:54.772514"
  },
    "stacktrace": null
}
```

#### 2.2 查询处理程序基础信息
**GET** `/api/programs/{id}`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/programs/1"
```

**响应示例**：
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "name": "test_program",
        "title": "测试程序",
        "description": "这是一个测试程序",
        "buildin": false,
        "lock": false
  },
    "stacktrace": null
}
```

#### 2.3 查询处理程序详细信息
**GET** `/api/programs/{id}/detail`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/programs/1/detail"
```

**响应示例**：
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "name": "test_program",
        "title": "测试程序",
        "description": "这是一个测试程序",
        "buildin": false,
        "file": "/uploads/test_program.py",
        "cmd": "python test_program.py",
        "lock": false,
        "inputParams": [
            {
                "id": 1,
                "name": "input_param",
                "type": "string",
                "description": "输入参数",
                "retval": false,
                "require": true
          }
        ],
        "outputParams": [
            {
                "id": 2,
                "name": "output_param",
                "type": "string",
                "description": "输入参数",
                "retval": true,
                "require": true
          }
        ],
        "updatedAt": "2025-09-20T11:03:54.772514"
  },
    "stacktrace": null
}
```


#### 2.4 列出所有处理程序
**GET** `/api/programs`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/programs"
```

**响应示例**：
```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "name": "test_program",
            "title": "测试程序",
            "description": "这是一个测试程序",
            "buildin": false,
            "lock": false
      }
    ],
    "stacktrace": null
}
```

#### 2.5 查询处理程序名称
**GET** `/api/programs/{id}/name`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/programs/1/name"
```

**响应示例**：
```json
{
    "code": 200,
    "message": "操作成功",
    "data": "test_program",
    "stacktrace": null
}
```

#### 2.6 删除处理程序
**DELETE** `/api/programs/{id}`

**cURL示例**:
```bash
curl -X DELETE "http://localhost:8080/api/programs/1"
```

#### 2.7 更新处理程序
**PUT** `/api/programs/{id}`

**请求体示例**:
```json
{
  "name": "updated_program",
  "title": "更新后的程序",
  "description": "更新后的描述",
  "file": "/uploads/updated_program.py",
  "cmd": "python updated_program.py"
}
```

**cURL示例**:
```bash
curl -X PUT "http://localhost:8080/api/programs/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "updated_program",
    "title": "更新后的程序",
    "description": "更新后的描述",
    "file": "/uploads/updated_program.py",
    "cmd": "python updated_program.py"
}'
```

**响应示例**:
```json
{
    "code": 200,
    "message": "处理程序更新成功",
    "data": {
        "id": 1,
        "name": "updated_program",
        "title": "更新后的程序",
        "description": "更新后的描述",
        "buildin": false,
        "file": "/uploads/updated_program.py",
        "cmd": "python updated_program.py",
        "lock": false,
        "inputParams": null,
        "outputParams": null,
        "updatedAt": "2025-09-20T11:03:54.772514"
  },
    "stacktrace": null
}
```

#### 2.8 为处理程序创建参数
**POST** `/api/programs/{programId}/params`

**请求体示例**:
```json
{
  "name": "input_param",
  "type": "string",
  "description": "输入参数",
  "retval": false,
  "require": true
}
```

**cURL示例**:
```bash
curl -X POST "http://localhost:8080/api/programs/1/params" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "input_param",
    "type": "string",
    "description": "输入参数",
    "retval": false,
    "require": true
}'
```

**响应示例**：
```json
{
    "code": 200,
    "message": "参数创建成功",
    "data": {
        "id": 1,
        "name": "input_param",
        "type": "string",
        "description": "输入参数",
        "retval": false,
        "require": true
  },
    "stacktrace": null
}
```

### 3. 工作流接口

#### 3.1 新建工作流
**POST** `/api/workflows`

**请求体示例**:
```json
{
  "name": "test_workflow",
  "description": "测试工作流",
  "data": "{}"
}
```

**cURL示例**:
```bash
curl -X POST "http://localhost:8080/api/workflows" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test_workflow",
    "description": "测试工作流",
    "data": "{}"
}'
```

**响应示例**:
```json
{
    "code": 200,
    "message": "工作流创建成功",
    "data": {
        "id": 1,
        "name": "test_workflow",
        "description": "测试工作流",
        "data": "{}",
        "lock": false,
        "createdAt": "2025-09-20T11:56:19.918592",
        "updatedAt": "2025-09-20T11:56:19.918592"
  },
    "stacktrace": null
}
```

#### 3.2 克隆工作流
**POST** `/api/workflows/{id}/clone`

**请求体示例**:
```json
{
  "name": "cloned_workflow"
}
```

**cURL示例**:
```bash
curl -X POST "http://localhost:8080/api/workflows/1/clone" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "cloned_workflow"
}'
```

**响应示例**:
```json
{
    "code": 200,
    "message": "工作流克隆成功",
    "data": {
        "id": 2,
        "name": "cloned_workflow",
        "description": "测试工作流",
        "data": "{}",
        "lock": false,
        "createdAt": "2025-09-20T11:57:57.597033",
        "updatedAt": "2025-09-20T11:57:57.597033"
    },
    "stacktrace": null
}
```

#### 3.3 查询工作流
**GET** `/api/workflows/{id}`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/workflows/1"
```

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "name": "test_workflow",
        "description": "测试工作流",
        "data": "{}",
        "lock": false,
        "createdAt": "2025-09-20T11:56:19.918592",
        "updatedAt": "2025-09-20T11:56:19.918592"
    },
    "stacktrace": null
}
```

#### 3.4 列出所有工作流
**GET** `/api/workflows`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/workflows"
```

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "name": "test_workflow",
            "description": "测试工作流",
            "lock": false,
            "createdAt": "2025-09-20T11:56:19.918592",
            "updatedAt": "2025-09-20T11:56:19.918592"
        },
        {
            "id": 2,
            "name": "cloned_workflow",
            "description": "测试工作流",
            "lock": false,
            "createdAt": "2025-09-20T11:57:57.597033",
            "updatedAt": "2025-09-20T11:57:57.597033"
        }
    ],
    "stacktrace": null
}
```

#### 3.5 删除工作流
**DELETE** `/api/workflows/{id}`

**cURL示例**:
```bash
curl -X DELETE "http://localhost:8080/api/workflows/1"
```

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": null,
    "stacktrace": null
}
```

#### 3.6 修改工作流
**PUT** `/api/workflows/{id}`

**请求体示例**:
```json
{
  "name": "updated_workflow",
  "description": "更新后的工作流",
  "data": "{}"
}
```

**cURL示例**:
```bash
curl -X PUT "http://localhost:8080/api/workflows/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "updated_workflow",
    "description": "更新后的工作流",
    "data": "{}"
}'
```

**响应示例**:
```json
{
    "code": 200,
    "message": "工作流更新成功",
    "data": {
        "id": 1,
        "name": "updated_workflow",
        "description": "更新后的工作流",
        "data": "{}",
        "lock": false,
        "createdAt": "2025-09-20T11:57:57.597033",
        "updatedAt": "2025-09-20T11:57:57.597033"
    },
    "stacktrace": null
}
```

#### 3.7 解锁工作流
**POST** `/api/workflows/{id}/unlock`

**cURL示例**:
```bash
curl -X POST "http://localhost:8080/api/workflows/1/unlock"
```

**响应示例**:
```json
{
    "code": 200,
    "message": "工作流解锁成功",
    "data": {
        "id": 1,
        "name": "updated_workflow",
        "description": "更新后的工作流",
        "data": "{}",
        "lock": false,
        "createdAt": "2025-09-20T11:57:57.597033",
        "updatedAt": "2025-09-20T12:09:11.315788"
    },
    "stacktrace": null
}
```

#### 3.8 查询使用工作流的项目
**GET** `/api/workflows/{id}/projects`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/workflows/1/projects"
```

#### 3.9 为工作流创建参数
**POST** `/api/workflows/{workflowId}/params`

**请求体示例**:
```json
{
  "name": "workflow_param",
  "type": "integer",
  "description": "工作流参数",
  "retval": false,
  "require": true
}
```

**cURL示例**:
```bash
curl -X POST "http://localhost:8080/api/workflows/1/params" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "workflow_param",
    "type": "integer",
    "description": "工作流参数",
    "retval": false,
    "require": true
}'
```

**响应示例**:
```json
{
    "code": 200,
    "message": "参数创建成功",
    "data": {
        "id": 3,
        "name": "workflow_param",
        "type": "integer",
        "description": "工作流参数",
        "retval": false,
        "require": true
    },
    "stacktrace": null
}
```

### 4. 参数接口

#### 4.1 删除参数
**DELETE** `/api/params/{id}`

**cURL示例**:
```bash
curl -X DELETE "http://localhost:8080/api/params/1"
```

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": null,
    "stacktrace": null
}
```

#### 4.2 修改参数
**PUT** `/api/params/{id}`

**请求体示例**:
```json
{
  "name": "updated_param",
  "type": "string",
  "description": "更新后的参数",
  "retval": false,
  "require": true
}
```

**cURL示例**:
```bash
curl -X PUT "http://localhost:8080/api/params/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "updated_param",
    "type": "string",
    "description": "更新后的参数",
    "retval": false,
    "require": true
}'
```

**响应示例**:
```json
{
    "code": 200,
    "message": "参数更新成功",
    "data": {
        "id": 1,
        "name": "updated_param",
        "type": "string",
        "description": "更新后的参数",
        "retval": false,
        "require": true
    },
    "stacktrace": null
}
```

#### 4.3 搜索参数
**GET** `/api/params/search?type=string&retval=false&isWorkflow=false`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/params/search?type=string&retval=false&isWorkflow=false"
```

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "id": 2,
            "name": "updated_param",
            "type": "string",
            "description": "更新后的参数",
            "require": true,
            "parentId": 1,
            "parentName": "updated_program"
        }
    ],
    "stacktrace": null
}
```

### 5. 项目接口

#### 5.1 新建项目
**POST** `/api/projects`

**请求体示例**:
```json
{
  "name": "test_project",
  "workflow": 1,
  "workflowInput": {
      "param1": "value1"
  }
}
```

**cURL示例**:
```bash
curl -X POST "http://localhost:8080/api/projects" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test_project",
    "workflow": 1,
    "workflowInput": {
        "param1": "value1"
    }
}'
```

**响应示例**:
```json
{
    "code": 200,
    "message": "项目创建成功",
    "data": {
        "id": 1,
        "name": "test_project",
        "workflow": 1,
        "workflowInput": {
            "param1": "value1"
        },
        "createdAt": "2025-09-20T13:05:43.530242"
    },
    "stacktrace": null
}
```

#### 5.2 查询项目
**GET** `/api/projects/{id}`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/projects/1"
```

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "name": "test_project",
        "workflow": 1,
        "workflowInput": {
            "param1": "value1"
        },
        "createdAt": "2025-09-20T13:05:43.530242"
    },
    "stacktrace": null
}
```

#### 5.3 列出所有项目
**GET** `/api/projects`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/projects"
```

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "id": 2,
            "name": "test_project_1",
            "workflow": 2,
            "workflowName": "updated_workflow",
            "createdAt": "2025-09-20T13:08:42.416319"
        },
        {
            "id": 1,
            "name": "test_project",
            "workflow": 1,
            "workflowName": "updated_workflow",
            "createdAt": "2025-09-20T13:05:43.530242"
        }
    ],
    "stacktrace": null
}
```

#### 5.4 修改项目
**PUT** `/api/projects/{id}`

**请求体示例**:
```json
{
  "name": "updated_project",
  "workflow": 2,
  "workflowInput": {
    "new_param": "new_value"
  }
}
```

**cURL示例**:
```bash
curl -X PUT "http://localhost:8080/api/projects/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "updated_project",
    "workflow": 2,
    "workflowInput": {
        "new_param": "new_value"
    }
}'
```

**响应示例**:
```json
{
    "code": 200,
    "message": "项目更新成功",
    "data": {
        "id": 1,
        "name": "updated_project",
        "workflow": 2,
        "workflowInput": {
            "new_param": "new_value"
        },
        "createdAt": "2025-09-20T13:05:43.530242"
    },
    "stacktrace": null
}
```

### 6. 任务接口

#### 6.1 分页查询任务
**GET** `/api/tasks?page=0&size=10`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/tasks?page=0&size=10"
```

#### 6.2 查询任务详情
**GET** `/api/tasks/{uuid}`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/tasks/550e8400-e29b-41d4-a716-446655440000"
```

**响应示例**:
```json

```

### 7. 运行记录接口

#### 7.1 分页查询运行记录
**GET** `/api/running-records?page=0&size=10&workflowId=1&projectId=1&finish=true`

**cURL示例**:
```bash
curl -X GET "http://localhost:8080/api/running-records?page=0&size=10&workflowId=1&projectId=1&finish=true"
```

## 错误码说明

- **200**: 操作成功
- **400**: 请求参数错误
- **403**: 禁止操作（如尝试修改已锁定的资源）
- **404**: 资源未找到
- **409**: 资源冲突（如名称重复）
- **413**: 文件大小超出限制
- **500**: 服务器内部错误
- **-1**: 通用业务错误

## 注意事项

1. 所有时间字段使用ISO 8601格式（如：2024-01-01T12:00:00）
2. JSON字段在请求和响应中都是字符串格式
3. UUID字段使用标准UUID格式
4. 分页查询的页码从0开始
5. 删除操作会级联删除相关数据
6. 锁定状态的资源无法修改或删除
7. 文件上传大小限制为100MB

## 数据库初始化

系统启动时会自动创建数据库表结构，无需手动执行SQL脚本。确保MySQL服务已启动并创建了对应的数据库。

## 开发环境配置

修改 `src/main/resources/application.properties` 中的数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/distriflow_task_engine?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```
