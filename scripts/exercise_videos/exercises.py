"""Keyframed motion for every exercise in the Fit10 Mom catalog.

Each entry is (keys, min_seconds): keys is a list of (pose, seconds) where
seconds is the time spent easing from that pose to the next one. The last key
eases back into the first, so every clip loops seamlessly.
"""
import math

from rig import FOREARM, SHIN, THIGH, TORSO, UPPER_ARM, add, direction, mul, pose

LEG = THIGH + SHIN + 0.02  # hip to ankle when the knee is straight


# ── Pose helpers ─────────────────────────────────────────────────────────────
def stand(x=0.0, **kw):
    base = dict(pelvis=(x, 0.92), fn=(x + 0.04, 0.06), ff=(x - 0.02, 0.06),
                hn=(x + 0.05, 0.90), hf=(x + 0.02, 0.90))
    base.update(kw)
    return pose(**base)


def plank_line(elev, ankle_x, ankle_y=0.12):
    """Straight body from the ankles, raised `elev` degrees off the floor."""
    a = 90 - elev
    d = direction(a)
    pelvis = (ankle_x + LEG * d[0], ankle_y + LEG * d[1])
    shoulder = add(pelvis, mul(d, TORSO))
    return pelvis, a, shoulder


def high_plank(elev, ankle_x, hands_x, **kw):
    pelvis, a, _ = plank_line(elev, ankle_x)
    base = dict(pelvis=pelvis, torso=a, fn=(ankle_x, 0.12), ff=(ankle_x - 0.02, 0.12),
                hn=(hands_x, 0.05), hf=(hands_x - 0.02, 0.05))
    base.update(kw)
    return pose(**base)


def quadruped(**kw):
    base = dict(pelvis=(-0.08, 0.52), torso=78, hn=(0.50, 0.05), hf=(0.48, 0.05),
                fn=(-0.50, 0.05), ff=(-0.52, 0.05), kn=1, kf=1, en=-1, ef=-1, neck=-10, pointed=True)
    base.update(kw)
    return pose(**base)


def supine_bridge(lift_deg, shoulder=(-0.55, 0.11), **kw):
    """Lying on the back, head to the left; the pelvis pivots around the shoulders."""
    t = math.radians(lift_deg)
    pelvis = (shoulder[0] + TORSO * math.cos(t), shoulder[1] + TORSO * math.sin(t))
    v = (shoulder[0] - pelvis[0], shoulder[1] - pelvis[1])
    a = math.degrees(math.atan2(v[0], v[1]))
    base = dict(pelvis=pelvis, torso=a, fn=(0.33, 0.06), ff=(0.31, 0.06),
                hn=(0.02, 0.05), hf=(0.0, 0.05), en=1, ef=1, neck=8 + lift_deg)
    base.update(kw)
    return pose(**base)


def front_stand(**kw):
    base = dict(view="front", pelvis=(0.0, 0.935), hipw=0.10, shw=0.19,
                fn=(0.12, 0.06), ff=(-0.12, 0.06), hn=(0.27, 0.88), hf=(-0.27, 0.88),
                kn=1, kf=-1, en=1, ef=-1)
    base.update(kw)
    return pose(**base)


def front_pushup(height, hands_half_width):
    """Push-up seen from the front: chest and shoulders face the camera, legs hidden behind."""
    return pose(view="front", legs=False, pelvis=(0.0, height - 0.15), torso=0, tlen=0.15,
                hipw=0.15, shw=0.21, neck_len=-0.11, neck=0,
                hn=(hands_half_width, 0.05), hf=(-hands_half_width, 0.05), en=1, ef=-1)


# ── Exercises ────────────────────────────────────────────────────────────────
def squats():
    up = stand(-0.05, fn=(0.02, 0.06), ff=(-0.02, 0.06), hn=(0.05, 0.92), hf=(0.02, 0.92))
    down = stand(-0.05, pelvis=(-0.27, 0.50), torso=38, fn=(0.02, 0.06), ff=(-0.02, 0.06),
                 hn=(0.55, 0.92), hf=(0.52, 0.92), neck=-12)
    return [(up, 1.1), (down, 0.25), (down, 1.1), (up, 0.35)]


def wall_sit():
    base = dict(pelvis=(-0.26, 0.50), torso=0, fn=(0.18, 0.06), ff=(0.15, 0.06),
                hn=(0.04, 0.56), hf=(0.01, 0.56))
    inhale = pose(**base)
    exhale = pose(**dict(base, pelvis=(-0.26, 0.495), hn=(0.05, 0.555), torso=-1.5))
    return [(inhale, 1.6), (exhale, 1.6)]


def push_ups():
    up = high_plank(23, -0.72, 0.62)
    down = high_plank(8, -0.72, 0.62, en=-1, ef=-1)
    return [(up, 1.0), (down, 0.2), (down, 0.9), (up, 0.35)]


