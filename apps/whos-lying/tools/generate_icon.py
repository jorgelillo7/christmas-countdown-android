"""Generates the launcher and store icons of Who's lying? from one geometry definition.

A line-up of three: two lilac players and, in the middle, the pink one in a fedora, glancing
sideways. The hat has a lilac outline so it still reads on the dark background at launcher size.

Usage (repo root): .venv/bin/python apps/whos-lying/tools/generate_icon.py
"""
import math
import sys
from pathlib import Path

from PIL import ImageDraw

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT.parent.parent / "scripts"))
from icon_lib import f, write_icon  # noqa: E402

TOP, BOTTOM = "#3A2490", "#100E20"
PLAYER, LIAR, EYE, INK = "#7C5CFF", "#FF4D94", "#F0EEFF", "#100E20"
HAT, HAT_OUTLINE = "#17122E", "#A996FF"

# Drawn in a 108x108 design space, then fitted (scaled around the drawing's middle) into the
# adaptive viewport so everything stays inside the r=33 safe circle.
SCALE, FROM_MID, TO_MID = 0.94, (54.0, 59.0), (54.0, 55.0)

# Players: head centre, size. Head radius 5.2 * size; body below it.
PLAYERS = [((34.0, 58.0), 1.0, PLAYER), ((74.0, 58.0), 1.0, PLAYER), ((54.0, 54.0), 1.28, LIAR)]
EYES_AT, EYES_SIZE = (54.0, 55.0), 1.28
HAT_AT, HAT_W, HAT_TILT, OUTLINE = (54.0, 48.6), 13.5, -8.0, 0.55


def fit(x, y):
    return TO_MID[0] + (x - FROM_MID[0]) * SCALE, TO_MID[1] + (y - FROM_MID[1]) * SCALE


def chaikin(points, rounds=4):
    """Corner cutting: turns a coarse polygon into a smooth closed curve."""
    for _ in range(rounds):
        out = []
        for (x0, y0), (x1, y1) in zip(points, points[1:] + points[:1]):
            out += [(0.75 * x0 + 0.25 * x1, 0.75 * y0 + 0.25 * y1), (0.25 * x0 + 0.75 * x1, 0.25 * y0 + 0.75 * y1)]
        points = out
    return points


def fedora():
    """Brim, crown and band polygons around the brim centre, tilted and placed, in viewport coordinates."""
    w = HAT_W
    hb, ht, h, dent = w * 0.52, w * 0.44, w * 0.70, w * 0.14
    crown = chaikin([(-hb, 0.6), (-hb, 0), (-ht, -h), (-ht * 0.5, -h - 0.02 * w), (0, -h + dent),
                     (ht * 0.5, -h - 0.02 * w), (ht, -h), (hb, 0), (hb, 0.6)])

    def side(y):
        return hb + (ht - hb) * (-y / h)

    y0, y1 = -h * 0.12, -h * 0.40
    band = [(-side(y0), y0), (-side(y1), y1), (side(y1), y1), (side(y0), y0)]
    brim = [(w * math.cos(t), w * 0.16 * math.sin(t) - w * 0.09 * abs(math.cos(t)) ** 3)
            for t in (i / 96 * math.tau for i in range(96))]
    a = math.radians(HAT_TILT)
    cx, cy = HAT_AT

    def place(points):
        return [fit(cx + x * math.cos(a) - y * math.sin(a), cy + x * math.sin(a) + y * math.cos(a)) for x, y in points]

    return place(brim), place(crown), place(band)


def player_shapes(centre, size):
    """Head circle (cx, cy, r) and body rounded rect (x0, y0, x1, y1, r), in viewport coordinates."""
    x, y = centre
    hx, hy = fit(x, y)
    bx0, by0 = fit(x - 8.5 * size, y + 7 * size)
    bx1, by1 = fit(x + 8.5 * size, y + 22 * size)
    return (hx, hy, 5.2 * size * SCALE), (bx0, by0, bx1, by1, 7 * size * SCALE)


def eye_shapes():
    """(eye ellipse cx, cy, rx, ry) and (pupil cx, cy, r) for both eyes, looking right."""
    (x, y), s = EYES_AT, EYES_SIZE
    out = []
    for dx in (-2.4, 2.4):
        ex, ey = fit(x + dx * s, y)
        px, py = fit(x + dx * s + 0.9 * s, y + 0.2 * s)
        out.append(((ex, ey, 1.9 * s * SCALE, 1.5 * s * SCALE), (px, py, 0.85 * s * SCALE)))
    return out


