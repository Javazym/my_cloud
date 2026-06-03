import json
import asyncio
import time
import logging
from typing import Dict
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from langchain_openai import ChatOpenAI
from config.init_config import settings
from tools.tools import review_product, batch_review_products, review_description as tool_review_description, review_images as tool_review_images
from api.schemas import (
    ReviewProductRequest, BatchReviewRequest,
    ReviewDescriptionRequest, ReviewImagesRequest,
    ChatRequest, ChatResponse, ErrorResponse,
)

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger(__name__)

app = FastAPI(
    title="商品审核 API",
    description="AI 商品审核系统 — 支持单商品/批量审核、描述审核、图片审核、综合评价",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


async def _run_sync(fn, *args, **kwargs):
    loop = asyncio.get_event_loop()
    return await loop.run_in_executor(None, lambda: fn(*args, **kwargs))


_chat_llm = ChatOpenAI(
    model=settings.LLM_MODEL,
    base_url=settings.SILICONFLOW_BASE_URL,
    api_key=settings.SILICONFLOW_API_KEY,
    temperature=settings.LLM_TEMPERATURE,
    max_tokens=settings.LLM_MAX_TOKENS,
)

_CHAT_SYSTEM_PROMPT = "你是一个友好的AI助手，可以回答各种问题，帮助用户解决疑惑。"


# ── 会话管理（内存存储）──────────────────────────────

class SessionStore:
    def __init__(self, max_history: int = 20, ttl: int = 1800):
        self._sessions: Dict[str, list] = {}
        self._timestamps: Dict[str, float] = {}
        self.max_history = max_history
        self.ttl = ttl

    def get_history(self, session_id: str) -> list:
        self._cleanup()
        return self._sessions.get(session_id, [])

    def add_message(self, session_id: str, role: str, content: str):
        self._sessions.setdefault(session_id, [])
        self._sessions[session_id].append({"role": role, "content": content})
        if len(self._sessions[session_id]) > self.max_history:
            self._sessions[session_id] = self._sessions[session_id][-self.max_history:]
        self._timestamps[session_id] = time.time()

    def _cleanup(self):
        now = time.time()
        expired = [sid for sid, ts in self._timestamps.items() if now - ts > self.ttl]
        for sid in expired:
            del self._sessions[sid]
            del self._timestamps[sid]


session_store = SessionStore()


@app.get("/api/health")
async def health():
    return {"status": "ok", "service": "商品审核 API"}


@app.post("/api/chat", response_model=ChatResponse)
async def api_chat(req: ChatRequest):
    """与 AI 助手对话（纯对话，不调用审核工具），每个 session_id 独立维护上下文"""
    try:
        history = session_store.get_history(req.session_id)

        messages = [{"role": "system", "content": _CHAT_SYSTEM_PROMPT}]
        messages.extend(history)
        messages.append({"role": "user", "content": req.message})

        reply = await _run_sync(lambda: _chat_llm.invoke(messages))

        session_store.add_message(req.session_id, "user", req.message)
        session_store.add_message(req.session_id, "assistant", reply.content)

        return ChatResponse(session_id=req.session_id, reply=reply.content)
    except Exception as e:
        logger.exception("对话失败")
        raise HTTPException(status_code=500, detail=str(e))


def _req_to_product_kwargs(req: ReviewProductRequest) -> dict:
    """将 ReviewProductRequest 转换为 review_product 的 invoke 参数字典"""
    kwargs = {
        "name": req.name,
        "description": req.description,
        "image_urls": req.image_urls,
        "sub_name": req.sub_name,
        "merchant_name": req.merchant_name,
        "category_name": req.category_name,
        "stock": req.stock,
        "sold_count": req.sold_count,
        "review_count": req.review_count,
        "favorite_count": req.favorite_count,
        "rating": req.rating,
        "tags": req.tags,
        "keywords": req.keywords,
        "is_hot": req.is_hot,
        "is_featured": req.is_featured,
        "is_new": req.is_new,
    }
    if req.price_info:
        kwargs["price"] = req.price_info.price
        kwargs["original_price"] = req.price_info.original_price
    if req.activity:
        kwargs["has_activity"] = req.activity.has_activity
        kwargs["activity_type"] = req.activity.activity_type
        kwargs["activity_id"] = req.activity.activity_id
        kwargs["activity_name"] = req.activity.activity_name
        kwargs["activity_price"] = req.activity.activity_price
        kwargs["activity_start_time"] = req.activity.activity_start_time
        kwargs["activity_end_time"] = req.activity.activity_end_time
        kwargs["activity_status"] = req.activity.activity_status
    return {k: v for k, v in kwargs.items() if v is not None}


@app.post("/api/review/product")
async def api_review_product(req: ReviewProductRequest):
    """完整审核单个商品（描述+图片+综合评价），支持价格、分类、活动等追加信息"""
    try:
        kwargs = _req_to_product_kwargs(req)
        result = await _run_sync(lambda: review_product.invoke(kwargs))
        return json.loads(result)
    except Exception as e:
        logger.exception("审核商品失败")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/review/batch")
async def api_review_batch(req: BatchReviewRequest):
    """批量审核多个商品，支持价格、分类、活动等追加信息"""
    try:
        products_data = [_req_to_product_kwargs(p) for p in req.products]
        result = await _run_sync(
            lambda: batch_review_products.invoke(json.dumps(products_data, ensure_ascii=False))
        )
        return json.loads(result)
    except Exception as e:
        logger.exception("批量审核失败")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/review/description")
async def api_review_description(req: ReviewDescriptionRequest):
    """审核商品描述文本，可传入价格、分类、商家等补充信息用于更精准的违规检测"""
    try:
        kwargs = {"description": req.description}
        if req.product_name is not None:
            kwargs["product_name"] = req.product_name
        if req.price is not None:
            kwargs["price"] = req.price
        if req.original_price is not None:
            kwargs["original_price"] = req.original_price
        if req.category_name is not None:
            kwargs["category_name"] = req.category_name
        if req.merchant_name is not None:
            kwargs["merchant_name"] = req.merchant_name
        if req.tags is not None:
            kwargs["tags"] = req.tags
        result = await _run_sync(
            lambda: tool_review_description.invoke(kwargs)
        )
        return json.loads(result)
    except Exception as e:
        logger.exception("描述审核失败")
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/review/images")
async def api_review_images(req: ReviewImagesRequest):
    """审核一组商品图片"""
    try:
        result = await _run_sync(tool_review_images.func, req.urls)
        return json.loads(result)
    except Exception as e:
        logger.exception("图片审核失败")
        raise HTTPException(status_code=500, detail=str(e))
