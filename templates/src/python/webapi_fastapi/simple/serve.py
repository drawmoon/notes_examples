import logging
from contextlib import asynccontextmanager
from typing import Awaitable, Callable

import uvicorn
from fastapi import FastAPI, Request, Response
from fastapi.responses import JSONResponse

from .apis import router
from .exception import ServerException

logger = logging.getLogger(__name__)


def main(host: str = "0.0.0.0", port: int = 8080, log_level: int = logging.INFO):
    from uvicorn.config import LOGGING_CONFIG

    logging.config.dictConfig(LOGGING_CONFIG)  # type: ignore

    @asynccontextmanager
    async def lifespan(f: FastAPI):
        # here you can do some initialization
        yield
        # here you can do some cleanup
        logger.info("Server stopped")

    app = FastAPI(lifespan=lifespan)
    app.include_router(router, prefix="/api")

    async def exception_handler(
        request: Request, call_next: Callable[[Request], Awaitable[Response]]
    ):
        try:
            response = await call_next(request)
        except ServerException as e:
            logger.error(f"Exception: {e} {e.error}, {e.details}")
            response = JSONResponse(
                content={"error": e.error, "details": e.details}, status_code=400
            )
        return response

    app.middleware("http")(exception_handler)

    uvicorn.run(app, host=host, port=port, workers=1)
