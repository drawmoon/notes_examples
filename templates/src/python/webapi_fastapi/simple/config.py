from typing import Any
from dynaconf import Dynaconf
from pydantic import BaseModel, model_validator, Field


class AppConfig(BaseModel):
    version: str = Field(default="v1", description="API version")

    @model_validator(mode="before")
    @classmethod
    def from_dynaconf(cls, data: Any) -> Any:
        if isinstance(data, Dynaconf):
            return {k: data[k] for k in cls.model_fields.keys()}  # type: ignore
        return data
