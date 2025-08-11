import os

from dotenv import load_dotenv
from openai import AsyncOpenAI
from openai.types.chat.chat_completion import ChatCompletion

load_dotenv()


client = AsyncOpenAI(
    api_key=os.environ.get("OPENAI_API_KEY"), base_url=os.environ.get("OPENAI_BASE_URL")
)


def fetch_current_weather(city: str, unit="Celsius") -> str:
    return "It's sunny and warm."


# 这里定义工具列表，其中包含了 fetch_current_weather 函数的描述和参数
# 模型会根据工具描述，动态的获取所需的实时数据提供给我们的系统
tools = [
    {
        "type": "function",
        "function": {
            "name": "fetch_current_weather",
            "description": "Get the current weather in a given location",
            "parameters": {
                "type": "object",
                "properties": {
                    "city": {
                        "type": "string",
                        "description": "The city for which to fetch the weather",
                    },
                    "unit": {},
                },
                "required": ["city"],
            },
        },
    }
]


async def main():
    response: ChatCompletion = await client.chat.completions.create(
        model="qwen3:30b-a3b-nothinking",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": "What the weather like in Paris?"},
        ],
        tools=tools,
        max_tokens=1024,
        temperature=0.5,
    )

    # 通过 tool_calls 可以获取到用户问题中提到的城市名称，即 "Paris"
    arguments = response.choices[0].message.tool_calls[0].function.arguments
    print(arguments)

    # 这种机制大大的提升了系统与模型交互的智能型和实用性
    # 允许模型与外部系统进行交互，通过定义工具，从而实现系统可以动态的调用函数，获取实时数据或执行复杂操作


if __name__ == "__main__":
    import asyncio

    asyncio.run(main())
