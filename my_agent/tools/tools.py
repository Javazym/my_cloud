from langchain.tools import tool
from typing import List, Optional
from config.init_config import settings
from llms.init_chat_model import chat_llm, raw_openai_client
from prompts.prompts import (
    SingleImageReview, ImagesReviewOutput, DescriptionReviewOutput,
    ViolationItem, ImprovementItem, ProductEvaluation,
    SingleProductReviewOutput, BatchProductReviewOutput, ProductInput,
    PriceInfo, ActivityInfo,
)
import requests
import base64
import os
import json
import re

# ── 常量 ──────────────────────────────────────────

IMAGE_REVIEW_BASE = (
    "请分析这张商品图片，判断是否存在以下违规行为：\n"
    "1. 违规内容：色情低俗、暴力血腥、违禁品等\n"
    "2. 广告夸大：虚假宣传、夸大功效、极限词等\n"
    "3. 侵权：冒用品牌、盗用图片、侵犯肖像权等\n"
    "4. 重复铺货：与同店或平台其他商品高度相似\n"
    "5. 其他违规：引战内容、政治敏感等"
)

JSON_OUTPUT_INSTR = (
    '请以JSON格式返回（不要用markdown代码块包裹），格式如下：\n'
    '{"conclusion": "通过/不通过/需人工复核",\n'
    ' "violations": [{"type": "违规类型", "severity": "严重/中等/轻微", "detail": "详细说明"}],\n'
    ' "suggestion": "整改建议"}'
)

IMAGE_REVIEW_PROMPT = f"{IMAGE_REVIEW_BASE}\n\n{JSON_OUTPUT_INSTR}"


# ── 工具函数 ──────────────────────────────────────

def _extract_json(text: str) -> dict:
    """从 LLM 响应中提取 JSON 并解析为 dict"""
    text = text.strip()
    # 直接解析
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        pass
    # 尝试提取 ```json ... ``` 块
    m = re.search(r'```(?:json)?\s*\n?(.*?)```', text, re.DOTALL)
    if m:
        return json.loads(m.group(1))
    # 尝试找第一个 { 到最后一个 }
    s = text.index('{')
    e = text.rindex('}')
    return json.loads(text[s:e + 1])


def _encode_image(image_source: str) -> str:
    """从URL或本地路径加载图片并编码为base64"""
    if os.path.isfile(image_source):
        with open(image_source, 'rb') as f:
            return base64.b64encode(f.read()).decode()
    resp = requests.get(image_source, timeout=15)
    resp.raise_for_status()
    return base64.b64encode(resp.content).decode()


def _analyze_single_image(image_source: str, index: int = 1) -> SingleImageReview:
    """分析单张图片并返回结构化结果"""
    image_base64 = _encode_image(image_source)
    prompt_text = f"请分析第{index}张商品图片：\n{IMAGE_REVIEW_PROMPT}"
    messages = [
        {
            'role': 'user',
            'content': [
                {'type': 'text', 'text': prompt_text},
                {'type': 'image_url', 'image_url': {'url': f'data:image/jpeg;base64,{image_base64}'}},
            ],
        }
    ]

    try:
        response = raw_openai_client.chat.completions.create(
            model=settings.IMAGE_LLM_MODEL,
            messages=messages,
            stream=False,
            max_tokens=4096,
        )
        data = _extract_json(response.choices[0].message.content)
        return SingleImageReview(
            image_index=index,
            image_source=image_source,
            conclusion=data.get('conclusion', '需人工复核'),
            violations=[ViolationItem(**v) for v in data.get('violations', [])],
            suggestion=data.get('suggestion', ''),
        )
    except Exception as e:
        return SingleImageReview(
            image_index=index,
            image_source=image_source,
            conclusion='需人工复核',
            violations=[ViolationItem(type='系统错误', severity='中等', detail=str(e))],
            suggestion='请人工复核此图片',
        )


# ── Tools ─────────────────────────────────────────

@tool
def analyze_image(url: str) -> str:
    """审核单张商品图片（支持URL或本地路径），返回结构化JSON审核结果"""
    result = _analyze_single_image(url, 1)
    return result.model_dump_json(indent=2, ensure_ascii=False)


