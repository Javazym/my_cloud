# Agent-Server API 文档

## 基本信息

- **服务名称**: agent-server (AI 商品审核服务)
- **基础路径**: `/api`
- **服务端口**: `8903`
- **完整 Base URL**: `http://localhost:8903`
- **技术栈**: Spring Boot + Python AI API 代理 + WebSocket

---

## 目录

1. [健康检查接口](#1-健康检查接口)
2. [商品审核接口](#2-商品审核接口)
   - [2.1 全量商品审核](#21-全量商品审核)
   - [2.2 批量商品审核](#22-批量商品审核)
   - [2.3 商品描述审核](#23-商品描述审核)
   - [2.4 商品图片审核](#24-商品图片审核)
3. [待审核商品查询](#3-待审核商品查询)
4. [WebSocket 实时聊天接口](#4-websocket-实时聊天接口)

---

## 1. 健康检查接口

### 接口信息

- **接口路径**: `GET /ai/health`
- **说明**: 检查 AI 服务健康状态

### 请求参数

无

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "ok"
  }
}
```

---

## 2. 商品审核接口

### 2.1 全量商品审核

#### 接口信息

- **接口路径**: `POST /ai/review/product`
- **说明**: 对单个商品进行全方位审核（包括商品信息、描述、图片等）

#### 请求参数

**Request Body** (`ReviewRequest`)

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品 ID |

**请求示例**:

```json
{
  "productId": 123456
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conclusion": "通过",
    "suggestion": "商品信息完整，描述清晰，图片合规",
    "productScore": 95,
    "descriptionScore": 90,
    "imageScore": 95,
    "details": {
      "productInfo": {
        "isComplete": true,
        "isCompliant": true
      },
      "description": {
        "quality": "high",
        "issues": []
      },
      "images": {
        "validCount": 5,
        "invalidCount": 0,
        "issues": []
      }
    }
  }
}
```

#### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| conclusion | String | 审核结论：通过/不通过/待审核 |
| suggestion | String | 审核建议 |
| productScore | Number | 商品信息评分 |
| descriptionScore | Number | 描述质量评分 |
| imageScore | Number | 图片质量评分 |
| details | Object | 详细审核结果 |

---

### 2.2 批量商品审核

#### 接口信息

- **接口路径**: `POST /ai/review/batch`
- **说明**: 对多个商品进行批量审核

#### 请求参数

**Request Body** (`BatchReviewRequest`)

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productIds | List\<Long\> | 是 | 商品 ID 列表，至少包含一个 ID |

**请求示例**:

```json
{
  "productIds": [123456, 123457, 123458]
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "products": [
      {
        "productId": 123456,
        "conclusion": "通过",
        "suggestion": "商品信息完整",
        "productScore": 95
      },
      {
        "productId": 123457,
        "conclusion": "不通过",
        "suggestion": "商品描述存在违规内容",
        "productScore": 60
      },
      {
        "productId": 123458,
        "conclusion": "通过",
        "suggestion": "符合规范",
        "productScore": 88
      }
    ],
    "summary": {
      "total": 3,
      "approved": 2,
      "rejected": 1
    }
  }
}
```

---

### 2.3 商品描述审核

#### 接口信息

- **接口路径**: `POST /ai/review/description`
- **说明**: 专门审核商品描述的规范性、完整性

#### 请求参数

**Request Body** (`ReviewRequest`)

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品 ID |

**请求示例**:

```json
{
  "productId": 123456
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conclusion": "通过",
    "suggestion": "描述清晰完整",
    "score": 92,
    "analysis": {
      "completeness": "完整",
      "accuracy": "准确",
      "compliance": "合规",
      "issues": [],
      "keywords": ["高品质", "限时优惠"]
    }
  }
}
```

#### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| conclusion | String | 审核结论 |
| suggestion | String | 修改建议 |
| score | Number | 描述质量评分 |
| analysis | Object | 详细分析结果 |

---

### 2.4 商品图片审核

#### 接口信息

- **接口路径**: `POST /ai/review/images`
- **说明**: 专门审核商品图片的合规性、质量

#### 请求参数

**Request Body** (`ReviewRequest`)

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品 ID |

**请求示例**:

```json
{
  "productId": 123456
}
```

#### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conclusion": "通过",
    "suggestion": "图片清晰，符合规范",
    "totalImages": 5,
    "passedImages": 5,
    "failedImages": 0,
    "images": [
      {
        "url": "https://example.com/image1.jpg",
        "passed": true,
        "quality": "high",
        "issues": []
      },
      {
        "url": "https://example.com/image2.jpg",
        "passed": true,
        "quality": "medium",
        "issues": ["分辨率较低"]
      }
    ]
  }
}
```

#### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| conclusion | String | 审核结论 |
| suggestion | String | 修改建议 |
| totalImages | Number | 总图片数 |
| passedImages | Number | 通过的图片数 |
| failedImages | Number | 未通过的图片数 |
| images | Array\<Object\> | 每张图片的详细审核结果 |

---

## 3. 待审核商品查询

### 接口信息

- **接口路径**: `GET /ai/products/pending`
- **说明**: 获取待审核商品列表（分页）

### 请求参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码，从 1 开始 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**请求示例**:

```
GET /ai/products/pending?pageNum=1&pageSize=20
```

### 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 123456,
        "name": "iPhone 15 Pro Max",
        "subName": "苹果旗舰手机",
        "images": [
          "https://example.com/img1.jpg",
          "https://example.com/img2.jpg"
        ],
        "merchantName": "苹果官方旗舰店",
        "categoryName": "手机",
        "price": 9999.00,
        "originalPrice": 10999.00,
        "stock": 1000,
        "soldCount": 5000,
        "rating": 4.8,
        "auditStatus": "PENDING",
        "createTime": "2025-06-06 10:30:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 156,
    "totalPages": 8,
    "first": true,
    "last": false,
    "numberOfElements": 20
  }
}
```

#### ProductSimpleVO 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 商品 ID |
| name | String | 商品名称 |
| subName | String | 商品副标题 |
| images | List\<String\> | 商品图片 URL 列表 |
| merchantName | String | 商家名称 |
| categoryName | String | 分类名称 |
| price | BigDecimal | 商品价格 |
| originalPrice | BigDecimal | 原价 |
| stock | Integer | 库存 |
| soldCount | Integer | 已售数量 |
| rating | Double | 评分 |
| auditStatus | String | 审核状态：PENDING/APPROVED/REJECTED |
| createTime | String | 创建时间 |

---

## 4. WebSocket 实时聊天接口

### 接口信息

- **协议**: WebSocket
- **连接地址**: `ws://localhost:8903/ws/chat`
- **说明**: 通过 WebSocket 与 AI 进行实时对话，用于商品审核咨询

### 连接建立

#### 连接参数

WebSocket 连接时需要在 URL 中携带以下查询参数：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | String | 是 | 用户 ID |
| productId | Long | 是 | 商品 ID |

**连接示例**:

```
ws://localhost:8903/ws/chat?userId=user123&productId=123456
```

#### 连接成功消息（服务端 → 客户端）

```json
{
  "type": "connected",
  "sessionId": "user123:uuid-xxx-yyy",
  "message": "AI商品审核助手已连接，请提出您的问题"
}
```

#### 商品上下文自动推送（服务端 → 客户端）

连接成功后，服务端会自动推送当前商品的详细信息：

```json
{
  "type": "context",
  "content": "以下是需要审核的商品信息：\n名称：iPhone 15 Pro Max\n描述：苹果旗舰手机...\n商家：苹果官方旗舰店\n分类：手机"
}
```

### 消息交互

#### 客户端发送消息（客户端 → 服务端）

**格式**: 纯文本

**示例**:

```
这个商品的描述是否符合广告法规范？
```

#### 服务端回复消息（服务端 → 客户端）

```json
{
  "type": "message",
  "reply": "经过审核，该商品描述存在以下问题：\n1. 使用了\"最顶级\"等绝对化用语，违反广告法\n2. 建议修改为\"高品质\"等表述"
}
```

### WebSocket 消息类型

#### 客户端 → 服务端消息

| 消息类型 | 格式 | 说明 |
|---------|------|------|
| 文本消息 | 纯字符串 | 用户提出的问题或指令 |

#### 服务端 → 客户端消息

| 字段名 | 类型 | 说明 |
|--------|------|------|
| type | String | 消息类型：connected/context/message/error |
| sessionId | String | 会话 ID（仅在 connected 类型中） |
| message | String | 提示信息（仅在 connected 类型中） |
| content | String | 商品上下文信息 |
| reply | String | AI 回复内容 |
| error | String | 错误信息 |

### WebSocket 事件处理

#### 连接建立 (`afterConnectionEstablished`)

1. 验证请求参数（userId、productId）
2. 创建聊天会话
3. 自动推送商品上下文
4. 返回连接成功消息

#### 接收消息 (`handleTextMessage`)

1. 接收用户输入的文本消息
2. 调用 AI 服务进行处理
3. 返回 AI 回复内容

#### 连接关闭 (`afterConnectionClosed`)

1. 清理会话数据
2. 释放资源

### 错误处理

#### 连接参数错误

```json
{
  "type": "error",
  "message": "缺少必要参数: userId, productId"
}
```

#### productId 格式错误

```json
{
  "type": "error",
  "message": "productId格式错误"
}
```

#### 会话不存在

```json
{
  "type": "error",
  "message": "会话不存在"
}
```

### WebSocket 使用示例（JavaScript）

```javascript
// 1. 建立连接
const userId = 'user123';
const productId = 123456;
const ws = new WebSocket(`ws://localhost:8903/ws/chat?userId=${userId}&productId=${productId}`);

// 2. 监听连接打开
ws.onopen = function() {
    console.log('WebSocket 连接成功');
};

// 3. 监听消息接收
ws.onmessage = function(event) {
    const data = JSON.parse(event.data);
    
    switch (data.type) {
        case 'connected':
            console.log('会话已建立:', data.sessionId);
            break;
        case 'context':
            console.log('商品上下文:', data.content);
            // 可以在此展示商品信息给前端用户
            break;
        case 'message':
            console.log('AI 回复:', data.reply);
            // 显示 AI 回复到聊天界面
            break;
        case 'error':
            console.error('错误:', data.message);
            break;
    }
};

// 4. 发送消息
function sendMessage(message) {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send(message);
    }
}

