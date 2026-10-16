package com.fit10.app.core.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Describes the multi-frame motion cycle for a given exercise with real model photos.
 */
data class ExerciseFrameSet(
    @param:DrawableRes val startRes: Int,
    @param:DrawableRes val peakRes: Int,
    val isIsometric: Boolean = false,
    val holdMs: Long = 1300L,
    val phaseLabelStart: String = "START POSITION",
    val phaseLabelPeak: String = "PEAK CONTRACTION",
    val tempoCue: String = "2s Tempo • Strict Form"
)

// ─────────────────────────────────────────────────────────────────────────────
// Frame registry — maps every exercise ID to real-model start & peak frames
// ─────────────────────────────────────────────────────────────────────────────

fun exerciseFrameSet(exerciseId: String): ExerciseFrameSet = when (exerciseId) {

    "ex_squats" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_squats_start,
        peakRes  = R.drawable.coach_squat,
        holdMs   = 1400L,
        phaseLabelStart = "STAND TALL — CHEST UP",
        phaseLabelPeak  = "DEEP SQUAT — THIGHS PARALLEL",
        tempoCue = "3s Lower • 1s Drive"
    )

    "ex_wall_sit" -> ExerciseFrameSet(
        startRes = R.drawable.coach_squat,
        peakRes  = R.drawable.coach_squat,
        isIsometric = true,
        phaseLabelPeak = "WALL SIT — 90° KNEE BEND",
        tempoCue = "Continuous Tension Hold"
    )

    "ex_push_ups",
    "ex_wide_push_ups",
    "ex_diamond_push_ups" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_push_ups_start,
        peakRes  = R.drawable.coach_pushup,
        holdMs   = 1300L,
        phaseLabelStart = "HIGH PLANK — ARMS LOCKED",
        phaseLabelPeak  = "CHEST TO FLOOR — ELBOWS 45°",
        tempoCue = "2s Down • 1s Push"
    )

    "ex_lunges",
    "ex_reverse_lunges" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_lunges_start,
        peakRes  = R.drawable.exercise_lunge,
        holdMs   = 1300L,
        phaseLabelStart = "UPRIGHT STANCE — BRACED",
        phaseLabelPeak  = "FORWARD STEP — BOTH KNEES 90°",
        tempoCue = "Step & Lower • Drive Back"
    )

    "ex_mountain_climbers" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_mountain_climber,
        peakRes  = R.drawable.exercise_mountain_climber_alt,
        holdMs   = 550L,
        phaseLabelStart = "RIGHT KNEE DRIVE",
        phaseLabelPeak  = "LEFT KNEE DRIVE",
        tempoCue = "Dynamic Athletic Cadence"
    )

    "ex_high_knees" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_high_knees,
        peakRes  = R.drawable.exercise_high_knees_alt,
        holdMs   = 500L,
        phaseLabelStart = "RIGHT KNEE TO CHEST",
        phaseLabelPeak  = "LEFT KNEE TO CHEST",
        tempoCue = "Quick Cadence • Spring Off Toes"
    )

    "ex_shoulder_taps" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_shoulder_taps,
        peakRes  = R.drawable.exercise_shoulder_taps_alt,
        holdMs   = 900L,
        phaseLabelStart = "LEFT SHOULDER TAP",
        phaseLabelPeak  = "RIGHT SHOULDER TAP",
        tempoCue = "Keep Hips Square • Anti-Rotation"
    )

    "ex_bird_dog" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_bird_dog,
        peakRes  = R.drawable.exercise_bird_dog_alt,
        holdMs   = 1400L,
        phaseLabelStart = "RIGHT ARM & LEFT LEG EXTENSION",
        phaseLabelPeak  = "LEFT ARM & RIGHT LEG EXTENSION",
        tempoCue = "Hold 2s • Neutral Spine"
    )

    "ex_bicycle_crunches" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_bicycle_crunch,
        peakRes  = R.drawable.exercise_bicycle_crunch_alt,
        holdMs   = 850L,
        phaseLabelStart = "RIGHT ELBOW TO LEFT KNEE",
        phaseLabelPeak  = "LEFT ELBOW TO RIGHT KNEE",
        tempoCue = "Controlled Rotation • Squeeze Obliques"
    )

    "ex_jumping_jacks" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_jumping_jacks_start,
        peakRes  = R.drawable.exercise_jumping_jacks,
        holdMs   = 700L,
        phaseLabelStart = "FEET TOGETHER — ARMS DOWN",
        phaseLabelPeak  = "JUMP WIDE — ARMS OVERHEAD",
        tempoCue = "Spring Ryhthm • Land Softly"
    )

    "ex_glute_bridges",
    "ex_glute_bridge_march" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_glute_bridge,
        peakRes  = R.drawable.exercise_glute_bridge,
        isIsometric = false,
        holdMs   = 1200L,
        phaseLabelStart = "HIPS ON MAT — CORE ENGAGED",
        phaseLabelPeak  = "DRIVE HIPS UP — GLUTE SQUEEZE",
        tempoCue = "Drive Through Heels • Pause at Top"
    )

    "ex_plank" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_plank,
        peakRes  = R.drawable.exercise_plank,
        isIsometric = true,
        phaseLabelPeak = "ISOMETRIC PLANK — CORE LOCKED",
        tempoCue = "Steady Nasal Breathing • Max Brace"
    )

    "ex_superman" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_superman,
        peakRes  = R.drawable.exercise_superman,
        isIsometric = true,
        phaseLabelPeak = "SUPERMAN HOLD — ARCH & SQUEEZE",
        tempoCue = "Lift Chest & Thighs • Engage Posterior"
    )

    "ex_prone_ytw" -> ExerciseFrameSet(
        startRes = R.drawable.exercise_superman,
        peakRes  = R.drawable.exercise_superman,
        isIsometric = true,
        phaseLabelPeak = "PRONE HOLD — SCAPULAR SQUEEZE",
        tempoCue = "Retract Shoulder Blades • Hold Position"
    )

    else -> ExerciseFrameSet(
        startRes = R.drawable.coach_maya,
        peakRes  = R.drawable.coach_maya,
        isIsometric = true,
        phaseLabelPeak = "ACTIVE FORM DEMO"
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Colors
// ─────────────────────────────────────────────────────────────────────────────

private val VoltGreen    = Color(0xFF00E676)
private val NeonEmerald  = Color(0xFF10B981)
private val ElectricCyan = Color(0xFF06B6D4)
private val AmberHold    = Color(0xFFF59E0B)

/**
 * Animated real-model exercise demonstration.
 *
 * Provides users with an authentic, living visual model for every exercise:
 *  • Real human athlete photos in precise anatomical start and peak poses
 *  • Smooth crossfade animation simulating the actual repetition stroke
 *  • Speed controls (1x Normal / 0.5x Slow-Motion for studying form mechanics)
 *  • Play/Pause freeze frame for inspecting joint posture
 *  • Live phase labels ("DESCENT / BOTTOM INFLECTION", "DRIVE")
 *  • Progress bar tracking the repetition tempo cycle
 */
@Composable
fun AnimatedExerciseDemo(
    exerciseId: String,
    exerciseName: String,
    primaryMuscles: List<String>,
    coachCue: String,
    modifier: Modifier = Modifier
) {
    val frameSet = remember(exerciseId) { exerciseFrameSet(exerciseId) }

    // User controls
    var isPlaying by remember { mutableStateOf(true) }
    var isSlowMotion by remember { mutableStateOf(false) }

    // Active frame index (0 = start/eccentric, 1 = peak/concentric)
    var activeFrame by remember(exerciseId) { mutableIntStateOf(0) }

    // Repetition hold duration adjusted for slow-mo
    val actualHoldMs = if (isSlowMotion) (frameSet.holdMs * 1.8f).toLong() else frameSet.holdMs

    // Automatic frame cycling loop
    LaunchedEffect(exerciseId, isPlaying, actualHoldMs, frameSet.isIsometric) {
        if (!frameSet.isIsometric && isPlaying) {
            while (true) {
                delay(actualHoldMs)
                activeFrame = 1 - activeFrame
            }
        }
    }

    // ── Smooth crossfade alpha between start and peak positions ──────────────
    val crossfadeAlpha by animateFloatAsState(
        targetValue = if (activeFrame == 1) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (isSlowMotion) 550 else 320,
            easing = FastOutSlowInEasing
        ),
        label = "PoseCrossfadeAlpha"
    )

    // ── Ken Burns breathing zoom for isometric holds ─────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "IsoBreathing")
    val isoScale by infiniteTransition.animateFloat(
        initialValue = 1.00f,
        targetValue  = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IsoScale"
    )

    // Subtle elevation pulse for glute bridge
    val bridgeElevation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = if (exerciseId.contains("bridge")) -8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BridgeElevation"
    )

    // Dynamic phase indicator
    val phaseLabel = if (activeFrame == 0) frameSet.phaseLabelStart else frameSet.phaseLabelPeak
    val phaseColor = if (frameSet.isIsometric) AmberHold else if (activeFrame == 0) ElectricCyan else VoltGreen

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(235.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF0F172A))
    ) {
        // ── 1. Start / Base Frame ────────────────────────────────────────────
        val baseScale = if (frameSet.isIsometric) isoScale else 1.02f
        Image(
            painter = painterResource(frameSet.startRes),
            contentDescription = "$exerciseName start posture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = baseScale
                    scaleY = baseScale
                    translationY = if (exerciseId.contains("bridge") && activeFrame == 0) 6f else 0f
                }
        )

        // ── 2. Peak / Inflection Frame (Crossfaded over start frame) ─────────
        if (!frameSet.isIsometric) {
            Image(
                painter = painterResource(frameSet.peakRes),
                contentDescription = "$exerciseName peak posture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = baseScale
                        scaleY = baseScale
                        alpha = crossfadeAlpha
                        translationY = bridgeElevation
                    }
            )
        }

        // ── 3. High-Contrast Gradient Scrim ─────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC090D16),
                            Color(0x22000000),
                            Color(0x22000000),
                            Color(0xEE090D16)
                        )
                    )
                )
        )

        // ── 4. Top Control Bar: Phase Badge + Speed & Play/Pause Controls ────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Phase Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(phaseColor.copy(alpha = 0.22f))
                    .border(1.dp, phaseColor.copy(alpha = 0.70f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(phaseColor)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = phaseLabel,
                        color = phaseColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }
            }

            // Interactive Controls: Slow-Mo toggle & Pause/Play
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 0.5x Slow-Motion button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSlowMotion) VoltGreen else Color(0x990F172A))
                        .clickable { isSlowMotion = !isSlowMotion }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.SlowMotionVideo,
                            contentDescription = "Slow Motion",
                            tint = if (isSlowMotion) Color.Black else Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isSlowMotion) "0.5x Form" else "1x Speed",
                            color = if (isSlowMotion) Color.Black else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Play / Pause freeze button
                if (!frameSet.isIsometric) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0x990F172A))
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // ── 5. Center Frame Indicator Dots (Dynamic exercises) ───────────────
        if (!frameSet.isIsometric) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 54.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (activeFrame == 0) 18.dp else 6.dp, 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (activeFrame == 0) ElectricCyan else Color.White.copy(alpha = 0.35f))
                )
                Box(
                    modifier = Modifier
                        .size(if (activeFrame == 1) 18.dp else 6.dp, 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (activeFrame == 1) VoltGreen else Color.White.copy(alpha = 0.35f))
                )
            }
        } else {
            // Isometric Hold Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 54.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AmberHold.copy(alpha = 0.3f))
                    .border(1.dp, AmberHold.copy(alpha = 0.8f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "ISOMETRIC TENSION HOLD",
                    color = AmberHold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }
        }

        // ── 6. Bottom Coach Cue & Tempo Banner ───────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xF20B132B)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = NeonEmerald,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = coachCue,
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Tempo Cue Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x3310B981))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = frameSet.tempoCue,
                        color = VoltGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
