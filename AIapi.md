# 商品审核 API 文档

Base URL: `http://localhost:8000`

---

## 目录

- [1. 健康检查](#1-健康检查)
- [2. 单商品审核](#2-单商品审核)
- [3. 批量商品审核](#3-批量商品审核)
- [4. 描述审核](#4-描述审核)
- [5. 图片审核](#5-图片审核)
- [6. AI 对话](#6-ai-对话)
- [附录：响应模型](#附录响应模型)

---

## 1. 健康检查

```
GET /api/health
```

**响应示例：**

```json
{
  "status": "ok",
  "service": "商品审核 API"
}
```

---

## 2. 单商品审核

完整审核单个商品（描述 + 图片 + 综合评价）。

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
| `tags` | string | 否 | 商品标签（逗号分隔） |
| `keywords` | string | 否 | 商品关键词 |
| `stock` | integer | 否 | 库存数量 |
| `sold_count` | integer | 否 | 已售数量 |
| `review_count` | integer | 否 | 评价数量 |
| `favorite_count` | integer | 否 | 收藏数量 |
| `rating` | float | 否 | 评分（1-5分） |
| `is_hot` | boolean | 否 | 是否热卖 |
| `is_featured` | boolean | 否 | 是否精选 |
| `is_new` | boolean | 否 | 是否新品 |
| `price_info` | object | 否 | 价格信息 |
| `price_info.price` | float | 否 | 当前售价 |
| `price_info.original_price` | float | 否 | 划线价/原价 |
| `activity` | object | 否 | 活动信息 |
| `activity.has_activity` | boolean | 否 | 是否有活动 |
| `activity.activity_type` | integer | 否 | 活动类型：1-秒杀，2-满减 |
| `activity.activity_id` | integer | 否 | 活动ID |
| `activity.activity_name` | string | 否 | 活动名称 |
| `activity.activity_price` | float | 否 | 活动价格 |
| `activity.activity_start_time` | string(ISO) | 否 | 活动开始时间 |
| `activity.activity_end_time` | string(ISO) | 否 | 活动结束时间 |
| `activity.activity_status` | integer | 否 | 活动状态：0-未开始，1-进行中，2-已结束 |

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

```json
{
  "product_name": "string",
  "description_review": {
    "conclusion": "通过 | 不通过 | 需人工复核",
    "violations": [
      {
        "type": "string",
        "severity": "严重 | 中等 | 轻微",
        "detail": "string"
      }
    ],
    "suggestion": "string"
  },
  "images_review": {
    "overall_conclusion": "通过 | 不通过 | 需人工复核",
    "images": [
      {
        "image_index": 1,
        "image_source": "string",
        "conclusion": "通过 | 不通过 | 需人工复核",
        "violations": [],
        "suggestion": "string"
      }
    ],
    "violation_summary": "string",
    "suggestion": "string"
  },
  "evaluation": {
    "rating": 4.5,
    "pros": ["string"],
    "cons": ["string"],
    "overall_evaluation": "string",
    "recommendation": "强烈推荐 | 推荐 | 谨慎购买 | 不推荐"
  },
  "conclusion": "通过 | 不通过 | 需人工复核",
  "fail_reason": "string",
  "improvements": [
    {
      "item": "string",
      "priority": "高 | 中 | 低",
      "detail": "string"
    }
  ],
  "suggestion": "string"
}
```

### 审核维度

系统从以下 10 个维度进行全面审核：

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

---

## 3. 批量商品审核

```
POST /api/review/batch
```

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `products` | array | **是** | 商品列表，每项结构与单商品审核请求体相同 |

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

```json
{
  "total_count": 2,
  "passed_count": 1,
  "failed_count": 1,
  "manual_review_count": 0,
  "products": [
    {
      "product_name": "商品A",
      "description_review": {},
      "evaluation": {},
      "conclusion": "通过",
      "fail_reason": "",
      "improvements": [],
      "suggestion": ""
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

```json
{
  "conclusion": "通过 | 不通过 | 需人工复核",
  "violations": [
    {
      "type": "极限词/违禁词",
      "severity": "严重 | 中等 | 轻微",
      "detail": "使用了违禁词「全网最低价」「唯一」"
    }
  ],
  "suggestion": "请删除极限词「全网最低价」「唯一」，修改为合规表述"
}
```

---

## 5. 图片审核

审核一组商品图片。

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

```json
{
  "overall_conclusion": "通过 | 不通过 | 需人工复核",
  "images": [
    {
      "image_index": 1,
      "image_source": "https://example.com/images/product-1.jpg",
      "conclusion": "通过 | 不通过 | 需人工复核",
      "violations": [
        {
          "type": "违规类型",
          "severity": "严重 | 中等 | 轻微",
          "detail": "违规详情"
        }
      ],
      "suggestion": "整改建议"
    }
  ],
  "violation_summary": "违规项汇总说明",
  "suggestion": "综合整改建议"
}
```

### 图片审核维度

- 违规内容：色情低俗、暴力血腥、违禁品
- 广告夸大：虚假宣传、夸大功效、极限词
- 侵权：冒用品牌、盗用图片、侵犯肖像权
- 重复铺货：与其他商品高度相似
- 其他：引战内容、政治敏感

---

## 6. AI 对话

与 AI 助手对话（纯对话，不调用审核工具）。

```
POST /api/chat
```

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `session_id` | string | **是** | 会话ID（由客户端生成，如UUID，同一用户请使用相同ID） |
| `message` | string | **是** | 用户当前的提问 |

### 请求示例

```json
{
  "session_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "message": "请问商品审核包括哪些维度？"
}
```

### 响应体

```json
{
  "session_id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "reply": "商品审核包括描述审核、图片审核、综合评价等..."
}
```

---

## 附录：响应模型

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

### 错误响应

```json
{
  "error": "错误信息描述"
}
```

HTTP 状态码：

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 500 | 服务器内部错误 |
