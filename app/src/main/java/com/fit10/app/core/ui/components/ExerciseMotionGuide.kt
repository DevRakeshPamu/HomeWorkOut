package com.fit10.app.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

private val NeonGreen = Color(0xFF10B981)
private val VoltGreen = Color(0xFF00F59B)
private val ElectricCyan = Color(0xFF06B6D4)
private val AmberGlow = Color(0xFFF59E0B)
private val CanvasDarkBg = Color(0xFF0B132B)
private val GridLineColor = Color(0x1A64748B)

/**
 * Exercise motion archetype determining biomechanical rendering logic.
 */
enum class ExerciseArchetype {
    SQUAT,
    PUSHUP,
    PLANK,
    JUMPING_JACKS,
    MOUNTAIN_CLIMBER,
    LUNGE,
    GLUTE_BRIDGE,
    BICYCLE_CRUNCH,
    HIGH_KNEES,
    DEFAULT
}

fun resolveExerciseArchetype(exerciseId: String, name: String = ""): ExerciseArchetype {
    val key = (exerciseId + " " + name).lowercase()
    return when {
        "squat" in key -> ExerciseArchetype.SQUAT
        "push_up" in key || "pushup" in key || "push up" in key -> ExerciseArchetype.PUSHUP
        "plank" in key && "side" !in key -> ExerciseArchetype.PLANK
        "jack" in key -> ExerciseArchetype.JUMPING_JACKS
        "climber" in key -> ExerciseArchetype.MOUNTAIN_CLIMBER
        "lunge" in key -> ExerciseArchetype.LUNGE
        "bridge" in key -> ExerciseArchetype.GLUTE_BRIDGE
        "crunch" in key || "bicycle" in key -> ExerciseArchetype.BICYCLE_CRUNCH
        "knee" in key -> ExerciseArchetype.HIGH_KNEES
        else -> ExerciseArchetype.DEFAULT
    }
}

/**
 * High-definition interactive biomechanical motion demonstration component.
 * Visualizes 60fps kinetic exercise cycles, joint alignments, real-time movement phases,
 * and muscle activation overlays.
 */