@tool
def review_images(urls: List[str]) -> str:
    """审核一组商品图片（传入多个URL或本地路径），返回结构化JSON综合审核结果"""
    image_results = [_analyze_single_image(url, i + 1) for i, url in enumerate(urls)]

    # 汇总
    images_json = json.dumps(
        [r.model_dump() for r in image_results],
        indent=2, ensure_ascii=False,
    )
    summary_prompt = f"""
以下是一组商品图片的审核结果，请汇总给出综合结论：

{images_json}

请以JSON格式返回（不要用markdown代码块包裹）：
{{"overall_conclusion": "通过/不通过/需人工复核",
  "violation_summary": "违规项汇总说明",
  "suggestion": "综合整改建议"}}
"""
    try:
        resp = chat_llm.invoke([{"role": "user", "content": summary_prompt}])
        summary_data = _extract_json(resp.content)
    except Exception as e:
        summary_data = {"overall_conclusion": "需人工复核", "violation_summary": f"汇总失败: {e}", "suggestion": "请人工复核"}

    output = ImagesReviewOutput(
        overall_conclusion=summary_data.get('overall_conclusion', '需人工复核'),
        images=image_results,
        violation_summary=summary_data.get('violation_summary', ''),
        suggestion=summary_data.get('suggestion', ''),
    )
    return output.model_dump_json(indent=2, ensure_ascii=False)


def _build_description_prompt(product: ProductInput) -> str:
    """根据商品完整信息构建描述审核提示词"""
    lines = ["请审核以下商品信息，从所有维度逐一检查是否存在违规："]
    lines.append("")
    lines.append(f"商品名称: {product.name}")
    if product.sub_name:
        lines.append(f"副标题: {product.sub_name}")
    if product.merchant_name:
        lines.append(f"商家: {product.merchant_name}")
    if product.category_name:
        lines.append(f"分类: {product.category_name}")
    if product.tags:
        lines.append(f"标签: {product.tags}")
    if product.keywords:
        lines.append(f"关键词: {product.keywords}")
    lines.append("")
    lines.append("--- 商品描述 ---")
    lines.append(product.description)
    lines.append("---")
    lines.append("")

    # 价格信息
    if product.price_info:
        lines.append("--- 价格信息 ---")
        lines.append(f"售价: {product.price_info.price}")
        if product.price_info.original_price:
            lines.append(f"划线价/原价: {product.price_info.original_price}")
        lines.append("---")
        lines.append("")

    # 库存与销量
    sales_info = []
    if product.stock is not None:
        sales_info.append(f"库存: {product.stock}")
    if product.sold_count is not None:
        sales_info.append(f"已售: {product.sold_count}")
    if product.review_count is not None:
        sales_info.append(f"评价数: {product.review_count}")
    if product.favorite_count is not None:
        sales_info.append(f"收藏数: {product.favorite_count}")
    if product.rating is not None:
        sales_info.append(f"评分: {product.rating}")
    if sales_info:
        lines.append("--- 销售数据 ---")
        lines.append("，".join(sales_info))
        lines.append("---")
        lines.append("")

    # 活动信息
    if product.activity and product.activity.has_activity:
        lines.append("--- 活动信息 ---")
        act = product.activity
        lines.append(f"活动名称: {act.activity_name or '未知'}")
        lines.append(f"活动类型: {'秒杀' if act.activity_type == 1 else '满减' if act.activity_type == 2 else '其他'}")
        if act.activity_price is not None:
            lines.append(f"活动价格: {act.activity_price}")
        if act.activity_start_time:
            lines.append(f"开始时间: {act.activity_start_time}")
        if act.activity_end_time:
            lines.append(f"结束时间: {act.activity_end_time}")
        status_map = {0: '未开始', 1: '进行中', 2: '已结束'}
        lines.append(f"活动状态: {status_map.get(act.activity_status, '未知')}")
        lines.append("---")
        lines.append("")

    # 营销标识
    flags = []
    if product.is_hot:
        flags.append("热卖")
    if product.is_featured:
        flags.append("精选")
    if product.is_new:
        flags.append("新品")
    if flags:
        lines.append(f"营销标识: {'、'.join(flags)}")
        lines.append("")

    lines.append("审核维度（请逐项检查）：")
    lines.append("1. 虚假宣传/夸大功效：描述及营销标识是否涉及不实功效、虚假数据、夸大效果")
    lines.append("2. 极限词/违禁词：是否包含广告法限制用语（最、第一、顶级、唯一等）")
    lines.append("3. 价格违规：售价与划线价的差异是否合理，是否存在先涨后降、价格欺诈")
    lines.append("4. 活动违规：活动价格是否真实优惠，活动描述是否与实际一致，活动时间是否合理")
    lines.append("5. 分类合规：商品分类是否合理，是否存在错放类目逃避监管")
    lines.append("6. 数据真实性：销量、评价数、评分等数据是否存在明显造假嫌疑")
    lines.append("7. 标签/关键词违规：标签和关键词是否包含违规或误导性内容")
    lines.append("8. 侵权风险：是否冒用他人品牌、专利、著作权")
    lines.append("9. 违禁品类：是否涉及禁止或限制销售的商品类别")
    lines.append("10. 其他违规：引战内容、政治敏感、不当比较等")
    lines.append("")
    lines.append(JSON_OUTPUT_INSTR)

    return "\n".join(lines)


