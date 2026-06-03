from langchain.chat_models import init_chat_model
from langchain_openai import ChatOpenAI
from config.init_config import settings
import openai

chat_llm = init_chat_model(
    model_provider="openai",
    model=settings.IMAGE_LLM_MODEL,
    base_url=settings.SILICONFLOW_BASE_URL,
    api_key=settings.SILICONFLOW_API_KEY,
    temperature=settings.LLM_TEMPERATURE,
    max_tokens=settings.LLM_MAX_TOKENS,
)
image_llm = ChatOpenAI(
    model=settings.IMAGE_LLM_MODEL,
    base_url=settings.SILICONFLOW_BASE_URL,
    api_key=settings.SILICONFLOW_API_KEY,
    temperature=settings.LLM_TEMPERATURE,
    max_tokens=settings.LLM_MAX_TOKENS,
)



# 原生 OpenAI 客户端，专门给图生图用
raw_openai_client = openai.OpenAI(
    base_url=settings.SILICONFLOW_BASE_URL,
    api_key=settings.SILICONFLOW_API_KEY
)

__all__ = ["chat_llm", "image_llm", "raw_openai_client"]