// 使用示例
sendMessage('这个商品的描述是否合规？');

// 5. 监听连接关闭
ws.onclose = function() {
    console.log('WebSocket 连接已关闭');
};

// 6. 监听错误
ws.onerror = function(error) {
    console.error('WebSocket 错误:', error);
};
```

### WebSocket 常见场景

#### 场景 1：询问商品审核结果

```
用户: 这个商品能通过审核吗？
AI: 根据审核结果，该商品存在以下问题...
```

#### 场景 2：请求修改建议

```
用户: 请帮我优化商品描述
AI: 建议对以下内容进行修改...
```

#### 场景 3：查询同类商品对比

```
用户: 和同类商品相比怎么样？
AI: 根据数据库分析，该商品在以下几个方面表现优秀...
```

---

## 通用说明

### 统一响应格式

所有 HTTP 接口均使用统一的响应格式 `ApiResult<T>`:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

#### 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码，200 表示成功，其他表示失败 |
| message | String | 响应消息 |
| data | Object | 响应数据 |

### 错误响应

当接口发生错误时，会返回对应的错误码和错误信息：

```json
{
  "code": 500,
  "message": "处理失败：商品不存在",
  "data": null
}
```

### 认证方式

当前版本暂未集成认证机制，后续版本将集成 JWT Token 认证。

### 限流策略

暂未实施限流策略，后续将根据业务需求添加。

---

## 数据模型

### ReviewRequest

用于单个商品审核请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | Long | 是 | 商品 ID |

### BatchReviewRequest

用于批量商品审核请求

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productIds | List\<Long\> | 是 | 商品 ID 列表 |

### ProductReviewRequest

完整商品信息审核请求（内部使用）

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 商品名称 |
| description | String | 是 | 商品描述 |
| subName | String | 否 | 商品副标题 |
| imageUrls | List\<String\> | 否 | 商品图片 URL 列表 |
| merchantName | String | 否 | 商家名称 |
| categoryName | String | 否 | 分类名称 |
| priceInfo | Object | 否 | 价格信息 |
| stock | Integer | 否 | 库存 |
| soldCount | Integer | 否 | 已售数量 |
| reviewCount | Integer | 否 | 评价数量 |
| favoriteCount | Integer | 否 | 收藏数量 |
| rating | Double | 否 | 评分 |
| tags | String | 否 | 标签 |
| keywords | String | 否 | 关键词 |
| isHot | Boolean | 否 | 是否热卖 |
| isFeatured | Boolean | 否 | 是否推荐 |
| isNew | Boolean | 否 | 是否新品 |

---

## 部署信息

### 环境配置

```properties
# 应用名称
spring.application.name=agent-server

# 服务端口
server.port=8903

# Python AI API 地址
python.api.base-url=http://localhost:8000

# MySQL 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/shopping_server
spring.datasource.username=root
spring.datasource.password=QLBM2905

# Jackson 日期格式化
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=GMT+8
```

### 依赖服务

- **Python AI API**: `http://localhost:8000` - 提供 AI 审核能力
- **MySQL 数据库**: `localhost:3306` - 存储商品数据
- **shopping-server**: 通过 Feign Client 调用商品服务

---

## 更新日志

### v1.0.0 (2025-06-06)

- 初始版本发布
- 支持单商品全量审核
- 支持批量商品审核
- 支持商品描述专项审核
- 支持商品图片专项审核
- 支持待审核商品分页查询
- 支持 WebSocket 实时对话

---

## 联系与支持

如有问题，请联系开发团队或查看项目文档。
