"""Render looping exercise demo clips into app/src/main/res/raw.

Usage (needs pillow, numpy and imageio-ffmpeg):
    python render.py                 # every exercise
    python render.py squats plank    # only these
    python render.py --preview DIR   # also write a contact-sheet PNG per clip
"""
import argparse
import math
import os
import sys

import imageio_ffmpeg
import numpy as np
from PIL import Image

import rig
from exercises import EXERCISES, SCENES

W, H, SS, FPS = 960, 640, 2, 30
MIN_SECONDS = 3.0
OUT_DIR = os.path.join(os.path.dirname(__file__), "..", "..", "app", "src", "main", "res", "raw")


def ease(t):
    return 0.5 - 0.5 * math.cos(math.pi * t)


def timeline(keys):
    cycle = sum(sec for _, sec in keys)
    reps = max(1, math.ceil(MIN_SECONDS / cycle))
    total = cycle * reps
    frames = round(total * FPS)

    def at(t):
        t = t % cycle
        for i, (p, sec) in enumerate(keys):
            if t < sec or i == len(keys) - 1:
                q = keys[(i + 1) % len(keys)][0]
                return rig.blend(p, q, ease(min(1.0, t / sec)))
            t -= sec
    return [at(i / FPS) for i in range(frames)]


def camera(poses, wall_x, top_down=False):
    xs, ys = [], []
    for p in poses:
        for pt in rig.points(p, rig.solve(p)):
            xs.append(pt[0])
            ys.append(pt[1])
    x0, x1 = min(xs), max(xs)
    if wall_x is not None:
        x0 = min(x0, wall_x - 0.15)
    top = max(ys)
    width, height = x1 - x0, max(top, 0.6)
    # Keep the figure between the player's top badge and its bottom cue banner.
    safe_top, safe_bottom = 70.0, 515.0
    scale = min(420.0, 820.0 / width, (safe_bottom - safe_top) / height)
    ground = min(safe_bottom, (safe_top + safe_bottom) / 2 + height * scale / 2)
    if top_down:
        x0, x1 = -0.45, 0.45  # mat width
        scale = min(scale, (safe_bottom - safe_top) / 2.3)
        ground = (safe_top + safe_bottom) / 2 + 1.06 * scale
    return rig.Canvas(W, H, SS, scale, (x0 + x1) / 2, ground), (x0, x1)


def shadow_span(sk):
    low = [pt for pt in (sk["pelvis"], sk["chest"], sk["head"],
                         sk["n"]["ankle"], sk["f"]["ankle"], sk["n"]["toe"], sk["f"]["toe"],
                         sk["n"]["wrist"], sk["f"]["wrist"], sk["n"]["knee"], sk["f"]["knee"]) if pt[1] < 0.6]
    if not low:
        return (-0.1, 0.1)
    xs = [pt[0] for pt in low]
    return (min(xs) - 0.08, max(xs) + 0.08)


def render(name, preview_dir=None):
    keys = EXERCISES[name]()
    poses = timeline(keys)
    scene = SCENES.get(name, {})
    wall_x, top_down = scene.get("wall_x"), scene.get("top_down", False)
    cv, (x0, x1) = camera(poses, wall_x, top_down)
    background = rig.make_background(W, H, SS, cv, (x0 - 0.15, x1 + 0.15), wall_x, top_down)

    out = os.path.normpath(os.path.join(OUT_DIR, f"video_{name}.mp4"))
    writer = imageio_ffmpeg.write_frames(
        out, (W, H), fps=FPS, codec="libx264", pix_fmt_out="yuv420p", quality=None,
        output_params=["-crf", "24", "-preset", "slow", "-profile:v", "main",
                       "-movflags", "+faststart", "-an"],
    )
    writer.send(None)
    sheet = []
    for i, p in enumerate(poses):
        frame = rig.render_frame(p, cv, background, shadow_span).resize((W, H), Image.LANCZOS)
        writer.send(np.asarray(frame, dtype=np.uint8).tobytes())
        if preview_dir and i % max(1, len(poses) // 6) == 0 and len(sheet) < 6:
            sheet.append(frame.resize((W // 3, H // 3), Image.LANCZOS))
    writer.close()
    if preview_dir:
        os.makedirs(preview_dir, exist_ok=True)
        grid = Image.new("RGB", (W // 3 * 3, H // 3 * 2))
        for j, im in enumerate(sheet):
            grid.paste(im, ((j % 3) * (W // 3), (j // 3) * (H // 3)))
        grid.save(os.path.join(preview_dir, f"{name}.png"))
    print(f"{name}: {len(poses) / FPS:.1f}s, {os.path.getsize(out) // 1024} KB")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("names", nargs="*")
    ap.add_argument("--preview")
    args = ap.parse_args()
    names = args.names or list(EXERCISES)
    unknown = [n for n in names if n not in EXERCISES]
    if unknown:
        sys.exit(f"unknown exercise(s): {', '.join(unknown)}")
    for n in names:
        render(n, args.preview)


if __name__ == "__main__":
    main()
