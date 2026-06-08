# 商品审核 API 文档

> Base URL: `http://localhost:8000`  
> 框架: FastAPI (Python)  
> 版本: 1.0.0

---

## 目录

- [1. 健康检查](#1-健康检查)
- [2. 单商品审核](#2-单商品审核)
- [3. 批量商品审核](#3-批量商品审核)
- [4. 描述审核](#4-描述审核)
- [5. 图片审核](#5-图片审核)
- [6. AI 对话](#6-ai-对话)
- [附录](#附录)
  - [审核结论](#审核结论)
  - [违规严重等级](#违规严重等级)
  - [整改优先级](#整改优先级)
  - [推荐建议](#推荐建议)
  - [活动类型](#活动类型)
  - [活动状态](#活动状态)
  - [图片审核维度](#图片审核维度)
  - [单商品审核维度](#单商品审核维度)
  - [错误响应](#错误响应)
  - [HTTP 状态码](#http-状态码)

---

## 1. 健康检查

检测服务是否正常运行。

```
GET /api/health
```

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| `status` | string | 服务状态，固定为 `"ok"` |
| `service` | string | 服务名称 |

### 响应示例

```json
{
  "status": "ok",
  "service": "商品审核 API"
}
```

---

## 2. 单商品审核

完整审核单个商品（描述审核 + 图片审核 + 综合评价）。

```
POST /api/review/product
```

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | string | **是** | 商品名称 |
| `description` | string | **是** | 商品描述文本 |
| `sub_name` | string | 否 | 商品副标题 |
| `image_urls` | string[] | 否 | 商品图片 URL 列表 |
| `merchant_name` | string | 否 | 商家名称 |
| `category_name` | string | 否 | 分类名称 |
| `price_info` | object | 否 | 价格信息（见下方） |
| `stock` | integer | 否 | 库存数量 |
| `sold_count` | integer | 否 | 已售数量 |
| `review_count` | integer | 否 | 评价数量 |
| `favorite_count` | integer | 否 | 收藏数量 |
| `rating` | float | 否 | 评分（1-5分） |
| `tags` | string | 否 | 商品标签（逗号分隔） |
| `keywords` | string | 否 | 商品关键词 |
| `is_hot` | boolean | 否 | 是否热卖 |
| `is_featured` | boolean | 否 | 是否精选 |
| `is_new` | boolean | 否 | 是否新品 |
| `activity` | object | 否 | 活动信息（见下方） |

#### `price_info` 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `price` | float | 否 | 当前售价 |
| `original_price` | float | 否 | 划线价/原价 |

#### `activity` 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `has_activity` | boolean | 是 | 是否有活动 |
| `activity_type` | integer | 否 | 活动类型：`1`-秒杀，`2`-满减 |
| `activity_id` | integer | 否 | 活动 ID |
| `activity_name` | string | 否 | 活动名称 |
| `activity_price` | float | 否 | 活动价格 |
| `activity_start_time` | string | 否 | 活动开始时间（ISO 格式） |
| `activity_end_time` | string | 否 | 活动结束时间（ISO 格式） |
| `activity_status` | integer | 否 | 活动状态：`0`-未开始，`1`-进行中，`2`-已结束 |

### 请求示例

```json
{
  "name": "iPhone 15 Pro Max",
  "sub_name": "原色钛金属 256GB",
  "description": "【官方正品】Apple iPhone 15 Pro Max 256GB 原色钛金属，A17 Pro芯片，钛金属设计，支持5G全网通。限时特价仅需7999元，原价9999元，库存有限，先到先得！",
  "image_urls": [
    "https://example.com/images/iphone15-1.jpg",
    "https://example.com/images/iphone15-2.jpg"
  ],
  "merchant_name": "某某数码旗舰店",
  "category_name": "手机通讯",
  "price_info": {
    "price": 7999,
    "original_price": 9999
  },
  "stock": 500,
  "sold_count": 3280,
  "review_count": 1250,
  "favorite_count": 5600,
  "rating": 4.8,
  "tags": "手机,苹果,iPhone,5G,旗舰",
  "keywords": "iPhone 15 pro max 手机 苹果",
  "is_hot": true,
  "is_new": true,
  "activity": {
    "has_activity": true,
    "activity_type": 1,
    "activity_id": 10086,
    "activity_name": "618限时秒杀",
    "activity_price": 7799,
    "activity_start_time": "2026-06-01T00:00:00",
    "activity_end_time": "2026-06-18T23:59:59",
    "activity_status": 1
  }
}
```

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| `product_name` | string | 商品名称 |
| `description_review` | object | 描述审核结果 |
| `images_review` | object | 图片审核结果（请求中无图片时为 `null`） |
| `evaluation` | object | 商品综合评价 |
| `conclusion` | string | 最终结论：`通过` / `不通过` / `需人工复核` |
| `fail_reason` | string | 审核不通过的原因（通过则为空） |
| `improvements` | array | 需整改项列表 |
| `suggestion` | string | 综合整改建议 |

#### `description_review` 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `conclusion` | string | 审核结论：`通过` / `不通过` / `需人工复核` |
| `violations` | array | 违规项列表（见下方 `violation` 结构） |
| `suggestion` | string | 整改建议 |

#### `images_review` 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `overall_conclusion` | string | 总体结论：`通过` / `不通过` / `需人工复核` |
| `images` | array | 各图片审核结果列表 |
| `violation_summary` | string | 所有图片违规项汇总说明 |
| `suggestion` | string | 综合整改建议 |

#### 单图片审核结果 `images[]` 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `image_index` | integer | 图片编号（从 1 开始） |
| `image_source` | string | 图片来源 URL |
| `conclusion` | string | 审核结论：`通过` / `不通过` / `需人工复核` |
| `violations` | array | 违规项列表 |
| `suggestion` | string | 整改建议 |

#### `evaluation` 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `rating` | float | 综合评分（1-5 分） |
| `pros` | string[] | 商品优点列表 |
| `cons` | string[] | 商品缺点/风险列表 |
| `overall_evaluation` | string | 综合评价描述 |
| `recommendation` | string | 推荐建议：`强烈推荐` / `推荐` / `谨慎购买` / `不推荐` |

#### `violation` 结构

| 字段 | 类型 | 说明 |
|------|------|------|
| `type` | string | 违规类型（如：虚假宣传、极限词、侵权等） |
| `severity` | string | 违规等级：`严重` / `中等` / `轻微` |
| `detail` | string | 违规详情说明 |

#### `improvements[]` 结构

| 字段 | 类型 | 说明 |
|------|------|------|
| `item` | string | 需要整改的具体项目 |
| `priority` | string | 优先级：`高` / `中` / `低` |
| `detail` | string | 整改说明 |

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
      },
      {
        "type": "虚假宣传/夸大功效",
        "severity": "轻微",
        "detail": "描述中「仅需7999元」可能对消费者产生误导"
      }
    ],
    "suggestion": "请删除诱导性用语「限时特价」「先到先得」，修改为客观描述"
  },
  "images_review": {
    "overall_conclusion": "通过",
    "images": [
      {
        "image_index": 1,
        "image_source": "https://example.com/images/iphone15-1.jpg",
        "conclusion": "通过",
        "violations": [],
        "suggestion": ""
      },
      {
        "image_index": 2,
        "image_source": "https://example.com/images/iphone15-2.jpg",
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
    "pros": ["品牌知名度高", "配置描述清晰"],
    "cons": ["描述存在诱导性用语"],
    "overall_evaluation": "商品本身配置和品质较好，但商品描述存在违规问题",
    "recommendation": "谨慎购买"
  },
  "conclusion": "不通过",
  "fail_reason": "描述中存在极限词和诱导性用语违规",
  "improvements": [
    {
      "item": "删除诱导性用语",
      "priority": "高",
      "detail": "删除「限时特价」「先到先得」等描述"
    },
    {
      "item": "优化价格描述",
      "priority": "中",
      "detail": "将「仅需7999元」改为「售价7999元」"
    }
  ],
  "suggestion": "请按整改建议修改后重新提交审核"
}
```

---

## 3. 批量商品审核

一次性审核多个商品，每项结构与单商品审核请求体相同。

```
POST /api/review/batch
```

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `products` | array | **是** | 商品列表，每项结构同 [单商品审核请求体](#2-单商品审核) |

### 请求示例

```json
{
  "products": [
    {
      "name": "商品A",
      "description": "商品A的描述",
      "price_info": {
        "price": 199,
        "original_price": 399
      },
      "merchant_name": "店铺A",
      "category_name": "数码产品"
    },
    {
      "name": "商品B",
      "description": "商品B的描述",
      "image_urls": ["https://example.com/img.jpg"],
      "tags": "美妆,护肤品"
    }
  ]
}
```

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| `total_count` | integer | 审核商品总数 |
| `passed_count` | integer | 通过数量 |
| `failed_count` | integer | 不通过数量 |
| `manual_review_count` | integer | 需人工复核数量 |
| `products` | array | 各商品审核结果（结构同单商品审核响应体） |
| `overall_summary` | string | 批量审核总体总结 |

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
      "description_review": {
        "conclusion": "通过",
        "violations": [],
        "suggestion": "商品描述合规，无需整改"
      },
      "evaluation": {
        "rating": 4.5,
        "pros": ["描述清晰", "价格合理"],
        "cons": [],
        "overall_evaluation": "商品整体表现良好",
        "recommendation": "推荐"
      },
      "conclusion": "通过",
      "fail_reason": "",
      "improvements": [],
      "suggestion": "商品合规，可以上架"
    }
  ],
  "overall_summary": "共审核 2 个商品，通过 1 个，不通过 1 个，需人工复核 0 个。"
}
```

---

## 4. 描述审核

仅审核商品描述文本，可传入价格、分类、商家等补充信息提升检测精度。

```
POST /api/review/description
```

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `description` | string | **是** | 商品描述文本 |
| `product_name` | string | 否 | 商品名称 |
| `price` | float | 否 | 商品售价 |
| `original_price` | float | 否 | 划线价/原价 |
| `category_name` | string | 否 | 分类名称 |
| `merchant_name` | string | 否 | 商家名称 |
| `tags` | string | 否 | 商品标签 |

### 请求示例

```json
{
  "description": "全网最低价！唯一正品！限时抢购仅需299元，原价999元",
  "product_name": "某商品",
  "price": 299,
  "original_price": 999,
  "category_name": "日用百货",
  "merchant_name": "某店铺"
}
```

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| `conclusion` | string | 审核结论：`通过` / `不通过` / `需人工复核` |
| `violations` | array | 违规项列表（结构同 `violation`） |
| `suggestion` | string | 整改建议 |

### 响应示例

```json
{
  "conclusion": "不通过",
  "violations": [
    {
      "type": "极限词/违禁词",
      "severity": "严重",
      "detail": "使用了违禁词「全网最低价」「唯一」"
    },
    {
      "type": "虚假宣传/夸大功效",
      "severity": "中等",
      "detail": "「限时抢购」涉嫌诱导性营销"
    }
  ],
  "suggestion": "请删除极限词「全网最低价」「唯一」，修改为合规表述"
}
```

---

## 5. 图片审核

审核一组商品图片，检测违规内容、广告夸大、侵权等问题。

```
POST /api/review/images
```

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `urls` | string[] | **是** | 图片 URL 列表 |

### 请求示例

```json
{
  "urls": [
    "https://example.com/images/product-1.jpg",
    "https://example.com/images/product-2.jpg"
  ]
}
```

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| `overall_conclusion` | string | 总体结论：`通过` / `不通过` / `需人工复核` |
| `images` | array | 各图片审核结果列表 |
| `violation_summary` | string | 所有图片违规项汇总说明 |
| `suggestion` | string | 综合整改建议 |

#### `images[]` 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `image_index` | integer | 图片编号（从 1 开始） |
| `image_source` | string | 图片来源 URL |
| `conclusion` | string | 审核结论：`通过` / `不通过` / `需人工复核` |
| `violations` | array | 违规项列表（结构同 `violation`） |
| `suggestion` | string | 整改建议 |

### 响应示例

```json
{
  "overall_conclusion": "不通过",
  "images": [
    {
      "image_index": 1,
      "image_source": "https://example.com/images/product-1.jpg",
      "conclusion": "不通过",
      "violations": [
        {
          "type": "违规内容",
          "severity": "严重",
          "detail": "图片包含违规内容"
        }
      ],
      "suggestion": "请替换违规图片"
    }
  ],
  "violation_summary": "第1张图片存在违规内容",
  "suggestion": "请删除违规图片后重新提交审核"
}
```

---

## 6. AI 对话

与 AI 助手对话（纯对话，不调用审核工具）。每个 `session_id` 独立维护上下文，支持最多 20 条历史消息，30 分钟无活动自动清理。

```
POST /api/chat
```

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `session_id` | string | **是** | 会话 ID（由客户端生成，如 UUID，同一用户请使用相同 ID） |
| `message` | string | **是** | 用户当前的提问 |

### 请求示例

```json
{
  "session_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "message": "请问商品审核包括哪些维度？"
}
```

### 响应体

| 字段 | 类型 | 说明 |
|------|------|------|
| `session_id` | string | 会话 ID（与请求一致） |
| `reply` | string | AI 回复内容 |

### 响应示例

```json
{
  "session_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "reply": "商品审核包括以下维度：1. 虚假宣传/夸大功效 2. 极限词/违禁词 3. 价格违规 4. 活动违规 5. 分类合规 6. 数据真实性 7. 标签/关键词违规 8. 侵权风险 9. 违禁品类 10. 其他违规"
}
```

---

## 附录

### 审核结论

| 结论 | 说明 |
|------|------|
| `通过` | 商品合规，可正常上架 |
| `不通过` | 存在违规，需整改后重新提交 |
| `需人工复核` | 系统无法确定，需要人工审核 |

### 违规严重等级

| 等级 | 说明 |
|------|------|
| `严重` | 涉及违禁品、严重虚假宣传、侵权等 |
| `中等` | 极限词、夸大功效、价格违规等 |
| `轻微` | 描述不规范、标签不准确等 |

### 整改优先级

| 优先级 | 说明 |
|--------|------|
| `高` | 必须立即整改，否则无法上架 |
| `中` | 建议尽快整改 |
| `低` | 建议优化，不影响上架 |

### 推荐建议

| 建议 | 说明 |
|------|------|
| `强烈推荐` | 商品质量好，性价比高 |
| `推荐` | 商品符合要求，可以推荐 |
| `谨慎购买` | 存在一定风险或问题 |
| `不推荐` | 商品存在严重问题 |

### 活动类型

| 值 | 说明 |
|----|------|
| `1` | 秒杀 |
| `2` | 满减 |

### 活动状态

| 值 | 说明 |
|----|------|
| `0` | 未开始 |
| `1` | 进行中 |
| `2` | 已结束 |

### 图片审核维度

| 维度 | 检测内容 |
|------|----------|
| 违规内容 | 色情低俗、暴力血腥、违禁品 |
| 广告夸大 | 虚假宣传、夸大功效、极限词 |
| 侵权 | 冒用品牌、盗用图片、侵犯肖像权 |
| 重复铺货 | 与其他商品高度相似 |
| 其他 | 引战内容、政治敏感 |

### 单商品审核维度

| # | 维度 | 检测内容 |
|---|------|----------|
| 1 | 虚假宣传/夸大功效 | 描述及营销标识是否涉及不实功效、虚假数据、夸大效果 |
| 2 | 极限词/违禁词 | 是否包含广告法限制用语（最、第一、顶级、唯一等） |
| 3 | 价格违规 | 售价与划线价的差异是否合理，是否存在先涨后降、价格欺诈 |
| 4 | 活动违规 | 活动价格是否真实优惠，活动描述是否与实际一致 |
| 5 | 分类合规 | 商品分类是否合理，是否存在错放类目逃避监管 |
| 6 | 数据真实性 | 销量、评价数、评分等数据是否存在明显造假嫌疑 |
| 7 | 标签/关键词违规 | 标签和关键词是否包含违规或误导性内容 |
| 8 | 侵权风险 | 是否冒用他人品牌、专利、著作权 |
| 9 | 违禁品类 | 是否涉及禁止或限制销售的商品类别 |
| 10 | 其他违规 | 引战内容、政治敏感、不当比较等 |

### 错误响应

当请求处理失败时，返回以下错误信息：

```json
{
  "error": "错误信息描述"
}
```

### HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 422 | 请求参数校验失败（字段缺失、类型错误等） |
| 500 | 服务器内部错误 |
