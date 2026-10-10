"""Generates every launcher/store icon asset of the tournaments app from one geometry definition.

A gold bracket climbing to a big "1" with a star on top (only one wins).

Outputs: see scripts/icon_lib.py (adaptive, monochrome and legacy icons, Play Store icon).

Usage (repo root): .venv/bin/python apps/tournaments/tools/generate_icon.py
"""
import math
import sys
from pathlib import Path

from PIL import ImageDraw

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT.parent.parent / "scripts"))
from icon_lib import fill, poly_path, write_icon  # noqa: E402

BG_TOP, BG_BOTTOM = "#1E3A52", "#12202D"
CREAM, GOLD = "#F4F1EA", "#FFC83D"

# Geometry in the 108x108 adaptive viewport (safe zone: radius 33 around the centre).
# A gold bracket climbing to a big "1" with a star on top: only one wins.
W = 3.4          # bracket line width
FLOOR = 80.0     # where the bracket's legs end (flat, the rest of the corners are square)
BRACKET = [
    [(35, FLOOR), (35, 71), (47, 71), (47, FLOOR)],
    [(61, FLOOR), (61, 71), (73, 71), (73, FLOOR)],
    [(41, 71), (41, 62.5), (67, 62.5), (67, 71)],
    [(54, 62.5), (54, 58)],
]
ONE = [(51, 36), (57.5, 36), (57.5, 53), (62, 53), (62, 58), (46, 58), (46, 53), (51, 53), (51, 42.5), (47, 44.5), (45.6, 39.6)]
STAR_CENTRE, STAR_R, STAR_r = (54, 26.5), 7.2, 3.1


def star():
    cx, cy = STAR_CENTRE
    return [(cx + (STAR_R if i % 2 == 0 else STAR_r) * math.cos(math.radians(-90 + i * 36)),
             cy + (STAR_R if i % 2 == 0 else STAR_r) * math.sin(math.radians(-90 + i * 36))) for i in range(10)]


def bracket_rects():
    """Each bracket segment as a rectangle W wide; legs stop flat at FLOOR."""
    rects = []
    for line in BRACKET:
        for (x0, y0), (x1, y1) in zip(line, line[1:]):
            bottom = max(y0, y1) + (0 if FLOOR in (y0, y1) else W / 2)
            rects.append([(min(x0, x1) - W / 2, min(y0, y1) - W / 2), (max(x0, x1) + W / 2, min(y0, y1) - W / 2),
                          (max(x0, x1) + W / 2, bottom), (min(x0, x1) - W / 2, bottom)])
    return rects


# The drawing above spans y 19-80; shrink it a little and centre it so the star clears round masks.
SCALE, MID_Y = 0.92, 49.7


def fit(points):
    return [(54 + (x - 54) * SCALE, 54 + (y - MID_Y) * SCALE) for x, y in points]


def shapes():
    """(polygon, colour) in drawing order, in viewport coordinates."""
    return [(fit(r), GOLD) for r in bracket_rects()] + [(fit(ONE), CREAM), (fit(star()), GOLD)]


def foreground():
    return [fill(poly_path(points), colour) for points, colour in shapes()]


def monochrome():
    return [fill(poly_path(points), "#FF000000") for points, _ in shapes()]


def draw(img, P, k):
    """The foreground with Pillow, matching the vector layers."""
    d = ImageDraw.Draw(img)
    for points, colour in shapes():
        d.polygon([P(*p) for p in points], fill=colour)


if __name__ == "__main__":
    write_icon(ROOT, BG_TOP, BG_BOTTOM, foreground(), monochrome(), draw)