def rr(x0, y0, x1, y1, r):
    return (f"M{f(x0 + r)},{f(y0)} H{f(x1 - r)} A{f(r)},{f(r)} 0 0 1 {f(x1)},{f(y0 + r)} V{f(y1 - r)} "
            f"A{f(r)},{f(r)} 0 0 1 {f(x1 - r)},{f(y1)} H{f(x0 + r)} A{f(r)},{f(r)} 0 0 1 {f(x0)},{f(y1 - r)} "
            f"V{f(y0 + r)} A{f(r)},{f(r)} 0 0 1 {f(x0 + r)},{f(y0)} Z")


def poly(points):
    (x, y), *rest = points
    return f"M{f(x)},{f(y)} " + " ".join(f"L{f(a)},{f(b)}" for a, b in rest) + " Z"


def circle(cx, cy, r):
    return f"M{f(cx - r)},{f(cy)} a{f(r)},{f(r)} 0 1 0 {f(2 * r)},0 a{f(r)},{f(r)} 0 1 0 {f(-2 * r)},0 Z"


def ellipse(cx, cy, rx, ry):
    return f"M{f(cx - rx)},{f(cy)} a{f(rx)},{f(ry)} 0 1 0 {f(2 * rx)},0 a{f(rx)},{f(ry)} 0 1 0 {f(-2 * rx)},0 Z"


def path(d, color=None, stroke=None, width=None):
    if color:
        attrs = f'android:fillColor="{color}"'
    else:
        attrs = (f'android:strokeColor="{stroke}"\n        android:strokeWidth="{f(width)}"\n'
                 '        android:strokeLineJoin="round"')
    return f'    <path\n        {attrs}\n        android:pathData="{d}" />'


def foreground():
    brim, crown, band = fedora()
    people = []
    for centre, size, colour in PLAYERS:
        head, body = player_shapes(centre, size)
        people += [path(circle(*head), colour), path(rr(*body), colour)]
    eyes = []
    for eye, pupil in eye_shapes():
        eyes += [path(ellipse(*eye), EYE), path(circle(*pupil), INK)]
    outline_width = 2 * OUTLINE * SCALE
    hat = ([path(poly(brim), stroke=HAT_OUTLINE, width=outline_width), path(poly(crown), stroke=HAT_OUTLINE, width=outline_width)]
           + [path(poly(crown), HAT), path(poly(band), LIAR), path(poly(brim), HAT)])
    return people + eyes + hat


def monochrome():
    """Themed icons: the two others at half strength, the liar and the hat solid."""
    brim, crown, _ = fedora()
    mono = []
    for (centre, size, _), alpha in zip(PLAYERS, ("#80000000", "#80000000", "#FF000000")):
        head, body = player_shapes(centre, size)
        mono += [path(circle(*head), alpha), path(rr(*body), alpha)]
    return mono + [path(poly(crown), "#FF000000"), path(poly(brim), "#FF000000")]


def draw(img, P, k):
    """The foreground with Pillow, matching the vector layers."""
    d = ImageDraw.Draw(img)
    for centre, scale, colour in PLAYERS:
        (hx, hy, hr), (x0, y0, x1, y1, r) = player_shapes(centre, scale)
        d.ellipse([P(hx - hr, hy - hr), P(hx + hr, hy + hr)], fill=colour)
        d.rounded_rectangle([P(x0, y0), P(x1, y1)], radius=r * k, fill=colour)
    for (ex, ey, rx, ry), (px, py, pr) in eye_shapes():
        d.ellipse([P(ex - rx, ey - ry), P(ex + rx, ey + ry)], fill=EYE)
        d.ellipse([P(px - pr, py - pr), P(px + pr, py + pr)], fill=INK)
    brim, crown, band = fedora()
    # Outline: the silhouette pushed out in a ring, behind the hat.
    o = OUTLINE * SCALE
    for j in range(36):
        dx, dy = o * math.cos(j / 36 * math.tau), o * math.sin(j / 36 * math.tau)
        for shape in (brim, crown):
            d.polygon([P(x + dx, y + dy) for x, y in shape], fill=HAT_OUTLINE)
    for shape, colour in ((crown, HAT), (band, LIAR), (brim, HAT)):
        d.polygon([P(*p) for p in shape], fill=colour)


if __name__ == "__main__":
    write_icon(ROOT, TOP, BOTTOM, foreground(), monochrome(), draw)