@tool
def review_description(description: str,
                       product_name: Optional[str] = None,
                       price: Optional[float] = None,
                       original_price: Optional[float] = None,
                       category_name: Optional[str] = None,
                       merchant_name: Optional[str] = None,
                       tags: Optional[str] = None,
                       ) -> str:
    """审核商品描述文本，可传入价格、分类、商家等补充信息用于更精准的违规检测"""
    product = ProductInput(
        name=product_name or "未知商品",
        description=description,
        category_name=category_name,
        merchant_name=merchant_name,
        tags=tags,
        price_info=PriceInfo(price=price, original_price=original_price) if price is not None else None,
    )
    prompt = _build_description_prompt(product)
    try:
        response = chat_llm.invoke([{"role": "user", "content": prompt}])
        data = _extract_json(response.content)
        result = DescriptionReviewOutput(
            conclusion=data.get('conclusion', '需人工复核'),
            violations=[ViolationItem(**v) for v in data.get('violations', [])],
            suggestion=data.get('suggestion', ''),
        )
    except Exception as e:
        result = DescriptionReviewOutput(
            conclusion='需人工复核',
            violations=[ViolationItem(type='系统错误', severity='中等', detail=str(e))],
            suggestion='请人工复核此描述',
        )
    return result.model_dump_json(indent=2, ensure_ascii=False)


@tool
def fetch_url(url: str) -> str:
    """获取指定URL的文本内容"""
    resp = requests.get(url, timeout=10)
    resp.raise_for_status()
    return resp.text


@tool
def answer_question(question: str) -> str:
    """让大模型回答用户问题"""
    messages = [
        {"role": "system", "content": "你是一个专业的AI助手。"},
        {"role": "user", "content": question}
    ]
    response = chat_llm.invoke(messages)
    return response.content


def _build_evaluation(product: ProductInput) -> ProductEvaluation:
    """根据商品完整信息生成综合评价"""
    lines = ["请根据以下商品完整信息，给出该商品的综合评价："]
    lines.append("")
    lines.append(f"商品名称: {product.name}")
    if product.sub_name:
        lines.append(f"副标题: {product.sub_name}")
    if product.category_name:
        lines.append(f"分类: {product.category_name}")
    if product.merchant_name:
        lines.append(f"商家: {product.merchant_name}")
    if product.price_info:
        p = product.price_info
        lines.append(f"售价: {p.price}" + (f"，划线价: {p.original_price}" if p.original_price else ""))
    if product.stock is not None:
        lines.append(f"库存: {product.stock}")
    if product.sold_count is not None:
        lines.append(f"已售: {product.sold_count}")
    if product.review_count is not None:
        lines.append(f"评价数: {product.review_count}")
    if product.favorite_count is not None:
        lines.append(f"收藏数: {product.favorite_count}")
    if product.rating is not None:
        lines.append(f"评分: {product.rating}")
    if product.tags:
        lines.append(f"标签: {product.tags}")
    if product.keywords:
        lines.append(f"关键词: {product.keywords}")
    marketing = []
    if product.is_hot: marketing.append("热卖")
    if product.is_featured: marketing.append("精选")
    if product.is_new: marketing.append("新品")
    if marketing:
        lines.append(f"营销标识: {'、'.join(marketing)}")
    lines.append("")
    lines.append("--- 商品描述 ---")
    lines.append(product.description)
    lines.append("---")

    if product.activity and product.activity.has_activity:
        lines.append("")
        lines.append("--- 活动信息 ---")
        a = product.activity
        lines.append(f"{a.activity_name or '活动'}，类型={'秒杀' if a.activity_type==1 else '满减' if a.activity_type==2 else '其他'}"
                     f"{f'，活动价: {a.activity_price}' if a.activity_price is not None else ''}")
        lines.append("---")

    lines.append("")
    lines.append("请以JSON格式返回（不要用markdown代码块包裹）：")
    lines.append('{"rating": 评分(1-5),')
    lines.append('  "pros": ["优点1", "优点2"],')
    lines.append('  "cons": ["缺点/风险1", "缺点/风险2"],')
    lines.append('  "overall_evaluation": "综合评价描述",')
    lines.append('  "recommendation": "强烈推荐/推荐/谨慎购买/不推荐"}')

    eval_prompt = "\n".join(lines)
    try:
        resp = chat_llm.invoke([{"role": "user", "content": eval_prompt}])
        data = _extract_json(resp.content)
        return ProductEvaluation(
            rating=float(data.get('rating', 3)),
            pros=data.get('pros', []),
            cons=data.get('cons', []),
            overall_evaluation=data.get('overall_evaluation', ''),
            recommendation=data.get('recommendation', '谨慎购买'),
        )
    except Exception as e:
        return ProductEvaluation(
            rating=3.0,
            pros=[],
            cons=[],
            overall_evaluation=f'评价生成失败: {e}',
            recommendation='谨慎购买',
        )


