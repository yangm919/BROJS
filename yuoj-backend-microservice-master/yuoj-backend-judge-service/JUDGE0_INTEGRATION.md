# Judge0 API 集成说明

## 概述

本项目已集成Judge0 API作为第三方代码沙箱，用于在线代码执行和判题。

## 功能特性

- 支持多种编程语言（Java、Python、C++、JavaScript等）
- 实时代码执行和结果获取
- 详细的执行信息（内存使用、执行时间、错误信息等）
- 可配置的执行限制（时间限制、内存限制）
- 安全的代码执行环境

## 配置说明

### 1. 获取RapidAPI密钥

1. 访问 [RapidAPI Judge0 CE](https://rapidapi.com/judge0-official/api/judge0-ce/)
2. 注册并订阅Judge0 CE API
3. 获取你的API密钥

### 2. 配置文件设置

在 `application.yml` 中配置Judge0 API参数：

```yaml
codesandbox:
  judge0:
    base-url: https://judge0-ce.p.rapidapi.com
    rapidapi-key: 087d919e2dmshf5f3c038d699943p1fb696jsnf1a52bbb84b2  # 已配置实际API密钥
    rapidapi-host: judge0-ce.p.rapidapi.com
    timeout: 30
    max-retries: 30
```

**注意**: API密钥已经配置完成，可以直接使用。

### 3. 环境变量配置（推荐）

为了安全起见，建议使用环境变量来配置API密钥：

```bash
export JUDGE0_RAPIDAPI_KEY=your_actual_api_key
```

然后在配置文件中使用：

```yaml
codesandbox:
  judge0:
    rapidapi-key: ${JUDGE0_RAPIDAPI_KEY}
```

## 支持的编程语言

| 语言 | Judge0 ID | 说明 |
|------|-----------|------|
| Java | 62 | OpenJDK 13.0.1 |
| Python | 71 | Python 3.8.1 |
| C++ | 54 | GCC 9.2.0 |
| C | 50 | GCC 9.2.0 |
| JavaScript | 63 | Node.js 12.14.0 |
| Go | 60 | Go 1.13.5 |
| Rust | 73 | Rust 1.40.0 |
| PHP | 68 | PHP 7.4.1 |
| Ruby | 72 | Ruby 2.7.0 |
| Swift | 83 | Swift 5.2.3 |
| Kotlin | 78 | Kotlin 1.3.70 |
| Scala | 81 | Scala 2.13.2 |
| TypeScript | 74 | TypeScript 3.7.4 |

## 使用示例

### 代码执行流程

1. **提交代码**：将用户代码提交到Judge0 API
2. **轮询结果**：定期查询执行结果直到完成
3. **处理结果**：解析执行结果并返回给用户

### 执行状态说明

- **状态ID 4**：Accepted（执行成功）
- **状态ID 5**：Wrong Answer（答案错误）
- **状态ID 6**：Compilation Error（编译错误）
- **状态ID 7**：Runtime Error（运行时错误）
- **状态ID 8**：Time Limit Exceeded（时间超限）
- **状态ID 9**：Memory Limit Exceeded（内存超限）

## 安全考虑

1. **网络访问限制**：默认禁用网络访问
2. **执行时间限制**：默认5秒时间限制
3. **内存限制**：默认512MB内存限制
4. **API密钥保护**：使用环境变量存储敏感信息

## 错误处理

系统会处理以下类型的错误：

- API调用失败
- 网络连接超时
- 代码执行超时
- 内存不足
- 编译错误
- 运行时错误

## 性能优化

1. **连接池**：使用WebClient的连接池管理
2. **超时设置**：合理的超时时间避免长时间等待
3. **重试机制**：失败时自动重试
4. **异步处理**：使用响应式编程提高性能

## 监控和日志

系统提供详细的日志记录：

- 代码提交日志
- 执行状态变化
- 错误信息记录
- 性能指标统计

## 故障排除

### 常见问题

1. **API密钥无效**
   - 检查RapidAPI密钥是否正确
   - 确认API订阅是否有效

2. **网络连接失败**
   - 检查网络连接
   - 确认防火墙设置

3. **代码执行超时**
   - 检查代码复杂度
   - 调整超时设置

4. **内存不足**
   - 检查代码内存使用
   - 调整内存限制

### 调试模式

启用调试日志：

```yaml
logging:
  level:
    com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl: DEBUG
```

## 更新和维护

- 定期检查Judge0 API的更新
- 监控API使用量和费用
- 及时更新支持的语言版本
- 优化性能和稳定性 