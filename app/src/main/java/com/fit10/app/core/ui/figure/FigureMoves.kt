package com.fit10.app.core.ui.figure

internal object FigureMoves {
    val ALL: Map<String, FigMove> = mapOf(
        "ex_squats" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(900, 200, floatArrayOf(-90f, 0f, -92f, 95f, 92f, 85f, 88f, 90f, 90f, 92f, 92f), Joint.ANKLE_N, 0.8f, 0.985f),
            FigFrame(1100, 300, floatArrayOf(-55f, 0f, -40f, 0f, -5f, 5f, 0f, 18f, 112f, 22f, 112f), Joint.ANKLE_N, 0.8f, 0.985f)
        )),
        "ex_lunges" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(900, 200, floatArrayOf(-90f, 0f, -92f, 95f, 92f, 85f, 88f, 88f, 90f, 92f, 92f), Joint.ANKLE_F, 0.7f, 0.985f),
            FigFrame(1100, 300, floatArrayOf(-88f, 0f, -90f, 92f, 90f, 88f, 90f, 5f, 92f, 112f, 178f), Joint.ANKLE_F, 0.55f, 0.985f)
        )),
        "ex_reverse_lunges" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(900, 200, floatArrayOf(-90f, 0f, -92f, 95f, 92f, 85f, 88f, 90f, 90f, 92f, 92f), Joint.ANKLE_N, 0.85f, 0.985f),
            FigFrame(1100, 300, floatArrayOf(-88f, 0f, -90f, 92f, 90f, 88f, 90f, 5f, 92f, 112f, 178f), Joint.ANKLE_N, 1.03f, 0.985f)
        )),
        "ex_wall_sit" to FigMove(front = false, wall = true, frames = listOf(
            FigFrame(1500, 300, floatArrayOf(-90f, 0f, -90f, 40f, 0f, 35f, -5f, 0f, 90f, 3f, 90f), Joint.PELVIS, 0.48f, 0.755f),
            FigFrame(1500, 300, floatArrayOf(-90f, 0f, -95f, 40f, 0f, 35f, -5f, 0f, 90f, 3f, 90f), Joint.PELVIS, 0.48f, 0.752f)
        )),
        "ex_high_knees" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(300, 0, floatArrayOf(-88f, 0f, -90f, 60f, -40f, 125f, 60f, -15f, 80f, 95f, 90f), Joint.ANKLE_F, 0.8f, 0.985f),
            FigFrame(300, 0, floatArrayOf(-88f, 0f, -90f, 125f, 60f, 60f, -40f, 95f, 90f, -15f, 80f), Joint.ANKLE_N, 0.8f, 0.985f)
        )),
        "ex_jumping_jacks" to FigMove(front = true, wall = false, frames = listOf(
            FigFrame(400, 50, floatArrayOf(-90f, 0f, -90f, 82f, 88f, 98f, 92f, 88f, 90f, 92f, 90f), Joint.PELVIS, 0.8f, 0.52f),
            FigFrame(400, 50, floatArrayOf(-90f, 0f, -90f, -55f, -75f, -125f, -105f, 68f, 72f, 112f, 108f), Joint.PELVIS, 0.8f, 0.54f)
        )),
        "ex_arm_circles" to FigMove(front = true, wall = false, frames = listOf(
            FigFrame(350, 0, floatArrayOf(-90f, 0f, -90f, -12f, -14f, 192f, 194f, 88f, 90f, 92f, 90f), Joint.PELVIS, 0.8f, 0.52f),
            FigFrame(350, 0, floatArrayOf(-90f, 0f, -90f, -2f, 2f, 182f, 178f, 88f, 90f, 92f, 90f), Joint.PELVIS, 0.8f, 0.52f),
            FigFrame(350, 0, floatArrayOf(-90f, 0f, -90f, 10f, 12f, 170f, 168f, 88f, 90f, 92f, 90f), Joint.PELVIS, 0.8f, 0.52f),
            FigFrame(350, 0, floatArrayOf(-90f, 0f, -90f, 0f, -4f, 180f, 184f, 88f, 90f, 92f, 90f), Joint.PELVIS, 0.8f, 0.52f)
        )),
        "ex_bird_dog" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(900, 150, floatArrayOf(-3f, 0f, 10f, 90f, 90f, 88f, 90f, 90f, 180f, 92f, 180f), Joint.KNEE_N, 0.65f, 0.975f),
            FigFrame(1100, 700, floatArrayOf(-3f, 0f, 0f, -2f, -2f, 88f, 90f, 90f, 180f, 178f, 180f), Joint.KNEE_N, 0.65f, 0.975f),
            FigFrame(900, 150, floatArrayOf(-3f, 0f, 10f, 90f, 90f, 88f, 90f, 90f, 180f, 92f, 180f), Joint.KNEE_F, 0.65f, 0.975f),
            FigFrame(1100, 700, floatArrayOf(-3f, 0f, 0f, 90f, 90f, -2f, -2f, 178f, 180f, 92f, 180f), Joint.KNEE_F, 0.65f, 0.975f)
        )),
        "ex_cat_cow" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(1500, 400, floatArrayOf(-3f, -1f, -35f, 90f, 90f, 88f, 90f, 90f, 180f, 92f, 180f), Joint.KNEE_N, 0.65f, 0.975f),
            FigFrame(1500, 400, floatArrayOf(-3f, 1.3f, 70f, 90f, 90f, 88f, 90f, 90f, 180f, 92f, 180f), Joint.KNEE_N, 0.65f, 0.975f)
        )),
        "ex_childs_pose" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(1800, 600, floatArrayOf(10f, 0.6f, 15f, 4f, 2f, 6f, 2f, 40f, 180f, 42f, 180f), Joint.KNEE_N, 0.75f, 0.975f),
            FigFrame(1800, 600, floatArrayOf(8f, 0.9f, 15f, 3f, 2f, 5f, 2f, 41f, 180f, 43f, 180f), Joint.KNEE_N, 0.75f, 0.975f)
        )),
        "ex_plank" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(1500, 300, floatArrayOf(-13f, 0f, -10f, 90f, 0f, 92f, 0f, 167f, 167f, 168f, 168f), Joint.ANKLE_N, 0.3f, 0.975f),
            FigFrame(1500, 300, floatArrayOf(-14f, 0f, -10f, 90f, 0f, 92f, 0f, 166f, 166f, 167f, 167f), Joint.ANKLE_N, 0.3f, 0.975f)
        )),
        "ex_push_ups" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(900, 150, floatArrayOf(-22f, 0f, -20f, 92f, 92f, 90f, 90f, 158f, 158f, 159f, 159f), Joint.ANKLE_N, 0.3f, 0.975f),
            FigFrame(1100, 200, floatArrayOf(-13f, 0f, -10f, 165f, 92f, 163f, 92f, 167f, 167f, 168f, 168f), Joint.ANKLE_N, 0.3f, 0.975f)
        )),
        "ex_shoulder_taps" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(600, 100, floatArrayOf(-22f, 0f, -20f, 92f, 92f, 90f, 90f, 158f, 158f, 159f, 159f), Joint.ANKLE_N, 0.3f, 0.975f),
            FigFrame(600, 250, floatArrayOf(-22f, 0f, -20f, 135f, -40f, 90f, 90f, 158f, 158f, 159f, 159f), Joint.ANKLE_N, 0.3f, 0.975f),
            FigFrame(600, 100, floatArrayOf(-22f, 0f, -20f, 92f, 92f, 90f, 90f, 158f, 158f, 159f, 159f), Joint.ANKLE_N, 0.3f, 0.975f),
            FigFrame(600, 250, floatArrayOf(-22f, 0f, -20f, 92f, 92f, 135f, -40f, 158f, 158f, 159f, 159f), Joint.ANKLE_N, 0.3f, 0.975f)
        )),
        "ex_mountain_climbers" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(300, 0, floatArrayOf(-22f, 0f, -20f, 92f, 92f, 90f, 90f, 25f, 155f, 159f, 159f), Joint.HAND_N, 1.12f, 0.975f),
            FigFrame(300, 0, floatArrayOf(-22f, 0f, -20f, 92f, 92f, 90f, 90f, 159f, 159f, 25f, 155f), Joint.HAND_N, 1.12f, 0.975f)
        )),
        "ex_glute_bridges" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(1000, 200, floatArrayOf(180f, 0f, 180f, 5f, 0f, 3f, 0f, -50f, 75f, -48f, 75f), Joint.ANKLE_N, 1.1f, 0.975f),
            FigFrame(1100, 600, floatArrayOf(150f, 0f, 168f, -12f, 0f, -14f, 0f, -20f, 105f, -18f, 105f), Joint.ANKLE_N, 1.1f, 0.975f)
        )),
        "ex_glute_bridge_march" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(700, 100, floatArrayOf(150f, 0f, 168f, -12f, 0f, -14f, 0f, -20f, 105f, -18f, 105f), Joint.ANKLE_F, 1.1f, 0.975f),
            FigFrame(700, 300, floatArrayOf(150f, 0f, 168f, -12f, 0f, -14f, 0f, -75f, 10f, -18f, 105f), Joint.ANKLE_F, 1.1f, 0.975f),
            FigFrame(700, 100, floatArrayOf(150f, 0f, 168f, -12f, 0f, -14f, 0f, -20f, 105f, -18f, 105f), Joint.ANKLE_N, 1.1f, 0.975f),
            FigFrame(700, 300, floatArrayOf(150f, 0f, 168f, -12f, 0f, -14f, 0f, -20f, 105f, -75f, 10f), Joint.ANKLE_N, 1.1f, 0.975f)
        )),
        "ex_bicycle_crunches" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(600, 100, floatArrayOf(200f, 0f, 215f, 230f, 330f, 228f, 330f, -110f, 0f, -15f, -12f), Joint.PELVIS, 0.85f, 0.955f),
            FigFrame(600, 100, floatArrayOf(200f, 0f, 215f, 230f, 330f, 228f, 330f, -15f, -12f, -110f, 0f), Joint.PELVIS, 0.85f, 0.955f)
        )),
        "ex_superman" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(1000, 200, floatArrayOf(0f, 0f, 0f, 0f, 0f, 2f, 0f, 180f, 180f, 182f, 180f), Joint.PELVIS, 0.75f, 0.955f),
            FigFrame(1100, 900, floatArrayOf(-12f, 0f, -20f, -22f, -22f, -20f, -20f, 190f, 192f, 191f, 193f), Joint.PELVIS, 0.75f, 0.955f)
        )),
        "ex_prone_ytw" to FigMove(front = false, wall = false, frames = listOf(
            FigFrame(900, 100, floatArrayOf(0f, 0f, 0f, 0f, 0f, 2f, 0f, 180f, 180f, 182f, 180f), Joint.PELVIS, 0.75f, 0.955f),
            FigFrame(1000, 600, floatArrayOf(-8f, 0f, -10f, -18f, -18f, -16f, -16f, 180f, 180f, 182f, 180f), Joint.PELVIS, 0.75f, 0.955f),
            FigFrame(1000, 600, floatArrayOf(-8f, 0f, -10f, 195f, 335f, 193f, 335f, 180f, 180f, 182f, 180f), Joint.PELVIS, 0.75f, 0.955f)
        ))
    )
}
