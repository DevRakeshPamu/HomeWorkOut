"""2D figure rig and renderer for Fit10 Mom exercise demo videos.

World units: ground is y = 0, y points up, the figure faces +x in side view.
A pose is a dict; limbs are solved with 2-bone IK from pelvis/shoulder to a
hand or foot target, so contacts with the floor stay put while the body moves.
"""
import math

from PIL import Image, ImageDraw

# ── Proportions (world units, standing height about 1.75) ───────────────────
THIGH, SHIN = 0.44, 0.44
UPPER_ARM, FOREARM = 0.30, 0.28
TORSO = 0.52
NECK, HEAD_R = 0.07, 0.105
FOOT = 0.13

# ── Palette (Fit10 Mom plum) ─────────────────────────────────────────────────
BG_TOP = (43, 26, 41)
BG_BOTTOM = (22, 15, 21)
MAT = (58, 37, 55)
MAT_EDGE = (82, 54, 77)
WALL = (52, 34, 49)
SHADOW = (10, 6, 9, 110)
SKIN = (242, 201, 174)
SKIN_FAR = (205, 165, 140)
TOP = (230, 168, 216)
TOP_SHADE = (205, 140, 192)
LEGGINGS = (142, 67, 128)
LEGGINGS_FAR = (98, 44, 88)
SHOE = (244, 236, 242)
SHOE_FAR = (190, 178, 188)
HAIR = (59, 34, 48)
HAIR_TOP = (96, 58, 82)  # lighter so the back of the head reads against the mat

BASE_POSE = dict(
    view="side",          # "side" or "front"
    pelvis=(0.0, 0.92),
    torso=0.0,            # degrees from straight up, positive leans toward +x
    tlen=TORSO,           # torso length (shortened for foreshortened front views)
    curve=0.0,            # spine bend; positive rounds the back upward (cat)
    neck=0.0,             # head tilt relative to the torso, degrees
    neck_len=NECK,
    hipw=0.0, shw=0.0,    # half widths, used in front view
    hn=(0.05, 0.90), hf=(0.02, 0.90),   # hand targets: near (right) and far (left)
    fn=(0.04, 0.06), ff=(-0.02, 0.06),  # foot targets
    en=-1, ef=-1,         # elbow bend sides (+1 counter-clockwise)
    kn=1, kf=1,           # knee bend sides
    legs=True,
    pointed=False,        # feet continue the shin line (prone, superman)
    from_above=False,     # front view rig seen from overhead: show the back of the head
)


def pose(**kw):
    p = dict(BASE_POSE)
    p.update(kw)
    return p


# ── Vector helpers ───────────────────────────────────────────────────────────
def add(a, b):
    return (a[0] + b[0], a[1] + b[1])


def sub(a, b):
    return (a[0] - b[0], a[1] - b[1])


def mul(a, s):
    return (a[0] * s, a[1] * s)


def length(a):
    return math.hypot(a[0], a[1])


def norm(a):
    l = length(a) or 1e-9
    return (a[0] / l, a[1] / l)


def rot(a, deg):
    r = math.radians(deg)
    c, s = math.cos(r), math.sin(r)
    return (a[0] * c - a[1] * s, a[0] * s + a[1] * c)


def direction(deg):
    """Unit vector for an angle measured from straight up, clockwise positive."""
    r = math.radians(deg)
    return (math.sin(r), math.cos(r))


def ik(root, target, l1, l2, side):
    """Two-bone IK. Returns (joint, end); side picks the bend direction."""
    v = sub(target, root)
    d = max(abs(l1 - l2) + 1e-4, min(l1 + l2 - 1e-4, length(v)))
    u = norm(v)
    cos_a = (l1 * l1 + d * d - l2 * l2) / (2 * l1 * d)
    a = math.degrees(math.acos(max(-1.0, min(1.0, cos_a))))
    joint = add(root, mul(rot(u, side * a), l1))
    end = add(root, mul(u, d))
    return joint, end


