from pydantic_settings import BaseSettings
from dotenv import load_dotenv

load_dotenv()

class Settings(BaseSettings):
    # 硅基 API
    SILICONFLOW_API_KEY: str
    SILICONFLOW_BASE_URL: str
    
    # 模型配置（全部在这里改！）
    IMAGE_LLM_MODEL: str
    LLM_MODEL: str
    LLM_TEMPERATURE: float = 0.7
    LLM_MAX_TOKENS: int = 1024
    LLM_STREAMING: bool = False

# 全局配置单例
settings = Settings()