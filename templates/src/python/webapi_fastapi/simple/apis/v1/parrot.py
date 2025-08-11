from fastapi import APIRouter

api = APIRouter(prefix="/parrots", tags=["parrots"])


@api.get("/")
def readme() -> str:
    return "Hello, this is a simple fastapi API."
