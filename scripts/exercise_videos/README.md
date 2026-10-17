# Exercise demo videos

Generates the looping demo clips in `app/src/main/res/raw/video_<exercise>.mp4`
(one per exercise ID, `ex_<exercise>`). They are original animations of a
plum-styled Fit10 Mom coach, so they are license-free. They stand in until the
trainer shoot described in the PRD.

- `rig.py`: 2D figure, IK limbs and drawing.
- `exercises.py`: keyframed motion for each exercise.
- `render.py`: renders frames and encodes H.264 (960x640, 30 fps, loops of at least 3 s).

```bash
python3 -m venv .venv && .venv/bin/pip install pillow numpy imageio-ffmpeg
.venv/bin/python render.py                      # all clips
.venv/bin/python render.py squats --preview out # one clip plus a contact sheet
```

To add an exercise, write a function in `exercises.py` that returns
`[(pose, seconds), ...]`, register it in `EXERCISES`, render it, and map the
ID in `exerciseVideoRes()` (`core/ui/components/ExerciseVideoPlayer.kt`).
