# encrypt-spring-boot-starter

一个无侵入的 Spring Boot Starter，可快速为 API 接口添加加解密功能。

## 功能特性

- **请求解密**: 自动解密客户端加密的请求参数
- **响应加密**: 自动加密服务端返回的响应数据
- **环境判断**: 支持 dev 环境跳过加解密
- **灵活配置**: 支持开启/关闭、自定义公私钥
- **可扩展**: 提供加密接口，用户可自定义实现

## 快速开始

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.z.ai</groupId>
    <artifactId>encrypt-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 application.yml

```yaml
encrypt:
  enabled: true                    # 是否启用加解密，默认 true
  app-public-key: your-public-key  # 公钥（用于解密请求）
  private-key: your-private-key    # 私钥（用于加密响应）
  profile: dev                     # 当前环境，默认 dev
  dev-skip: true                   # dev 环境是否跳过加解密，默认 true
```

### 3. 使用注解

在需要加解密的接口方法上添加 `@ApiDecrypt` 注解：

```java
import com.z.ai.encrypt.annotation.ApiDecrypt;

@ApiDecrypt(in = true, out = true)
@PostMapping("/test")
public Result test(@RequestBody RequestDTO dto) {
    return Result.ok(dto);
}
```

注解说明：
- `in`: 是否解密请求体（默认 false）
- `out`: 是否加密响应体（默认 false）

## 配置说明

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `encrypt.enabled` | boolean | true | 是否启用加解密功能 |
| `encrypt.app-public-key` | String | - | RSA 公钥（用于解密请求） |
| `encrypt.private-key` | String | - | RSA 私钥（用于加密响应） |
| `encrypt.profile` | String | dev | 当前环境标识 |
| `encrypt.dev-skip` | boolean | true | dev 环境是否跳过加解密 |

## 高级用法

### 自定义加密实现

如果默认的 RSA 加密实现不满足需求，可以实现 `EncryptService` 接口并注入 Bean：

```java
import com.z.ai.encrypt.EncryptService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptConfig {

    @Bean
    public EncryptService encryptService() {
        return new CustomEncryptService();
    }
}

public class CustomEncryptService implements EncryptService {

    @Override
    public JSONObject decrypt(JSONObject jsonObject) {
        // 自定义解密逻辑
        return jsonObject;
    }

    @Override
    public JSONObject encrypt(JSONObject jsonObject) {
        // 自定义加密逻辑
        return jsonObject;
    }

    @Override
    public String encryptString(String content) {
        // 自定义字符串加密逻辑
        return content;
    }
}
```

### 只启用请求解密

```java
@ApiDecrypt(in = true, out = false)
@PostMapping("/test")
public Result test(@RequestBody RequestDTO dto) {
    return Result.ok(dto);
}
```

### 只启用响应加密

```java
@ApiDecrypt(in = false, out = true)
@PostMapping("/test")
public Result test(@RequestBody RequestDTO dto) {
    return Result.ok(dto);
}
```

## 注意事项

1. **公私钥格式**: 公私钥需要是 PEM 格式（带 `-----BEGIN PUBLIC KEY-----` 头尾），Starter 会自动处理换行和空格
2. **RequestBody 类型**: 当前实现要求请求体是 JSON 格式
3. **Order 优先级**: 已设置最高优先级 `@Order(Ordered.HIGHEST_PRECEDENCE)`，确保在其他 Advice 之前执行

## 项目结构

```
encrypt-spring-boot-starter/
├── pom.xml
└── src/main/java/com/z/ai/encrypt/
    ├── EncryptAutoConfiguration.java      # 自动配置类
    ├── EncryptProperties.java             # 配置属性类
    ├── EncryptService.java                # 加密服务接口
    ├── DefaultEncryptService.java         # 默认 RSA 加密实现
    ├── annotation/
    │   └── ApiDecrypt.java                 # 加解密注解
    ├── advice/
    │   ├── EncryptRequestAdvice.java       # 请求解密
    │   └── EncryptResponseAdvice.java      # 响应加密
    └── exception/
        └── EncryptException.java           # 加解密异常
```

## 技术栈

- Spring Boot 2.7.x
- Spring MVC
- FastJSON
- Lombok
- Java 8+

## License

MIT
