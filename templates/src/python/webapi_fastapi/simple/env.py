from os import environ as env

from dotenv import load_dotenv

load_dotenv(override=True)
load_dotenv(".env.local", override=True)


__all__ = ["env"]
