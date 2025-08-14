# Judge0 判题功能使用指南

## 🎯 概述

本项目已成功集成Judge0 API实现完整的判题功能，支持多种编程语言的在线代码执行和自动判题。

## 🚀 核心功能

### 1. 多语言支持
- **Python** - 支持Python 3.8.1
- **Java** - 支持OpenJDK 13.0.1
- **C++** - 支持GCC 9.2.0
- **JavaScript** - 支持Node.js 12.14.0
- **C** - 支持GCC 9.2.0
- **Go, Rust, PHP, Ruby, Swift, Kotlin, Scala, TypeScript** - 完整支持

### 2. 判题功能
- ✅ 代码执行和结果获取
- ✅ 多测试用例支持
- ✅ 答案正确性判断
- ✅ 编译错误检测
- ✅ 运行时错误处理
- ✅ 时间超限检测
- ✅ 内存超限检测
- ✅ 详细的执行信息统计

### 3. 安全特性
- 🔒 网络访问限制
- ⏱️ 执行时间限制（5秒）
- 💾 内存使用限制（512MB）
- 🛡️ 安全的代码执行环境

## 📋 使用方法

### 1. 基本判题流程

```java
@Autowired
private Judge0JudgeService judge0JudgeService;

// 执行判题
QuestionSubmit result = judge0JudgeService.doJudgeWithJudge0(questionSubmitId);
```

### 2. API接口调用

#### 单个判题
```bash
POST /api/judge0/do?questionSubmitId=123
```

#### 批量判题
```bash
POST /api/judge0/batch
Content-Type: application/json

[123, 124, 125]
```

#### 健康检查
```bash
GET /api/judge0/health
```

### 3. 代码示例

#### Python判题示例
```python
# 题目：计算两个数的和
a, b = map(int, input().split())
print(a + b)
```

#### Java判题示例
```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        System.out.println(a + b);
    }
}
```

#### JavaScript判题示例
```javascript
const readline = require('readline');
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

rl.on('line', (line) => {
    const [a, b] = line.split(' ').map(Number);
    console.log(a + b);
    rl.close();
});
```

## 🔧 配置说明

### 1. 配置文件
```yaml
codesandbox:
  type: judge0  # 使用Judge0作为代码沙箱
  judge0:
    base-url: https://judge0-ce.p.rapidapi.com
    rapidapi-key: 087d919e2dmshf5f3c038d699943p1fb696jsnf1a52bbb84b2
    rapidapi-host: judge0-ce.p.rapidapi.com
    timeout: 30
    max-retries: 30
```

### 2. 环境变量（推荐）
```bash
export JUDGE0_RAPIDAPI_KEY=your_api_key
```

## 📊 判题结果说明

### 状态码
| 状态码 | 含义 | 说明 |
|--------|------|------|
| 0 | 成功 | 代码执行成功，答案正确 |
| 1 | 答案错误 | 输出与期望不符 |
| 2 | 编译错误 | 代码编译失败 |
| 3 | 运行时错误 | 代码运行时出错 |
| 4 | 时间超限 | 执行时间超过限制 |
| 5 | 内存超限 | 内存使用超过限制 |
| 6 | 系统错误 | 系统内部错误 |

### 判题信息
```json
{
  "message": "答案正确",
  "memory": 1024,
  "time": 100
}
```

## 🧪 测试和验证

### 1. 运行单元测试
```bash
# 运行所有测试
mvn test

# 运行Judge0相关测试
mvn test -Dtest=Judge0ApiKeyTest
mvn test -Dtest=Judge0JudgeServiceTest
```

### 2. 运行示例
```java
@Autowired
private Judge0JudgeExample judgeExample;

// 运行所有示例
judgeExample.runAllExamples();
```

### 3. 测试用例
- ✅ API密钥验证
- ✅ 代码执行测试
- ✅ 多语言支持测试
- ✅ 错误处理测试
- ✅ 性能测试

## 🔍 故障排除

### 常见问题

1. **API密钥无效**
   ```
   错误：401 Unauthorized
   解决：检查RapidAPI密钥是否正确
   ```

2. **网络连接失败**
   ```
   错误：Connection timeout
   解决：检查网络连接和防火墙设置
   ```

3. **代码执行超时**
   ```
   错误：Time limit exceeded
   解决：优化代码性能，减少执行时间
   ```

4. **内存不足**
   ```
   错误：Memory limit exceeded
   解决：优化代码内存使用
   ```

### 调试模式
```yaml
logging:
  level:
    com.yupi.yuojbackendjudgeservice.judge: DEBUG
    com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl: DEBUG
```

## 📈 性能优化

### 1. 连接池管理
- 使用WebClient连接池
- 合理的超时设置
- 自动重试机制

### 2. 异步处理
- 支持异步代码执行
- 非阻塞I/O操作
- 响应式编程

### 3. 缓存策略
- 可扩展的缓存机制
- 结果缓存支持
- 性能监控

## 🔄 集成指南

### 1. 现有系统集成
```java
// 替换原有的判题服务
@Autowired
private Judge0JudgeService judge0JudgeService;

// 在原有判题流程中调用
QuestionSubmit result = judge0JudgeService.doJudgeWithJudge0(questionSubmitId);
```

### 2. 消息队列集成
```java
// 支持RabbitMQ消息队列
@RabbitListener(queues = "judge_queue")
public void handleJudgeMessage(QuestionSubmit questionSubmit) {
    judge0JudgeService.doJudgeWithJudge0(questionSubmit.getId());
}
```

### 3. 前端集成
```javascript
// 前端调用判题API
async function submitCode(questionId, code, language) {
    const response = await fetch('/api/judge0/do', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            questionSubmitId: questionId,
            code: code,
            language: language
        })
    });
    return await response.json();
}
```

## 📚 扩展功能

### 1. 新语言支持
在`Judge0LanguageMapper`中添加新语言：
```java
LANGUAGE_MAP.put("new_language", languageId);
```

### 2. 自定义判题策略
继承`Judge0JudgeService`并重写判题逻辑：
```java
public class CustomJudgeService extends Judge0JudgeService {
    @Override
    public JudgeInfo judgeCode(ExecuteCodeResponse response, Question question) {
        // 自定义判题逻辑
    }
}
```

### 3. 性能监控
添加性能监控指标：
```java
// 记录执行时间
long startTime = System.currentTimeMillis();
// ... 执行代码
long endTime = System.currentTimeMillis();
log.info("执行时间: {}ms", endTime - startTime);
```

## 🎉 总结

Judge0判题功能已完全集成到系统中，提供了：

- ✅ 完整的判题功能
- ✅ 多语言支持
- ✅ 安全的执行环境
- ✅ 详细的错误处理
- ✅ 性能监控
- ✅ 易于扩展的架构

现在你可以使用这个强大的判题系统来处理各种编程题目的自动判题需求！ 