def _build_improvements(violations: List[ViolationItem]) -> List[ImprovementItem]:
    """从违规项生成整改项"""
    priority_map = {'严重': '高', '中等': '中', '轻微': '低'}
    improvements = []
    for v in violations:
        improvements.append(ImprovementItem(
            item=f"修复「{v.type}」问题",
            priority=priority_map.get(v.severity, '中'),
            detail=v.detail,
        ))
    if not improvements:
        improvements.append(ImprovementItem(
            item='无需整改',
            priority='低',
            detail='商品审核通过，无需整改',
        ))
    return improvements


@tool
def review_product(
    name: str,
    description: str,
    image_urls: Optional[List[str]] = None,
    sub_name: Optional[str] = None,
    merchant_id: Optional[int] = None,
    merchant_name: Optional[str] = None,
    category_id: Optional[int] = None,
    category_name: Optional[str] = None,
    price: Optional[float] = None,
    original_price: Optional[float] = None,
    stock: Optional[int] = None,
    sold_count: Optional[int] = None,
    review_count: Optional[int] = None,
    favorite_count: Optional[int] = None,
    rating: Optional[float] = None,
    tags: Optional[str] = None,
    keywords: Optional[str] = None,
    is_hot: Optional[bool] = None,
    is_featured: Optional[bool] = None,
    is_new: Optional[bool] = None,
    publish_status: Optional[int] = None,
    audit_status: Optional[int] = None,
    # 活动信息（通过单独的字段传入）
    has_activity: Optional[bool] = None,
    activity_type: Optional[int] = None,
    activity_id: Optional[int] = None,
    activity_name: Optional[str] = None,
    activity_price: Optional[float] = None,
    activity_start_time: Optional[str] = None,
    activity_end_time: Optional[str] = None,
    activity_status: Optional[int] = None,
) -> str:
    """完整审核单个商品（描述+图片+综合评价），返回结构化JSON审核报告，包含审核结论、不通过原因、整改项、商品评价。支持传入价格、分类、活动等追加信息用于精准审核。"""
    # 构建完整商品对象
    activity = None
    if has_activity:
        activity = ActivityInfo(
            has_activity=True,
            activity_type=activity_type,
            activity_id=activity_id,
            activity_name=activity_name,
            activity_price=activity_price,
            activity_start_time=activity_start_time,
            activity_end_time=activity_end_time,
            activity_status=activity_status,
        )

    product = ProductInput(
        name=name,
        sub_name=sub_name,
        description=description,
        image_urls=image_urls or [],
        merchant_id=merchant_id,
        merchant_name=merchant_name,
        category_id=category_id,
        category_name=category_name,
        price_info=PriceInfo(price=price, original_price=original_price) if price is not None else None,
        stock=stock,
        sold_count=sold_count,
        review_count=review_count,
        favorite_count=favorite_count,
        rating=rating,
        tags=tags,
        keywords=keywords,
        is_hot=is_hot,
        is_featured=is_featured,
        is_new=is_new,
        publish_status=publish_status,
        audit_status=audit_status,
        activity=activity,
    )

    # 审核描述（传入补充信息）
    desc_review_input = {
        "description": description,
        "product_name": name,
        "price": price,
        "original_price": original_price,
        "category_name": category_name,
        "merchant_name": merchant_name,
        "tags": tags,
    }
    desc_review_input = {k: v for k, v in desc_review_input.items() if v is not None}
    desc_review_raw = review_description.invoke(desc_review_input)
    desc_review_data = json.loads(desc_review_raw)
    desc_review = DescriptionReviewOutput(
        conclusion=desc_review_data.get('conclusion', '需人工复核'),
        violations=[ViolationItem(**v) for v in desc_review_data.get('violations', [])],
        suggestion=desc_review_data.get('suggestion', ''),
    )

    # 审核图片
    images_review = None
    if image_urls:
        images_review_raw = review_images.invoke({"urls": image_urls})
        images_review_data = json.loads(images_review_raw)
        images_review = ImagesReviewOutput(
            overall_conclusion=images_review_data.get('overall_conclusion', '需人工复核'),
            images=[SingleImageReview(**img) for img in images_review_data.get('images', [])],
            violation_summary=images_review_data.get('violation_summary', ''),
            suggestion=images_review_data.get('suggestion', ''),
        )

    # 综合评价（使用完整商品信息）
    evaluation = _build_evaluation(product)

    # 汇总所有违规
    all_violations = list(desc_review.violations)
    if images_review:
        for img in images_review.images:
            all_violations.extend(img.violations)

    improvements = _build_improvements(all_violations)

    # 确定最终结论
    conclusions = [desc_review.conclusion]
    if images_review:
        conclusions.append(images_review.overall_conclusion)

    if '不通过' in conclusions:
        conclusion = '不通过'
        fail_reason = '；'.join([
            f"描述审核不通过" if desc_review.conclusion == '不通过' else '',
            f"图片审核不通过" if images_review and images_review.overall_conclusion == '不通过' else '',
        ])
        fail_reason = fail_reason.strip('；')
    elif '需人工复核' in conclusions:
        conclusion = '需人工复核'
        fail_reason = ''
    else:
        conclusion = '通过'
        fail_reason = ''

    # 汇总建议
    suggestion_parts = [desc_review.suggestion]
    if images_review and images_review.suggestion:
        suggestion_parts.append(images_review.suggestion)
    suggestion = '；'.join(suggestion_parts)

    output = SingleProductReviewOutput(
        product_name=name,
        description_review=desc_review,
        images_review=images_review,
        evaluation=evaluation,
        conclusion=conclusion,
        fail_reason=fail_reason,
        improvements=improvements,
        suggestion=suggestion,
    )
    return output.model_dump_json(indent=2, ensure_ascii=False)


