"""Builds the Google Play listing graphics of What do you vote? from raw app screenshots.

The drawing lives in scripts/store_assets.py (shared by every app); this file only holds the
app's colours and headlines. Inputs and outputs: see that module.

Usage (repo root): .venv/bin/python apps/group-polls/tools/generate_store_assets.py
"""
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT.parent.parent / "scripts"))
from store_assets import Style, build  # noqa: E402

# The app's light palette: sky background, red against blue.
NIGHT_TOP, NIGHT = (221, 235, 255), (243, 246, 251)
GOLD, SNOW = (45, 107, 255), (27, 36, 51)
ACCENTS = [(242, 56, 74), (45, 107, 255), (61, 183, 255)]

LOCALES = {
    "en-US": {
        "title": "What do you vote? Group polls",
        "tagline": "Private by link · Public feed · No ads",
        "headlines": {
            "01-feed": ("Red or blue?", "Ask everyone"),
            "02-vote": ("Tap your half.", "That's your vote"),
            "03-predict": ("Guess what", "most people pick"),
            "04-result": ("See where", "everyone stands"),
            "05-private": ("Private polls:", "see who voted what"),
            "06-create": ("Ask your friends", "with one link"),
        },
    },
    "es-ES": {
        "title": "¿Qué votáis? Sondeos",
        "tagline": "Privadas por enlace · Públicas · Sin anuncios",
        "headlines": {
            "01-feed": ("¿Rojo o azul?", "Pregunta a todos"),
            "02-vote": ("Toca tu mitad.", "Ese es tu voto"),
            "03-predict": ("Adivina qué", "votará la mayoría"),
            "04-result": ("Mira qué opina", "todo el mundo"),
            "05-private": ("Privadas:", "quién ha votado qué"),
            "06-create": ("Pregunta a tus amigos", "con un enlace"),
        },
    },
}

STYLE = Style(
    top=NIGHT_TOP,
    bottom=NIGHT,
    headline=SNOW,
    highlight=GOLD,
    tagline=(107, 118, 137),
    accents=ACCENTS,
)


if __name__ == "__main__":
    build(ROOT, STYLE, LOCALES)
