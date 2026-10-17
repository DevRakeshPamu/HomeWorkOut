# Exercise demo videos

Every exercise ID (`ex_<exercise>`) has its own looping clip in
`app/src/main/res/raw/video_<exercise>.mp4` (960x640, H.264, 30 fps). There
are two sources:

| Source | Exercises | Script |
| --- | --- | --- |
| Real footage from Pexels (free commercial use) | squats, push-ups (knee), lunges, reverse lunges, mountain climbers, high knees, bird dog, cat-cow, child's pose, bicycle crunches, glute bridges, plank, jumping jacks, arm circles | `stock.py` |
| Original animated coach | wall sit, wide push-ups, diamond push-ups, shoulder taps, glute bridge march, superman, prone Y-T-W | `render.py` |

The animated clips cover moves that Pexels has no clean, correct-form clip
for. Swap them for real footage when the trainer shoot from the PRD happens.
Credits and licence details are in [CREDITS.md](CREDITS.md).

## Setup

```bash
python3 -m venv .venv && .venv/bin/pip install pillow numpy imageio-ffmpeg
```

## Real footage (`stock.py`)

`STOCK_CLIPS` records each clip's Pexels ID, creator, download URL, the
segment used (`start`, `length`) and a crop. To rebuild:

1. Download each `url` to `DIR/<exercise>.mp4`.
2. Run `.venv/bin/python stock.py DIR` (or name specific exercises).

Each clip is cut to one clean repetition, and the loop seam is crossfaded. The
crop is fitted inside the area the player keeps clear (top badge, bottom cue
banner). The rest of the frame is filled with the studio colour or a blurred
copy of the shot.

## Animated clips (`render.py`)

- `rig.py`: 2D figure, IK limbs and drawing.
- `exercises.py`: keyframed motion for each exercise.

```bash
.venv/bin/python render.py wall_sit superman --preview out
```

Note that `render.py` with no names re-renders every exercise in
`exercises.py`, which overwrites the real-footage clips. Pass names explicitly.

To add an exercise, give it a clip from either source and map its ID in
`exerciseVideoRes()` (`core/ui/components/ExerciseVideoPlayer.kt`).
