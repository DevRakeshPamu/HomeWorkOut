package com.fit10.app.feature.home

import androidx.compose.foundation.border
import androidx.compose.material.icons.rounded.Check
import com.fit10.app.domain.plan.PlanDay
import com.fit10.app.domain.plan.WeekPlan
import java.time.format.TextStyle
import java.util.Locale

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

        state.reviewNote?.let { note ->
            Spacer(Modifier.height(16.dp))
            Text(
                note, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer).padding(14.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Staggered(appeared, 1) {
            val plan = state.plan
            if (breathingFirst || plan == null) {
                TodayCard(
                    eyebrow = "Gentle recovery",
                    title = "Core connection breath",
                    meta = "2 min · Breathing and pelvic floor",
                    reason = if (startingPoint == StartingPoint.SEE_DOCTOR) "Check with your doctor before starting workouts."
                    else "Until you're cleared, we keep it to breathing and posture.",
                    cta = "Start",
                    onStart = { haptics.perform(HapticKind.Confirm); onStartBreathing() }
                )
            } else {
                val next = plan.days.firstOrNull { it.date.isAfter(java.time.LocalDate.now()) && it.focus != null }
                val nextText = next?.let { "Next: ${it.focus!!.label}, ${it.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}" }
                TodayCard(
                    eyebrow = "Week ${plan.week} · ${plan.level.title}",
                    title = when {
                        plan.doneToday -> "Done for today"
                        plan.isRestDay -> "Rest day"
                        else -> plan.today.title
                    },
                    meta = when {
                        plan.doneToday -> nextText ?: "See you next week"
                        plan.isRestDay -> "Optional · ${plan.today.title} · ${plan.today.estimatedDurationMinutes} min"
                        else -> "${plan.today.estimatedDurationMinutes} min · ${plan.today.exercises.size} moves"
                    },
                    reason = if (plan.doneToday) "Rest and recover. Your plan adjusts as you go." else plan.reason,
                    cta = when {
                        plan.doneToday -> "Do a bonus session"
                        plan.isRestDay -> "Start anyway"
                        else -> "Start"
                    },
                    onStart = { haptics.perform(HapticKind.Confirm); onStartWorkout(plan.today.id) }
                )
            }
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
                plan = state.plan,
                sessionsThisWeek = state.sessionsThisWeek,
                weeklyTarget = daysTarget,
                streakDays = state.history.currentStreakDays,
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
private fun TodayCard(eyebrow: String, title: String, meta: String, reason: String, cta: String, onStart: () -> Unit) {
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
                Text(cta, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
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
private fun ProgressSection(plan: WeekPlan?, sessionsThisWeek: Int, weeklyTarget: Int, streakDays: Int) {
    Column {
        Text("This week", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$sessionsThisWeek", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(" of $weeklyTarget done", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp))
                    Spacer(Modifier.weight(1f))
                    if (streakDays > 0) {
                        Icon(Icons.Rounded.LocalFireDepartment, null, tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp).padding(bottom = 2.dp))
                        Text("$streakDays-day streak", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }
                plan?.let {
                    Spacer(Modifier.height(16.dp))
                    WeekStrip(it.days)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "${it.level.title}: ${it.level.blurb.lowercase()}. Plan updates in ${it.daysToReview} days based on how it goes.",
                        fontSize = 13.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(days: List<PlanDay>) {
    val primary = MaterialTheme.colorScheme.primary
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        days.forEach { d ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    d.date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    fontSize = 12.sp,
                    fontWeight = if (d.isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (d.isToday) primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                val bg = when {
                    d.done -> primary
                    d.focus != null && d.date.isBefore(java.time.LocalDate.now()) -> MaterialTheme.colorScheme.surfaceVariant
                    d.focus != null -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Box(
                    Modifier.size(34.dp).clip(CircleShape).background(bg)
                        .then(if (d.isToday && !d.done) Modifier.border(2.dp, primary, CircleShape) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        d.done -> Icon(Icons.Rounded.Check, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                        d.focus == null -> Text("Rest", fontSize = 9.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}
