from pydantic import BaseModel, Field
from typing import List, Optional

# ── 工具输入格式 ──────────────────────────────────

class ImageToolInput(BaseModel):
    image_path: str = Field(description="图片的URL或本地路径")
    prompt: str = Field(description="分析图片的提示词")

class ImagesReviewInput(BaseModel):
    urls: list[str] = Field(description="一组图片的URL或本地路径列表")

class DescriptionReviewInput(BaseModel):
    description: str = Field(description="商品描述文本")

class FetchUrlInput(BaseModel):
    url: str = Field(description="要获取内容的URL")

class QuestionInput(BaseModel):
    question: str = Field(description="要回答的问题")

class PriceInfo(BaseModel):
    price: Optional[float] = Field(default=None, description="当前售价")
    original_price: Optional[float] = Field(default=None, description="原始价格/划线价")
    cost_price: Optional[float] = Field(default=None, description="成本价（内部参考）")


class ActivityInfo(BaseModel):
    has_activity: bool = Field(default=False, description="是否有活动")
    activity_type: Optional[int] = Field(default=None, description="活动类型：1-秒杀，2-满减")
    activity_id: Optional[int] = Field(default=None, description="活动ID")
    activity_name: Optional[str] = Field(default=None, description="活动名称")
    activity_price: Optional[float] = Field(default=None, description="活动价格（秒杀价或优惠后价格）")
    activity_start_time: Optional[str] = Field(default=None, description="活动开始时间（ISO格式）")
    activity_end_time: Optional[str] = Field(default=None, description="活动结束时间（ISO格式）")
    activity_status: Optional[int] = Field(default=None, description="活动状态：0-未开始，1-进行中，2-已结束")


class ProductInput(BaseModel):
    name: str = Field(description="商品名称")
    sub_name: Optional[str] = Field(default=None, description="商品副标题")
    description: str = Field(description="商品描述文本")
    image_urls: List[str] = Field(default_factory=list, description="商品图片URL列表")

    # 商家信息
    merchant_id: Optional[int] = Field(default=None, description="商家ID")
    merchant_name: Optional[str] = Field(default=None, description="商家名称")

    # 分类信息
    category_id: Optional[int] = Field(default=None, description="分类ID")
    category_name: Optional[str] = Field(default=None, description="分类名称")

    # 价格信息
    price_info: Optional[PriceInfo] = Field(default=None, description="价格信息")

    # 库存与销量
    stock: Optional[int] = Field(default=None, description="库存数量")
    sold_count: Optional[int] = Field(default=None, description="已售数量")
    review_count: Optional[int] = Field(default=None, description="评价数量")
    favorite_count: Optional[int] = Field(default=None, description="收藏数量")
    rating: Optional[float] = Field(default=None, description="评分（1-5分）")

    # 标签与关键词
    tags: Optional[str] = Field(default=None, description="商品标签（逗号分隔）")
    keywords: Optional[str] = Field(default=None, description="商品关键词")

    # 营销标识
    is_hot: Optional[bool] = Field(default=None, description="是否热卖")
    is_featured: Optional[bool] = Field(default=None, description="是否精选")
    is_new: Optional[bool] = Field(default=None, description="是否新品")

    # 当前状态
    publish_status: Optional[int] = Field(default=None, description="发布状态")
    audit_status: Optional[int] = Field(default=None, description="审核状态")

    # 活动信息
    activity: Optional[ActivityInfo] = Field(default=None, description="活动信息")

class BatchProductReviewInput(BaseModel):
    products: List[ProductInput] = Field(description="待审核的商品列表")

# ── 结构化输出格式 ────────────────────────────────

class ViolationItem(BaseModel):
    type: str = Field(description="违规类型，如：虚假宣传/极限词/侵权/违禁品类/价格违规/违规内容/广告夸大/重复铺货/其他")
    severity: str = Field(description="违规等级：严重 / 中等 / 轻微")
    detail: str = Field(description="违规详情说明")

class ImprovementItem(BaseModel):
    item: str = Field(description="需要整改的具体项目")
    priority: str = Field(description="优先级：高 / 中 / 低")
    detail: str = Field(description="整改说明")

class SingleImageReview(BaseModel):
    image_index: int = Field(description="图片编号(从1开始)")
    image_source: str = Field(description="图片来源URL或路径")
    conclusion: str = Field(description="审核结论：通过 / 不通过 / 需人工复核")
    violations: List[ViolationItem] = Field(default_factory=list, description="违规项列表")
    suggestion: str = Field(default="", description="整改建议")

class ImagesReviewOutput(BaseModel):
    overall_conclusion: str = Field(description="总体结论：通过 / 不通过 / 需人工复核")
    images: List[SingleImageReview] = Field(description="各图片审核结果")
    violation_summary: str = Field(default="", description="所有图片违规项的汇总说明")
    suggestion: str = Field(default="", description="综合整改建议")

class DescriptionReviewOutput(BaseModel):
    conclusion: str = Field(description="审核结论：通过 / 不通过 / 需人工复核")
    violations: List[ViolationItem] = Field(default_factory=list, description="违规项列表")
    suggestion: str = Field(default="", description="整改建议")

class ProductEvaluation(BaseModel):
    rating: float = Field(description="综合评分（1-5分）", ge=1, le=5)
    pros: List[str] = Field(default_factory=list, description="商品优点")
    cons: List[str] = Field(default_factory=list, description="商品缺点/风险")
    overall_evaluation: str = Field(description="综合评价描述")
    recommendation: str = Field(description="推荐建议：强烈推荐 / 推荐 / 谨慎购买 / 不推荐")

class SingleProductReviewOutput(BaseModel):
    product_name: str = Field(description="商品名称")
    description_review: DescriptionReviewOutput = Field(description="描述审核结果")
    images_review: Optional[ImagesReviewOutput] = Field(default=None, description="图片审核结果")
    evaluation: ProductEvaluation = Field(description="商品综合评价")
    conclusion: str = Field(description="最终结论：通过 / 不通过 / 需人工复核")
    fail_reason: str = Field(default="", description="审核不通过的原因（如通过则为空）")
    improvements: List[ImprovementItem] = Field(default_factory=list, description="需要整改的地方")
    suggestion: str = Field(default="", description="综合整改建议")

class BatchProductReviewOutput(BaseModel):
    total_count: int = Field(description="审核商品总数")
    passed_count: int = Field(description="通过数量")
    failed_count: int = Field(description="不通过数量")
    manual_review_count: int = Field(description="需人工复核数量")
    products: List[SingleProductReviewOutput] = Field(description="各商品审核结果")
    overall_summary: str = Field(description="批量审核总体总结")