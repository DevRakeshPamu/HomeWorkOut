package com.fit10.app.feature.player

import androidx.compose.animation.AnimatedContent
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.ui.theme.Fit10Theme
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.fit10.app.core.ui.components.AnimatedExerciseDemo
import com.fit10.app.core.ui.components.ExerciseMotionGuide
import com.fit10.app.core.ui.components.ExerciseVideoPlayer
import com.fit10.app.core.ui.components.ExerciseVideoPlayerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.R
import com.fit10.app.domain.workout.WorkoutSessionState

private val BrandPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
private val BrandBright: Color
    @Composable @ReadOnlyComposable get() = Fit10Theme.extendedColors.brandBright
private val BrandDeep: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
private val BrandContainer: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
private val BorderInactive: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant
private val TextMain: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
private val TextMuted: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val CardSurface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlayerScreen(
    viewModel: WorkoutPlayerViewModel,
    onWorkoutFinished: (sessionId: String, workoutId: String) -> Unit,
    onExit: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showExitDialog by remember { mutableStateOf(false) }
    var videoModalExercise by remember { mutableStateOf<com.fit10.app.domain.model.Exercise?>(null) }

    LaunchedEffect(state) {
        if (state is WorkoutSessionState.Completed) {
            val completed = state as WorkoutSessionState.Completed
            onWorkoutFinished(completed.result.sessionId, completed.workout.id)
        } else if (state is WorkoutSessionState.Abandoned) {
            onExit()
        }
    }

    androidx.activity.compose.BackHandler { showExitDialog = true }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "End Workout Early?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Your completed exercises will be logged, but the session won't count as fully finished.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.abandon()
                    onExit()
                }) {
                    Text(
                        "End Workout",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(
                        "Keep Going 💪",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = BrandPrimary
                    )
                }
            }
        )
    }

    // Modal Video Player Sheet
    videoModalExercise?.let { ex ->
        ExerciseVideoPlayerSheet(
            exercise = ex,
            onDismiss = { videoModalExercise = null }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextMain
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Rounded.VolumeUp, contentDescription = "Audio", tint = TextMain)
                    }
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "Settings", tint = TextMain)
                    }
                    Image(
                        painter = painterResource(id = R.drawable.coach_maya),
                        contentDescription = "Coach",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(2.dp, BrandPrimary, CircleShape)
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val s = state) {
                is WorkoutSessionState.Countdown       -> CountdownView(countdown = s)
                is WorkoutSessionState.ActiveExercise  -> ActiveExercisePlayerView(
                    state = s, isPaused = false,
                    onPause = viewModel::pause, onSkip = viewModel::skip, onPrevious = viewModel::previous,
                    onShowVideo = { ex ->
                        viewModel.pause()
                        videoModalExercise = ex
                    }
                )
                is WorkoutSessionState.RestInterval    -> RestIntervalPlayerView(
                    state = s, onSkipRest = viewModel::skip
                )
                is WorkoutSessionState.Paused          -> {
                    val frozen = s.frozenState
                    when (frozen) {
                        is WorkoutSessionState.ActiveExercise -> ActiveExercisePlayerView(
                            state = frozen, isPaused = true,
                            onPause = viewModel::resume, onSkip = viewModel::skip, onPrevious = viewModel::previous,
                            onShowVideo = { ex ->
                                videoModalExercise = ex
                            }
                        )
                        is WorkoutSessionState.RestInterval   -> RestIntervalPlayerView(
                            state = frozen, onSkipRest = viewModel::skip
                        )
                        else -> Unit
                    }
                }
                else -> Unit
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Active Exercise Player
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ActiveExercisePlayerView(
    state: WorkoutSessionState.ActiveExercise,
    isPaused: Boolean,
    onPause: () -> Unit,
    onSkip: () -> Unit,
    onPrevious: () -> Unit,
    onShowVideo: (com.fit10.app.domain.model.Exercise) -> Unit
) {
    val haptics = rememberFit10Haptics()
    val totalMoves  = state.workout.exercises.size
    val currentMove = state.exerciseIndex + 1
    val exercise    = state.currentExercise.exercise
    val totalSeconds   = state.totalExerciseSeconds.coerceAtLeast(1)
    val progressFraction = (state.secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    // Animated progress bar value
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "ExerciseProgress"
    )

    // Button interactions
    val pauseInteraction = remember { MutableInteractionSource() }
    val isPausePressed by pauseInteraction.collectIsPressedAsState()
    val pauseScale by animateFloatAsState(
        targetValue = if (isPausePressed) 0.94f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "PauseScale"
    )

    // Motion mode state — default to Coach View (real model photo) per user request
    var useKineticMotion by remember { mutableStateOf(false) }

    /**
     * Exhaustive exercise ID → drawable mapping.
     * Every exercise in the catalog resolves to its unique real-model photo.
     * Falls back to coach_maya for any unlisted exercise.
     */
    val coachImageRes = remember(exercise.id) {
        when {
            // Push-up family
            "ex_push_ups" == exercise.id        -> R.drawable.coach_pushup
            "ex_wide_push_ups" == exercise.id   -> R.drawable.coach_pushup
            "ex_diamond_push_ups" == exercise.id -> R.drawable.coach_pushup
            // Squat family
            "ex_squats" == exercise.id          -> R.drawable.coach_squat
            // Lunge family
            "ex_lunges" == exercise.id          -> R.drawable.exercise_lunge
            "ex_reverse_lunges" == exercise.id  -> R.drawable.exercise_lunge
            // Glute bridge family
            "ex_glute_bridges" == exercise.id       -> R.drawable.exercise_glute_bridge
            "ex_glute_bridge_march" == exercise.id  -> R.drawable.exercise_glute_bridge
            // Plank family
            "ex_plank" == exercise.id           -> R.drawable.exercise_plank
            // Shoulder taps
            "ex_shoulder_taps" == exercise.id   -> R.drawable.exercise_shoulder_taps
            // Mountain Climbers
            "ex_mountain_climbers" == exercise.id -> R.drawable.exercise_mountain_climber
            // Bird Dog
            "ex_bird_dog" == exercise.id        -> R.drawable.exercise_bird_dog
            // Bicycle Crunches
            "ex_bicycle_crunches" == exercise.id -> R.drawable.exercise_bicycle_crunch
            // High Knees
            "ex_high_knees" == exercise.id      -> R.drawable.exercise_high_knees
            // Jumping Jacks
            "ex_jumping_jacks" == exercise.id   -> R.drawable.exercise_jumping_jacks
            // Superman Hold
            "ex_superman" == exercise.id        -> R.drawable.exercise_superman
            // Wall Sit — uses squat image (closest posture match)
            "ex_wall_sit" == exercise.id        -> R.drawable.coach_squat
            // Prone YTW — uses superman image (both are prone posterior chain)
            "ex_prone_ytw" == exercise.id       -> R.drawable.exercise_superman
            // Arm Circles / Warmup / Mobility / Cooldown
            else -> R.drawable.coach_maya
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Exercise tag + segmented progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(BrandContainer)
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "EXERCISE $currentMove OF $totalMoves",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandDeep
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Segmented progress bar with animated glow
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..totalMoves) {
                val isActive   = i == currentMove
                val isComplete = i < currentMove
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            when {
                                isActive   -> Brush.horizontalGradient(listOf(BrandPrimary, BrandBright))
                                isComplete -> Brush.horizontalGradient(listOf(BrandPrimary, BrandPrimary))
                                else       -> Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.colorScheme.outlineVariant))
                            }
                        )
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Demonstration Mode Toggle & Video Sheet Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (!useKineticMotion) BrandPrimary else Color.Transparent)
                        .clickable { haptics.perform(HapticKind.Tick); useKineticMotion = false }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🎬 Video",
                        color = if (!useKineticMotion) MaterialTheme.colorScheme.onPrimary else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (useKineticMotion) BrandPrimary else Color.Transparent)
                        .clickable { haptics.perform(HapticKind.Tick); useKineticMotion = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⚡ Skeleton",
                        color = if (useKineticMotion) MaterialTheme.colorScheme.onPrimary else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick "Watch Video Tutorial" pill (pauses workout)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(BrandContainer)
                    .border(1.dp, BrandPrimary.copy(alpha = 0.4f), RoundedCornerShape(50))
                    .clickable {
                        onShowVideo(exercise)
                    }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Videocam,
                        contentDescription = "Video Guide",
                        tint = BrandDeep,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Video Masterclass",
                        color = BrandDeep,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Active Demonstration: Video Demo or Kinetic Skeleton Guide
        if (useKineticMotion) {
            ExerciseMotionGuide(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                showControls = true,
                onOpenVideoModal = {
                    onShowVideo(exercise)
                }
            )
        } else {
            ExerciseVideoPlayer(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                coachCue = exercise.tips.firstOrNull() ?: "Keep core braced & joints aligned with steady breathing.",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(235.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Exercise name row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = exercise.focusArea.name.replace("_", " ").uppercase(),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = exercise.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BrandContainer
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "${state.currentExercise.reps ?: 14}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = BrandDeep)
                    Text(text = "REPS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandDeep, letterSpacing = 0.8.sp)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Timer card with large countdown
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardSurface,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "REMAINING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(4.dp))

                // Large animated timer with cross-fade on digit change
                val seconds = state.secondsRemaining
                val formattedTime = String.format("%02d:%02d", seconds / 60, seconds % 60)
                AnimatedContent(
                    targetState = formattedTime,
                    transitionSpec = {
                        fadeIn(tween(200)) + scaleIn(tween(200), initialScale = 0.9f) togetherWith
                                fadeOut(tween(150))
                    },
                    label = "TimerCrossfade"
                ) { time ->
                    Text(
                        text = time,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMain,
                        letterSpacing = (-2).sp
                    )
                }

                Spacer(Modifier.height(2.dp))
                Text(text = "Pace: 3s Down • 1s Up", fontSize = 12.sp, color = BrandPrimary, fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.height(14.dp))

                // Progress bar
                val glowColor = MaterialTheme.colorScheme.primary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(BrandPrimary, BrandBright)
                                )
                            )
                            .drawBehind {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(glowColor.copy(alpha = 0f), glowColor.copy(alpha = 0.27f))
                                    ),
                                    topLeft = androidx.compose.ui.geometry.Offset(0f, -4f),
                                    size = this.size.copy(height = this.size.height + 8f)
                                )
                            }
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Up Next
                val nextMove = state.workout.exercises.getOrNull(state.exerciseIndex + 1)?.exercise?.name ?: "Cooldown ✨"
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, BorderInactive),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Up Next: $nextMove", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextMain)
                        Icon(imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Controls row — Prev | PAUSE | Skip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prev
            ControlButton(
                onClick = { haptics.perform(HapticKind.Tick); onPrevious() },
                modifier = Modifier.weight(0.9f)
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.SkipPrevious, contentDescription = "Previous", tint = TextMain, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Prev", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMain)
                }
            }

            Spacer(Modifier.width(10.dp))

            // Pause / Resume — gradient with press scale
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .scale(pauseScale)
                    .clip(RoundedCornerShape(50))
                    .background(Brush.horizontalGradient(listOf(Fit10Theme.extendedColors.brandStrong, BrandPrimary)))
                    .clickable(interactionSource = pauseInteraction, indication = null, onClick = { haptics.perform(HapticKind.Soft); onPause() }),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isPaused) "RESUME" else "PAUSE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            // Skip
            ControlButton(
                onClick = { haptics.perform(HapticKind.Tick); onSkip() },
                modifier = Modifier.weight(0.9f)
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Skip", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMain)
                    Spacer(Modifier.width(4.dp))
                    Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = "Skip", tint = TextMain, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stats footer
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CardSurface,
            border = BorderStroke(1.dp, BorderInactive)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatPill("Form Score", "96%", MaterialTheme.colorScheme.primary)
                Box(Modifier.size(4.dp).clip(CircleShape).background(BorderInactive))
                StatPill("Elapsed", "04:12", Fit10Theme.extendedColors.timer)
                Box(Modifier.size(4.dp).clip(CircleShape).background(BorderInactive))
                StatPill("Calories", "~42 kcal", Fit10Theme.extendedColors.streak)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Rest & Recover View
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RestIntervalPlayerView(
    state: WorkoutSessionState.RestInterval,
    onSkipRest: () -> Unit
) {
    val haptics = rememberFit10Haptics()
    var extraSeconds by remember { mutableIntStateOf(0) }
    val displaySeconds = state.secondsRemaining + extraSeconds

    val progressFraction = (displaySeconds.toFloat() / 20f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "RestProgress"
    )

    // Infinite pulse on the circle
    val infiniteTransition = rememberInfiniteTransition(label = "RestPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "CirclePulse"
    )
    val heartPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "HeartPulse"
    )

    val skipInteraction = remember { MutableInteractionSource() }
    val isSkipPressed by skipInteraction.collectIsPressedAsState()
    val skipScale by animateFloatAsState(
        targetValue = if (isSkipPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "SkipScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(10.dp))

        // PULSE RESET tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(BrandContainer)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(BrandPrimary))
                Spacer(Modifier.width(8.dp))
                Text(text = "PULSE RESET", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDeep, letterSpacing = 0.8.sp)
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(text = "Rest & Recover", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextMain)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Superb form! Catch your breath, drop your shoulders, and hydrate.",
            fontSize = 13.5.sp, color = TextMuted, textAlign = TextAlign.Center, lineHeight = 19.sp
        )

        Spacer(Modifier.height(24.dp))

        // Pulsing circular countdown with gradient stroke
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(pulseScale),
            contentAlignment = Alignment.Center
        ) {
            // Gradient ring — drawn via Canvas
            val trackColor = MaterialTheme.colorScheme.outlineVariant
            val ringColors = listOf(Fit10Theme.extendedColors.brandStrong, BrandBright, Fit10Theme.extendedColors.brandStrong)
            androidx.compose.foundation.Canvas(modifier = Modifier.size(200.dp)) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2

                // Track ring
                drawCircle(
                    color = trackColor,
                    radius = radius,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )
                // Progress arc (gradient via rotating sweep)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = ringColors
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val formattedTime = String.format("%02d:%02d", displaySeconds / 60, displaySeconds % 60)
                Text(
                    text = formattedTime,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMain,
                    letterSpacing = (-1).sp
                )
                Text(text = "SECONDS LEFT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp).scale(heartPulse)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = "118 bpm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Up Next card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = CardSurface,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.coach_pushup),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(68.dp).clip(RoundedCornerShape(14.dp))
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "UP NEXT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary, letterSpacing = 0.8.sp)
                    Text(text = state.nextExercise.exercise.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextMain)
                    Text(text = "${state.nextExercise.durationSeconds}s • Upper Body & Chest", fontSize = 12.sp, color = TextMuted)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PillBadge("Low Impact")
                        PillBadge("Posture Focus")
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Hydration tip
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BrandContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.WaterDrop, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text(text = "Take 3 deep belly breaths and a sip of water before your next push.", fontSize = 12.sp, color = BrandDeep, lineHeight = 17.sp)
            }
        }

        Spacer(Modifier.height(20.dp))

        // +10s & Skip Rest buttons
        Surface(
            onClick = { haptics.perform(HapticKind.Tick); extraSeconds += 10 },
            shape = RoundedCornerShape(50),
            color = CardSurface,
            border = BorderStroke(1.5.dp, BorderInactive),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "+ 10 sec recovery", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .scale(skipScale)
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(Fit10Theme.extendedColors.brandStrong, BrandPrimary)))
                .clickable(interactionSource = skipInteraction, indication = null, onClick = { haptics.perform(HapticKind.Confirm); onSkipRest() }),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.FastForward, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = "Skip Rest & Begin", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Countdown View
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CountdownView(countdown: WorkoutSessionState.Countdown) {
    val infiniteTransition = rememberInfiniteTransition(label = "CountdownPulse")
    val outerRingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.15f,
        animationSpec = infiniteRepeatable(
            animation   = tween(700, easing = FastOutSlowInEasing),
            repeatMode  = RepeatMode.Reverse
        ),
        label = "OuterRing"
    )
    val innerRingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.08f,
        animationSpec = infiniteRepeatable(
            animation   = tween(700, easing = FastOutSlowInEasing, delayMillis = 150),
            repeatMode  = RepeatMode.Reverse
        ),
        label = "InnerRing"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(BrandContainer)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = "⚡ GET READY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandDeep, letterSpacing = 1.5.sp)
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Pulsing concentric rings
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            // Outer pulse ring
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(outerRingScale)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = 0.08f))
            )
            // Middle ring
            Box(
                modifier = Modifier
                    .size(165.dp)
                    .scale(innerRingScale)
                    .clip(CircleShape)
                    .background(BrandPrimary.copy(alpha = 0.14f))
            )
            // Inner circle with countdown
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Fit10Theme.extendedColors.brandStrong, BrandPrimary))
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = countdown.secondsRemaining,
                    transitionSpec = {
                        (scaleIn(tween(200)) + fadeIn(tween(200))) togetherWith
                                (scaleOut(tween(150)) + fadeOut(tween(150)))
                    },
                    label = "CountdownDigit"
                ) { count ->
                    Text(
                        text = "$count",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = countdown.workout.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        val firstMove = countdown.workout.exercises.firstOrNull()?.exercise?.name ?: "Warmup"
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, BorderInactive)
        ) {
            Text(
                text = "First move: $firstMove",
                fontSize = 13.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ControlButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "ControlBtnScale"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = CardSurface,
        border = BorderStroke(1.5.dp, BorderInactive),
        modifier = modifier.height(52.dp).scale(scale)
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
private fun PillBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(text = text, fontSize = 10.5.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 9.5.sp, color = TextMuted, fontWeight = FontWeight.Medium)
    }
}
