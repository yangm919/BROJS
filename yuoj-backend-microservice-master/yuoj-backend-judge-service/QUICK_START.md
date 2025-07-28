# Judge0 API 快速启动指南

## 🚀 快速开始

### 1. 配置已完成 ✅

API密钥已经配置完成：
- **API密钥**: `087d919e2dmshf5f3c038d699943p1fb696jsnf1a52bbb84b2`
- **配置位置**: `application.yml`
- **状态**: 已就绪

### 2. 运行测试

#### 方法一：运行单元测试
```bash
cd yuoj-backend-judge-service
mvn test -Dtest=Judge0ApiKeyTest
```

#### 方法二：运行特定测试方法
```bash
# 测试API密钥配置
mvn test -Dtest=Judge0ApiKeyTest#testApiKeyConfiguration

# 测试Python代码执行
mvn test -Dtest=Judge0ApiKeyTest#testSimpleCodeExecution

# 测试Java代码执行
mvn test -Dtest=Judge0ApiKeyTest#testJavaCodeExecution

# 测试JavaScript代码执行
mvn test -Dtest=Judge0ApiKeyTest#testJavaScriptCodeExecution
```

### 3. 启动服务

```bash
# 启动Judge服务
mvn spring-boot:run
```

### 4. 使用示例

#### 基本使用
```java
@Autowired
private ThirdPartyCodeSandbox codeSandbox;

// Python代码执行
ExecuteCodeRequest request = ExecuteCodeRequest.builder()
    .code("print('Hello World')")
    .language("python")
    .inputList(Arrays.asList(""))
    .build();

ExecuteCodeResponse response = codeSandbox.executeCode(request);
```

#### 运行使用示例
```java
@Autowired
private Judge0UsageExample usageExample;

// 运行所有示例
usageExample.runAllExamples();
```

## 📋 支持的语言

| 语言 | 示例代码 |
|------|----------|
| Python | `print("Hello World")` |
| Java | `System.out.println("Hello World");` |
| JavaScript | `console.log("Hello World");` |
| C++ | `cout << "Hello World" << endl;` |
| C | `printf("Hello World\n");` |

## 🔧 故障排除

### 常见问题

1. **API密钥无效**
   - 检查网络连接
   - 确认RapidAPI订阅状态

2. **代码执行超时**
   - 检查代码复杂度
   - 调整超时设置

3. **编译错误**
   - 检查代码语法
   - 确认语言支持

### 调试模式

启用详细日志：
```yaml
logging:
  level:
    com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl: DEBUG
```

## 📊 测试结果

运行测试后，你应该看到类似以下的输出：

```
✅ API密钥配置正确: 087d919e2...
✅ 代码执行成功！
✅ Java代码执行成功！
✅ JavaScript代码执行成功！
✅ 错误处理正常！
```

## 🎯 下一步

1. 集成到你的判题系统中
2. 添加更多语言支持
3. 优化性能和错误处理
4. 添加缓存机制

## 📞 支持

如果遇到问题，请检查：
1. 网络连接
2. API密钥有效性
3. 代码语法
4. 日志输出 