from typing import Any


class ServerException(Exception):
    error: str
    details: str

    def __init__(self, error: str, details: str, *args: Any):
        super().__init__(*args)
        self.error = error
        self.details = details
