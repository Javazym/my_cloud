# llms/__init__.py
from langchain.chat_models import init_chat_model
from langchain_openai import ChatOpenAI
from config.init_config import settings

llm = init_chat_model(
    model_name="deepseek-ai/DeepSeek-R1",
    model_provider="openai",
    model=settings.LLM_MODEL,
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

    
__all__ = ["llm", "image_llm"]