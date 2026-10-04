"""Generates the Google Play developer profile images (shared by all apps).

Outputs (in branding/developer-profile/):
  developer-icon-512.png            512x512 developer icon
  developer-header-4096x2304.jpg    4096x2304 developer page header

Usage: python3 branding/tools/generate_developer_profile.py   (needs Pillow and numpy)
"""
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageFont

OUT = Path(__file__).resolve().parent.parent / "developer-profile"
OUT.mkdir(exist_ok=True)
BG_TOP, BG_BOTTOM = np.array([22, 27, 34]), np.array([10, 13, 18])
ACCENT = (61, 220, 132)          # Android green
ACCENT_SOFT = (61, 220, 132, 40)
TEXT = (230, 237, 243)
MUTED = (110, 118, 129)
MENLO = "/System/Library/Fonts/Menlo.ttc"


def font(size, bold=True):
    return ImageFont.truetype(MENLO, size, index=1 if bold else 0)


def base(w, h, glow_x, glow_y, glow_r):
    """Dark editor-like gradient with a soft green glow."""
    y, x = np.mgrid[0:h, 0:w].astype(np.float32)
    t = (y / h)[..., None]
    img = BG_TOP * (1 - t) + BG_BOTTOM * t
    d = np.sqrt((x - glow_x * w) ** 2 + (y - glow_y * h) ** 2) / max(w, h)
    img += np.exp(-(d / glow_r) ** 2)[..., None] * np.array(ACCENT) * 0.16
    return Image.fromarray(np.clip(img, 0, 255).astype(np.uint8), "RGB")


def dot_grid(img, step, radius, color):
    d = ImageDraw.Draw(img, "RGBA")
    w, h = img.size
    for yy in range(step // 2, h, step):
        for xx in range(step // 2, w, step):
            d.ellipse([xx - radius, yy - radius, xx + radius, yy + radius], fill=color)


def centered(draw, text, f, cx, cy):
    bb = draw.textbbox((0, 0), text, font=f)
    return cx - (bb[2] - bb[0]) / 2 - bb[0], cy - (bb[3] - bb[1]) / 2 - bb[1]


# --- Developer icon 512x512 (rendered 2x) ---
S = 1024
icon = base(S, S, 0.5, 0.5, 0.45)
dot_grid(icon, 64, 2.5, (255, 255, 255, 18))
d = ImageDraw.Draw(icon)
f = font(int(S * 0.36))
mono = "JL"
cursor_w, gap = S * 0.2, S * 0.035
bb = d.textbbox((0, 0), mono, font=f)
tw = bb[2] - bb[0]
total = tw + gap + cursor_w
x0 = (S - total) / 2 - bb[0]
_, y0 = centered(d, mono, f, S / 2, S * 0.5)
# Glow behind the cursor
glow = Image.new("RGBA", (S, S), (0, 0, 0, 0))
gx = x0 + bb[0] + tw + gap
gd = ImageDraw.Draw(glow)
cy_bottom = y0 + bb[3]
gd.rectangle([gx, cy_bottom - S * 0.05, gx + cursor_w, cy_bottom], fill=(*ACCENT, 160))
glow = glow.filter(ImageFilter.GaussianBlur(S * 0.03))
icon.paste(glow, (0, 0), glow)
d = ImageDraw.Draw(icon)
d.text((x0, y0), mono, font=f, fill=TEXT)
d.rectangle([gx, cy_bottom - S * 0.05, gx + cursor_w, cy_bottom], fill=ACCENT)
icon.resize((512, 512), Image.LANCZOS).save(f"{OUT}/developer-icon-512.png", optimize=True)

# --- Header 4096x2304 ---
W, H = 4096, 2304
hdr = base(W, H, 0.78, 0.3, 0.45)
dot_grid(hdr, 96, 3, (255, 255, 255, 14))
d = ImageDraw.Draw(hdr, "RGBA")

# Faint code block on the right, like an editor in the background
code = [
    ("fun", " main() {"),
    ("    val", " ideas = listOf(\"apps\", \"tools\", \"games\")"),
    ("    ideas", ".forEach { build(it) }"),
    ("}", ""),
]
fc = font(64, bold=False)
lx, ly = W * 0.50, H * 0.20
for i, (kw, rest) in enumerate(code):
    yy = ly + i * 104
    d.text((lx - 150, yy), f"{i + 1:>2}", font=fc, fill=(*MUTED, 90))
    d.text((lx, yy), kw, font=fc, fill=(*ACCENT, 120))
    kx = d.textbbox((lx, yy), kw, font=fc)[2]
    d.text((kx, yy), rest, font=fc, fill=(*TEXT, 70))

# Prompt-style tagline in the safe centre area
fp = font(150)
prompt = "> "
line = "building Android apps"
px, py = centered(d, prompt + line + "_", fp, W / 2, H * 0.62)
d.text((px, py), prompt, font=fp, fill=ACCENT)
x2 = d.textbbox((px, py), prompt, font=fp)[2]
d.text((x2, py), line, font=fp, fill=TEXT)
x3 = d.textbbox((x2, py), line, font=fp)[2]
bb = d.textbbox((x2, py), line, font=fp)
d.rectangle([x3 + 10, bb[3] - 24, x3 + 10 + 86, bb[3]], fill=ACCENT)

hdr.save(f"{OUT}/developer-header-4096x2304.jpg", quality=90, optimize=True)
print(f"Developer profile images written to {OUT}")
