# Database Initialization Script Documentation

This directory contains database initialization scripts for the online judge system.

## File Description

### 1. `create_table.sql` (Original Version)
- Basic database table structure creation script
- Contains all necessary tables and indexes
- Does not include initial data

### 2. `init.sql` (Complete Version)
- Complete database initialization script
- Contains database creation, table structure, indexes, initial data
- Includes statistical views and permission settings
- Suitable for production environment use

### 3. `init-docker.sql` (Docker Version)
- Initialization script optimized specifically for Docker environment
- Contains basic table structure and necessary initial data
- Suitable for containerized deployment

## Database Structure

### Core Tables

1. **user** - User table
   - Stores basic user information
   - Supports multiple login methods (account password, WeChat, etc.)
   - Includes user role management

2. **question** - Question table
   - Stores programming question information
   - Contains question content, answers, judge configuration
   - Supports tag classification

3. **question_submit** - Question submission table
   - Records user code submissions
   - Contains judge status and results
   - Supports multiple programming languages

4. **post** - Post table
   - Community communication functionality
   - Supports likes and favorites

5. **post_thumb** - Post like table
   - Records user likes for posts

6. **post_favour** - Post favorite table
   - Records user favorites for posts

### Index Design

- Primary key indexes: id fields for all tables
- Foreign key indexes: userId, questionId and other related fields
- Business indexes: userAccount, title, status and other commonly queried fields
- Unique indexes: (postId, userId) combination for post_thumb and post_favour

## Usage Methods

### Method 1: Using Docker Compose (Recommended)

```bash
# Execute in project root directory
docker-compose up -d

# Database will be initialized automatically
```

### Method 2: Manual SQL Script Execution

```bash
# Connect to MySQL
mysql -u root -p

# Execute initialization script
source /path/to/init-docker.sql;
```

### Method 3: Using MySQL Command Line

```bash
# Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS yuoj CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Execute initialization script
mysql -u root -p yuoj < init-docker.sql
```

## Initial Data

### Default Users
- **Administrator account**: admin
- **Administrator password**: 12345678
- **Test account**: test
- **Test password**: 12345678

### Sample Questions
- **Two Sum**: Classic array hash table question
- Contains complete question description, examples and answers
- Configured with judge cases and limits

### Sample Posts
- System introduction and welcome information
- Programming tips sharing

## Important Notes

1. **Character Set**: Uses utf8mb4 to support complete Unicode characters
2. **Collation**: utf8mb4_unicode_ci provides good sorting performance
3. **Logical Deletion**: All tables support soft deletion (isDelete field)
4. **Timestamps**: Automatically maintains createTime and updateTime
5. **Index Optimization**: Indexes optimized for common query scenarios

## Database Configuration

### Connection Information
- **Database name**: yuoj
- **Character set**: utf8mb4
- **Collation**: utf8mb4_unicode_ci

### Application Configuration
Configure in Spring Boot application's `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yuoj?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## Backup and Recovery

### Backup Database
```bash
mysqldump -u root -p yuoj > yuoj_backup.sql
```

### Restore Database
```bash
mysql -u root -p yuoj < yuoj_backup.sql
```

## Version History

- **v1.0**: Basic table structure
- **v1.1**: Added index optimization
- **v1.2**: Added initial data
- **v1.3**: Added statistical views
- **v2.0**: Docker environment optimization 