def lerp(a, b, t):
    if isinstance(a, tuple):
        return tuple(x + (y - x) * t for x, y in zip(a, b))
    if isinstance(a, (int, float)) and not isinstance(a, bool):
        return a + (b - a) * t
    return a if t < 0.5 else b


def blend(p, q, t):
    return {k: lerp(p[k], q[k], t) for k in p}


# ── Skeleton ─────────────────────────────────────────────────────────────────
def solve(p):
    d = direction(p["torso"])
    n = (-d[1], d[0])  # back side of the torso
    pelvis = p["pelvis"]
    mid = add(add(pelvis, mul(d, p["tlen"] / 2)), mul(n, p["curve"]))
    chest = add(pelvis, mul(d, p["tlen"]))
    # Shoulders follow the curve's end tangent so the head follows a rounded or arched back.
    tangent = norm(sub(chest, mid))
    head_dir = rot(tangent, -p["neck"])
    head = add(chest, mul(head_dir, p["neck_len"] + HEAD_R))
    side_n = (d[1], -d[0])  # screen-right of the torso, used in front view

    sk = dict(pelvis=pelvis, mid=mid, chest=chest, head=head, d=d, back=n, head_dir=head_dir)
    for limb, sign in (("n", 1), ("f", -1)):
        shoulder = add(chest, mul(side_n, sign * p["shw"]))
        hip = add(pelvis, mul(side_n, sign * p["hipw"]))
        elbow, wrist = ik(shoulder, p["h" + limb], UPPER_ARM, FOREARM, p["e" + limb])
        knee, ankle = ik(hip, p["f" + limb], THIGH, SHIN, p["k" + limb])
        shin_dir = norm(sub(ankle, knee))
        if p["view"] == "front":
            foot_dir = (sign * 0.35, -0.25)
        elif p["pointed"]:
            foot_dir = shin_dir
        elif ankle[1] < 0.13 and abs(shin_dir[1]) > 0.55:
            foot_dir = (1.0, 0.0)  # planted foot stays flat on the floor
        else:
            foot_dir = rot(shin_dir, 90)
        toe = add(ankle, mul(norm(foot_dir), FOOT))
        sk[limb] = dict(shoulder=shoulder, elbow=elbow, wrist=wrist, hip=hip, knee=knee, ankle=ankle, toe=toe)
    return sk


def points(p, sk):
    pts = [sk["pelvis"], sk["chest"], add(sk["head"], (0, HEAD_R)), sub(sk["head"], (0, HEAD_R)),
           add(sk["head"], (HEAD_R, 0)), sub(sk["head"], (HEAD_R, 0))]
    for limb in ("n", "f"):
        L = sk[limb]
        pts += [L["wrist"], L["elbow"], L["shoulder"]]
        if p["legs"]:
            pts += [L["knee"], L["ankle"], L["toe"]]
    return pts


# ── Drawing ──────────────────────────────────────────────────────────────────
class Canvas:
    def __init__(self, w, h, ss, scale, origin_x, ground_px):
        self.w, self.h, self.ss = w, h, ss
        self.scale, self.ox, self.gy = scale, origin_x, ground_px

    def px(self, p):
        return ((p[0] - self.ox) * self.scale * self.ss + self.w * self.ss / 2,
                (self.gy - p[1] * self.scale) * self.ss)

    def r(self, r):
        return r * self.scale * self.ss


def capsule(draw, cv, a, ra, b, rb, color):
    pa, pb = cv.px(a), cv.px(b)
    Ra, Rb = cv.r(ra), cv.r(rb)
    v = (pb[0] - pa[0], pb[1] - pa[1])
    l = math.hypot(*v) or 1e-6
    nx, ny = -v[1] / l, v[0] / l
    draw.polygon([(pa[0] + nx * Ra, pa[1] + ny * Ra), (pb[0] + nx * Rb, pb[1] + ny * Rb),
                  (pb[0] - nx * Rb, pb[1] - ny * Rb), (pa[0] - nx * Ra, pa[1] - ny * Ra)], fill=color)
    draw.ellipse([pa[0] - Ra, pa[1] - Ra, pa[0] + Ra, pa[1] + Ra], fill=color)
    draw.ellipse([pb[0] - Rb, pb[1] - Rb, pb[0] + Rb, pb[1] + Rb], fill=color)


