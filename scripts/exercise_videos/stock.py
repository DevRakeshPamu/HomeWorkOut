"""Turn downloaded Pexels clips into the app's looping exercise videos.

Usage (same venv as render.py):
    python stock.py DOWNLOAD_DIR            # every clip in STOCK_CLIPS
    python stock.py DOWNLOAD_DIR squats     # only these

DOWNLOAD_DIR holds <exercise>.mp4 files downloaded from the URLs in
STOCK_CLIPS (720p renditions). Each clip is cut to one clean loop, the seam
is crossfaded so it repeats smoothly, the crop is fitted inside the area the
player leaves clear (top badge, bottom cue banner) and the rest of the frame is
filled with a blurred, dimmed copy of the same shot.
"""
import argparse
import os

import imageio_ffmpeg
import numpy as np
from PIL import Image, ImageEnhance, ImageFilter

W, H, FPS = 960, 640, 30
SAFE = (30, 20, 930, 520)   # x0, y0, x1, y1 the subject must stay inside
FADE = 0.4                  # seconds of crossfade at the loop seam
OUT_DIR = os.path.join(os.path.dirname(__file__), "..", "..", "app", "src", "main", "res", "raw")

# Pexels License (https://www.pexels.com/license/): free for commercial use, editing allowed.
# crop = (x0, x1, y0, y1) as fractions of the source frame.
STOCK_CLIPS = {
    "squats":           dict(id=6525519, by="Polina Tankilevitch", start=1.0, length=3.1, crop=(0.05, 0.95, 0.20, 0.93),
                             url="https://videos.pexels.com/video-files/6525519/6525519-hd_720_1280_30fps.mp4"),
    "push_ups":         dict(id=6286173, by="Gustavo Fring", start=1.0, length=4.0, crop=(0, 1, 0, 1),
                             url="https://videos.pexels.com/video-files/6286173/6286173-hd_1280_720_30fps.mp4"),
    "lunges":           dict(id=6525491, by="Polina Tankilevitch", start=0.4, length=3.3, crop=(0.18, 0.82, 0.05, 0.95),
                             url="https://videos.pexels.com/video-files/6525491/6525491-hd_1280_720_25fps.mp4"),
    "reverse_lunges":   dict(id=4488004, by="Olia Danilevich", start=2.5, length=7.3, crop=(0, 1, 0.02, 0.98),
                             url="https://videos.pexels.com/video-files/4488004/4488004-hd_720_1280_25fps.mp4"),
    "mountain_climbers": dict(id=6525469, by="Polina Tankilevitch", start=0.0, length=2.0, crop=(0.25, 0.78, 0.22, 0.95),
                              url="https://videos.pexels.com/video-files/6525469/6525469-hd_1280_720_25fps.mp4"),
    "high_knees":       dict(id=5025964, by="Olia Danilevich", start=0.5, length=4.0, crop=(0.05, 0.95, 0.18, 0.97),
                             url="https://videos.pexels.com/video-files/5025964/5025964-hd_720_1280_25fps.mp4"),
    "bird_dog":         dict(id=6437929, by="Marta Wave", start=0.5, length=5.0, crop=(0, 1, 0.25, 0.95),
                             url="https://videos.pexels.com/video-files/6437929/6437929-hd_720_1280_30fps.mp4"),
    "cat_cow":          dict(id=7662722, by="Anastasia Shuraeva", start=0.5, length=8.0, crop=(0, 1, 0.25, 0.95),
                             url="https://videos.pexels.com/video-files/7662722/7662722-hd_720_1280_24fps.mp4"),
    "childs_pose":      dict(id=5991872, by="Kampus Production", start=0.5, length=6.0, crop=(0, 1, 0, 1),
                             url="https://videos.pexels.com/video-files/5991872/5991872-hd_1280_720_25fps.mp4"),
    "bicycle_crunches": dict(id=6525494, by="Polina Tankilevitch", start=0.2, length=6.0, crop=(0.15, 0.95, 0.28, 0.92),
                             url="https://videos.pexels.com/video-files/6525494/6525494-hd_1280_720_25fps.mp4"),
    "glute_bridges":    dict(id=6525487, by="Polina Tankilevitch", start=0.2, length=6.2, crop=(0.15, 0.95, 0.22, 0.92),
                             url="https://videos.pexels.com/video-files/6525487/6525487-hd_1280_720_25fps.mp4"),
    "plank":            dict(id=6525496, by="Polina Tankilevitch", start=0.5, length=5.0, crop=(0.12, 0.92, 0.35, 0.95),
                             url="https://videos.pexels.com/video-files/6525496/6525496-hd_1280_720_25fps.mp4"),
    "jumping_jacks":    dict(id=4764124, by="Gustavo Fring", start=0.5, length=8.0, crop=(0.08, 0.92, 0, 1),
                             url="https://videos.pexels.com/video-files/4764124/4764124-hd_1280_720_30fps.mp4"),
    "arm_circles":      dict(id=5510091, by="Maksim Goncharenok", start=0.4, length=5.4, crop=(0.22, 0.78, 0.14, 0.92),
                             url="https://videos.pexels.com/video-files/5510091/5510091-hd_720_1280_25fps.mp4"),
}