@Composable
fun ExerciseMotionGuide(
    exerciseId: String,
    exerciseName: String,
    modifier: Modifier = Modifier,
    showControls: Boolean = true,
    onOpenVideoModal: (() -> Unit)? = null
) {
    val archetype = remember(exerciseId, exerciseName) {
        resolveExerciseArchetype(exerciseId, exerciseName)
    }

    var isSlowMotion by remember { mutableStateOf(false) }
    var isFrontAngle by remember { mutableStateOf(archetype == ExerciseArchetype.JUMPING_JACKS || archetype == ExerciseArchetype.HIGH_KNEES) }
    var isManuallyPaused by remember { mutableStateOf(false) }
    var manualProgress by remember { mutableFloatStateOf(0.5f) }

    // Motion animation cycle
    val cycleDuration = if (isSlowMotion) 4000 else 2000
    val infiniteTransition = rememberInfiniteTransition(label = "ExerciseMotionTransition")

    val animatedCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = cycleDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MotionCycle"
    )

    val currentProgress = if (isManuallyPaused) manualProgress else animatedCycle

    // Derive active movement phase & joint metrics
    val (phaseText, phaseColor) = when {
        currentProgress < 0.42f -> "ECCENTRIC (LOWERING) ↓" to ElectricCyan
        currentProgress < 0.60f -> "PEAK CONTRACTION ⏱" to VoltGreen
        else -> "CONCENTRIC (DRIVE) ↑" to AmberGlow
    }

    val primaryMuscles = remember(archetype) {
        when (archetype) {
            ExerciseArchetype.SQUAT -> listOf("Quadriceps", "Glutes", "Hamstrings")
            ExerciseArchetype.PUSHUP -> listOf("Chest (Pecs)", "Triceps", "Anterior Delts")
            ExerciseArchetype.PLANK -> listOf("Transverse Abdominis", "Core Lock", "Shoulders")
            ExerciseArchetype.JUMPING_JACKS -> listOf("Calves", "Full Body Cardio", "Deltoids")
            ExerciseArchetype.MOUNTAIN_CLIMBER -> listOf("Core", "Hip Flexors", "Shoulders")
            ExerciseArchetype.LUNGE -> listOf("Quadriceps", "Glutes", "Adductors")
            ExerciseArchetype.GLUTE_BRIDGE -> listOf("Gluteus Maximus", "Hamstrings", "Erector Spinae")
            ExerciseArchetype.BICYCLE_CRUNCH -> listOf("Obliques", "Rectus Abdominis", "Hip Flexors")
            ExerciseArchetype.HIGH_KNEES -> listOf("Hip Flexors", "Calves", "Core")
            ExerciseArchetype.DEFAULT -> listOf("Full Body", "Core Stabilizers")
        }
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CanvasDarkBg,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Phase ticker + Angle toggle + Video CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phase Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(phaseColor.copy(alpha = 0.16f))
                        .border(1.dp, phaseColor.copy(alpha = 0.4f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(phaseColor)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = phaseText,
                            color = phaseColor,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Header Action Pills: Angle Toggle & Video Modal
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Front / Side Angle Switch
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF1E293B))
                            .clickable { isFrontAngle = !isFrontAngle }
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Visibility,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isFrontAngle) "Front" else "Side 90°",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Video Tutorial Sheet Trigger
                    if (onOpenVideoModal != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(NeonGreen.copy(alpha = 0.22f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(50))
                                .clickable { onOpenVideoModal() }
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Videocam,
                                    contentDescription = "Video",
                                    tint = VoltGreen,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Video",
                                    color = VoltGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Biomechanical Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF070D1F))
            ) {
                // Kinetic Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawKineticGrid()
                    drawExerciseBiomechanics(
                        archetype = archetype,
                        progress = currentProgress,
                        isFrontAngle = isFrontAngle
                    )
                }

                // Overlay Callout: Joint Angle & Tempo
                val angleCallout = when (archetype) {
                    ExerciseArchetype.SQUAT -> "Knee: ${(160 - currentProgress * 70).toInt()}°"
                    ExerciseArchetype.PUSHUP -> "Elbow: ${(165 - currentProgress * 75).toInt()}°"
                    ExerciseArchetype.PLANK -> "Spine Alignment: 180°"
                    ExerciseArchetype.LUNGE -> "Front Knee: ${(155 - currentProgress * 65).toInt()}°"
                    ExerciseArchetype.GLUTE_BRIDGE -> "Hip Extension: ${(120 + currentProgress * 60).toInt()}°"
                    ExerciseArchetype.MOUNTAIN_CLIMBER -> "Hip Flexion: 85°"
                    else -> "Cadence: Optimal"
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC0F172A))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = angleCallout,
                        color = VoltGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Depth Ticker Indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC0F172A))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    val depthPct = (currentProgress * 100).toInt()
                    Text(
                        text = "Depth: $depthPct%",
                        color = Color(0xFFE2E8F0),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Footer Controls: Play/Pause, 0.5x Slow-Motion, and Active Muscle Tags
            if (showControls) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play / Pause & Slow Motion Toggles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play / Pause
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .clickable { isManuallyPaused = !isManuallyPaused }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isManuallyPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // 0.5x Slow-Motion Toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSlowMotion) NeonGreen else Color(0xFF1E293B))
                                .clickable { isSlowMotion = !isSlowMotion }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.SlowMotionVideo,
                                    contentDescription = "Slow Motion",
                                    tint = if (isSlowMotion) Color.Black else Color(0xFFCBD5E1),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (isSlowMotion) "0.5x Slow-Mo" else "1.0x Tempo",
                                    color = if (isSlowMotion) Color.Black else Color(0xFFCBD5E1),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Target Muscle Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        primaryMuscles.take(2).forEach { muscle ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = muscle,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Biomechanical Canvas Renderers
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawKineticGrid() {
    val width = size.width
    val height = size.height

    // Ground plane line
    val groundY = height * 0.85f
    drawLine(
        color = Color(0x3310B981),
        start = Offset(0f, groundY),
        end = Offset(width, groundY),
        strokeWidth = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
    )

    // Subtle perspective grid
    for (i in 1..4) {
        val x = width * (i / 5f)
        drawLine(
            color = GridLineColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawExerciseBiomechanics(
    archetype: ExerciseArchetype,
    progress: Float,
    isFrontAngle: Boolean
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val groundY = h * 0.85f

    when (archetype) {
        ExerciseArchetype.SQUAT -> drawSquatMotion(cx, groundY, progress, isFrontAngle)
        ExerciseArchetype.PUSHUP -> drawPushupMotion(cx, groundY, progress)
        ExerciseArchetype.PLANK -> drawPlankMotion(cx, groundY, progress)
        ExerciseArchetype.JUMPING_JACKS -> drawJumpingJacksMotion(cx, groundY, progress)
        ExerciseArchetype.MOUNTAIN_CLIMBER -> drawMountainClimberMotion(cx, groundY, progress)
        ExerciseArchetype.LUNGE -> drawLungeMotion(cx, groundY, progress)
        ExerciseArchetype.GLUTE_BRIDGE -> drawGluteBridgeMotion(cx, groundY, progress)
        ExerciseArchetype.BICYCLE_CRUNCH -> drawBicycleCrunchMotion(cx, groundY, progress)
        ExerciseArchetype.HIGH_KNEES -> drawHighKneesMotion(cx, groundY, progress)
        ExerciseArchetype.DEFAULT -> drawSquatMotion(cx, groundY, progress, isFrontAngle)
    }
}

/**
 * Renders Squat motion:
 * Depth drops as progress increases, hip hinges backward, knees flex to 90°.
 */
private fun DrawScope.drawSquatMotion(cx: Float, groundY: Float, progress: Float, isFrontAngle: Boolean) {
    val squatDepth = progress * 48f
    val hipXOffset = if (isFrontAngle) 0f else -progress * 24f
    val torsoLeanX = if (isFrontAngle) 0f else progress * 14f

    val feetY = groundY - 6f
    val headRadius = 14f

    if (isFrontAngle) {
        // Front View Squat
        val stanceWidth = 44f
        val leftFoot = Offset(cx - stanceWidth, feetY)
        val rightFoot = Offset(cx + stanceWidth, feetY)

        val hipY = groundY - 80f + squatDepth
        val pelvis = Offset(cx, hipY)

        val kneeSpread = 38f + progress * 8f
        val leftKnee = Offset(cx - kneeSpread, groundY - 42f + squatDepth * 0.5f)
        val rightKnee = Offset(cx + kneeSpread, groundY - 42f + squatDepth * 0.5f)

        val chest = Offset(cx, hipY - 48f)
        val head = Offset(cx, chest.y - headRadius - 6f)

        // Draw Limbs
        drawBone(pelvis, leftKnee)
        drawBone(leftKnee, leftFoot)
        drawBone(pelvis, rightKnee)
        drawBone(rightKnee, rightFoot)
        drawBone(pelvis, chest, strokeWidth = 8f, color = VoltGreen)
        drawJoint(head, headRadius, isHead = true)

        // Arms extended forward for balance
        drawBone(chest, Offset(cx - 24f, chest.y + 12f))
        drawBone(chest, Offset(cx + 24f, chest.y + 12f))

        // Target muscle glow: Quads
        drawMuscleGlow(leftKnee, 18f, VoltGreen)
        drawMuscleGlow(rightKnee, 18f, VoltGreen)
    } else {
        // Side View Squat
        val foot = Offset(cx, feetY)
        val knee = Offset(cx + 12f + progress * 10f, groundY - 44f + squatDepth * 0.5f)
        val hip = Offset(cx + hipXOffset, groundY - 84f + squatDepth)
        val chest = Offset(cx + hipXOffset + torsoLeanX, hip.y - 48f)
        val head = Offset(chest.x + 4f, chest.y - headRadius - 6f)

        // Arms reaching forward
        val hand = Offset(chest.x + 36f, chest.y + 8f)

        // Draw Skeletal Chain
        drawBone(foot, knee)
        drawBone(knee, hip)
        drawBone(hip, chest, strokeWidth = 8f, color = VoltGreen)
        drawBone(chest, hand)
        drawJoint(head, headRadius, isHead = true)

        // Joints
        drawJoint(foot, 5f)
        drawJoint(knee, 7f, VoltGreen)
        drawJoint(hip, 7f, VoltGreen)

        // Quad & Glute Muscle Activation Glow
        drawMuscleGlow(knee, 16f, VoltGreen)
        drawMuscleGlow(hip, 18f, AmberGlow)
    }
}

/**
 * Renders Push-up motion:
 * Body lowers toward the floor, elbows flex to 90° at bottom.
 */
private fun DrawScope.drawPushupMotion(cx: Float, groundY: Float, progress: Float) {
    val pushDepth = progress * 34f
    val headRadius = 13f

    val feet = Offset(cx - 80f, groundY - 8f)
    val hand = Offset(cx + 42f, groundY - 6f)

    val chestY = groundY - 50f + pushDepth
    val chestX = cx + 32f

    val hipY = groundY - 42f + pushDepth * 0.85f
    val hipX = cx - 18f

    val chest = Offset(chestX, chestY)
    val hip = Offset(hipX, hipY)
    val head = Offset(chestX + 22f, chestY - 10f)

    // Elbow flexion position
    val elbowX = cx + 22f - progress * 12f
    val elbowY = groundY - 28f + pushDepth * 0.6f
    val elbow = Offset(elbowX, elbowY)

    // Bones
    drawBone(feet, hip)
    drawBone(hip, chest, strokeWidth = 8f, color = VoltGreen)
    drawBone(chest, elbow)
    drawBone(elbow, hand)
    drawJoint(head, headRadius, isHead = true)

    // Joints & Muscle Glow (Chest & Triceps)
    drawJoint(feet, 5f)
    drawJoint(elbow, 7f, VoltGreen)
    drawJoint(chest, 8f, VoltGreen)
    drawMuscleGlow(chest, 20f, VoltGreen)
    drawMuscleGlow(elbow, 14f, AmberGlow)
}

/**
 * Renders Forearm Plank:
 * Neutral rigid spine, isometric core wave, glowing midsection.
 */
private fun DrawScope.drawPlankMotion(cx: Float, groundY: Float, progress: Float) {
    val headRadius = 13f
    val feet = Offset(cx - 75f, groundY - 10f)
    val elbow = Offset(cx + 40f, groundY - 8f)
    val forearm = Offset(cx + 65f, groundY - 8f)

    val hip = Offset(cx - 15f, groundY - 42f + (sin(progress * 3.1415f) * 2f))
    val shoulder = Offset(cx + 40f, groundY - 42f)
    val head = Offset(cx + 66f, groundY - 42f)

    // Core wave glow
    drawBone(feet, hip)
    drawBone(hip, shoulder, strokeWidth = 8f, color = VoltGreen)
    drawBone(shoulder, elbow)
    drawBone(elbow, forearm)
    drawJoint(head, headRadius, isHead = true)

    drawJoint(elbow, 6f, VoltGreen)
    drawJoint(shoulder, 6f, VoltGreen)

    // Core lock glow
    drawMuscleGlow(Offset(cx + 10f, groundY - 42f), 22f + progress * 4f, VoltGreen)
}

/**
 * Renders Jumping Jacks:
 * Dynamic overhead arm arcs and jumping feet spread.
 */
private fun DrawScope.drawJumpingJacksMotion(cx: Float, groundY: Float, progress: Float) {
    val headRadius = 14f
    val jumpBounce = (sin(progress * 3.1415f) * 16f)
    val feetSpread = 16f + progress * 46f

    val feetY = groundY - 6f - jumpBounce
    val pelvisY = groundY - 78f - jumpBounce

    val leftFoot = Offset(cx - feetSpread, feetY)
    val rightFoot = Offset(cx + feetSpread, feetY)
    val pelvis = Offset(cx, pelvisY)
    val chest = Offset(cx, pelvisY - 46f)
    val head = Offset(cx, chest.y - headRadius - 6f)

    // Arms arc overhead as progress increases
    val armAngle = 0.4f + progress * 2.3f // radians
    val armLength = 48f
    val leftHand = Offset(cx - cos(armAngle) * armLength, chest.y - sin(armAngle) * armLength)
    val rightHand = Offset(cx + cos(armAngle) * armLength, chest.y - sin(armAngle) * armLength)

    drawBone(pelvis, leftFoot)
    drawBone(pelvis, rightFoot)
    drawBone(pelvis, chest, strokeWidth = 8f, color = VoltGreen)
    drawBone(chest, leftHand)
    drawBone(chest, rightHand)
    drawJoint(head, headRadius, isHead = true)

    drawMuscleGlow(chest, 20f, ElectricCyan)
    drawMuscleGlow(leftFoot, 12f, VoltGreen)
    drawMuscleGlow(rightFoot, 12f, VoltGreen)
}

/**
 * Renders Mountain Climber:
 * Alternating high plank knee drive.
 */
private fun DrawScope.drawMountainClimberMotion(cx: Float, groundY: Float, progress: Float) {
    val headRadius = 13f
    val hand = Offset(cx + 42f, groundY - 8f)
    val shoulder = Offset(cx + 38f, groundY - 48f)
    val hip = Offset(cx - 20f, groundY - 46f)
    val head = Offset(cx + 62f, groundY - 44f)

    // Alternating leg drives
    val backFoot = Offset(cx - 78f, groundY - 8f)
    val drivingKneeX = hip.x + 28f + progress * 14f
    val drivingKneeY = groundY - 32f - progress * 10f
    val drivingKnee = Offset(drivingKneeX, drivingKneeY)
    val drivingFoot = Offset(drivingKneeX - 18f, groundY - 14f)

    drawBone(hip, shoulder, strokeWidth = 8f, color = VoltGreen)
    drawBone(shoulder, hand)
    drawBone(hip, backFoot)
    drawBone(hip, drivingKnee)
    drawBone(drivingKnee, drivingFoot)
    drawJoint(head, headRadius, isHead = true)

    drawJoint(hand, 6f)
    drawJoint(drivingKnee, 7f, VoltGreen)
    drawMuscleGlow(drivingKnee, 16f, VoltGreen)
}

/**
 * Renders Alternating Lunge:
 * Front knee lowers to 90°, back knee hovers over ground.
 */
private fun DrawScope.drawLungeMotion(cx: Float, groundY: Float, progress: Float) {
    val headRadius = 14f
    val lungeDepth = progress * 32f

    val frontFoot = Offset(cx + 36f, groundY - 6f)
    val frontKnee = Offset(cx + 36f, groundY - 40f + lungeDepth * 0.5f)

    val backFoot = Offset(cx - 52f, groundY - 6f)
    val backKnee = Offset(cx - 16f, groundY - 36f + lungeDepth)

    val hip = Offset(cx - 4f, groundY - 76f + lungeDepth)
    val chest = Offset(cx - 2f, hip.y - 48f)
    val head = Offset(cx, chest.y - headRadius - 6f)

    drawBone(hip, frontKnee)
    drawBone(frontKnee, frontFoot)
    drawBone(hip, backKnee)
    drawBone(backKnee, backFoot)
    drawBone(hip, chest, strokeWidth = 8f, color = VoltGreen)
    drawJoint(head, headRadius, isHead = true)

    drawJoint(frontKnee, 7f, VoltGreen)
    drawJoint(backKnee, 7f, AmberGlow)
    drawMuscleGlow(frontKnee, 18f, VoltGreen)
}

/**
 * Renders Glute Bridge:
 * Pelvis rises to bridge extension.
 */
private fun DrawScope.drawGluteBridgeMotion(cx: Float, groundY: Float, progress: Float) {
    val headRadius = 13f
    val shoulders = Offset(cx - 50f, groundY - 14f)
    val head = Offset(cx - 72f, groundY - 14f)
    val feet = Offset(cx + 42f, groundY - 8f)

    val bridgeHeight = progress * 40f
    val hip = Offset(cx - 6f, groundY - 24f - bridgeHeight)
    val knees = Offset(cx + 34f, groundY - 46f)

    drawBone(shoulders, hip, strokeWidth = 8f, color = VoltGreen)
    drawBone(hip, knees)
    drawBone(knees, feet)
    drawJoint(head, headRadius, isHead = true)

    drawJoint(hip, 8f, VoltGreen)
    drawMuscleGlow(hip, 22f, VoltGreen)
}

/**
 * Renders Bicycle Crunch:
 * Core rotation and leg extension.
 */
private fun DrawScope.drawBicycleCrunchMotion(cx: Float, groundY: Float, progress: Float) {
    val headRadius = 13f
    val pelvis = Offset(cx - 10f, groundY - 16f)
    val chest = Offset(cx - 36f, groundY - 32f)
    val head = Offset(cx - 56f, groundY - 36f)

    val leftKnee = Offset(cx + 6f - progress * 16f, groundY - 46f)
    val rightLegExtended = Offset(cx + 65f, groundY - 26f)

    drawBone(pelvis, chest, strokeWidth = 8f, color = VoltGreen)
    drawBone(pelvis, leftKnee)
    drawBone(pelvis, rightLegExtended)
    drawJoint(head, headRadius, isHead = true)

    drawMuscleGlow(pelvis, 20f, VoltGreen)
}

/**
 * Renders High Knees:
 * Rapid alternating knee drives.
 */
private fun DrawScope.drawHighKneesMotion(cx: Float, groundY: Float, progress: Float) {
    val headRadius = 14f
    val pelvis = Offset(cx, groundY - 84f)
    val chest = Offset(cx, pelvis.y - 46f)
    val head = Offset(cx, chest.y - headRadius - 6f)

    val stanceFoot = Offset(cx - 12f, groundY - 6f)
    val drivenKnee = Offset(cx + 18f, groundY - 68f - progress * 16f)
    val drivenFoot = Offset(cx + 14f, drivenKnee.y + 36f)

    drawBone(pelvis, stanceFoot)
    drawBone(pelvis, drivenKnee)
    drawBone(drivenKnee, drivenFoot)
    drawBone(pelvis, chest, strokeWidth = 8f, color = VoltGreen)
    drawJoint(head, headRadius, isHead = true)

    drawMuscleGlow(drivenKnee, 18f, VoltGreen)
}

// ─────────────────────────────────────────────────────────────────────────────
// Skeletal Primitives
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawBone(
    start: Offset,
    end: Offset,
    strokeWidth: Float = 5f,
    color: Color = Color.White
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawJoint(
    center: Offset,
    radius: Float,
    color: Color = Color.White,
    isHead: Boolean = false
) {
    if (isHead) {
        drawCircle(
            color = Color(0xFF1E293B),
            radius = radius,
            center = center
        )
        drawCircle(
            color = VoltGreen,
            radius = radius,
            center = center,
            style = Stroke(width = 2.5f)
        )
    } else {
        drawCircle(
            color = color,
            radius = radius,
            center = center
        )
    }
}

private fun DrawScope.drawMuscleGlow(center: Offset, radius: Float, color: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.55f), color.copy(alpha = 0f)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}
