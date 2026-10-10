"""Generates every launcher/store icon asset from a single geometry definition.

Outputs: see scripts/icon_lib.py (adaptive, monochrome and legacy icons, Play Store icon).

Usage (repo root): .venv/bin/python apps/christmas-countdown/tools/generate_icon.py
"""
import sys
from pathlib import Path

from PIL import ImageDraw

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT.parent.parent / "scripts"))
from icon_lib import circle_path, f, fill, poly_path, write_icon  # noqa: E402

# Palette
BG_START, BG_END = "#D7263D", "#7A0F1C"
PAGE, HEADER, RINGS = "#FFF8EE", "#1F7A4D", "#FFC857"
TREE, STAR, TRUNK = "#2A9D5C", "#FFB703", "#8D5524"
BAUBLE_RED, BAUBLE_GOLD = "#E63946", "#FFB703"

# Geometry in the 108x108 adaptive-icon viewport (safe zone: circle of radius 33 at the centre).
PAGE_RECT = (32, 36, 76, 80, 6)
HEADER_RECT = (32, 36, 76, 47, 6)
RING_RECTS = [(41, 31, 45, 40, 2), (63, 31, 67, 40, 2)]
TREE_TRIANGLES = [
    [(54, 54), (47.5, 61), (60.5, 61)],
    [(54, 58.5), (45, 67), (63, 67)],
    [(54, 63.5), (42.5, 73), (65.5, 73)],
]
TRUNK_RECT = (52, 73, 56, 76.5)
STAR_CENTER, STAR_RADIUS = (54, 52), 3.4
BAUBLES = [((51, 64.5), 1.2, BAUBLE_RED), ((57.5, 69.5), 1.2, BAUBLE_RED),
           ((49, 70.5), 1.1, BAUBLE_GOLD), ((56.5, 61), 1.0, BAUBLE_GOLD)]


def star_points(cx, cy, r):
    import math
    pts = []
    for i in range(10):
        rad = r if i % 2 == 0 else r * 0.45
        a = -math.pi / 2 + i * math.pi / 5
        pts.append((cx + rad * math.cos(a), cy + rad * math.sin(a)))
    return pts


def rr_path(x0, y0, x1, y1, r):
    return (f"M{f(x0 + r)},{f(y0)} H{f(x1 - r)} A{f(r)},{f(r)} 0 0 1 {f(x1)},{f(y0 + r)} "
            f"V{f(y1 - r)} A{f(r)},{f(r)} 0 0 1 {f(x1 - r)},{f(y1)} H{f(x0 + r)} "
            f"A{f(r)},{f(r)} 0 0 1 {f(x0)},{f(y1 - r)} V{f(y0 + r)} A{f(r)},{f(r)} 0 0 1 {f(x0 + r)},{f(y0)} Z")


def top_rounded_path(x0, y0, x1, y1, r):
    return (f"M{f(x0)},{f(y1)} V{f(y0 + r)} A{f(r)},{f(r)} 0 0 1 {f(x0 + r)},{f(y0)} "
            f"H{f(x1 - r)} A{f(r)},{f(r)} 0 0 1 {f(x1)},{f(y0 + r)} V{f(y1)} Z")


def rect_path(x0, y0, x1, y1):
    return f"M{f(x0)},{f(y0)} H{f(x1)} V{f(y1)} H{f(x0)} Z"


def tree_paths(color_tree, color_star, color_trunk, baubles=True):
    out = [fill(poly_path(t), color_tree) for t in TREE_TRIANGLES]
    out.append(fill(rect_path(*TRUNK_RECT), color_trunk))
    out.append(fill(poly_path(star_points(*STAR_CENTER, STAR_RADIUS)), color_star))
    if baubles:
        out += [fill(circle_path(*c, r), col) for c, r, col in BAUBLES]
    return out


def foreground():
    x0, y0, x1, y1, r = PAGE_RECT
    return ([fill(rr_path(x0, y0 + 1.5, x1, y1 + 1.5, r), "#40000000"),  # soft drop shadow
             fill(rr_path(*PAGE_RECT), PAGE),
             fill(top_rounded_path(*HEADER_RECT), HEADER)]
            + [fill(rr_path(*ring), RINGS) for ring in RING_RECTS]
            + tree_paths(TREE, STAR, TRUNK))


def monochrome():
    """Themed icons: page outline, solid header and rings, solid tree."""
    x0, y0, x1, y1, r = PAGE_RECT
    outline = (f'    <path\n        android:strokeColor="#FF000000"\n        android:strokeWidth="2.4"\n'
               f'        android:pathData="{rr_path(x0 + 1.2, y0 + 1.2, x1 - 1.2, y1 - 1.2, r - 1.2)}" />')
    return ([outline, fill(top_rounded_path(*HEADER_RECT), "#FF000000")]
            + [fill(rr_path(*ring), "#FF000000") for ring in RING_RECTS]
            + tree_paths("#FF000000", "#FF000000", "#FF000000", baubles=False))


def draw(img, P, k):
    """The foreground with Pillow, matching the vector layers."""
    d = ImageDraw.Draw(img, "RGBA")

    def rrect(x0, y0, x1, y1, r, color):
        d.rounded_rectangle([P(x0, y0), P(x1, y1)], radius=r * k, fill=color)

    x0, y0, x1, y1, r = PAGE_RECT
    rrect(x0, y0 + 1.5, x1, y1 + 1.5, r, (0, 0, 0, 64))
    rrect(*PAGE_RECT, PAGE)
    hx0, hy0, hx1, hy1, hr = HEADER_RECT
    rrect(hx0, hy0, hx1, hy1, hr, HEADER)
    d.rectangle([P(hx0, hy0 + hr), P(hx1, hy1)], fill=HEADER)
    for ring in RING_RECTS:
        rrect(*ring, RINGS)
    for t in TREE_TRIANGLES:
        d.polygon([P(*p) for p in t], fill=TREE)
    d.rectangle([P(TRUNK_RECT[0], TRUNK_RECT[1]), P(TRUNK_RECT[2], TRUNK_RECT[3])], fill=TRUNK)
    d.polygon([P(*p) for p in star_points(*STAR_CENTER, STAR_RADIUS)], fill=STAR)
    for (cx, cy), br, col in BAUBLES:
        d.ellipse([P(cx - br, cy - br), P(cx + br, cy + br)], fill=col)


if __name__ == "__main__":
    write_icon(ROOT, BG_START, BG_END, foreground(), monochrome(), draw, diagonal=True)