def _build_product_kwargs(p: ProductInput) -> dict:
    """将 ProductInput 转换为 review_product 的关键字参数"""
    kwargs = {
        "name": p.name,
        "description": p.description,
        "image_urls": p.image_urls or None,
        "sub_name": p.sub_name,
        "merchant_id": p.merchant_id,
        "merchant_name": p.merchant_name,
        "category_id": p.category_id,
        "category_name": p.category_name,
        "stock": p.stock,
        "sold_count": p.sold_count,
        "review_count": p.review_count,
        "favorite_count": p.favorite_count,
        "rating": p.rating,
        "tags": p.tags,
        "keywords": p.keywords,
        "is_hot": p.is_hot,
        "is_featured": p.is_featured,
        "is_new": p.is_new,
        "publish_status": p.publish_status,
        "audit_status": p.audit_status,
    }
    if p.price_info:
        kwargs["price"] = p.price_info.price
        kwargs["original_price"] = p.price_info.original_price
    if p.activity:
        kwargs["has_activity"] = p.activity.has_activity
        kwargs["activity_type"] = p.activity.activity_type
        kwargs["activity_id"] = p.activity.activity_id
        kwargs["activity_name"] = p.activity.activity_name
        kwargs["activity_price"] = p.activity.activity_price
        kwargs["activity_start_time"] = p.activity.activity_start_time
        kwargs["activity_end_time"] = p.activity.activity_end_time
        kwargs["activity_status"] = p.activity.activity_status
    return {k: v for k, v in kwargs.items() if v is not None}


@tool
def batch_review_products(products_json: str) -> str:
    """批量审核多个商品。传入JSON数组，每个商品可包含 name, description, image_urls, price_info, activity 等字段"""
    try:
        products_data = json.loads(products_json)
        products = [ProductInput(**p) for p in products_data]
    except Exception as e:
        return json.dumps({"error": f"解析输入失败: {e}"}, indent=2, ensure_ascii=False)

    results = []
    for p in products:
        single_result = json.loads(review_product.invoke(_build_product_kwargs(p)))
        results.append(SingleProductReviewOutput(**single_result))

    passed = sum(1 for r in results if r.conclusion == '通过')
    failed = sum(1 for r in results if r.conclusion == '不通过')
    manual = sum(1 for r in results if r.conclusion == '需人工复核')

    output = BatchProductReviewOutput(
        total_count=len(results),
        passed_count=passed,
        failed_count=failed,
        manual_review_count=manual,
        products=results,
        overall_summary=f"共审核 {len(results)} 个商品，通过 {passed} 个，不通过 {failed} 个，需人工复核 {manual} 个。",
    )
    return output.model_dump_json(indent=2, ensure_ascii=False)

    