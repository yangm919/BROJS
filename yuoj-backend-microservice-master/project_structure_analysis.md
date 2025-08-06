# YUOJ项目前后端结构分析

## 项目架构概览

### 前端架构 (Vue.js)
```
yuoj-frontend-master/
├── src/
│   ├── components/          # Vue组件
│   ├── views/              # 页面视图
│   │   ├── question/       # 题目相关页面
│   │   └── user/          # 用户相关页面
│   ├── router/            # 路由配置
│   ├── store/             # Vuex状态管理
│   └── plugins/           # 插件配置
├── public/                # 静态资源
└── package.json           # 依赖配置
```

### 后端微服务架构 (Spring Cloud)
```
yuoj-backend-microservice-master/
├── yuoj-backend-gateway/          # API网关 (端口: 8101)
├── yuoj-backend-user-service/     # 用户服务 (端口: 8102)
├── yuoj-backend-question-service/ # 题目服务 (端口: 8103)
├── yuoj-backend-judge-service/    # 判题服务 (端口: 8104)
├── yuoj-backend-model/            # 数据模型
├── yuoj-backend-common/           # 公共组件
└── yuoj-backend-service-client/   # 服务客户端
```

### 基础设施服务
```
Docker Services:
├── yuoj-mysql      # MySQL数据库 (端口: 3306)
├── yuoj-redis      # Redis缓存 (端口: 6379)
├── yuoj-rabbitmq   # RabbitMQ消息队列 (端口: 5672, 15672)
└── yuoj-nacos      # Nacos服务发现 (端口: 8848)
```

## 数据模型分析

### Question实体结构
```java
public class Question {
    private Long id;           // 题目ID
    private String title;      // 题目标题
    private String content;    // 题目内容
    private String tags;       // 标签列表 (JSON格式)
    private String answer;     // 题目答案 (代码模板)
    private Integer submitNum; // 提交次数
    private Integer acceptedNum; // 通过次数
    private String judgeCase;  // 判题用例 (JSON格式)
    private String judgeConfig; // 判题配置 (JSON格式)
    private Integer thumbNum;  // 点赞数
    private Integer favourNum; // 收藏数
    private Long userId;       // 创建用户ID
    private Date createTime;   // 创建时间
    private Date updateTime;   // 更新时间
    private Integer isDelete;  // 是否删除
}
```

### 数据格式规范

#### 输入格式
- **数组格式**: `[1,2,3,4,5]`
- **字符串格式**: `"hello world"`
- **多行输入**: 每行一个测试用例

#### 输出格式
- **整数**: `123`
- **字符串**: `"result"`
- **数组**: `[1,2,3]`

#### 判题用例格式
```json
[
  {"input": "[-2,1,-3,4,-1,2,1,-5,4]", "output": "6"},
  {"input": "[1]", "output": "1"},
  {"input": "[5,4,-1,7,8]", "output": "23"}
]
```

#### 判题配置格式
```json
{
  "timeLimit": 1000,      // 时间限制 (毫秒)
  "memoryLimit": 256000,  // 内存限制 (KB)
  "stackLimit": 1000      // 栈限制 (KB)
}
```

## API接口分析

### 题目相关接口
- `POST /api/question/list/page` - 获取题目列表
- `GET /api/question/get` - 获取题目详情
- `POST /api/question/add` - 添加题目
- `POST /api/question/update` - 更新题目
- `POST /api/question/delete` - 删除题目

### 提交相关接口
- `POST /api/question_submit/do` - 提交代码
- `POST /api/question_submit/list/page` - 获取提交记录

### 用户相关接口
- `POST /api/user/login` - 用户登录
- `POST /api/user/register` - 用户注册
- `GET /api/user/get/login` - 获取当前登录用户

## 判题系统分析

### 判题流程
1. **代码提交** → Question Service
2. **任务分发** → RabbitMQ
3. **代码执行** → Judge Service
4. **结果返回** → Question Service
5. **状态更新** → 数据库

### 支持的编程语言
- Java (主要支持)
- 可扩展支持其他语言

### 安全机制
- 代码沙箱隔离
- 资源限制 (时间、内存、栈)
- 文件系统限制

## 部署架构

### 本地开发环境
```
前端: http://localhost:8080
网关: http://localhost:8101
用户服务: http://localhost:8102
题目服务: http://localhost:8103
判题服务: http://localhost:8104
```

### 生产环境
```
Nginx反向代理 → Vue前端
Nginx反向代理 → Spring Cloud Gateway
微服务集群 → 各业务服务
```

## 技术栈总结

### 前端技术栈
- **框架**: Vue 3 + TypeScript
- **路由**: Vue Router 4
- **状态管理**: Vuex 4
- **UI组件**: Arco Design Vue
- **代码编辑器**: Monaco Editor
- **构建工具**: Vue CLI

### 后端技术栈
- **框架**: Spring Boot + Spring Cloud
- **网关**: Spring Cloud Gateway
- **服务发现**: Nacos
- **消息队列**: RabbitMQ
- **数据库**: MySQL 8
- **缓存**: Redis 6
- **ORM**: MyBatis Plus

### 基础设施
- **容器化**: Docker + Docker Compose
- **反向代理**: Nginx
- **代码沙箱**: 自定义安全沙箱

## 扩展建议

### 功能扩展
1. **多语言支持**: 添加Python、C++、JavaScript等语言支持
2. **在线IDE**: 集成更完整的在线开发环境
3. **竞赛系统**: 支持编程竞赛和排行榜
4. **题目分类**: 按难度、类型、标签分类管理

### 性能优化
1. **缓存策略**: Redis缓存热点数据
2. **负载均衡**: 多实例部署
3. **数据库优化**: 索引优化、读写分离
4. **CDN加速**: 静态资源CDN分发

### 安全加固
1. **代码沙箱**: 更严格的执行环境隔离
2. **API限流**: 防止恶意提交
3. **用户权限**: 细粒度权限控制
4. **审计日志**: 完整的操作日志记录 