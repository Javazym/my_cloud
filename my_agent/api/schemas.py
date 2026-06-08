from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel
from typing import List, Optional


class PriceInfoSchema(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
    price: Optional[float] = Field(default=None, description="当前售价")
    original_price: Optional[float] = Field(default=None, description="原始价格/划线价")


class ActivityInfoSchema(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
    has_activity: bool = Field(default=False, description="是否有活动")
    activity_type: Optional[int] = Field(default=None, description="活动类型：1-秒杀，2-满减")
    activity_id: Optional[int] = Field(default=None, description="活动ID")
    activity_name: Optional[str] = Field(default=None, description="活动名称")
    activity_price: Optional[float] = Field(default=None, description="活动价格")
    activity_start_time: Optional[str] = Field(default=None, description="活动开始时间（ISO格式）")
    activity_end_time: Optional[str] = Field(default=None, description="活动结束时间（ISO格式）")
    activity_status: Optional[int] = Field(default=None, description="活动状态：0-未开始，1-进行中，2-已结束")


class ReviewProductRequest(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
    name: str = Field(description="商品名称")
    sub_name: Optional[str] = Field(default=None, description="商品副标题")
    description: str = Field(description="商品描述文本")
    image_urls: Optional[List[str]] = Field(default=None, description="商品图片URL列表")

    # 商家信息
    merchant_name: Optional[str] = Field(default=None, description="商家名称")

    # 分类信息
    category_name: Optional[str] = Field(default=None, description="分类名称")

    # 价格信息
    price_info: Optional[PriceInfoSchema] = Field(default=None, description="价格信息")

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

    # 活动信息
    activity: Optional[ActivityInfoSchema] = Field(default=None, description="活动信息")


class BatchReviewRequest(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
    products: List[ReviewProductRequest] = Field(description="待审核的商品列表")


class ReviewDescriptionRequest(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
    description: str = Field(description="商品描述文本")
    product_name: Optional[str] = Field(default=None, description="商品名称")
    price: Optional[float] = Field(default=None, description="商品售价")
    original_price: Optional[float] = Field(default=None, description="商品划线价/原价")
    category_name: Optional[str] = Field(default=None, description="分类名称")
    merchant_name: Optional[str] = Field(default=None, description="商家名称")
    tags: Optional[str] = Field(default=None, description="商品标签")


class ReviewImagesRequest(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
    urls: List[str] = Field(description="商品图片URL列表")


class ChatRequest(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
    session_id: str = Field(description="会话ID（由客户端生成，如 UUID，同一用户请使用相同 ID）")
    message: str = Field(description="用户当前的提问")


class ChatResponse(BaseModel):
    session_id: str = Field(description="会话ID")
    reply: str = Field(description="AI 回复内容")


class ErrorResponse(BaseModel):
    error: str = Field(description="错误信息")
