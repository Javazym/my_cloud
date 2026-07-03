# Agent-Server API 文档

## 基本信息

- **服务名称**: `agent-server`（AI 商品审核服务）
- **服务端口**: `8903`
- **Controller 基础路径**: `/ai`
- **Nacos 注册名**: `agent-server`
- **网关前缀**: `/agent-server`（通过网关访问需加此前缀）
- **完整网关 URL**: `http://<gateway>/agent-server/ai/...`
- **直连 URL**: `http://localhost:8903/ai/...`

---

## 目录

1. [健康检查](#1-健康检查)
2. [单商品全量审核](#2-单商品全量审核)
3. [批量审核（同步）](#3-批量审核同步)
4. [批量审核（流式 SSE）](#4-批量审核流式-sse)
5. [描述审核](#5-描述审核)
6. [图片审核](#6-图片审核)
7. [待审核商品列表](#7-待审核商品列表)
8. [WebSocket AI 对话](#8-websocket-ai-对话)
9. [数据模型](#9-数据模型)

---

## 1. 健康检查

检测 AI 审核服务是否正常运行。

```
GET /ai/health
```

### 请求参数

无

### 响应示例

```json
{
  "status": "ok",
  "service": "商品审核 API"
}
```

---

## 2. 单商品全量审核

完整审核单个商品（描述审核 + 图片审核 + 综合评价）。

```
POST /ai/review/product
Content-Type: application/json

{
  "productId": 1
}
```

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productId | Long | 是 | 商品 ID |

### 响应体（来自 Python AI）

| 字段 | 类型 | 说明 |
|------|------|------|
| product_name | String | 商品名称 |
| description_review | Object | 描述审核结果 |
| images_review | Object / null | 图片审核结果（无图片时为 null） |
| evaluation | Object | 商品综合评价 |
| conclusion | String | 最终结论：`通过` / `不通过` / `需人工复核` |
| fail_reason | String | 不通过原因（通过则为空） |
| improvements | Array | 需整改项列表 |
| suggestion | String | 综合整改建议 |

#### description_review

| 字段 | 类型 | 说明 |
|------|------|------|
| conclusion | String | 审核结论：`通过` / `不通过` / `需人工复核` |
| violations | Array | 违规项列表 |
| suggestion | String | 整改建议 |

#### images_review（无图片时为 null）

| 字段 | 类型 | 说明 |
|------|------|------|
| overall_conclusion | String | 总体结论：`通过` / `不通过` / `需人工复核` |
| images | Array | 各图片审核结果 |
| violation_summary | String | 违规项汇总 |
| suggestion | String | 综合整改建议 |

#### evaluation

| 字段 | 类型 | 说明 |
|------|------|------|
| rating | Float | 综合评分（1-5） |
| pros | String[] | 商品优点 |
| cons | String[] | 商品缺点/风险 |
| overall_evaluation | String | 综合评价 |
| recommendation | String | 推荐建议：`强烈推荐` / `推荐` / `谨慎购买` / `不推荐` |

#### violations[]

| 字段 | 类型 | 说明 |
|------|------|------|
| type | String | 违规类型：虚假宣传、极限词、侵权、价格违规等 |
| severity | String | 等级：`严重` / `中等` / `轻微` |
| detail | String | 违规详情 |

#### improvements[]

| 字段 | 类型 | 说明 |
|------|------|------|
| item | String | 整改项目 |
| priority | String | 优先级：`高` / `中` / `低` |
| detail | String | 整改说明 |

### 响应示例

```json
{
  "product_name": "iPhone 15 Pro Max",
  "description_review": {
    "conclusion": "不通过",
    "violations": [
      {
        "type": "极限词/违禁词",
        "severity": "中等",
        "detail": "描述中包含「限时特价」「先到先得」等诱导性用语"
      }
    ],
    "suggestion": "请删除诱导性用语"
  },
  "images_review": {
    "overall_conclusion": "通过",
    "images": [
      {
        "image_index": 1,
        "image_source": "https://example.com/img.jpg",
        "conclusion": "通过",
        "violations": [],
        "suggestion": ""
      }
    ],
    "violation_summary": "",
    "suggestion": ""
  },
  "evaluation": {
    "rating": 4.0,
    "pros": ["品牌知名度高"],
    "cons": ["描述存在诱导性用语"],
    "overall_evaluation": "商品本身品质较好，但描述存在违规",
    "recommendation": "谨慎购买"
  },
  "conclusion": "不通过",
  "fail_reason": "描述中存在极限词违规",
  "improvements": [
    {
      "item": "删除诱导性用语",
      "priority": "高",
      "detail": "删除「限时特价」「先到先得」"
    }
  ],
  "suggestion": "请按整改建议修改后重新提交"
}
```

---

## 3. 批量审核（同步）

一次性审核多个商品，等待所有审核完成后一次性返回。

```
POST /ai/review/batch
Content-Type: application/json

{
  "productIds": [1, 2, 3]
}
```

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productIds | Long[] | 是 | 商品 ID 列表 |

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| total_count | Integer | 审核商品总数 |
| passed_count | Integer | 通过数量 |
| failed_count | Integer | 不通过数量 |
| manual_review_count | Integer | 需人工复核数量 |
| products | Array | 各商品审核结果（结构同单商品审核） |
| overall_summary | String | 批量审核总结 |

### 响应示例

```json
{
  "total_count": 2,
  "passed_count": 1,
  "failed_count": 1,
  "manual_review_count": 0,
  "products": [
    {
      "product_name": "商品A",
      "description_review": { "conclusion": "通过", "violations": [], "suggestion": "" },
      "images_review": null,
      "evaluation": {
        "rating": 4.5,
        "pros": ["描述清晰"],
        "cons": [],
        "overall_evaluation": "商品表现良好",
        "recommendation": "推荐"
      },
      "conclusion": "通过",
      "fail_reason": "",
      "improvements": [],
      "suggestion": "商品合规"
    }
  ],
  "overall_summary": "共审核 2 个商品，通过 1 个，不通过 1 个，需人工复核 0 个。"
}
```

---

## 4. 批量审核（流式 SSE）

**逐个审核，每完成一个立即推送结果，解决长时间无响应问题。**

```
POST /ai/review/batch/stream
Content-Type: application/json

{
  "productIds": [1, 2, 3]
}
```

### 请求参数

同批量审核（同步）。

### 响应格式

SSE (Server-Sent Events)，`Content-Type: text/event-stream`。

### 事件类型

| 事件名 | 触发时机 | data 字段 |
|--------|---------|-----------|
| `start` | 开始处理 | `{"total": N}` |
| `progress` | 每审核完一个商品 | `{"index": i, "total": N, "productId": id, "result": {...}}` |
| `error` | 单个商品失败 | `{"index": i, "productId": id, "error": "..."}` |
| `complete` | 全部完成 | `{"total": N}` |

`progress` 事件中 `result` 的结构与单商品审核响应体完全相同。

### 前端消费示例（fetch + ReadableStream）

```typescript
async function batchReviewStream(productIds: number[]) {
  const response = await fetch('/agent-server/ai/review/batch/stream', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ productIds })
  });

  const reader = response.body!.getReader();
  const decoder = new TextDecoder();
  let buffer = '';

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    buffer += decoder.decode(value, { stream: true });
    const lines = buffer.split('\n');
    buffer = lines.pop() || '';

    let eventType = '';
    for (const line of lines) {
      if (line.startsWith('event: ')) {
        eventType = line.slice(7);
      } else if (line.startsWith('data: ')) {
        const data = JSON.parse(line.slice(6));
        switch (eventType) {
          case 'start':
            setTotal(data.total);
            break;
          case 'progress':
            // data: { index, total, productId, result: { conclusion, ... } }
            updateResult(data.productId, data.result);
            setProgress(`${data.index + 1} / ${data.total}`);
            break;
          case 'error':
            console.error('审核失败:', data.productId, data.error);
            break;
          case 'complete':
            setDone(true);
            break;
        }
      }
    }
  }
}
```

### 前端消费示例（EventSource 包装，需适配 POST）

浏览器原生 `EventSource` 不支持 POST，可用 `fetch` + `ReadableStream` 方式（如上），或用 polyfill 库。

---

## 5. 描述审核

仅审核商品描述文本。

```
POST /ai/review/description
Content-Type: application/json

{
  "productId": 1
}
```

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productId | Long | 是 | 商品 ID |

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| conclusion | String | `通过` / `不通过` / `需人工复核` |
| violations | Array | 违规项列表 |
| suggestion | String | 整改建议 |

### 响应示例

```json
{
  "conclusion": "不通过",
  "violations": [
    {
      "type": "极限词/违禁词",
      "severity": "严重",
      "detail": "使用了违禁词「全网最低价」「唯一」"
    }
  ],
  "suggestion": "请删除极限词，修改为合规表述"
}
```

---

## 6. 图片审核

审核一组商品图片。

```
POST /ai/review/images
Content-Type: application/json

{
  "productId": 1
}
```

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| productId | Long | 是 | 商品 ID |

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| overall_conclusion | String | `通过` / `不通过` / `需人工复核` |
| images | Array | 各图片审核结果 |
| violation_summary | String | 违规汇总 |
| suggestion | String | 综合整改建议 |

### 响应示例

```json
{
  "overall_conclusion": "不通过",
  "images": [
    {
      "image_index": 1,
      "image_source": "https://example.com/img.jpg",
      "conclusion": "不通过",
      "violations": [
        { "type": "违规内容", "severity": "严重", "detail": "图片包含违规内容" }
      ],
      "suggestion": "请替换违规图片"
    }
  ],
  "violation_summary": "第1张图片存在违规内容",
  "suggestion": "请删除违规图片后重新提交"
}
```

---

## 7. 待审核商品列表

获取待审核商品列表（分页）。

```
GET /ai/products/pending?pageNum=1&pageSize=10
```

### 请求参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| pageNum | Integer | 否 | 1 | 页码，从 1 开始 |
| pageSize | Integer | 否 | 10 | 每页数量 |

### 响应体（统一 ResponseResult）

```json
{
  "code": 1000,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "商品名称",
      "description": "商品描述",
      "subName": "副标题",
      "images": ["https://.../img1.jpg"],
      "merchantName": "商家名称",
      "categoryName": "分类名称",
      "price": 99.99,
      "originalPrice": 199.99,
      "stock": 100,
      "soldCount": 50,
      "reviewCount": 10,
      "favoriteCount": 20,
      "rating": 4.5,
      "tags": ["标签1", "标签2"],
      "keywords": "关键词",
      "auditStatus": 0,
      "publishStatus": 0,
      "isHot": 1,
      "isFeatured": 0,
      "isNew": 1,
      "merchantId": 1,
      "categoryId": 1,
      "createTime": "2026-06-08T12:00:00"
    }
  ]
}
```

> `ResponseResult` 统一格式：`{ "code": 1000, "message": "success", "data": T }`

---

## 8. WebSocket AI 对话

通过 WebSocket 与 AI 实时对话，用于审核咨询。

```
ws://<host>:8903/ws/chat?userId={userId}&productId={productId}
```

### 连接参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | String | 是 | 用户标识 |
| productId | Long | 是 | 商品 ID |

### 消息类型

#### 服务端 → 客户端

| type | 说明 | 字段 |
|------|------|------|
| `connected` | 连接成功 | sessionId, message |
| `message` | AI 回复 | reply |
| `error` | 错误 | message |

#### 客户端 → 服务端

纯文本消息，直接发送字符串。

### 交互流程

1. 客户端建立 WebSocket 连接（带 userId、productId）
2. 服务端返回 `connected` 消息
3. 服务端自动推送商品上下文信息
4. 客户端发送文本消息提问
5. 服务端返回 `message` 类型的 AI 回复

### JavaScript 示例

```javascript
const ws = new WebSocket('ws://localhost:8903/ws/chat?userId=user123&productId=1');

ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  switch (data.type) {
    case 'connected':
      console.log('会话已建立:', data.sessionId);
      break;
    case 'message':
      console.log('AI:', data.reply);
      break;
    case 'error':
      console.error('错误:', data.message);
      break;
  }
};

ws.onopen = () => ws.send('这个商品描述是否合规？');
```

---

## 9. 数据模型

### 审核结论

| 值 | 说明 |
|----|------|
| `通过` | 商品合规 |
| `不通过` | 存在违规，需整改 |
| `需人工复核` | 无法确定，需人工判断 |

### 违规等级

| 值 | 说明 |
|----|------|
| `严重` | 违禁品、严重虚假宣传、侵权 |
| `中等` | 极限词、夸大功效、价格违规 |
| `轻微` | 描述不规范、标签不准确 |

### 整改优先级

| 值 | 说明 |
|----|------|
| `高` | 必须立即整改 |
| `中` | 建议尽快整改 |
| `低` | 建议优化 |

### 推荐建议

| 值 | 说明 |
|----|------|
| `强烈推荐` | 商品质量好，性价比高 |
| `推荐` | 符合要求 |
| `谨慎购买` | 存在一定风险 |
| `不推荐` | 存在严重问题 |

---

## 网关访问说明

由于 `agent-server` 是微服务，通过网关统一入口访问时需添加服务前缀：

| 环境 | 网关地址 | 完整路径示例 |
|------|---------|-------------|
| 本地直连 | `http://localhost:8903` | `POST http://localhost:8903/ai/review/product` |
| 通过网关 | `http://<gateway-host>:<gateway-port>` | `POST http://<gateway>/agent-server/ai/review/product` |

网关路由规则：
- 路径前缀 `/agent-server/**` → 转发到 `lb://agent-server`
- `StripPrefix=1`（剥掉 `agent-server` 前缀）
