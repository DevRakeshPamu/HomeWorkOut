package com.fit10.app.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.ui.components.UserAvatar
import com.fit10.app.domain.model.StartingPoint
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartWorkout: (workoutId: String) -> Unit,
    onStartBreathing: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = rememberFit10Haptics()
    var appeared by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val name = state.userProfile?.name?.trim().orEmpty()
    val startingPoint = state.postpartum?.startingPoint ?: StartingPoint.PHASE_ONE
    val breathingFirst = startingPoint != StartingPoint.PHASE_ONE
    val daysTarget = state.postpartum?.daysPerWeek ?: 5

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(greeting, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    name.ifBlank { "Welcome" }, fontSize = 30.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            UserAvatar(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable { haptics.perform(HapticKind.Tick); onOpenProfile() }
            )
        }

        Spacer(Modifier.height(24.dp))
        Staggered(appeared, 0) {
            CheckInCard(state.sleepToday) { haptics.perform(HapticKind.Tick); viewModel.checkIn(it) }
        }

        Spacer(Modifier.height(16.dp))
        Staggered(appeared, 1) {
            TodayCard(
                eyebrow = when (startingPoint) {
                    StartingPoint.PHASE_ONE -> "Phase 1 · Reconnect"
                    else -> "Gentle recovery"
                },
                title = if (breathingFirst) "Core connection breath" else state.recommendedWorkout.title,
                meta = if (breathingFirst) "2 min · Breathing and pelvic floor" else "${state.recommendedWorkout.estimatedDurationMinutes} min · ${state.recommendedWorkout.exercises.size} moves",
                reason = when {
                    startingPoint == StartingPoint.SEE_DOCTOR -> "Check with your doctor before starting workouts."
                    breathingFirst -> "Until you're cleared, we keep it to breathing and posture."
                    else -> state.sleepToday?.message ?: "Picked to rest yesterday's muscles."
                },
                onStart = {
                    haptics.perform(HapticKind.Confirm)
                    if (breathingFirst) onStartBreathing() else onStartWorkout(state.recommendedWorkout.id)
                }
            )
        }

        if (!breathingFirst) {
            Spacer(Modifier.height(12.dp))
            Staggered(appeared, 2) {
                QuickAction(Icons.Rounded.SelfImprovement, "2-minute breathing", "For tired days or a quick reset") {
                    haptics.perform(HapticKind.Tick); onStartBreathing()
                }
            }
        }

        Spacer(Modifier.height(28.dp))
        Staggered(appeared, 3) {
            ProgressSection(
                sessionsThisWeek = state.sessionsThisWeek,
                weeklyTarget = daysTarget,
                streakDays = state.history.currentStreakDays,
                totalSessions = state.history.totalWorkoutsCompleted
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Staggered(visible: Boolean, index: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(450, delayMillis = 70 * index)) +
            slideInVertically(tween(450, delayMillis = 70 * index)) { it / 6 }
    ) { content() }
}

@Composable
private fun CheckInCard(selected: SleepQuality?, onSelect: (SleepQuality) -> Unit) {
    Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text("How did you sleep?", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SleepQuality.entries.forEach { quality ->
                    val isSelected = quality == selected
                    val bg by animateColorAsState(
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, label = "chip"
                    )
                    val fg by animateColorAsState(
                        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, label = "chipText"
                    )
                    val scale by animateFloatAsState(if (isSelected) 1.04f else 1f, spring(dampingRatio = 0.5f), label = "chipScale")
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .clip(RoundedCornerShape(14.dp))
                            .background(bg)
                            .clickable { onSelect(quality) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(quality.label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = fg)
                    }
                }
            }
            AnimatedContent(
                targetState = selected,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(150)) },
                label = "checkInMessage"
            ) { quality ->
                if (quality != null) {
                    Text(
                        quality.message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                } else {
                    Spacer(Modifier.height(0.dp))
                }
            }
        }
    }
}

@Composable
private fun TodayCard(eyebrow: String, title: String, meta: String, reason: String, onStart: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, spring(dampingRatio = 0.6f), label = "today")
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth().scale(scale)
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Spa, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(eyebrow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(14.dp))
            Text(title, fontSize = 26.sp, lineHeight = 31.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(6.dp))
            Text(meta, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
            Spacer(Modifier.height(16.dp))
            Text(reason, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(interactionSource = interaction, indication = null, onClick = onStart),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.PlayArrow, null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(6.dp))
                Text("Start", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ProgressSection(sessionsThisWeek: Int, weeklyTarget: Int, streakDays: Int, totalSessions: Int) {
    val weekProgress by animateFloatAsState(
        (sessionsThisWeek / weeklyTarget.toFloat()).coerceIn(0f, 1f), tween(800), label = "week"
    )
    val journeyWeek = (totalSessions / 5 + 1).coerceAtMost(12)
    Column {
    Text("Your progress", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(12.dp))
    Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$sessionsThisWeek", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(" of $weeklyTarget this week", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp))
                Spacer(Modifier.weight(1f))
                if (streakDays > 0) {
                    Icon(Icons.Rounded.LocalFireDepartment, null, tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp).padding(bottom = 2.dp))
                    Text("$streakDays-day streak", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { weekProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                drawStopIndicator = {}
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Week $journeyWeek of your 12-week journey",
                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    }
}