def wide_push_ups():
    up, down = front_pushup(0.62, 0.60), front_pushup(0.26, 0.60)
    return [(up, 1.0), (down, 0.2), (down, 0.9), (up, 0.35)]


def diamond_push_ups():
    up, down = front_pushup(0.62, 0.07), front_pushup(0.30, 0.07)
    return [(up, 1.0), (down, 0.2), (down, 0.9), (up, 0.35)]


def lunges():
    def stand_l(): return stand(-0.30, fn=(-0.27, 0.06), ff=(-0.30, 0.06), hn=(-0.20, 0.98), hf=(-0.22, 0.98), en=1, ef=1)
    keys = []
    for near in (True, False):
        front, back = ("fn", "ff") if near else ("ff", "fn")
        step = stand_l()
        step[front] = (0.10, 0.22)
        step["pelvis"] = (-0.22, 0.90)
        low = stand_l()
        low.update(pelvis=(0.06, 0.48), hn=(0.16, 0.56), hf=(0.14, 0.56))
        low[front] = (0.46, 0.06)
        low[back] = (-0.34, 0.12)
        keys += [(stand_l(), 0.45), (step, 0.45), (low, 0.25), (low, 0.6), (step, 0.45)]
    return keys


def reverse_lunges():
    def stand_l(): return stand(0.0, hn=(0.10, 0.98), hf=(0.08, 0.98), en=1, ef=1)
    keys = []
    for near in (True, False):
        moving = "fn" if near else "ff"
        lift = stand_l()
        lift[moving] = (-0.30, 0.22)
        lift["pelvis"] = (-0.04, 0.90)
        low = stand_l()
        low.update(pelvis=(-0.20, 0.48), hn=(-0.10, 0.56), hf=(-0.12, 0.56))
        low[moving] = (-0.74, 0.12)
        keys += [(stand_l(), 0.45), (lift, 0.45), (low, 0.25), (low, 0.6), (lift, 0.45)]
    return keys


def mountain_climbers():
    base = high_plank(24, -0.72, 0.62)
    hip = base["pelvis"]
    near_in = dict(base, fn=(hip[0] - 0.02, 0.34))
    far_in = dict(base, ff=(hip[0] - 0.04, 0.34))
    return [(pose(**near_in), 0.32), (pose(**base), 0.1), (pose(**far_in), 0.32), (pose(**base), 0.1)]


def high_knees():
    def step(near_up, bounce):
        p = stand(0.0, pelvis=(0.0, 0.92 + bounce), torso=4)
        knee_side, stance = ("fn", "ff") if near_up else ("ff", "fn")
        p[knee_side] = (0.16, 0.52 + bounce)
        p[stance] = (0.0, 0.06)
        arm_fwd, arm_back = ("hf", "hn") if near_up else ("hn", "hf")
        p[arm_fwd] = (0.26, 1.22)
        p[arm_back] = (-0.22, 1.02)
        p["en"] = p["ef"] = 1
        return p
    return [(step(True, 0.05), 0.28), (step(False, 0.05), 0.28)]


def shoulder_taps():
    base = high_plank(24, -0.72, 0.62)
    _, _, shoulder = plank_line(24, -0.72)
    tap = (shoulder[0] - 0.02, shoulder[1] - 0.05)
    near = pose(**dict(base, hn=tap, en=1))
    far = pose(**dict(base, hf=tap, ef=1))
    return [(base, 0.4), (near, 0.25), (near, 0.4), (base, 0.25), (base, 0.4), (far, 0.25), (far, 0.4), (base, 0.25)]


def bird_dog():
    neutral = quadruped()
    a = quadruped(hn=(1.02, 0.68), ff=(-1.02, 0.56))
    b = quadruped(hf=(1.02, 0.68), fn=(-1.02, 0.56))
    return [(neutral, 0.8), (a, 0.3), (a, 0.8), (neutral, 0.8), (b, 0.3), (b, 0.8)]


def cat_cow():
    neutral = quadruped()
    cat = quadruped(curve=0.11, neck=-35, pelvis=(-0.08, 0.51))
    cow = quadruped(curve=-0.08, neck=28, pelvis=(-0.08, 0.52))
    return [(neutral, 1.0), (cat, 0.4), (cat, 1.3), (cow, 0.4), (cow, 1.0)]


def childs_pose():
    base = dict(pelvis=(-0.33, 0.27), torso=99, neck=-40, fn=(-0.36, 0.05), ff=(-0.38, 0.05),
                kn=1, kf=1, pointed=True, hn=(0.86, 0.05), hf=(0.84, 0.05), en=1, ef=1)
    inhale = pose(**dict(base, pelvis=(-0.33, 0.285), torso=97))
    exhale = pose(**base)
    return [(inhale, 2.0), (exhale, 2.0)]


