# 认证服务管理员和用户管理接口说明

## 概述
本次更新为认证服务添加了管理员创建功能和完整的用户管理功能，同时完善了用户实体的属性。

## 新增功能

### 1. 用户实体增强
在 `SysUser` 实体中添加了以下字段：
- `phone`: 手机号（唯一）
- `realName`: 真实姓名
- `avatar`: 头像URL
- `updateTime`: 更新时间

### 2. 管理员创建接口

#### 接口地址
`POST /auth/admin/create`

#### 请求参数 (AdminCreateDto)
```json
{
  "username": "admin_username",
  "password": "admin_password",
  "confirmPassword": "admin_password",
  "email": "admin@example.com",
  "phone": "13800138000",
  "realName": "管理员姓名",
  "avatar": "https://example.com/avatar.jpg"
}
```

#### 响应示例
```json
{
  "code": 1000,
  "success": true,
  "httpStatus": 200,
  "message": "管理员创建成功",
  "data": "管理员创建成功",
  "timestamp": "2026-05-01T12:00:00"
}
```

### 3. 用户管理接口

#### 3.1 获取用户列表（分页）

##### 接口地址
`GET /auth/users`

##### 请求参数 (UserQueryDto)
- `username`: 用户名（可选，用于搜索）
- `email`: 邮箱（可选，用于搜索）
- `phone`: 手机号（可选，用于搜索）
- `status`: 状态（可选，用于筛选）
- `pageNum`: 页码，默认1
- `pageSize`: 每页大小，默认10

##### 示例请求
```
GET /auth/users?pageNum=1&pageSize=10
```

##### 响应示例
```json
{
  "code": 1000,
  "success": true,
  "httpStatus": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "id": "user-id",
        "username": "testuser",
        "email": "test@example.com",
        "phone": "13800138000",
        "realName": "测试用户",
        "avatar": "https://example.com/avatar.jpg",
        "status": 1,
        "createTime": "2026-05-01T10:00:00",
        "updateTime": "2026-05-01T10:00:00",
        "roles": ["USER"]
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  },
  "timestamp": "2026-05-01T12:00:00"
}
```

#### 3.2 获取单个用户信息

##### 接口地址
`GET /auth/users/{id}`

##### 路径参数
- `id`: 用户ID

##### 响应示例
```json
{
  "code": 1000,
  "success": true,
  "httpStatus": 200,
  "message": "操作成功",
  "data": {
    "id": "user-id",
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "realName": "测试用户",
    "avatar": "https://example.com/avatar.jpg",
    "status": 1,
    "createTime": "2026-05-01T10:00:00",
    "updateTime": "2026-05-01T10:00:00",
    "roles": ["USER"]
  },
  "timestamp": "2026-05-01T12:00:00"
}
```

#### 3.3 更新用户信息

##### 接口地址
`PUT /auth/users`

##### 请求参数 (UserUpdateDto)
```json
{
  "id": "user-id",
  "username": "new_username",
  "email": "new_email@example.com",
  "phone": "13900139000",
  "realName": "新姓名",
  "avatar": "https://example.com/new_avatar.jpg",
  "status": 1
}
```

##### 响应示例
```json
{
  "code": 1000,
  "success": true,
  "httpStatus": 200,
  "message": "用户信息更新成功",
  "data": "用户信息更新成功",
  "timestamp": "2026-05-01T12:00:00"
}
```

#### 3.4 删除用户

##### 接口地址
`DELETE /auth/users/{id}`

##### 路径参数
- `id`: 用户ID

##### 响应示例
```json
{
  "code": 1000,
  "success": true,
  "httpStatus": 200,
  "message": "用户删除成功",
  "data": "用户删除成功",
  "timestamp": "2026-05-01T12:00:00"
}
```

#### 3.5 更新用户状态

##### 接口地址
`PUT /auth/users/{id}/status`

##### 路径参数
- `id`: 用户ID

##### 请求参数
- `status`: 新状态（1-启用，0-禁用）

##### 示例请求
```
PUT /auth/users/user-id/status?status=0
```

##### 响应示例
```json
{
  "code": 1000,
  "success": true,
  "httpStatus": 200,
  "message": "用户状态更新成功",
  "data": "用户状态更新成功",
  "timestamp": "2026-05-01T12:00:00"
}
```

## 安全说明

1. **敏感信息隐藏**: 所有用户查询接口都使用 `UserVO` 对象返回数据，不会暴露密码等敏感信息
2. **角色分配**: 管理员创建时会自动分配管理员角色（优先查找名为"ADMIN"的角色，如果不存在则使用ID为1的角色）
3. **数据验证**: 创建管理员时会验证用户名、邮箱、手机号的唯一性

## 数据库变更

需要在数据库中执行以下SQL来添加新字段：

```sql
ALTER TABLE sys_user 
ADD COLUMN phone VARCHAR(20) UNIQUE,
ADD COLUMN real_name VARCHAR(100),
ADD COLUMN avatar VARCHAR(255),
ADD COLUMN update_time DATETIME;
```

## 注意事项

1. 确保数据库中存在管理员角色（角色名为"ADMIN"或ID为1）
2. 管理员创建接口应该只允许超级管理员访问，建议在实际使用时添加权限控制
3. 用户管理接口也应该添加相应的权限控制，确保只有授权用户可以访问