def read(path):
    reader = imageio_ffmpeg.read_frames(path)
    meta = next(reader)
    w, h = meta["size"]
    frames = [np.frombuffer(f, np.uint8).reshape(h, w, 3) for f in reader]
    return frames, meta["fps"]


def frame_at(frames, fps, t):
    return frames[min(len(frames) - 1, max(0, int(round(t * fps))))]


def compose(src, crop):
    h, w = src.shape[:2]
    x0, x1, y0, y1 = crop
    sub = Image.fromarray(src[int(y0 * h):int(y1 * h), int(x0 * w):int(x1 * w)])
    sx0, sy0, sx1, sy1 = SAFE
    scale = min((sx1 - sx0) / sub.width, (sy1 - sy0) / sub.height)
    fg = sub.resize((round(sub.width * scale), round(sub.height * scale)), Image.LANCZOS)
    # Plain studio backdrop: extend it with its own colour so the inset disappears.
    edge = np.concatenate([np.asarray(fg)[:6].reshape(-1, 3), np.asarray(fg)[:, :6].reshape(-1, 3),
                           np.asarray(fg)[:, -6:].reshape(-1, 3)])
    if edge.std(axis=0).max() < 14:
        bg = Image.new("RGB", (W, H), tuple(int(v) for v in np.median(edge, axis=0)))
        bg.paste(fg, (sx0 + ((sx1 - sx0) - fg.width) // 2, sy0 + ((sy1 - sy0) - fg.height) // 2), feather(fg.size))
        return np.asarray(bg)
    # Otherwise: the same shot, zoomed to fill and blurred.
    full = Image.fromarray(src)
    k = max(W / full.width, H / full.height)
    bg = full.resize((round(full.width * k), round(full.height * k)), Image.BILINEAR)
    bg = bg.crop(((bg.width - W) // 2, (bg.height - H) // 2, (bg.width - W) // 2 + W, (bg.height - H) // 2 + H))
    bg = ImageEnhance.Brightness(bg.filter(ImageFilter.GaussianBlur(28))).enhance(0.9)
    x = sx0 + ((sx1 - sx0) - fg.width) // 2
    y = sy0 + ((sy1 - sy0) - fg.height) // 2
    bg.paste(fg, (x, y), feather(fg.size))
    return np.asarray(bg)


_masks = {}


def feather(size, edge=36):
    """Alpha mask that fades the inset's edges into the blurred fill."""
    if size not in _masks:
        w, h = size
        xs = np.minimum(np.arange(w), np.arange(w)[::-1]) / edge
        ys = np.minimum(np.arange(h), np.arange(h)[::-1]) / edge
        m = np.clip(np.minimum.outer(ys, xs), 0, 1)
        _masks[size] = Image.fromarray((m * 255).astype(np.uint8))
    return _masks[size]


def process(name, src_dir):
    c = STOCK_CLIPS[name]
    frames, fps = read(os.path.join(src_dir, f"{name}.mp4"))
    total = len(frames) / fps
    length = min(c["length"], total - c["start"] - FADE)
    out = os.path.normpath(os.path.join(OUT_DIR, f"video_{name}.mp4"))
    writer = imageio_ffmpeg.write_frames(
        out, (W, H), fps=FPS, codec="libx264", pix_fmt_out="yuv420p", quality=None,
        output_params=["-crf", "26", "-preset", "slow", "-profile:v", "main", "-movflags", "+faststart", "-an"],
    )
    writer.send(None)
    n = round(length * FPS)
    for i in range(n):
        t = i / FPS
        a = frame_at(frames, fps, c["start"] + FADE + t).astype(np.float32)
        if t > length - FADE:  # blend the tail into the head so the loop has no jump
            mix = (t - (length - FADE)) / FADE
            b = frame_at(frames, fps, c["start"] + t - (length - FADE)).astype(np.float32)
            a = a * (1 - mix) + b * mix
        writer.send(compose(a.astype(np.uint8), c["crop"]).tobytes())
    writer.close()
    print(f"{name}: {n / FPS:.1f}s, {os.path.getsize(out) // 1024} KB")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("src_dir")
    ap.add_argument("names", nargs="*")
    args = ap.parse_args()
    for name in args.names or list(STOCK_CLIPS):
        process(name, args.src_dir)


if __name__ == "__main__":
    main()