def circle(draw, cv, c, r, color):
    p, R = cv.px(c), cv.r(r)
    draw.ellipse([p[0] - R, p[1] - R, p[0] + R, p[1] + R], fill=color)


def bezier(a, c, b, t):
    return add(add(mul(a, (1 - t) ** 2), mul(c, 2 * (1 - t) * t)), mul(b, t * t))


def draw_leg(draw, cv, L, far):
    pants = LEGGINGS_FAR if far else LEGGINGS
    capsule(draw, cv, L["hip"], 0.078, L["knee"], 0.062, pants)
    capsule(draw, cv, L["knee"], 0.062, L["ankle"], 0.045, pants)
    capsule(draw, cv, L["ankle"], 0.045, L["toe"], 0.035, SHOE_FAR if far else SHOE)


def draw_arm(draw, cv, L, far):
    skin = SKIN_FAR if far else SKIN
    capsule(draw, cv, L["shoulder"], 0.052, L["elbow"], 0.045, skin)
    capsule(draw, cv, L["elbow"], 0.045, L["wrist"], 0.036, skin)
    circle(draw, cv, L["wrist"], 0.042, skin)


def draw_torso(draw, cv, p, sk):
    if p["view"] == "front":
        hn, hf = sk["n"]["hip"], sk["f"]["hip"]
        sn, sf = sk["n"]["shoulder"], sk["f"]["shoulder"]
        waist_n = add(hn, mul(sub(sn, hn), 0.32))
        waist_f = add(hf, mul(sub(sf, hf), 0.32))
        draw.polygon([cv.px(x) for x in (waist_n, sn, sf, waist_f)], fill=TOP)
        lower = LEGGINGS if p["legs"] else TOP
        draw.polygon([cv.px(x) for x in (hn, waist_n, waist_f, hf)], fill=lower)
        for s in (sn, sf):
            circle(draw, cv, s, 0.06, TOP)
        for h in (hn, hf):
            circle(draw, cv, h, 0.07, lower)
        return
    a, c, b = sk["pelvis"], add(sk["mid"], mul(sk["back"], p["curve"])), sk["chest"]
    steps = 14
    for i in range(steps + 1):
        t = i / steps
        pt = bezier(a, c, b, t)
        r = 0.105 + 0.02 * math.sin(t * math.pi) + (0.01 if t > 0.75 else 0.0)
        circle(draw, cv, pt, r, LEGGINGS if t < 0.3 else TOP)
    # A soft shade along the back so the torso reads as a volume, not a sticker.
    for i in range(6, steps + 1):
        t = i / steps
        pt = add(bezier(a, c, b, t), mul(sk["back"], 0.05))
        circle(draw, cv, pt, 0.055, TOP_SHADE)


