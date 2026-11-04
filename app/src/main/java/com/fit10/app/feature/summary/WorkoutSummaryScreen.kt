package com.fit10.app.feature.summary

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.ui.theme.Fit10Theme
import com.fit10.app.domain.model.WorkoutFeedback
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WorkoutSummaryScreen(
    viewModel: WorkoutSummaryViewModel,
    onDone: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = rememberFit10Haptics()
    val result = state.result
    var appeared by remember { mutableStateOf(false) }
    val pop = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        appeared = true
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    BackHandler { onDone() }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Confetti(Modifier.fillMaxSize())
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))
            Box(
                Modifier.size(112.dp).scale(pop.value).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Check, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(56.dp))
            }
            Spacer(Modifier.height(28.dp))
            Text("You showed up for yourself", fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Text(result?.workoutTitle ?: "", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(28.dp))
            AnimatedVisibility(appeared, enter = fadeIn(tween(500, 250)) + slideInVertically(tween(500, 250)) { it / 4 }) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Stat("Minutes", ((result?.durationSeconds ?: 0) + 30) / 60, Modifier.weight(1f))
                    Stat("Moves", result?.exercisesCompletedCount ?: 0, Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(32.dp))
            AnimatedVisibility(appeared, enter = fadeIn(tween(500, 450))) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("How did that feel?", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            WorkoutFeedback.TOO_EASY to "Too easy",
                            WorkoutFeedback.JUST_RIGHT to "Just right",
                            WorkoutFeedback.TOO_HARD to "Too hard",
                        ).forEach { (fb, label) ->
                            val selected = state.selectedFeedback == fb
                            val bg by animateColorAsState(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, label = "fb")
                            val fg by animateColorAsState(if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, label = "fbText")
                            val scale by animateFloatAsState(if (selected) 1.05f else 1f, spring(dampingRatio = 0.5f), label = "fbScale")
                            Box(
                                Modifier
                                    .weight(1f)
                                    .scale(scale)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(bg)
                                    .clickable { haptics.perform(HapticKind.Tick); viewModel.selectFeedback(fb) }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = fg)
                            }
                        }
                    }
                    AnimatedContent(
                        targetState = state.adaptationMessage,
                        transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(150)) },
                        label = "adapt"
                    ) { message ->
                        if (message != null) {
                            Text(message, fontSize = 14.sp, lineHeight = 20.sp, textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp))
                        } else {
                            Spacer(Modifier.height(0.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f, fill = false))
            Spacer(Modifier.height(36.dp))
            Button(
                onClick = { haptics.perform(HapticKind.Confirm); onDone() },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Done", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Stat(label: String, value: Int, modifier: Modifier) {
    val shown by animateIntAsState(value, tween(900, easing = FastOutSlowInEasing), label = "stat")
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = modifier) {
        Column(Modifier.padding(vertical = 18.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$shown", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Confetti(modifier: Modifier) {
    val colors = Fit10Theme.extendedColors.celebration
    val pieces = remember { List(36) { Triple(Random.nextFloat() * 360f, 0.35f + Random.nextFloat() * 0.65f, Random.nextInt(colors.size)) } }
    val t = remember { Animatable(0f) }
    LaunchedEffect(Unit) { t.animateTo(1f, tween(1600, easing = FastOutSlowInEasing)) }
    if (t.value >= 1f) return
    Canvas(modifier) {
        val origin = Offset(size.width / 2, 200.dp.toPx())
        val reach = size.width * 0.55f
        pieces.forEach { (angle, speed, colorIndex) ->
            val rad = Math.toRadians(angle.toDouble())
            val d = reach * speed * t.value
            val gravity = 900f * t.value * t.value
            drawCircle(
                color = colors[colorIndex].copy(alpha = 1f - t.value),
                radius = 5.dp.toPx(),
                center = Offset(origin.x + (cos(rad) * d).toFloat(), origin.y + (sin(rad) * d).toFloat() + gravity)
            )
        }
    }
}
