package com.fit10.app.feature.player

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.ui.components.ExerciseVideo
import com.fit10.app.core.ui.components.IconTile
import com.fit10.app.core.ui.components.exerciseVideoRes
import com.fit10.app.domain.model.WorkoutExercise
import com.fit10.app.domain.workout.WorkoutSessionState

@Composable
fun WorkoutPlayerScreen(
    viewModel: WorkoutPlayerViewModel,
    onWorkoutFinished: (sessionId: String, workoutId: String) -> Unit,
    onExit: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptics = rememberFit10Haptics()
    var showExit by remember { mutableStateOf(false) }
    var showHurt by remember { mutableStateOf(false) }

    val saved by viewModel.savedResult.collectAsStateWithLifecycle()
    LaunchedEffect(saved) {
        saved?.let { onWorkoutFinished(it.sessionId, it.workoutId) }
    }
    LaunchedEffect(state) {
        if (state is WorkoutSessionState.Abandoned) onExit()
    }

    val paused = state is WorkoutSessionState.Paused
    val shown = (state as? WorkoutSessionState.Paused)?.frozenState ?: state

    fun askExit() {
        if (!paused) viewModel.pause()
        showExit = true
    }
    BackHandler { askExit() }

    if (showExit) {
        AlertDialog(
            onDismissRequest = { showExit = false },
            title = { Text("End this workout?") },
            text = { Text("You can start again anytime. Every minute you did still counts for your body.") },
            confirmButton = { TextButton(onClick = { haptics.perform(HapticKind.Tick); showExit = false; viewModel.abandon() }) { Text("End workout") } },
            dismissButton = { TextButton(onClick = { showExit = false; viewModel.resume() }) { Text("Keep going") } }
        )
    }
    if (showHurt) {
        AlertDialog(
            onDismissRequest = { showHurt = false },
            title = { Text("Let's stop here") },
            text = {
                Text(
                    "Pain, pressure in your pelvis, leaking or bleeding are signs to rest. If it doesn't settle, please talk to your doctor or a women's health physiotherapist."
                )
            },
            confirmButton = { TextButton(onClick = { showHurt = false; viewModel.abandon() }) { Text("End workout") } },
            dismissButton = { TextButton(onClick = { showHurt = false; viewModel.resume() }) { Text("I feel okay now") } }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopBar(shown, onClose = { haptics.perform(HapticKind.Tick); askExit() })

        AnimatedContent(
            targetState = shown,
            contentKey = { it::class to ((it as? WorkoutSessionState.ActiveExercise)?.exerciseIndex ?: (it as? WorkoutSessionState.RestInterval)?.exerciseIndex) },
            transitionSpec = { (fadeIn(tween(350)) + scaleIn(tween(350), initialScale = 0.97f)) togetherWith fadeOut(tween(200)) },
            modifier = Modifier.weight(1f),
            label = "phase"
        ) { s ->
            when (s) {
                is WorkoutSessionState.Countdown -> CountdownView(s)
                is WorkoutSessionState.ActiveExercise -> ExerciseView(s)
                is WorkoutSessionState.RestInterval -> RestView(s)
                else -> Box(Modifier.fillMaxSize())
            }
        }

        AnimatedVisibility(
            visible = paused,
            enter = slideInVertically(spring(dampingRatio = 0.8f)) { it } + fadeIn(),
            exit = slideOutVertically(tween(200)) { it } + fadeOut()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Text("Paused", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("Baby needs you? Take your time, we'll wait.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }

        if (shown !is WorkoutSessionState.Countdown) {
            Controls(
                paused = paused,
                isRest = shown is WorkoutSessionState.RestInterval,
                onPrevious = { haptics.perform(HapticKind.Tick); viewModel.previous() },
                onTogglePause = { haptics.perform(HapticKind.Soft); if (paused) viewModel.resume() else viewModel.pause() },
                onNext = { haptics.perform(HapticKind.Tick); viewModel.skip() }
            )
            Text(
                "Something hurts?",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { haptics.perform(HapticKind.Reject); if (!paused) viewModel.pause(); showHurt = true }
                    .padding(vertical = 14.dp)
            )
        }
    }
}

@Composable
private fun TopBar(state: WorkoutSessionState, onClose: () -> Unit) {
    val total = when (state) {
        is WorkoutSessionState.ActiveExercise -> state.workout.exercises.size
        is WorkoutSessionState.RestInterval -> state.workout.exercises.size
        is WorkoutSessionState.Countdown -> state.workout.exercises.size
        else -> 0
    }
    val index = when (state) {
        is WorkoutSessionState.ActiveExercise -> state.exerciseIndex
        is WorkoutSessionState.RestInterval -> state.exerciseIndex + 1
        else -> -1
    }
    Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, contentDescription = "End workout") }
        Spacer(Modifier.weight(1f))
        if (index >= 0 && total > 0) {
            Text("${(index + 1).coerceAtMost(total)} of $total", fontSize = 15.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.width(48.dp))
    }
    if (total > 0) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(total) { i ->
                val done = i < index || (state is WorkoutSessionState.RestInterval && i <= state.exerciseIndex)
                val current = i == index && state is WorkoutSessionState.ActiveExercise
                val fill by animateFloatAsState(if (done) 1f else if (current) 0.5f else 0f, tween(400), label = "seg")
                Box(
                    Modifier.weight(1f).height(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Box(Modifier.fillMaxWidth(fill).height(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                }
            }
        }
    }
}

@Composable
private fun CountdownView(s: WorkoutSessionState.Countdown) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Get ready", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))
        AnimatedContent(
            targetState = s.secondsRemaining,
            transitionSpec = { (scaleIn(spring(dampingRatio = 0.5f), initialScale = 0.6f) + fadeIn()) togetherWith (scaleOut(targetScale = 1.3f) + fadeOut()) },
            label = "count"
        ) { n ->
            Text("$n", fontSize = 120.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))
        s.workout.exercises.firstOrNull()?.let {
            Text("First up: ${it.exercise.name}", fontSize = 17.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseView(s: WorkoutSessionState.ActiveExercise) {
    val ex = s.currentExercise
    val next = s.workout.exercises.getOrNull(s.exerciseIndex + 1)
    var showHow by remember { mutableStateOf(false) }
    val pulse = rememberInfiniteTransition(label = "pulse")
    val glow by pulse.animateFloat(0.92f, 1.06f, infiniteRepeatable(tween(4000), RepeatMode.Reverse), label = "glow")
    val glowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)

    val video = remember(ex.exercise.id) { exerciseVideoRes(ex.exercise.id) }
    val subtitle = ex.reps?.let { "$it reps · at your pace" } ?: (cueFor(ex) ?: "Breathe slowly")

    if (video != null) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            ExerciseVideo(
                video,
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth().weight(1f, fill = false).aspectRatio(1f).clip(RoundedCornerShape(28.dp))
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(ex.exercise.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(subtitle, fontSize = 15.sp, lineHeight = 20.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.width(16.dp))
                TimerRing(s.secondsRemaining, s.totalExerciseSeconds, 96.dp)
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (ex.exercise.instructions.isNotEmpty()) {
                    TextButton(onClick = { showHow = true }) { Text("How to do it", fontSize = 15.sp) }
                }
                Spacer(Modifier.weight(1f))
                next?.let {
                    Text("Next · ${it.exercise.name}", fontSize = 14.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 12.dp))
                }
            }
        }
    } else Column(Modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        Text(ex.exercise.name, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, fontSize = 16.sp, lineHeight = 22.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(0.6f))
        Box(contentAlignment = Alignment.Center) {
            Box(Modifier.size(260.dp).scale(glow).clip(CircleShape).background(glowColor))
            TimerRing(s.secondsRemaining, s.totalExerciseSeconds, 220.dp)
        }
        Spacer(Modifier.weight(0.6f))
        if (ex.exercise.instructions.isNotEmpty()) {
            TextButton(onClick = { showHow = true }) { Text("How to do it", fontSize = 15.sp) }
        }
        next?.let {
            Text("Up next · ${it.exercise.name}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.weight(1f))
    }

    if (showHow) {
        ModalBottomSheet(onDismissRequest = { showHow = false }) {
            Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
                Text(ex.exercise.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                ex.exercise.instructions.forEachIndexed { i, step ->
                    Row(Modifier.padding(vertical = 6.dp)) {
                        Box(Modifier.size(26.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                            Text("${i + 1}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(step, fontSize = 15.sp, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun RestView(s: WorkoutSessionState.RestInterval) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Rest", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text("Breathe out slowly. Shake it out.", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        TimerRing(s.secondsRemaining, s.totalRestSeconds, 200.dp)
        Spacer(Modifier.height(32.dp))
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(Icons.Rounded.Spa, 48.dp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Up next", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(s.nextExercise.exercise.name, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun TimerRing(secondsRemaining: Int, total: Int, size: Dp) {
    val target = if (total > 0) secondsRemaining / total.toFloat() else 0f
    val progress by animateFloatAsState(target, tween(1000, easing = LinearEasing), label = "ring")
    val track = MaterialTheme.colorScheme.outlineVariant
    val color = MaterialTheme.colorScheme.primary
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
            drawArc(track, -90f, 360f, false, style = stroke)
            drawArc(color, -90f, 360f * progress, false, style = stroke)
        }
        Text(
            "%d:%02d".format(secondsRemaining / 60, secondsRemaining % 60),
            fontSize = (size.value / 4.2f).sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun Controls(paused: Boolean, isRest: Boolean, onPrevious: () -> Unit, onTogglePause: () -> Unit, onNext: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundButton(Icons.Rounded.SkipPrevious, "Previous", 56.dp, primary = false, onClick = onPrevious)
        RoundButton(if (paused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause, if (paused) "Resume" else "Pause", 80.dp, primary = true, onClick = onTogglePause)
        RoundButton(Icons.Rounded.SkipNext, if (isRest) "Skip rest" else "Next", 56.dp, primary = false, onClick = onNext)
    }
}

@Composable
private fun RoundButton(icon: ImageVector, label: String, size: Dp, primary: Boolean, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f, spring(dampingRatio = 0.5f), label = "btn")
    Box(
        Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(if (primary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon, contentDescription = label,
            tint = if (primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(size * 0.45f)
        )
    }
}

private fun cueFor(ex: WorkoutExercise): String? =
    ex.exercise.tips.firstOrNull() ?: ex.exercise.instructions.firstOrNull()
