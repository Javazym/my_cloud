from typing import Literal
from langgraph.graph import StateGraph, END
from langgraph.prebuilt import ToolNode
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage
from config.init_config import settings
from tools.tools import analyze_image, review_images, review_description, answer_question, fetch_url, review_product, batch_review_products
from state.state import AgentState

tools = [analyze_image, review_images, review_description, answer_question, fetch_url, review_product, batch_review_products]

# 流式推理模型（支持 token 级流式输出）
stream_llm = ChatOpenAI(
    model=settings.LLM_MODEL,
    base_url=settings.OPENAI_BASE_URL,
    api_key=settings.OPENAI_API_KEY,
    temperature=settings.LLM_TEMPERATURE,
    max_tokens=settings.LLM_MAX_TOKENS,
    streaming=True,
)

SYSTEM_PROMPT = """你是一个专业的商品审核助手，负责审核商品图片和描述是否存在违规行为，并给出综合评价。

可用工具：
1. analyze_image(url) — 审核单张商品图片（URL或本地路径），返回结构化JSON
2. review_images(urls) — 审核一组商品图片并汇总综合结论，返回结构化JSON
3. review_description(description) — 审核商品描述文本，返回结构化JSON
4. fetch_url(url) — 获取指定URL的文本内容
5. answer_question(question) — 让大模型回答用户问题
6. review_product(name, description, image_urls) — 完整审核单个商品（描述+图片+综合评价），输出包含：审核结论、不通过原因、整改项、商品评分/评价
7. batch_review_products(products_json) — 批量审核多个商品，传入JSON数组格式：[{"name":"商品名","description":"描述","image_urls":["url1","url2"]}]

工作流程：
- 收到审核任务后，先调用相应工具获取结构化结果
- 根据结构化结果用中文向用户输出友好的审核结论
- 对于需要完整审核的商品，使用 review_product 可获得描述审核+图片审核+综合评价+整改建议+不通过原因
- 对于多个商品（2个及以上），优先使用 batch_review_products 批量审核"""


def should_continue(state: AgentState) -> Literal["tools", END]:
    last = state["messages"][-1]
    if hasattr(last, "tool_calls") and last.tool_calls:
        return "tools"
    return END


def call_model(state: AgentState):
    messages = [SystemMessage(content=SYSTEM_PROMPT)] + state["messages"]
    response = stream_llm.invoke(messages)
    return {"messages": [response]}


workflow = StateGraph(AgentState)
workflow.add_node("agent", call_model)
workflow.add_node("tools", ToolNode(tools))
workflow.set_entry_point("agent")
workflow.add_conditional_edges("agent", should_continue)
workflow.add_edge("tools", "agent")

agent = workflow.compile()
