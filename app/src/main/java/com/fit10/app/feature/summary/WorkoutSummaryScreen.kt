package com.fit10.app.feature.summary

import androidx.compose.animation.core.FastOutSlowInEasing
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.ui.theme.Fit10Theme
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.R
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val BrandPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
private val BrandBright: Color
    @Composable @ReadOnlyComposable get() = Fit10Theme.extendedColors.brandBright
private val BrandDeep: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
private val BrandContainer: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
private val BackgroundSurface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
private val BorderInactive: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant
private val TextMain: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
private val TextMuted: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val CardSurface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

@Composable
fun WorkoutSummaryScreen(
    viewModel: WorkoutSummaryViewModel,
    onDone: () -> Unit
) {
    val haptics = rememberFit10Haptics()
    val state  by viewModel.uiState.collectAsStateWithLifecycle()
    val result = state.result

    val minutes           = ((result?.durationSeconds ?: 600) + 59) / 60
    val calories          = result?.caloriesBurned ?: 78
    val completedExercises = result?.exercisesCompletedCount ?: 6
    val totalExercises    = result?.totalExercisesCount ?: 6

    // Entrance animation state
    var checkVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        checkVisible = true
    }
    val checkScale by animateFloatAsState(
        targetValue = if (checkVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "CheckScale"
    )

    // Animated stat count-ups
    val animMinutes  by animateIntAsState(targetValue = if (checkVisible) minutes else 0,  animationSpec = tween(1200, easing = FastOutSlowInEasing), label = "Minutes")
    val animCalories by animateIntAsState(targetValue = if (checkVisible) calories else 0, animationSpec = tween(1400, easing = FastOutSlowInEasing), label = "Calories")
    val animExercises by animateIntAsState(targetValue = if (checkVisible) completedExercises else 0, animationSpec = tween(1000, easing = FastOutSlowInEasing), label = "Exercises")

    // Done button press
    val doneInteraction = remember { MutableInteractionSource() }
    val isDonePressed by doneInteraction.collectIsPressedAsState()
    val doneScale by animateFloatAsState(
        targetValue = if (isDonePressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "DoneScale"
    )

    Scaffold(
        containerColor = BackgroundSurface,
        bottomBar = {
            Surface(
                color = CardSurface,
                shadowElevation = 12.dp,
                border = BorderStroke(1.dp, BorderInactive)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Gradient Save button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .scale(doneScale)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.horizontalGradient(listOf(Fit10Theme.extendedColors.brandStrong, BrandPrimary)))
                            .clickable(
                                interactionSource = doneInteraction,
                                indication = null,
                                onClick = { haptics.perform(HapticKind.Confirm); onDone() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Save & Continue →", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    // Share button
                    Surface(
                        onClick = {},
                        shape = RoundedCornerShape(16.dp),
                        color = CardSurface,
                        border = BorderStroke(1.dp, BorderInactive),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Rounded.Share, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Share Progress", color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Confetti particles layer
            ConfettiCanvas()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Coach avatar top-right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coach_maya),
                        contentDescription = "Coach Maya",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(2.dp, BrandPrimary, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Celebration check icon ──────────────────────────────────
                Box(
                    modifier = Modifier.size(120.dp).scale(checkScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Outermost ring
                    Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(BrandPrimary.copy(alpha = 0.08f)))
                    // Middle ring
                    Box(modifier = Modifier.size(96.dp).clip(CircleShape).background(BrandPrimary.copy(alpha = 0.16f)))
                    // Inner gradient circle
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Fit10Theme.extendedColors.brandStrong, BrandPrimary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(38.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Session badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BrandContainer)
                        .border(1.dp, BrandPrimary.copy(alpha = 0.3f), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "SESSION 10 COMPLETE 🎉",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = BrandDeep
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Workout Complete!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextMain)

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Great job! You're one step closer to your daily consistency goal.",
                    fontSize = 13.5.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Stats 2×2 grid ───────────────────────────────────────────
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryStatCard(
                        icon = Icons.Rounded.Timer,
                        iconColor = Fit10Theme.extendedColors.timer,
                        iconBg = Fit10Theme.extendedColors.timerContainer,
                        value = "$animMinutes min",
                        label = "Minutes Moved",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.Rounded.FitnessCenter,
                        iconColor = BrandPrimary,
                        iconBg = BrandContainer,
                        value = "$animExercises / $totalExercises",
                        label = "Exercises Done",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SummaryStatCard(
                        icon = Icons.Rounded.LocalFireDepartment,
                        iconColor = Fit10Theme.extendedColors.streak,
                        iconBg = Fit10Theme.extendedColors.streakContainer,
                        value = "$animCalories kcal",
                        label = "Calories Burned",
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.Rounded.CalendarToday,
                        iconColor = Fit10Theme.extendedColors.calm,
                        iconBg = Fit10Theme.extendedColors.calmContainer,
                        value = "4 Days 🔥",
                        label = "Current Streak",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── Daily Micro-Habit Card ──────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CardSurface,
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(22.dp).clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(Fit10Theme.extendedColors.brandStrong, BrandPrimary))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(13.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Daily Micro-Habit Achieved!", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandDeep)
                            }
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(BrandContainer).padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(text = "4/7", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandDeep)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "You've built 40 consecutive minutes of mindful movement this week.", fontSize = 12.sp, color = TextMuted, lineHeight = 17.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Weekly bubble row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            val days = listOf("M", "T", "W", "T", "F", "S", "S")
                            days.forEachIndexed { index, day ->
                                val isCompleted = index < 4
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isCompleted) Brush.linearGradient(listOf(Fit10Theme.extendedColors.brandStrong, BrandPrimary))
                                                else Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Icon(imageVector = Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                        } else {
                                            Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        }
                                    }
                                    Text(text = day, fontSize = 9.5.sp, color = if (isCompleted) BrandDeep else TextMuted, fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Quote Card ──────────────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CardSurface,
                    border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Green quote mark
                        Box(
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                                .background(BrandContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "❝", fontSize = 24.sp, color = BrandPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "CONSISTENCY OVER INTENSITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp, color = BrandDeep)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = "10 deliberate minutes today compounds into 60+ active hours this year.", fontSize = 12.sp, color = TextMuted, lineHeight = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

// ── Confetti Canvas ──────────────────────────────────────────────────────────

@Composable
private fun ConfettiCanvas() {
    val infiniteTransition = rememberInfiniteTransition(label = "Confetti")
    val tick by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "ConfettiTick"
    )

    val particles = remember {
        List(24) {
            Triple(
                Random.nextFloat(),             // x position fraction
                Random.nextFloat(),             // y start fraction
                Random.nextFloat() * 360f       // angle
            )
        }
    }

    val confettiColors = Fit10Theme.extendedColors.celebration

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEachIndexed { i, (xFrac, yFrac, angle) ->
            val phase = (tick + i * 0.04f) % 1f
            val y = (yFrac + phase * 1.2f) % 1.2f
            val x = xFrac + sin(angle + tick * 3.14f) * 0.04f
            val alpha = if (y > 0.9f) ((1f - (y - 0.9f) / 0.3f)).coerceIn(0f, 1f) else 0.6f
            drawCircle(
                color = confettiColors[i % confettiColors.size].copy(alpha = alpha * 0.4f),
                radius = 4.dp.toPx(),
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

// ── Stat Card ────────────────────────────────────────────────────────────────

@Composable
private fun SummaryStatCard(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardSurface,
        shadowElevation = 3.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextMain)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, fontSize = 11.5.sp, color = TextMuted)
        }
    }
}
