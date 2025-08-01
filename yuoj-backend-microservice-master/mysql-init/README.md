# 数据库初始化脚本说明

本目录包含在线判题系统的数据库初始化脚本。

## 文件说明

### 1. `create_table.sql` (原始版本)
- 基础的数据库表结构创建脚本
- 包含所有必要的表和索引
- 不包含初始数据

### 2. `init.sql` (完整版本)
- 完整的数据库初始化脚本
- 包含数据库创建、表结构、索引、初始数据
- 包含统计视图和权限设置
- 适合生产环境使用

### 3. `init-docker.sql` (Docker版本)
- 专门为Docker环境优化的初始化脚本
- 包含基本的表结构和必要的初始数据
- 适合容器化部署

## 数据库结构

### 核心表

1. **user** - 用户表
   - 存储用户基本信息
   - 支持多种登录方式（账号密码、微信等）
   - 包含用户角色管理

2. **question** - 题目表
   - 存储编程题目信息
   - 包含题目内容、答案、判题配置
   - 支持标签分类

3. **question_submit** - 题目提交表
   - 记录用户的代码提交
   - 包含判题状态和结果
   - 支持多种编程语言

4. **post** - 帖子表
   - 社区交流功能
   - 支持点赞和收藏

5. **post_thumb** - 帖子点赞表
   - 记录用户对帖子的点赞

6. **post_favour** - 帖子收藏表
   - 记录用户对帖子的收藏

### 索引设计

- 主键索引：所有表的id字段
- 外键索引：userId、questionId等关联字段
- 业务索引：userAccount、title、status等常用查询字段
- 唯一索引：post_thumb和post_favour的(postId, userId)组合

## 使用方法

### 方法1：使用Docker Compose（推荐）

```bash
# 在项目根目录执行
docker-compose up -d

# 数据库会自动初始化
```

### 方法2：手动执行SQL脚本

```bash
# 连接到MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/init-docker.sql;
```

### 方法3：使用MySQL命令行

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS yuoj CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行初始化脚本
mysql -u root -p yuoj < init-docker.sql
```

## 初始数据

### 默认用户
- **管理员账号**: admin
- **管理员密码**: 12345678
- **测试账号**: test
- **测试密码**: 12345678

### 示例题目
- **两数之和**: 经典的数组哈希表题目
- 包含完整的题目描述、示例和答案
- 配置了判题用例和限制

### 示例帖子
- 系统介绍和欢迎信息
- 编程技巧分享

## 注意事项

1. **字符集**: 使用utf8mb4支持完整的Unicode字符
2. **排序规则**: utf8mb4_unicode_ci提供良好的排序性能
3. **逻辑删除**: 所有表都支持软删除（isDelete字段）
4. **时间戳**: 自动维护createTime和updateTime
5. **索引优化**: 针对常用查询场景优化了索引

## 数据库配置

### 连接信息
- **数据库名**: yuoj
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci

### 应用配置
在Spring Boot应用的`application.yml`中配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yuoj?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 备份和恢复

### 备份数据库
```bash
mysqldump -u root -p yuoj > yuoj_backup.sql
```

### 恢复数据库
```bash
mysql -u root -p yuoj < yuoj_backup.sql
```

## 版本历史

- **v1.0**: 基础表结构
- **v1.1**: 添加索引优化
- **v1.2**: 添加初始数据
- **v1.3**: 添加统计视图
- **v2.0**: Docker环境优化 