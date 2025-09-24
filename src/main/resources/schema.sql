-- 数据库初始化脚本
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS distriflow_task_engine CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE distriflow_task_engine;

-- 处理程序表
CREATE TABLE IF NOT EXISTS Program (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL UNIQUE,
    title VARCHAR(128),
    description TEXT,
    buildin BOOLEAN NOT NULL DEFAULT FALSE,
    file VARCHAR(512),
    cmd VARCHAR(512),
    `lock` BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 工作流表
CREATE TABLE IF NOT EXISTS Workflow (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    data JSON,
    `lock` BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 参数表
CREATE TABLE IF NOT EXISTS Param (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    program INT,
    workflow INT,
    name VARCHAR(128) NOT NULL,
    type VARCHAR(128),
    description TEXT,
    retval BOOLEAN NOT NULL DEFAULT FALSE,
    `require` BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (program) REFERENCES Program(id) ON DELETE CASCADE,
    FOREIGN KEY (workflow) REFERENCES Workflow(id) ON DELETE CASCADE,
    CONSTRAINT CHK_Param_SingleParent CHECK (
        (program IS NOT NULL AND workflow IS NULL) OR
        (program IS NULL AND workflow IS NOT NULL)
    ),
    CONSTRAINT UQ_Program_Param UNIQUE (program, name),
    CONSTRAINT UQ_Workflow_Param UNIQUE (workflow, name)
);

-- 项目表
CREATE TABLE IF NOT EXISTS Project (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    finish BOOLEAN NOT NULL DEFAULT FALSE,
    workflow INT,
    workflow_input JSON NOT NULL,
    FOREIGN KEY (workflow) REFERENCES Workflow(id) ON DELETE SET NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 运行记录表
CREATE TABLE IF NOT EXISTS RunningRecord (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    workflow INT,
    workflow_data JSON NOT NULL,
    workflow_input JSON NOT NULL,
    workflow_output JSON,
    project INT,
    finish BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (workflow) REFERENCES Workflow(id) ON DELETE SET NULL,
    FOREIGN KEY (project) REFERENCES Project(id) ON DELETE SET NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 任务表
CREATE TABLE IF NOT EXISTS Task (
    uuid BINARY(16) PRIMARY KEY NOT NULL,
    parent BINARY(16),
    name VARCHAR(128),
    running_record INT NOT NULL,
    project INT,
    workflow INT,
    node_id INT,
    program INT,
    params JSON NOT NULL,
    status INT NOT NULL DEFAULT 0,
    retmsg TEXT,
    retdata JSON NOT NULL,
    processing_node_mac VARCHAR(17),
    FOREIGN KEY (parent) REFERENCES Task(uuid) ON DELETE SET NULL,
    FOREIGN KEY (running_record) REFERENCES RunningRecord(id) ON DELETE CASCADE,
    FOREIGN KEY (project) REFERENCES Project(id) ON DELETE SET NULL,
    FOREIGN KEY (workflow) REFERENCES Workflow(id) ON DELETE SET NULL,
    FOREIGN KEY (program) REFERENCES Program(id) ON DELETE SET NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);