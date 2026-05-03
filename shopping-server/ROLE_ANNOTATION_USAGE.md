# 角色校验注解使用说明

## 概述
本项目提供了两个注解用于控制接口的访问权限：
- `@IgnoreAuth` - 忽略认证（白名单）
- `@RequireRole` - 要求特定角色

## 注解说明

### 1. @IgnoreAuth（白名单注解）

标记该注解的接口**不需要登录**即可访问。

#### 使用示例

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    // 方法级别：单个接口不需要认证
    @IgnoreAuth
    @PostMapping("/login")
    public ResponseResult<?> login(@RequestBody LoginDto dto) {
        return authService.login(dto);
    }
    
    // 方法级别：注册接口不需要认证
    @IgnoreAuth
    @PostMapping("/register")
    public ResponseResult<?> register(@RequestBody RegisterDto dto) {
        return authService.register(dto);
    }
}
```

#### 类级别使用

```java
@IgnoreAuth  // 整个Controller都不需要认证
@RestController
@RequestMapping("/public")
public class PublicController {
    
    @GetMapping("/info")
    public ResponseResult<?> getInfo() {
        // 所有接口都不需要认证
    }
}
```

---

### 2. @RequireRole（角色校验注解）

标记该注解的接口**需要特定角色**才能访问。

#### 使用示例

```java
@RestController
@RequestMapping("/admin")
@RequireRole("ADMIN")  // 类级别：所有接口都需要ADMIN角色
public class AdminController {
    
    @GetMapping("/users")
    public ResponseResult<?> getUsers() {
        // 只有ADMIN角色可以访问
    }
    
    @PostMapping("/user")
    public ResponseResult<?> createUser() {
        // 只有ADMIN角色可以访问
    }
}
```

#### 方法级别覆盖

```java
@RestController
@RequestMapping("/merchant")
public class MerchantController {
    
    @GetMapping("/products")
    @RequireRole({"MERCHANT", "ADMIN"})  // MERCHANT或ADMIN都可以访问
    public ResponseResult<?> getProducts() {
        // ...
    }
    
    @DeleteMapping("/product/{id}")
    @RequireRole("ADMIN")  // 只有ADMIN可以删除
    public ResponseResult<?> deleteProduct(@PathVariable String id) {
        // ...
    }
}
```

#### 多角色支持

```java
// 允许多个角色访问
@RequireRole({"ADMIN", "MERCHANT", "USER"})
@GetMapping("/data")
public ResponseResult<?> getData() {
    // ADMIN、MERCHANT、USER 都可以访问
}
```

---

## 常见角色类型

根据项目设计，常见的角色包括：

- `ADMIN` - 系统管理员
- `MERCHANT` - 商家
- `USER` - 普通用户

---

## 多角色支持

### 网关传递多角色

网关会在请求头中传递多个角色，用逗号分隔：

```
X-User-Role: ADMIN,MERCHANT
```

### 过滤器自动解析

UserAuthFilter 会自动解析多个角色：

```java
// 网关传递: X-User-Role: ADMIN,MERCHANT
// 过滤器解析为: ["ADMIN", "MERCHANT"]
// Spring Security 中会创建两个权限:
// - ROLE_ADMIN
// - ROLE_MERCHANT
```

### 角色校验逻辑

**只要用户的任意一个角色在允许列表中，就通过验证**

```java
@RequireRole({"ADMIN", "MERCHANT"})
@GetMapping("/products")
public ResponseResult<?> getProducts() {
    // 用户有 ADMIN 或 MERCHANT 任意一个角色即可访问
}

