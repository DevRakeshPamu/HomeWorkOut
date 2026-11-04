package com.fit10.app.feature.breathing

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.ui.theme.Fit10Theme

private const val TOTAL_MS = 120_000L
private const val INHALE_MS = 4_000L
private const val CYCLE_MS = 10_000L

@Composable
fun BreathingSessionScreen(onFinished: () -> Unit) {
    val haptics = rememberFit10Haptics()
    var elapsed by rememberSaveable { mutableLongStateOf(0L) }
    var paused by rememberSaveable { mutableStateOf(false) }
    val done = elapsed >= TOTAL_MS

    LaunchedEffect(paused, done) {
        if (paused || done) return@LaunchedEffect
        var last = withFrameMillis { it }
        while (elapsed < TOTAL_MS) {
            val now = withFrameMillis { it }
            val before = elapsed
            elapsed = (elapsed + (now - last)).coerceAtMost(TOTAL_MS)
            last = now
            val crossedInhale = before % CYCLE_MS < INHALE_MS && elapsed % CYCLE_MS >= INHALE_MS
            val crossedCycle = elapsed / CYCLE_MS > before / CYCLE_MS
            if (crossedInhale || crossedCycle) haptics.perform(HapticKind.Tick)
        }
        haptics.perform(HapticKind.Confirm)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AnimatedContent(
            targetState = done,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
            label = "breathing"
        ) { finished ->
            if (finished) DoneView(onFinished)
            else SessionView(elapsed, paused, onTogglePause = {
                haptics.perform(HapticKind.Soft)
                paused = !paused
            }, onEnd = onFinished)
        }
    }
}

@Composable
private fun SessionView(elapsed: Long, paused: Boolean, onTogglePause: () -> Unit, onEnd: () -> Unit) {
    val inCycle = elapsed % CYCLE_MS
    val inhaling = inCycle < INHALE_MS
    val phaseFraction = if (inhaling) inCycle / INHALE_MS.toFloat() else (inCycle - INHALE_MS) / (CYCLE_MS - INHALE_MS).toFloat()
    val eased = FastOutSlowInEasing.transform(phaseFraction)
    val circleScale = if (inhaling) 0.55f + 0.45f * eased else 1f - 0.45f * eased
    val remaining = ((TOTAL_MS - elapsed + 999) / 1000).toInt()
    val primary = MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.outlineVariant

    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onEnd) { Icon(Icons.Rounded.Close, contentDescription = "End session") }
            Spacer(Modifier.weight(1f))
            Text("%d:%02d".format(remaining / 60, remaining % 60), fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(24.dp))
        Text("Core connection breath", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.weight(1f))

        Box(Modifier.size(280.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(280.dp)) {
                val stroke = 6.dp.toPx()
                drawArc(track, -90f, 360f, false, style = Stroke(stroke))
                drawArc(primary, -90f, 360f * elapsed / TOTAL_MS, false, style = Stroke(stroke, cap = StrokeCap.Round))
            }
            Box(
                Modifier.size(220.dp).scale(circleScale).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
            Box(
                Modifier.size(120.dp).scale(circleScale).clip(CircleShape)
                    .background(Fit10Theme.extendedColors.brandBright.copy(alpha = 0.35f))
            )
        }

        Spacer(Modifier.height(32.dp))
        AnimatedContent(
            targetState = inhaling,
            transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(300)) },
            label = "cue"
        ) { inhale ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (inhale) "Breathe in" else "Breathe out", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (inhale) "Let your belly and ribs expand. Relax your pelvic floor."
                    else "Gently draw your belly in and lift your pelvic floor.",
                    fontSize = 15.sp, lineHeight = 22.sp, textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        Spacer(Modifier.weight(1f))
        FilledIconButton(
            onClick = onTogglePause,
            modifier = Modifier.size(72.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                if (paused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                contentDescription = if (paused) "Resume" else "Pause",
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(if (paused) "Paused. Take your time." else " ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun DoneView(onFinished: () -> Unit) {
    val pop = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(112.dp).scale(pop.value).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(56.dp))
        }
        Spacer(Modifier.height(28.dp))
        Text("Your first session is done", fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Text(
            "Two calm minutes for yourself. That's how recovery starts. See you tomorrow.",
            fontSize = 16.sp, lineHeight = 24.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(36.dp))
        Button(onClick = onFinished, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Go to my plan", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
