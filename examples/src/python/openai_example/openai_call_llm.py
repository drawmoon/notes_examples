import os

from dotenv import load_dotenv
from openai import AsyncOpenAI
from openai.types.chat.chat_completion import ChatCompletion

load_dotenv()


client = AsyncOpenAI(
    api_key=os.environ.get("OPENAI_API_KEY"), base_url=os.environ.get("OPENAI_BASE_URL")
)


async def main():
    response: ChatCompletion = await client.chat.completions.create(
        model="qwen3:30b-a3b-nothinking",
        messages=[
            {"role": "system", "content": "You are a helpful assistant."},
            {"role": "user", "content": "What is the meaning of life?"},
        ],
        max_tokens=1024,
        temperature=0.5,
    )

    print(response.choices[0].message.content)


if __name__ == "__main__":
    import asyncio

    asyncio.run(main())