// 示例：
// 用户角色: ["MERCHANT", "USER"] → ✅ 通过（有 MERCHANT）
// 用户角色: ["ADMIN"] → ✅ 通过（有 ADMIN）
// 用户角色: ["USER"] → ❌ 拒绝（没有 ADMIN 或 MERCHANT）
```

---

## 完整示例

### AuthController（认证相关）

```java
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 登录接口 - 不需要认证
    @IgnoreAuth
    @PostMapping("/login")
    public ResponseResult<?> login(@RequestBody LoginDto dto) {
        String token = authService.login(dto);
        return ResponseResult.success(token);
    }

    // 注册接口 - 不需要认证
    @IgnoreAuth
    @PostMapping("/register")
    public ResponseResult<?> register(@RequestBody RegisterDto dto) {
        String result = authService.register(dto);
        return ResponseResult.success(result);
    }

    // 发送验证码 - 不需要认证
    @IgnoreAuth
    @GetMapping("/send-code")
    public ResponseResult<?> sendCode(@RequestParam String email) {
        return ResponseResult.success(emailService.sendVerificationCode(email));
    }
}
```

### AdminController（管理员接口）

```java
@Slf4j
@RestController
@RequestMapping("/admin")
@RequireRole("ADMIN")  // 所有接口都需要ADMIN角色
public class AdminController {

    @Autowired
    private UserService userService;

    // 获取用户列表 - 需要ADMIN角色
    @GetMapping("/users")
    public ResponseResult<?> getUsers(UserQueryDto queryDto) {
        return ResponseResult.success(userService.getUsers(queryDto));
    }

    // 创建管理员 - 需要ADMIN角色
    @PostMapping("/admin/create")
    public ResponseResult<?> createAdmin(@RequestBody AdminCreateDto dto) {
        return ResponseResult.success(userService.createAdmin(dto));
    }

    // 删除用户 - 需要ADMIN角色
    @DeleteMapping("/users/{id}")
    public ResponseResult<?> deleteUser(@PathVariable String id) {
        return ResponseResult.success(userService.deleteUser(id));
    }
}
```

### MerchantController（商家接口）

```java
@Slf4j
@RestController
@RequestMapping("/merchant")
@RequireRole({"MERCHANT", "ADMIN"})  // MERCHANT或ADMIN都可以访问
public class MerchantController {

    // 获取商品列表 - MERCHANT或ADMIN都可以
    @GetMapping("/products")
    public ResponseResult<?> getProducts() {
        // ...
    }

    // 上架商品 - 只有MERCHANT可以
    @PostMapping("/product")
    @RequireRole("MERCHANT")
    public ResponseResult<?> addProduct(@RequestBody ProductDto dto) {
        // ...
    }
}
```

### PublicController（公开接口）

```java
@Slf4j
@RestController
@RequestMapping("/public")
@IgnoreAuth  // 所有接口都不需要认证
public class PublicController {

    @GetMapping("/categories")
    public ResponseResult<?> getCategories() {
        // 获取分类列表，所有人可访问
    }

    @GetMapping("/product/{id}")
    public ResponseResult<?> getProductDetail(@PathVariable String id) {
        // 查看商品详情，所有人可访问
    }
}
```

---

## 执行流程

```
请求进入
    ↓
检查是否有 @IgnoreAuth 注解
    ↓ 是
跳过认证，直接放行
    ↓ 否
检查请求头中的用户信息
    ↓ 没有用户信息
返回 401 未授权
    ↓ 有用户信息
设置安全上下文
    ↓
检查是否有 @RequireRole 注解
    ↓ 没有
默认允许访问
    ↓ 有
检查用户角色是否匹配
    ↓ 匹配
允许访问
    ↓ 不匹配
返回 403 权限不足
```

---

## 响应码说明

- `3000` - 未登录或登录已过期
- `3011` - 权限不足，无法执行此操作

---

## 注意事项

1. **优先级**：方法级别的注解优先于类级别的注解
2. **默认行为**：如果没有添加任何注解，默认需要认证但不限制角色
3. **角色名称**：角色名称必须与网关传递的 `X-User-Role` 头中的值完全匹配
4. **大小写敏感**：角色名称区分大小写，建议统一使用大写

---

## 调试技巧

在过滤器中已经添加了详细的日志输出：

```
用户认证 - UserId: xxx, Role: xxx, Username: xxx
角色校验 - 用户角色: ADMIN, 允许的角色: [ADMIN, MERCHANT], 结果: true
```

可以通过查看日志来排查权限问题。
