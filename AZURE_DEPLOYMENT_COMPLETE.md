# Azure 虚拟机应用部署说明文档

## 目录
- [1. 项目概述](#1-项目概述)
- [2. 环境准备](#2-环境准备)
- [3. SSH 连接](#3-ssh-连接)
- [4. 服务器环境配置](#4-服务器环境配置)
- [5. 本地构建](#5-本地构建)
- [6. 文件传输](#6-文件传输)
- [7. 后端部署](#7-后端部署)
- [8. 前端部署](#8-前端部署)
- [9. Nginx 配置](#9-nginx-配置)
- [10. 应用管理](#10-应用管理)
- [11. 故障排查](#11-故障排查)

## 1. 项目概述

### 1.1 技术栈
- **后端**: Java Spring Boot 微服务架构
  - Gateway Service (端口: 8101)
  - User Service (端口: 8102)
  - Question Service (端口: 8103)
  - Judge Service (端口: 8104)
- **前端**: Vue 3 + TypeScript + Arco Design
- **数据库**: MySQL 8.0
- **缓存**: Redis 6
- **消息队列**: RabbitMQ 3.12.6
- **服务发现**: Nacos 2.2.0
- **容器化**: Docker + Docker Compose
- **Web服务器**: Nginx

### 1.2 系统架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx (80)    │    │   Frontend      │    │   Backend       │
│   (反向代理)     │◄──►│   (Vue 3)       │◄──►│   (Spring Boot) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                       ┌─────────────────────────────────────────┐
                       │              Infrastructure             │
                       │  ┌─────────┐ ┌─────────┐ ┌─────────┐  │
                       │  │ MySQL   │ │ Redis   │ │RabbitMQ │  │
                       │  │ (3306)  │ │ (6379)  │ │ (5672)  │  │
                       │  └─────────┘ └─────────┘ └─────────┘  │
                       └─────────────────────────────────────────┘
```

## 2. 环境准备

### 2.1 本地环境要求
```bash
# 检查本地环境
node --version    # 需要 16+
java -version     # 需要 8+
mvn --version     # 需要 3.6+
docker --version  # 需要 20.10+
```

### 2.2 Azure 虚拟机配置建议
- **操作系统**: Ubuntu 20.04 LTS
- **规格**: Standard_B2s (2 vCPU, 4 GB RAM)
- **磁盘**: 至少 30GB SSD
- **网络**: 开放端口 22(SSH), 80(HTTP), 443(HTTPS)

### 2.3 服务器环境要求
- Docker 20.10+
- Docker Compose 2.0+
- Nginx 1.18+
- 至少 4GB RAM
- 至少 30GB 磁盘空间

## 3. SSH 连接

### 3.1 密钥文件准备
```bash
# 设置密钥文件权限
chmod 400 ~/Downloads/enabled.pem

# 验证密钥文件
ls -la ~/Downloads/enabled.pem
```

### 3.2 连接命令
```bash
# 连接到 Azure 虚拟机
ssh -i ~/Downloads/enabled.pem ubuntu@4.213.48.226
```

### 3.3 连接注意事项
- 首次连接会提示确认服务器指纹，输入 `yes` 确认
- 如果连接失败，检查：
  - 密钥文件路径和权限
  - 虚拟机IP地址是否正确
  - 网络安全组是否开放22端口

## 4. 服务器环境配置

### 4.1 更新系统
```bash
# 更新系统包
sudo apt update
sudo apt upgrade -y

# 安装基础工具
sudo apt install -y curl wget git unzip
```

### 4.2 安装 Docker
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
docker-compose --version
```

### 4.3 安装 Docker Compose
```bash
# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker-compose --version
```

### 4.4 安装 Nginx
```bash
# 安装 Nginx
sudo apt install nginx -y

# 启动并设置开机自启
sudo systemctl start nginx
sudo systemctl enable nginx

# 验证安装
nginx -v
```

### 4.5 创建应用目录
```bash
# 创建应用目录结构
sudo mkdir -p /opt/yuoj-app
sudo chown ubuntu:ubuntu /opt/yuoj-app
cd /opt/yuoj-app

# 创建子目录
mkdir -p backend frontend nginx-config mysql-init
```

## 5. 本地构建

### 5.1 后端构建

#### 5.1.1 构建微服务
```bash
# 进入后端目录
cd yuoj-backend-microservice-master

# 构建所有微服务
mvn clean package -DskipTests

# 验证构建结果
ls -la yuoj-backend-*/target/*.jar
```

#### 5.1.2 创建后端 Dockerfile
```dockerfile
# 创建 backend/Dockerfile
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

#### 5.1.3 创建 Docker Compose 配置
```yaml
# 创建 backend/docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8
    container_name: yuoj-mysql
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: yuoj
    ports:
      - "3306:3306"
    volumes:
      - ./mysql-data:/var/lib/mysql
      - ./mysql-init:/docker-entrypoint-initdb.d
    networks:
      - yuoj-network

  redis:
    image: redis:6
    container_name: yuoj-redis
    ports:
      - "6379:6379"
    volumes:
      - ./redis-data:/data
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
      - ./rabbitmq-data:/var/lib/rabbitmq
    networks:
      - yuoj-network

  nacos:
    image: nacos/nacos-server:v2.2.0-slim
    container_name: yuoj-nacos
    ports:
      - "8848:8848"
    volumes:
      - ./nacos-data:/home/nacos/data
    environment:
      - MODE=standalone
      - PREFER_HOST_MODE=hostname
      - TZ=Asia/Shanghai
    networks:
      - yuoj-network

  backend:
    build: .
    container_name: yuoj-backend
    ports:
      - "8101:8101"
      - "8102:8102"
      - "8103:8103"
      - "8104:8104"
    depends_on:
      - mysql
      - redis
      - rabbitmq
      - nacos
    networks:
      - yuoj-network

networks:
  yuoj-network:
    driver: bridge
```

### 5.2 前端构建

#### 5.2.1 构建前端
```bash
# 进入前端目录
cd yuoj-frontend-master

# 安装依赖
npm install

# 构建生产版本
npm run build

# 验证构建结果
ls -la dist/
```

#### 5.2.2 创建前端 Dockerfile
```dockerfile
# 创建 frontend/Dockerfile
FROM nginx:alpine

# 复制前端文件
COPY dist/ /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

## 6. 文件传输

### 6.1 使用 SCP 传输文件
```bash
# 传输后端文件
scp -i ~/Downloads/enabled.pem -r backend/ ubuntu@4.213.48.226:/opt/yuoj-app/

# 传输前端文件
scp -i ~/Downloads/enabled.pem -r frontend/ ubuntu@4.213.48.226:/opt/yuoj-app/

# 传输 Nginx 配置
scp -i ~/Downloads/enabled.pem nginx-config/nginx.conf ubuntu@4.213.48.226:/opt/yuoj-app/nginx-config/
```

### 6.2 使用 rsync 传输文件（推荐）
```bash
# 传输后端文件
rsync -avz -e "ssh -i ~/Downloads/enabled.pem" backend/ ubuntu@4.213.48.226:/opt/yuoj-app/backend/

# 传输前端文件
rsync -avz -e "ssh -i ~/Downloads/enabled.pem" frontend/ ubuntu@4.213.48.226:/opt/yuoj-app/frontend/

# 传输 Nginx 配置
rsync -avz -e "ssh -i ~/Downloads/enabled.pem" nginx-config/ ubuntu@4.213.48.226:/opt/yuoj-app/nginx-config/
```

### 6.3 验证文件传输
```bash
# 连接到服务器
ssh -i ~/Downloads/enabled.pem ubuntu@4.213.48.226

# 检查文件是否正确传输
ls -la /opt/yuoj-app/
tree /opt/yuoj-app/
```

## 7. 后端部署

### 7.1 数据库初始化
```bash
# 进入应用目录
cd /opt/yuoj-app/backend

# 创建数据库初始化脚本
cat > mysql-init/init.sql << 'EOF'
-- 创建数据库
CREATE DATABASE IF NOT EXISTS yuoj DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yuoj;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    userAccount VARCHAR(256) NOT NULL COMMENT '账号',
    userPassword VARCHAR(512) NOT NULL COMMENT '密码',
    userName VARCHAR(256) COMMENT '用户昵称',
    userAvatar VARCHAR(1024) COMMENT '用户头像',
    userProfile VARCHAR(512) COMMENT '用户简介',
    userRole VARCHAR(256) DEFAULT 'user' NOT NULL COMMENT '用户角色',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除'
);

-- 题目表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    title VARCHAR(512) COMMENT '标题',
    content TEXT COMMENT '内容',
    tags VARCHAR(1024) COMMENT '标签列表（json 数组）',
    answer TEXT COMMENT '题目答案',
    submitNum INT DEFAULT 0 NOT NULL COMMENT '题目提交数',
    acceptedNum INT DEFAULT 0 NOT NULL COMMENT '题目通过数',
    judgeCase TEXT COMMENT '判题用例（json 数组）',
    judgeConfig TEXT COMMENT '判题配置（json 对象）',
    thumbNum INT DEFAULT 0 NOT NULL COMMENT '点赞数',
    favourNum INT DEFAULT 0 NOT NULL COMMENT '收藏数',
    userId BIGINT NOT NULL COMMENT '创建用户 id',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除'
);

-- 题目提交表
CREATE TABLE IF NOT EXISTS question_submit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    language VARCHAR(128) NOT NULL COMMENT '编程语言',
    code TEXT NOT NULL COMMENT '用户代码',
    judgeInfo TEXT COMMENT '判题信息（json 对象）',
    status INT DEFAULT 0 NOT NULL COMMENT '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    questionId BIGINT NOT NULL COMMENT '题目 id',
    userId BIGINT NOT NULL COMMENT '创建用户 id',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除'
);
EOF
```

### 7.2 启动后端服务
```bash
# 进入后端目录
cd /opt/yuoj-app/backend

# 启动基础设施服务
docker-compose up -d mysql redis rabbitmq nacos

# 等待服务启动完成
sleep 30

# 启动后端应用
docker-compose up -d backend

# 查看服务状态
docker-compose ps
docker logs yuoj-backend
```

### 7.3 验证后端服务
```bash
# 检查服务状态
docker ps

# 测试 API 连接
curl -X GET http://localhost:8101/api/health
curl -X GET http://localhost:8102/api/health
curl -X GET http://localhost:8103/api/health
curl -X GET http://localhost:8104/api/health
```

## 8. 前端部署

### 8.1 构建前端镜像
```bash
# 进入前端目录
cd /opt/yuoj-app/frontend

# 构建前端镜像
docker build -t yuoj-frontend .

# 运行前端容器
docker run -d --name yuoj-frontend -p 8080:80 yuoj-frontend

# 查看容器状态
docker ps
docker logs yuoj-frontend
```

### 8.2 验证前端服务
```bash
# 测试前端访问
curl -I http://localhost:8080

# 检查前端文件
docker exec yuoj-frontend ls -la /usr/share/nginx/html/
```

## 9. Nginx 配置

### 9.1 创建 Nginx 配置文件
```bash
# 创建 Nginx 配置文件
cat > /opt/yuoj-app/nginx-config/nginx.conf << 'EOF'
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
            root /opt/yuoj-app/frontend/dist;
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
EOF
```

### 9.2 配置 Nginx
```bash
# 备份原配置
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup

# 复制新配置
sudo cp /opt/yuoj-app/nginx-config/nginx.conf /etc/nginx/nginx.conf

# 测试配置
sudo nginx -t

# 重新加载配置
sudo systemctl reload nginx

# 检查 Nginx 状态
sudo systemctl status nginx
```

### 9.3 验证 Nginx 配置
```bash
# 测试 Nginx 访问
curl -I http://localhost

# 测试 API 代理
curl -X GET http://localhost/api/health

# 检查 Nginx 日志
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

## 10. 应用管理

### 10.1 启动应用
```bash
# 启动所有服务
cd /opt/yuoj-app/backend
docker-compose up -d

cd /opt/yuoj-app/frontend
docker start yuoj-frontend

# 检查所有服务状态
docker ps
docker-compose ps
```

### 10.2 停止应用
```bash
# 停止后端服务
cd /opt/yuoj-app/backend
docker-compose down

# 停止前端服务
docker stop yuoj-frontend
docker rm yuoj-frontend
```

### 10.3 重启应用
```bash
# 重启后端服务
cd /opt/yuoj-app/backend
docker-compose restart

# 重启前端服务
docker restart yuoj-frontend

# 重启 Nginx
sudo systemctl restart nginx
```

### 10.4 查看日志
```bash
# 查看后端日志
docker-compose logs -f

# 查看前端日志
docker logs -f yuoj-frontend

# 查看 Nginx 日志
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

### 10.5 更新应用
```bash
# 更新后端
cd /opt/yuoj-app/backend
docker-compose down
docker-compose build --no-cache
docker-compose up -d

# 更新前端
cd /opt/yuoj-app/frontend
docker stop yuoj-frontend
docker rm yuoj-frontend
docker build -t yuoj-frontend .
docker run -d --name yuoj-frontend -p 8080:80 yuoj-frontend
```

## 11. 故障排查

### 11.1 常见问题排查

#### 11.1.1 连接问题
```bash
# 检查 SSH 连接
ssh -i ~/Downloads/enabled.pem -v ubuntu@4.213.48.226

# 检查端口开放
sudo netstat -tlnp | grep :22
sudo netstat -tlnp | grep :80
```

#### 11.1.2 Docker 问题
```bash
# 检查 Docker 服务
sudo systemctl status docker

# 检查 Docker 网络
docker network ls
docker network inspect yuoj-network

# 清理 Docker 资源
docker system prune -f
```

#### 11.1.3 服务问题
```bash
# 检查服务状态
docker ps -a
docker-compose ps

# 查看服务日志
docker logs yuoj-backend
docker logs yuoj-frontend
docker logs yuoj-mysql
docker logs yuoj-redis
docker logs yuoj-rabbitmq
docker logs yuoj-nacos
```

#### 11.1.4 Nginx 问题
```bash
# 检查 Nginx 配置
sudo nginx -t

# 检查 Nginx 状态
sudo systemctl status nginx

# 查看 Nginx 错误日志
sudo tail -f /var/log/nginx/error.log
```

### 11.2 性能监控
```bash
# 查看系统资源
htop
df -h
free -h

# 查看 Docker 资源使用
docker stats

# 查看网络连接
netstat -tlnp
```

### 11.3 备份和恢复
```bash
# 备份数据库
docker exec yuoj-mysql mysqldump -u root -p123456 yuoj > backup.sql

# 备份配置文件
tar -czf config-backup.tar.gz /opt/yuoj-app/

# 恢复数据库
docker exec -i yuoj-mysql mysql -u root -p123456 yuoj < backup.sql
```

### 11.4 安全配置
```bash
# 配置防火墙
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# 更新系统
sudo apt update && sudo apt upgrade -y

# 配置 SSH 安全
sudo nano /etc/ssh/sshd_config
# 设置 PermitRootLogin no
# 设置 PasswordAuthentication no
sudo systemctl restart ssh
```

## 12. 部署验证

### 12.1 功能测试
```bash
# 测试前端访问
curl -I http://4.213.48.226

# 测试 API 接口
curl -X GET http://4.213.48.226/api/health

# 测试数据库连接
docker exec yuoj-mysql mysql -u root -p123456 -e "SHOW DATABASES;"
```

### 12.2 性能测试
```bash
# 压力测试
ab -n 1000 -c 10 http://4.213.48.226/

# 监控系统资源
htop
docker stats
```

## 13. 维护指南

### 13.1 日常维护
```bash
# 每日检查
docker ps
docker system df
df -h
free -h

# 清理日志
sudo journalctl --vacuum-time=7d
docker system prune -f
```

### 13.2 监控脚本
```bash
# 创建监控脚本
cat > /opt/yuoj-app/monitor.sh << 'EOF'
#!/bin/bash
echo "=== System Status ==="
date
echo "Memory:"
free -h
echo "Disk:"
df -h
echo "Docker:"
docker ps
echo "Services:"
docker-compose ps
EOF

chmod +x /opt/yuoj-app/monitor.sh
```

### 13.3 自动化部署
```bash
# 创建部署脚本
cat > /opt/yuoj-app/deploy.sh << 'EOF'
#!/bin/bash
set -e

echo "Starting deployment..."

# 停止服务
docker-compose down

# 更新代码
git pull

# 重新构建
docker-compose build --no-cache

# 启动服务
docker-compose up -d

echo "Deployment completed!"
EOF

chmod +x /opt/yuoj-app/deploy.sh
```

---

## 注意事项

1. **安全配置**: 确保 SSH 密钥文件权限正确 (400)
2. **网络配置**: 确保 Azure 网络安全组开放必要端口
3. **资源监控**: 定期检查系统资源使用情况
4. **备份策略**: 定期备份数据库和配置文件
5. **日志管理**: 定期清理日志文件避免磁盘空间不足
6. **更新维护**: 定期更新系统和 Docker 镜像

## 联系信息

如有问题，请检查：
- 系统日志: `/var/log/`
- Docker 日志: `docker logs <container_name>`
- Nginx 日志: `/var/log/nginx/`
- 应用日志: `/opt/yuoj-app/logs/` 