def bicycle_crunches():
    def side(near):
        p = pose(pelvis=(0.0, 0.12), torso=-68, neck=10, en=1, ef=1,
                 hn=(-0.52, 0.42), hf=(-0.50, 0.44))
        bent, straight = ("fn", "ff") if near else ("ff", "fn")
        p[bent] = (0.28, 0.46)
        p[straight] = (0.86, 0.30)
        p["kn"] = p["kf"] = 1
        return p
    return [(side(True), 0.6), (side(True), 0.15), (side(False), 0.6), (side(False), 0.15)]


def glute_bridges():
    down, up = supine_bridge(0), supine_bridge(30)
    return [(down, 1.0), (up, 0.6), (up, 1.0), (down, 0.4)]


def glute_bridge_march():
    up = supine_bridge(30)
    a = supine_bridge(30, fn=(0.34, 0.66))
    b = supine_bridge(30, ff=(0.32, 0.66))
    return [(up, 0.5), (a, 0.3), (a, 0.5), (up, 0.5), (b, 0.3), (b, 0.5)]


def plank():
    pelvis, a, shoulder = plank_line(11, -0.74)
    forearm = (shoulder[0] + FOREARM, 0.05)
    base = dict(pelvis=pelvis, torso=a, fn=(-0.74, 0.12), ff=(-0.76, 0.12),
                hn=forearm, hf=(forearm[0] - 0.02, 0.05), en=-1, ef=-1)
    inhale = pose(**base)
    exhale = pose(**dict(base, pelvis=(pelvis[0], pelvis[1] + 0.012)))
    return [(inhale, 1.6), (exhale, 1.6)]


def superman():
    rest = pose(pelvis=(-0.08, 0.12), torso=90, neck=-6, pointed=True,
                hn=(1.05, 0.10), hf=(1.03, 0.10), en=1, ef=1,
                fn=(-0.98, 0.07), ff=(-1.0, 0.07), kn=-1, kf=-1)
    lift = pose(**dict(rest, torso=80, neck=-14, hn=(1.02, 0.40), hf=(1.00, 0.40),
                       fn=(-0.94, 0.28), ff=(-0.96, 0.28)))
    return [(rest, 0.8), (lift, 0.4), (lift, 1.0), (rest, 0.5)]


def prone_ytw():
    """Seen from above: lying face down on the mat, arms trace Y, T and W shapes."""
    def above(**kw):
        return front_stand(from_above=True, fn=(0.10, 0.06), ff=(-0.10, 0.06), **kw)
    y = above(hn=(0.52, 2.02), hf=(-0.52, 2.02), en=-1, ef=1)
    t = above(hn=(0.78, 1.47), hf=(-0.78, 1.47), en=-1, ef=1)
    w = above(hn=(0.50, 1.66), hf=(-0.50, 1.66), en=-1, ef=1)
    return [(y, 0.5), (y, 0.6), (t, 0.5), (t, 0.6), (w, 0.5), (w, 0.6)]


def jumping_jacks():
    closed = front_stand(fn=(0.10, 0.06), ff=(-0.10, 0.06), hn=(0.27, 0.86), hf=(-0.27, 0.86))
    air = front_stand(pelvis=(0.0, 1.02), fn=(0.24, 0.16), ff=(-0.24, 0.16), hn=(0.78, 1.50), hf=(-0.78, 1.50))
    opened = front_stand(pelvis=(0.0, 0.90), fn=(0.40, 0.06), ff=(-0.40, 0.06), hn=(0.26, 2.04), hf=(-0.26, 2.04),
                         en=-1, ef=1)
    return [(closed, 0.22), (air, 0.22), (opened, 0.22), (air, 0.22)]


def arm_circles():
    keys = []
    steps = 8
    for i in range(steps):
        t = 2 * math.pi * i / steps
        dx, dy = 0.025 * math.cos(t), 0.09 * math.sin(t)
        keys.append((front_stand(fn=(0.16, 0.06), ff=(-0.16, 0.06),
                                 hn=(0.82 + dx, 1.47 + dy), hf=(-0.82 - dx, 1.47 + dy)), 0.14))
    return keys


# Scene extras: a wall behind the figure, or a camera looking down at the mat.
SCENES = {
    "wall_sit": dict(wall_x=-0.40),
    "prone_ytw": dict(top_down=True),
}

EXERCISES = {
    "squats": squats,
    "wall_sit": wall_sit,
    "push_ups": push_ups,
    "wide_push_ups": wide_push_ups,
    "diamond_push_ups": diamond_push_ups,
    "lunges": lunges,
    "reverse_lunges": reverse_lunges,
    "mountain_climbers": mountain_climbers,
    "high_knees": high_knees,
    "shoulder_taps": shoulder_taps,
    "bird_dog": bird_dog,
    "cat_cow": cat_cow,
    "childs_pose": childs_pose,
    "bicycle_crunches": bicycle_crunches,
    "glute_bridges": glute_bridges,
    "glute_bridge_march": glute_bridge_march,
    "plank": plank,
    "superman": superman,
    "prone_ytw": prone_ytw,
    "jumping_jacks": jumping_jacks,
    "arm_circles": arm_circles,
}
