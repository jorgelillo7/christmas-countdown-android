"""Generates the launcher and store icons of "¿Qué votáis?" from one geometry definition.

A speech bubble split red | blue (the two answers) with a cream question mark, on the same deep
blue night as the tournaments icon: ask your friends, two answers.

Usage (repo root): .venv/bin/python apps/group-polls/tools/generate_icon.py
"""
import math
import sys
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT.parent.parent / "scripts"))
from icon_lib import f, fill, write_icon  # noqa: E402

TOP, BOTTOM = "#1E3A52", "#12202D"
RED, BLUE, CREAM = "#F2384A", "#2D6BFF", "#F4F1EA"

# 108x108 adaptive viewport, content inside the r=33 safe circle.
BX0, BY0, BX1, BY1, BR = 28.0, 30.0, 80.0, 71.0, 9.0   # bubble body
SPLIT = 54.0                                           # red left of it, blue right
TAIL = [(68.0, 69.0), (56.0, 69.0), (70.0, 82.0)]      # on the blue side (the impostor's bubble points left)
Q_CX, Q_CY, Q_R, Q_W = 54.0, 44.0, 6.8, 4.2           # question mark: hook centre, radius, width
Q_FROM, Q_SWEEP = 200.0, 250.0                         # hook from 200 deg, clockwise, ends straight below
Q_STEM = Q_R * 0.35
Q_DOT_Y, Q_DOT_R = Q_CY + Q_R + Q_STEM + Q_W * 1.6, Q_W * 0.62


def rr(x0, y0, x1, y1, r):
    return (f"M{f(x0 + r)},{f(y0)} H{f(x1 - r)} A{f(r)},{f(r)} 0 0 1 {f(x1)},{f(y0 + r)} V{f(y1 - r)} "
            f"A{f(r)},{f(r)} 0 0 1 {f(x1 - r)},{f(y1)} H{f(x0 + r)} A{f(r)},{f(r)} 0 0 1 {f(x0)},{f(y1 - r)} "
            f"V{f(y0 + r)} A{f(r)},{f(r)} 0 0 1 {f(x0 + r)},{f(y0)} Z")


def stroke(d, color, width):
    return (f'    <path\n        android:strokeColor="{color}"\n        android:strokeWidth="{width}"\n'
            f'        android:strokeLineCap="round"\n        android:strokeLineJoin="round"\n        android:pathData="{d}" />')


def point(angle_deg, r=Q_R):
    a = math.radians(angle_deg)
    return Q_CX + r * math.cos(a), Q_CY + r * math.sin(a)


def half(left):
    """One half of the bubble body, rounded only on its outer corners."""
    x, sweep = (BX0, 0) if left else (BX1, 1)
    inward = BR if left else -BR
    return (f"M{f(SPLIT)},{f(BY0)} H{f(x + inward)} A{f(BR)},{f(BR)} 0 0 {sweep} {f(x)},{f(BY0 + BR)} "
            f"V{f(BY1 - BR)} A{f(BR)},{f(BR)} 0 0 {sweep} {f(x + inward)},{f(BY1)} H{f(SPLIT)} Z")


def tail_path():
    (x, y), *rest = TAIL
    return f"M{f(x)},{f(y)} " + " ".join(f"L{f(a)},{f(b)}" for a, b in rest) + " Z"


def question_path():
    sx, sy = point(Q_FROM)
    ex, ey = point(Q_FROM + Q_SWEEP)
    return f"M{f(sx)},{f(sy)} A{f(Q_R)},{f(Q_R)} 0 1 1 {f(ex)},{f(ey)} V{f(ey + Q_STEM)}"


def dot_path():
    return f"M{f(Q_CX - Q_DOT_R)},{f(Q_DOT_Y)} a{f(Q_DOT_R)},{f(Q_DOT_R)} 0 1 0 {f(2 * Q_DOT_R)},0 a{f(Q_DOT_R)},{f(Q_DOT_R)} 0 1 0 {f(-2 * Q_DOT_R)},0 Z"


def bubble_outline():
    return rr(BX0, BY0, BX1, BY1, BR)


def foreground():
    return [
        fill(half(left=True), RED), fill(half(left=False), BLUE), fill(tail_path(), BLUE),
        stroke(question_path(), CREAM, Q_W), fill(dot_path(), CREAM),
    ]


def monochrome():
    """Themed icons: the bubble's outline and the question mark."""
    return [
        stroke(bubble_outline(), "#FF000000", 3.4), fill(tail_path(), "#FF000000"),
        stroke(question_path(), "#FF000000", Q_W), fill(dot_path(), "#FF000000"),
    ]


def draw(img, P, k):
    """The foreground with Pillow, matching the vector layers."""
    s = img.size[0]
    d = ImageDraw.Draw(img, "RGBA")
    body = Image.new("L", (s, s), 0)
    bd = ImageDraw.Draw(body)
    bd.rounded_rectangle([P(BX0, BY0), P(BX1, BY1)], radius=BR * k, fill=255)
    bd.polygon([P(*p) for p in TAIL], fill=255)
    left = Image.new("L", (s, s), 0)
    ImageDraw.Draw(left).rectangle([(0, 0), (P(SPLIT, 0)[0], s)], fill=255)
    zero = Image.new("L", (s, s), 0)
    img.paste(Image.new("RGB", (s, s), RED), (0, 0), Image.composite(body, zero, left))
    img.paste(Image.new("RGB", (s, s), BLUE), (0, 0), Image.composite(zero, body, left))
    d = ImageDraw.Draw(img)
    hook = [point(Q_FROM + Q_SWEEP * i / 48) for i in range(49)]
    hook.append((Q_CX, hook[-1][1] + Q_STEM))
    w = Q_W * k
    d.line([P(*p) for p in hook], fill=CREAM, width=int(w), joint="curve")
    for p in (hook[0], hook[-1]):
        cx, cy = P(*p)
        d.ellipse([cx - w / 2, cy - w / 2, cx + w / 2, cy + w / 2], fill=CREAM)
    cx, cy = P(Q_CX, Q_DOT_Y)
    d.ellipse([cx - Q_DOT_R * k, cy - Q_DOT_R * k, cx + Q_DOT_R * k, cy + Q_DOT_R * k], fill=CREAM)


if __name__ == "__main__":
    write_icon(ROOT, TOP, BOTTOM, foreground(), monochrome(), draw)
