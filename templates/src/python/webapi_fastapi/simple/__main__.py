import fire

from .serve import main

if __name__ == "__main__":
    fire.Fire(main)  # type: ignore