def draw_head(draw, cv, p, sk):
    h = sk["head"]
    capsule(draw, cv, sk["chest"], 0.04, h, 0.04, SKIN)
    if p["from_above"]:
        circle(draw, cv, h, HEAD_R + 0.012, SKIN_FAR)  # ears and neck peeking out
        circle(draw, cv, add(h, mul(sk["head_dir"], 0.10)), 0.055, HAIR_TOP)
        circle(draw, cv, h, HEAD_R, HAIR_TOP)
        return
    if p["view"] == "front":
        circle(draw, cv, add(h, mul(sk["head_dir"], 0.11)), 0.06, HAIR)
        circle(draw, cv, add(h, mul(sk["head_dir"], 0.015)), HEAD_R + 0.012, HAIR)
        circle(draw, cv, sub(h, mul(sk["head_dir"], 0.012)), HEAD_R * 0.92, SKIN)
        return
    back = (-sk["head_dir"][1], sk["head_dir"][0])
    circle(draw, cv, add(add(h, mul(back, 0.12)), mul(sk["head_dir"], 0.03)), 0.058, HAIR)
    circle(draw, cv, h, HEAD_R, SKIN)
    # Hair cap over the back and top of the head.
    cap = add(h, add(mul(back, 0.025), mul(sk["head_dir"], 0.02)))
    circle(draw, cv, cap, HEAD_R * 0.93, HAIR)
    circle(draw, cv, add(h, sub(mul(sk["head_dir"], -0.012), mul(back, 0.03))), HEAD_R * 0.8, SKIN)


def render_frame(p, cv, background, shadow_span):
    img = background.copy()
    draw = ImageDraw.Draw(img, "RGBA")
    sk = solve(p)

    # Soft contact shadow under the body (none when looking down from above).
    x0, x1 = shadow_span(sk) if not p["from_above"] else (0.0, 0.0)
    a, b = cv.px((x0, 0.0)), cv.px((x1, 0.0))
    hgt = cv.r(0.035)
    draw.ellipse([a[0], a[1] - hgt, b[0], b[1] + hgt * 0.6], fill=SHADOW)

    if p["view"] == "front":
        if p["legs"]:
            draw_leg(draw, cv, sk["f"], False)
            draw_leg(draw, cv, sk["n"], False)
        draw_torso(draw, cv, p, sk)
        draw_arm(draw, cv, sk["f"], False)
        draw_arm(draw, cv, sk["n"], False)
        draw_head(draw, cv, p, sk)
    else:
        draw_arm(draw, cv, sk["f"], True)
        if p["legs"]:
            draw_leg(draw, cv, sk["f"], True)
        draw_torso(draw, cv, p, sk)
        draw_head(draw, cv, p, sk)
        if p["legs"]:
            draw_leg(draw, cv, sk["n"], False)
        draw_arm(draw, cv, sk["n"], False)
    return img


def make_background(w, h, ss, cv, mat_span, wall_x=None, top_down=False):
    W, H = w * ss, h * ss
    img = Image.new("RGB", (W, H))
    top, bot = BG_TOP, BG_BOTTOM
    for y in range(H):
        t = y / (H - 1)
        c = tuple(int(top[i] + (bot[i] - top[i]) * t) for i in range(3))
        img.paste(c, (0, y, W, y + 1))
    draw = ImageDraw.Draw(img)
    if top_down:
        # Looking straight down at a yoga mat; no floor line.
        a, b = cv.px((mat_span[0], 2.25)), cv.px((mat_span[1], -0.12))
        draw.rounded_rectangle([a[0], a[1], b[0], b[1]], radius=cv.r(0.06), fill=MAT)
        draw.rounded_rectangle([a[0], a[1], b[0], b[1]], radius=cv.r(0.06), outline=MAT_EDGE, width=3 * ss)
        return img
    gy = cv.px((0, 0))[1]
    draw.rectangle([0, gy, W, H], fill=tuple(max(0, c - 4) for c in BG_BOTTOM))
    if wall_x is not None:
        wx = cv.px((wall_x, 0))[0]
        draw.rectangle([0, 0, wx, gy], fill=WALL)
        draw.rectangle([wx - 3 * ss, 0, wx, gy], fill=MAT_EDGE)
    a, b = cv.px((mat_span[0], 0)), cv.px((mat_span[1], 0))
    r = cv.r(0.03)
    draw.rounded_rectangle([a[0], gy - r * 0.4, b[0], gy + r * 1.2], radius=r, fill=MAT)
    draw.rounded_rectangle([a[0], gy - r * 0.4, b[0], gy + r * 0.1], radius=r * 0.5, fill=MAT_EDGE)
    return img
