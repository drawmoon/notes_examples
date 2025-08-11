from fastapi import APIRouter

from .parrot import api as parrot_api

router = APIRouter()
router.include_router(parrot_api)
