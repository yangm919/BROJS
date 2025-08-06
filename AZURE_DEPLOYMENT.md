# Azure 虚拟机应用部署说明文档

## 目录
- [1. 环境准备](#1-环境准备)
- [2. SSH 连接](#2-ssh-连接)
- [3. 服务器环境配置](#3-服务器环境配置)
- [4. 数据库初始化](#4-数据库初始化)
- [5. 后端部署](#5-后端部署)
- [6. 前端部署](#6-前端部署)
- [7. Nginx 配置](#7-nginx-配置)
- [8. 应用管理](#8-应用管理)
- [9. 故障排查](#9-故障排查)

## 1. 环境准备

### 1.1 本地环境要求
- Node.js 16+ (前端构建)
- Java 8+ (后端构建)
- Maven 3.6+ (后端依赖管理)
- Docker (容器化部署)
- SSH 客户端

### 1.2 服务器环境要求
- Ubuntu 20.04 LTS
- Docker 20.10+
- Docker Compose 2.0+
- Nginx 1.18+
- 至少 4GB RAM
- 至少 20GB 磁盘空间

## 2. SSH 连接

### 2.1 连接命令
```bash
ssh -i ~/Downloads/enabled.pem ubuntu@4.213.48.226
```

### 2.2 连接注意事项
- 确保密钥文件权限正确：`chmod 400 ~/Downloads/enabled.pem`
- 首次连接会提示确认服务器指纹，输入 `yes` 确认
- 如果连接失败，检查密钥文件路径和权限

## 3. 服务器环境配置

### 3.1 更新系统
```bash
sudo apt update
sudo apt upgrade -y
```

### 3.2 安装 Docker
```bash
# 安装 Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 将当前用户添加到 docker 组
sudo usermod -aG docker $USER

# 启动 Docker 服务
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
```

### 3.3 安装 Docker Compose
```bash
# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker-compose --version
```

### 3.4 安装 Nginx
```bash
sudo apt install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
```

### 3.5 创建应用目录
```bash
# 创建应用目录
sudo mkdir -p /opt/yuoj-app
sudo chown ubuntu:ubuntu /opt/yuoj-app
cd /opt/yuoj-app

# 创建子目录
mkdir backend frontend nginx-config
```

## 4. 数据库初始化

### 4.1 Database Initialization Overview

The Online Judge System requires the following database components:
- **MySQL**: Main database, stores users, questions, submission records, etc.
- **Redis**: Cache and session storage
- **RabbitMQ**: Message queue, used for asynchronous judging

### 4.2 Prepare Database Initialization Scripts

#### 4.2.1 Prepare Initialization Files Locally

Create database initialization files in the local project root directory:

```bash
# Create database initialization directory
mkdir -p backend/mysql-init

# Copy initialization script
cp yuoj-backend-microservice-master/mysql-init/init-docker.sql backend/mysql-init/
```

#### 4.2.2 Create Database Initialization Configuration

Create `backend/mysql-init/init.sql` locally:

```sql
-- Database initialization script
CREATE DATABASE IF NOT EXISTS `yuoj` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `yuoj`;

-- User table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'User ID' PRIMARY KEY,
    `userAccount` VARCHAR(256) NOT NULL COMMENT 'User account',
    `userPassword` VARCHAR(512) NOT NULL COMMENT 'User password',
    `unionId` VARCHAR(256) NULL COMMENT 'WeChat Open Platform ID',
    `mpOpenId` VARCHAR(256) NULL COMMENT 'WeChat Official Account openId',
    `userName` VARCHAR(256) NULL COMMENT 'User nickname',
    `userAvatar` VARCHAR(1024) NULL COMMENT 'User avatar',
    `userProfile` VARCHAR(512) NULL COMMENT 'User profile',
    `userRole` VARCHAR(256) DEFAULT 'user' NOT NULL COMMENT 'User role: user/admin/ban',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_unionId` (`unionId`),
    INDEX `idx_userAccount` (`userAccount`),
    INDEX `idx_userRole` (`userRole`)
) COMMENT 'User table' COLLATE = utf8mb4_unicode_ci;

-- Question table
CREATE TABLE IF NOT EXISTS `question` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Question ID' PRIMARY KEY,
    `title` VARCHAR(512) NULL COMMENT 'Question title',
    `content` TEXT NULL COMMENT 'Question content',
    `tags` VARCHAR(1024) NULL COMMENT 'Tags list (json array)',
    `answer` TEXT NULL COMMENT 'Question answer',
    `submitNum` INT DEFAULT 0 NOT NULL COMMENT 'Submit count',
    `acceptedNum` INT DEFAULT 0 NOT NULL COMMENT 'Accepted count',
    `judgeCase` TEXT NULL COMMENT 'Judge cases (json array)',
    `judgeConfig` TEXT NULL COMMENT 'Judge configuration (json object)',
    `thumbNum` INT DEFAULT 0 NOT NULL COMMENT 'Thumb count',
    `favourNum` INT DEFAULT 0 NOT NULL COMMENT 'Favour count',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_userId` (`userId`),
    INDEX `idx_title` (`title`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT 'Question table' COLLATE = utf8mb4_unicode_ci;

-- Question submit table
CREATE TABLE IF NOT EXISTS `question_submit` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Submit ID' PRIMARY KEY,
    `language` VARCHAR(128) NOT NULL COMMENT 'Programming language',
    `code` TEXT NOT NULL COMMENT 'User code',
    `judgeInfo` TEXT NULL COMMENT 'Judge information (json object)',
    `status` INT DEFAULT 0 NOT NULL COMMENT 'Judge status (0 - pending, 1 - judging, 2 - success, 3 - failed)',
    `questionId` BIGINT NOT NULL COMMENT 'Question ID',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_questionId` (`questionId`),
    INDEX `idx_userId` (`userId`),
    INDEX `idx_status` (`status`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT 'Question submit table' COLLATE = utf8mb4_unicode_ci;

-- Post table
CREATE TABLE IF NOT EXISTS `post` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Post ID' PRIMARY KEY,
    `title` VARCHAR(512) NULL COMMENT 'Post title',
    `content` TEXT NULL COMMENT 'Post content',
    `tags` VARCHAR(1024) NULL COMMENT 'Tags list (json array)',
    `thumbNum` INT DEFAULT 0 NOT NULL COMMENT 'Thumb count',
    `favourNum` INT DEFAULT 0 NOT NULL COMMENT 'Favour count',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    `isDelete` TINYINT DEFAULT 0 NOT NULL COMMENT 'Is deleted',
    INDEX `idx_userId` (`userId`),
    INDEX `idx_title` (`title`),
    INDEX `idx_createTime` (`createTime`)
) COMMENT 'Post table' COLLATE = utf8mb4_unicode_ci;

-- Post thumb table
CREATE TABLE IF NOT EXISTS `post_thumb` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Thumb ID' PRIMARY KEY,
    `postId` BIGINT NOT NULL COMMENT 'Post ID',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    INDEX `idx_postId` (`postId`),
    INDEX `idx_userId` (`userId`),
    UNIQUE KEY `uk_post_user` (`postId`, `userId`)
) COMMENT 'Post thumb table' COLLATE = utf8mb4_unicode_ci;

-- Post favour table
CREATE TABLE IF NOT EXISTS `post_favour` (
    `id` BIGINT AUTO_INCREMENT COMMENT 'Favour ID' PRIMARY KEY,
    `postId` BIGINT NOT NULL COMMENT 'Post ID',
    `userId` BIGINT NOT NULL COMMENT 'Creator user ID',
    `createTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT 'Create time',
    `updateTime` DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    INDEX `idx_postId` (`postId`),
    INDEX `idx_userId` (`userId`),
    UNIQUE KEY `uk_post_user` (`postId`, `userId`)
) COMMENT 'Post favour table' COLLATE = utf8mb4_unicode_ci;

-- Insert initial data
INSERT INTO `user` (`userAccount`, `userPassword`, `userName`, `userRole`, `userProfile`) VALUES
('admin', '03caebb36670995fc5e097d0d8a6f908', 'Administrator', 'admin', 'System Administrator'),
('test', '03caebb36670995fc5e097d0d8a6f908', 'Test User', 'user', 'Test user account');

-- Insert sample question
INSERT INTO `question` (`title`, `content`, `tags`, `answer`, `judgeCase`, `judgeConfig`, `userId`) VALUES
('Two Sum', 
'Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.

You may assume that each input would have exactly one solution, and you may not use the same element twice.

You can return the answer in any order.

Example 1:
Input: nums = [2,7,11,15], target = 9
Output: [0,1]
Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].

Example 2:
Input: nums = [3,2,4], target = 6
Output: [1,2]

Example 3:
Input: nums = [3,3], target = 6
Output: [0,1]

Constraints:
2 <= nums.length <= 104
-109 <= nums[i] <= 109
-109 <= target <= 109
Only one valid answer exists', 
'["Array", "Hash Table"]', 
'```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }
}
```', 
'[{"input": "[2,7,11,15]\n9", "output": "[0,1]"}, {"input": "[3,2,4]\n6", "output": "[1,2]"}, {"input": "[3,3]\n6", "output": "[0,1]"}]', 
'{"timeLimit": 1000, "memoryLimit": 256000}', 
1);

-- Insert sample post
INSERT INTO `post` (`title`, `content`, `tags`, `userId`) VALUES
('Welcome to Online Judge System', 
'Welcome to our Online Judge System! This is a powerful programming practice platform that supports multiple programming languages.

Main features:
- Question practice: Provides programming questions of various difficulties
- Online judging: Real-time code compilation and execution
- Community communication: Share problem-solving ideas and experiences
- Personal center: View problem-solving records and statistics

Start your programming journey now!', 
'["Welcome", "Introduction"]', 
1);
```

### 4.3 Transfer Database Initialization Files

```bash
# Transfer database initialization files to server
scp -i ~/Downloads/enabled.pem -r backend/mysql-init/* ubuntu@4.213.48.226:/opt/yuoj-app/backend/mysql-init/
```

### 4.4 Initialize Database on Server

#### 4.4.1 Method 1: Using Docker Compose (Recommended)

```bash
# SSH connect to server
ssh -i ~/Downloads/enabled.pem ubuntu@4.213.48.226

# Enter application directory
cd /opt/yuoj-app/backend

# Start database services (only database-related services)
docker-compose up -d mysql redis rabbitmq nacos

# Wait for database services to fully start
sleep 30

# Check database service status
docker-compose ps

# Verify database connection
docker exec -it yuoj-mysql mysql -u root -p123456 -e "SHOW DATABASES;"
```

#### 4.4.2 Method 2: Manual Database Initialization

```bash
# SSH connect to server
ssh -i ~/Downloads/enabled.pem ubuntu@4.213.48.226

# Enter application directory
cd /opt/yuoj-app/backend

# Start MySQL container
docker run -d \
  --name yuoj-mysql \
  --network yuoj-network \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=yuoj \
  -p 3306:3306 \
  -v /opt/yuoj-app/backend/mysql-data:/var/lib/mysql \
  -v /opt/yuoj-app/backend/mysql-init:/docker-entrypoint-initdb.d \
  mysql:8

# Wait for MySQL to start
sleep 60

# Verify database initialization
docker exec -it yuoj-mysql mysql -u root -p123456 -e "USE yuoj; SHOW TABLES;"
```

### 4.5 Verify Database Initialization

```bash
# Check database tables
docker exec -it yuoj-mysql mysql -u root -p123456 -e "USE yuoj; SHOW TABLES;"

# Check initial data
docker exec -it yuoj-mysql mysql -u root -p123456 -e "USE yuoj; SELECT COUNT(*) as user_count FROM user;"
docker exec -it yuoj-mysql mysql -u root -p123456 -e "USE yuoj; SELECT COUNT(*) as question_count FROM question;"
docker exec -it yuoj-mysql mysql -u root -p123456 -e "USE yuoj; SELECT COUNT(*) as post_count FROM post;"

# Check user data
docker exec -it yuoj-mysql mysql -u root -p123456 -e "USE yuoj; SELECT userAccount, userName, userRole FROM user;"
```

### 4.6 Database Configuration

#### 4.6.1 Default User Accounts

- **Admin Account**: `admin`
- **Admin Password**: `12345678`
- **Test Account**: `test`
- **Test Password**: `12345678`

#### 4.6.2 Database Connection Information

- **Database Name**: `yuoj`
- **Character Set**: `utf8mb4`
- **Collation**: `utf8mb4_unicode_ci`
- **Port**: `3306`
- **Username**: `root`
- **Password**: `123456`

#### 4.6.3 Database Table Structure

| Table Name | Description | Main Fields |
|------------|-------------|-------------|
| `user` | User table | id, userAccount, userPassword, userRole |
| `question` | Question table | id, title, content, judgeCase, judgeConfig |
| `question_submit` | Question submit table | id, language, code, status, questionId, userId |
| `post` | Post table | id, title, content, userId |
| `post_thumb` | Post thumb table | id, postId, userId |
| `post_favour` | Post favour table | id, postId, userId |

### 4.7 Database Backup and Recovery

#### 4.7.1 Backup Database

```bash
# Backup entire database
docker exec yuoj-mysql mysqldump -u root -p123456 yuoj > yuoj_backup_$(date +%Y%m%d_%H%M%S).sql

# Backup specific tables
docker exec yuoj-mysql mysqldump -u root -p123456 yuoj user question > tables_backup.sql
```

#### 4.7.2 Restore Database

```bash
# Restore database
docker exec -i yuoj-mysql mysql -u root -p123456 yuoj < yuoj_backup_20250101_120000.sql

# Restore specific tables
docker exec -i yuoj-mysql mysql -u root -p123456 yuoj < tables_backup.sql
```

### 4.8 Database Maintenance

#### 4.8.1 Regular Backup Script

Create backup script `/opt/yuoj-app/backup-db.sh`:

```bash
#!/bin/bash

# Set variables
BACKUP_DIR="/opt/yuoj-app/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="yuoj_backup_$DATE.sql"

# Create backup directory
mkdir -p $BACKUP_DIR

# Execute backup
docker exec yuoj-mysql mysqldump -u root -p123456 yuoj > $BACKUP_DIR/$BACKUP_FILE

# Compress backup file
gzip $BACKUP_DIR/$BACKUP_FILE

# Delete backups older than 7 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "Database backup completed: $BACKUP_DIR/$BACKUP_FILE.gz"
```

#### 4.8.2 Set Scheduled Backup

```bash
# Edit crontab
crontab -e

# Add scheduled task (backup at 2 AM daily)
0 2 * * * /opt/yuoj-app/backup-db.sh
```

## 5. 后端部署

### 5.1 本地构建 JAR 包

在本地项目根目录执行：
```bash
# 进入后端项目目录
cd yuoj-backend-microservice-master

# 清理并构建所有模块
mvn clean install -DskipTests

# 构建各个服务的 JAR 包
mvn clean package -DskipTests -pl yuoj-backend-gateway
mvn clean package -DskipTests -pl yuoj-backend-user-service
mvn clean package -DskipTests -pl yuoj-backend-question-service
mvn clean package -DskipTests -pl yuoj-backend-judge-service
```

### 5.2 创建 Dockerfile

在本地创建 `backend/Dockerfile`：
```dockerfile
FROM openjdk:8-jdk-alpine

WORKDIR /app

# 复制 JAR 文件
COPY yuoj-backend-gateway-0.0.1-SNAPSHOT.jar gateway.jar
COPY yuoj-backend-user-service-0.0.1-SNAPSHOT.jar user-service.jar
COPY yuoj-backend-question-service-0.0.1-SNAPSHOT.jar question-service.jar
COPY yuoj-backend-judge-service-0.0.1-SNAPSHOT.jar judge-service.jar

# 复制配置文件
COPY application.yml application.yml

EXPOSE 8101 8102 8103 8104

CMD ["java", "-jar", "gateway.jar"]
```

### 5.3 创建 docker-compose.yml

在本地创建 `backend/docker-compose.yml`：
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8
    container_name: yuoj-mysql
    environment:
      MYSQL_ROOT_PASSWORD: 123456
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - yuoj-network

  redis:
    image: redis:6
    container_name: yuoj-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - yuoj-network

  rabbitmq:
    image: rabbitmq:3.12.6-management
    container_name: yuoj-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    ports:
      - "5672:5672"
      - "15672:15672"
    volumes:
      - rabbitmq-data:/var/lib/rabbitmq
    networks:
      - yuoj-network

  nacos:
    image: nacos/nacos-server:v2.2.0-slim
    container_name: yuoj-nacos
    ports:
      - "8848:8848"
    volumes:
      - nacos-data:/home/nacos/data
    environment:
      - MODE=standalone
      - PREFER_HOST_MODE=hostname
      - TZ=Asia/Shanghai
    networks:
      - yuoj-network

  gateway:
    build: .
    container_name: yuoj-gateway
    ports:
      - "8101:8101"
    environment:
      - NACOS_HOST=yuoj-nacos
    depends_on:
      - nacos
    networks:
      - yuoj-network

  user-service:
    build: .
    container_name: yuoj-user-service
    ports:
      - "8102:8102"
    environment:
      - NACOS_HOST=yuoj-nacos
    depends_on:
      - gateway
    networks:
      - yuoj-network

  question-service:
    build: .
    container_name: yuoj-question-service
    ports:
      - "8103:8103"
    environment:
      - NACOS_HOST=yuoj-nacos
    depends_on:
      - user-service
    networks:
      - yuoj-network

  judge-service:
    build: .
    container_name: yuoj-judge-service
    ports:
      - "8104:8104"
    environment:
      - NACOS_HOST=yuoj-nacos
    depends_on:
      - question-service
    networks:
      - yuoj-network

volumes:
  mysql-data:
  redis-data:
  rabbitmq-data:
  nacos-data:

networks:
  yuoj-network:
    driver: bridge
```

### 5.4 传输文件到服务器

```bash
# 从本地传输文件到服务器
scp -i ~/Downloads/enabled.pem -r backend/* ubuntu@4.213.48.226:/opt/yuoj-app/backend/
```

### 5.5 在服务器上启动后端服务

```bash
# SSH 连接到服务器
ssh -i ~/Downloads/enabled.pem ubuntu@4.213.48.226

# 进入应用目录
cd /opt/yuoj-app/backend

# 构建并启动服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 6. 前端部署

### 6.1 本地构建前端

在本地前端目录执行：
```bash
# 进入前端项目目录
cd yuoj-frontend-master

# 安装依赖
npm install

# 构建生产版本
npm run build
```

### 6.2 传输前端文件

```bash
# 传输 dist 文件夹到服务器
scp -i ~/Downloads/enabled.pem -r dist/* ubuntu@4.213.48.226:/opt/yuoj-app/frontend/
```

### 6.3 创建前端 Dockerfile

在本地创建 `frontend/Dockerfile`：
```dockerfile
FROM nginx:alpine

# 复制前端文件
COPY . /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 6.4 传输前端 Dockerfile

```bash
# 传输前端 Dockerfile
scp -i ~/Downloads/enabled.pem frontend/Dockerfile ubuntu@4.213.48.226:/opt/yuoj-app/frontend/
```

## 7. Nginx 配置

### 7.1 创建 Nginx 配置文件

在本地创建 `nginx-config/nginx.conf`：
```nginx
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    # 日志格式
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;
    error_log /var/log/nginx/error.log;

    # Gzip 压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/json
        application/javascript
        application/xml+rss
        application/atom+xml
        image/svg+xml;

    # 上游服务器配置
    upstream backend {
        server 127.0.0.1:8101;
    }

    # HTTP 服务器
    server {
        listen 80;
        server_name localhost;

        # 前端静态文件
        location / {
            root /usr/share/nginx/html;
            index index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        # API 代理
        location /api/ {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # 超时设置
            proxy_connect_timeout 30s;
            proxy_send_timeout 30s;
            proxy_read_timeout 30s;
        }

        # 健康检查
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

### 7.2 传输 Nginx 配置

```bash
# 传输 Nginx 配置文件
scp -i ~/Downloads/enabled.pem nginx-config/nginx.conf ubuntu@4.213.48.226:/opt/yuoj-app/nginx-config/
```

### 7.3 在服务器上配置 Nginx

```bash
# SSH 连接到服务器
ssh -i ~/Downloads/enabled.pem ubuntu@4.213.48.226

# 备份原配置
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup

# 复制新配置
sudo cp /opt/yuoj-app/nginx-config/nginx.conf /etc/nginx/nginx.conf

# 测试配置
sudo nginx -t

# 重启 Nginx
sudo systemctl restart nginx

# 检查 Nginx 状态
sudo systemctl status nginx
```

## 8. 应用管理

### 8.1 启动应用

```bash
# 启动后端服务
cd /opt/yuoj-app/backend
docker-compose up -d

# 检查服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

### 8.2 停止应用

```bash
# 停止后端服务
cd /opt/yuoj-app/backend
docker-compose down

# 停止 Nginx
sudo systemctl stop nginx
```

### 8.3 重启应用

```bash
# 重启后端服务
cd /opt/yuoj-app/backend
docker-compose restart

# 重启 Nginx
sudo systemctl restart nginx
```

### 8.4 查看应用状态

```bash
# 查看 Docker 容器状态
docker ps

# 查看 Nginx 状态
sudo systemctl status nginx

# 查看端口占用
sudo netstat -tlnp

# 查看服务日志
docker-compose logs -f gateway
docker-compose logs -f user-service
docker-compose logs -f question-service
docker-compose logs -f judge-service
```

### 8.5 更新应用

```bash
# 停止服务
cd /opt/yuoj-app/backend
docker-compose down

# 重新构建镜像
docker-compose build --no-cache

# 启动服务
docker-compose up -d
```

## 9. 故障排查

### 9.1 常见问题

#### 9.1.1 SSH 连接失败
```bash
# 检查密钥权限
chmod 400 ~/Downloads/enabled.pem

# 检查网络连接
ping 4.213.48.226

# 使用详细模式连接
ssh -v -i ~/Downloads/enabled.pem ubuntu@4.213.48.226
```

#### 9.1.2 Docker 服务无法启动
```bash
# 检查 Docker 服务状态
sudo systemctl status docker

# 重启 Docker 服务
sudo systemctl restart docker

# 检查 Docker 权限
sudo usermod -aG docker $USER
newgrp docker
```

#### 9.1.3 应用无法访问
```bash
# 检查端口是否开放
sudo netstat -tlnp | grep :80
sudo netstat -tlnp | grep :8101

# 检查防火墙
sudo ufw status
sudo ufw allow 80
sudo ufw allow 8101

# 检查 Nginx 配置
sudo nginx -t
sudo systemctl status nginx
```

#### 9.1.4 数据库连接失败
```bash
# 检查 MySQL 容器状态
docker ps | grep mysql

# 查看 MySQL 日志
docker logs yuoj-mysql

# 进入 MySQL 容器
docker exec -it yuoj-mysql mysql -u root -p

# 检查数据库表是否存在
docker exec -it yuoj-mysql mysql -u root -p123456 -e "USE yuoj; SHOW TABLES;"

# 检查数据库初始化是否成功
docker exec -it yuoj-mysql mysql -u root -p123456 -e "USE yuoj; SELECT COUNT(*) FROM user;"
```

#### 9.1.5 数据库初始化失败
```bash
# 检查初始化脚本是否存在
ls -la /opt/yuoj-app/backend/mysql-init/

# 重新初始化数据库
docker-compose down
docker volume rm backend_mysql-data
docker-compose up -d mysql

# 手动执行初始化脚本
docker exec -i yuoj-mysql mysql -u root -p123456 < /opt/yuoj-app/backend/mysql-init/init-docker.sql
```

### 9.2 日志查看

```bash
# 查看应用日志
cd /opt/yuoj-app/backend
docker-compose logs -f

# 查看 Nginx 日志
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log

# 查看系统日志
sudo journalctl -u nginx -f
sudo journalctl -u docker -f
```

### 9.3 性能监控

```bash
# 查看系统资源使用
htop

# 查看磁盘使用
df -h

# 查看内存使用
free -h

# 查看 Docker 资源使用
docker stats
```

### 9.4 备份和恢复

```bash
# 备份数据库
docker exec yuoj-mysql mysqldump -u root -p123456 yuoj > backup.sql

# 备份配置文件
sudo cp /etc/nginx/nginx.conf /opt/yuoj-app/backup/nginx.conf.backup

# 恢复数据库
docker exec -i yuoj-mysql mysql -u root -p123456 yuoj < backup.sql
```

## 10. 安全配置

### 10.1 防火墙配置

```bash
# 安装 UFW
sudo apt install ufw

# 配置防火墙规则
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 80
sudo ufw allow 443

# 启用防火墙
sudo ufw enable
```

### 10.2 SSL 证书配置（可选）

```bash
# 安装 Certbot
sudo apt install certbot python3-certbot-nginx

# 获取 SSL 证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo crontab -e
# 添加以下行：
# 0 12 * * * /usr/bin/certbot renew --quiet
```

## 11. 维护命令

### 11.1 日常维护

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 清理 Docker 镜像
docker system prune -f

# 清理日志
sudo journalctl --vacuum-time=7d
```

### 11.2 监控脚本

创建监控脚本 `/opt/yuoj-app/monitor.sh`：
```bash
#!/bin/bash

# 检查服务状态
echo "=== 服务状态检查 ==="
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo "=== 系统资源使用 ==="
free -h
df -h

echo "=== 端口监听状态 ==="
sudo netstat -tlnp | grep -E ":(80|8101|8102|8103|8104)"
```

## 注意事项

1. **安全注意事项**
   - 定期更新系统和软件包
   - 使用强密码和密钥认证
   - 配置防火墙规则
   - 定期备份重要数据

2. **性能注意事项**
   - 监控系统资源使用情况
   - 定期清理日志和临时文件
   - 根据负载调整容器资源配置

3. **部署注意事项**
   - 确保所有服务依赖正确配置
   - 测试所有功能模块
   - 准备回滚方案
   - 记录部署步骤和配置

4. **维护注意事项**
   - 定期检查服务状态
   - 监控应用日志
   - 及时处理错误和警告
   - 保持文档更新

---

**文档版本**: 1.0  
**最后更新**: 2025-08-01  
**维护人员**: 系统管理员 