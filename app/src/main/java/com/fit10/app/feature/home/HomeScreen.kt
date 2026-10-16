package com.fit10.app.feature.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.R
import com.fit10.app.core.ui.components.Fit10Chip
import com.fit10.app.core.ui.components.Fit10SectionHeader
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.Workout
import java.util.Calendar

// ── Design tokens ──────────────────────────────────────────────────────────
private val BrandPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
private val BrandDeep: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
private val BrandBright: Color
    @Composable @ReadOnlyComposable get() = Fit10Theme.extendedColors.brandBright
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

private val HeroGradient: Brush
    @Composable @ReadOnlyComposable get() = Brush.linearGradient(
    colors = listOf(Fit10Theme.extendedColors.brandStrong, MaterialTheme.colorScheme.primary)
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartWorkout: (workoutId: String) -> Unit,
    onPreviewWorkout: ((workoutId: String) -> Unit)? = null,
    onNavigateToExplore: (FocusArea?) -> Unit,
    onManageSubscription: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToPaywall: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val workout = state.recommendedWorkout
    val userName = state.userProfile?.name?.takeIf { it.isNotBlank() } ?: "Athlete"

    // Staggered entrance: track whether content has appeared
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val greetingEmoji = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "☀️"
            hour < 17 -> "⚡"
            else -> "🌙"
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── 1. Header ────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -20 }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$greeting, $userName $greetingEmoji",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ready for your 10 minutes today?",
                            fontSize = 13.5.sp,
                            color = TextMuted
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Notification Bell
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardSurface)
                                .border(1.dp, BorderInactive, RoundedCornerShape(12.dp))
                                .clickable(onClick = onNavigateToReminders),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = "Notifications",
                                tint = TextMain,
                                modifier = Modifier.size(20.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.error)
                                    .align(Alignment.TopEnd)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        // Profile Avatar
                        Image(
                            painter = painterResource(id = R.drawable.coach_maya),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(2.dp, BrandPrimary, CircleShape)
                                .clickable(onClick = onManageSubscription)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── 2. Hero Workout Card ──────────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, 100)) + slideInVertically(tween(500, 100)) { 30 }
            ) {
                HeroWorkoutCard(
                    workout = workout,
                    onStartWorkout = { onStartWorkout(workout.id) },
                    onPreviewWorkout = {
                        if (onPreviewWorkout != null) onPreviewWorkout(workout.id)
                        else onStartWorkout(workout.id)
                    }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── 3. Daily Momentum Grid ─────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, 200))
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Momentum",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextMain
                        )
                        Text(
                            text = "Refreshes in 8h",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MomentumStatCard(
                            icon = Icons.Rounded.LocalFireDepartment,
                            iconColor = Fit10Theme.extendedColors.streak,
                            iconBg = Fit10Theme.extendedColors.streakContainer,
                            label = "STREAK",
                            value = "3 Days",
                            modifier = Modifier.weight(1f)
                        )
                        MomentumStatCard(
                            icon = Icons.Rounded.TrackChanges,
                            iconColor = BrandPrimary,
                            iconBg = BrandContainer,
                            label = "WORKOUTS",
                            value = "2 / 4",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MomentumStatCard(
                            icon = Icons.Rounded.Schedule,
                            iconColor = Fit10Theme.extendedColors.timer,
                            iconBg = Fit10Theme.extendedColors.timerContainer,
                            label = "MINUTES",
                            value = "20 min",
                            modifier = Modifier.weight(1f)
                        )
                        MomentumStatCard(
                            icon = Icons.Rounded.Bolt,
                            iconColor = Fit10Theme.extendedColors.calm,
                            iconBg = Fit10Theme.extendedColors.calmContainer,
                            label = "BURNED",
                            value = "185 kcal",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── 4. Weekly Consistency ──────────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, 300))
            ) {
                WeeklyConsistencyCard()
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── 5. Next Up in Your Program ─────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, 400))
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Next Up in Your Program",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextMain
                        )
                        Text(
                            text = "View Roadmap →",
                            fontSize = 12.sp,
                            color = BrandPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onNavigateToPaywall)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Next Up card with left accent bar
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CardSurface,
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Left green accent bar
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(80.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(BrandBright, BrandPrimary)
                                        ),
                                        RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                    )
                            )
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.coach_pushup),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TOMORROW • DAY 2",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Core Stability & Balance",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMain
                                    )
                                    Text(
                                        text = "⏱ 10 min • Mobility Focus",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BrandContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Alarm,
                                        contentDescription = null,
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── 6. Target Muscle Groups ───────────────────────────────────
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, 500))
            ) {
                Column {
                    Fit10SectionHeader(
                        title = "Target Muscle Group",
                        actionText = "See All",
                        onActionClick = { onNavigateToExplore(null) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Fit10Chip(text = "Full Body",  selected = true,  onClick = { onNavigateToExplore(FocusArea.FULL_BODY) })
                        Fit10Chip(text = "Core & Abs", selected = false, onClick = { onNavigateToExplore(FocusArea.CORE) })
                        Fit10Chip(text = "Lower Body", selected = false, onClick = { onNavigateToExplore(FocusArea.LOWER_BODY) })
                        Fit10Chip(text = "Upper Body", selected = false, onClick = { onNavigateToExplore(FocusArea.UPPER_BODY) })
                        Fit10Chip(text = "Mobility",   selected = false, onClick = { onNavigateToExplore(FocusArea.MOBILITY) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Hero Workout Card ─────────────────────────────────────────────────────

@Composable
private fun HeroWorkoutCard(
    workout: Workout,
    onStartWorkout: () -> Unit,
    onPreviewWorkout: () -> Unit
) {
    val haptics = rememberFit10Haptics()
    val startInteraction = remember { MutableInteractionSource() }
    val isStartPressed by startInteraction.collectIsPressedAsState()
    val startScale by animateFloatAsState(
        targetValue = if (isStartPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "StartBtnScale"
    )

    // Infinite subtle glow pulse on start button
    val infiniteTransition = rememberInfiniteTransition(label = "HeroPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CardSurface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BrandContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "DAY 1 • TODAY'S SESSION",
                        color = BrandDeep,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⚡ 10 MIN",
                        color = TextMain,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Full Body Kickstart",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Awaken energy, engage major stabilizers, and break an invigorating micro-sweat in 6 movements.",
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Workout Image with gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_bg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay — darkens bottom for text legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Fit10Theme.extendedColors.mediaScrim),
                                startY = 60f
                            )
                        )
                )
                // HIIT badge
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(50))
                        .background(Fit10Theme.extendedColors.mediaScrim)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⚡ HIIT & Flow",
                        color = Fit10Theme.extendedColors.onMediaScrim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Exercises count badge
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(50))
                        .background(BrandPrimary.copy(alpha = 0.9f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "6 moves",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Spec chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HeroTag("🏋 Full Body")
                HeroTag("⏱ 10 min")
                HeroTag("🌱 Beginner")
                HeroTag("🏠 No equipment")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Workout Button — gradient + press scale
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(startScale)
                    .height(52.dp)
                    .clip(RoundedCornerShape(50))
                    .background(HeroGradient)
                    .clickable(
                        interactionSource = startInteraction,
                        indication = null,
                        onClick = { haptics.perform(HapticKind.Confirm); onStartWorkout() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Workout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preview exercises link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .clickable(onClick = onPreviewWorkout)
                    .padding(vertical = 10.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preview 6 exercises",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMain
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Weekly Consistency Card ───────────────────────────────────────────────

@Composable
private fun WeeklyConsistencyCard() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = CardSurface,
        shadowElevation = 2.dp,
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
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Weekly Consistency",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextMain
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BrandContainer)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Week 2",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandDeep
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            val days = listOf(
                Triple("M", "10m",    true),
                Triple("T", "Rest",   false),
                Triple("W", "Today",  false),
                Triple("T", "Day 2",  false),
                Triple("F", "Day 3",  false),
                Triple("S", "Day 4",  false),
                Triple("S", "Rest",   false)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { idx, (letter, label, isCompleted) ->
                    val isToday = idx == 2
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isToday     -> Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, Fit10Theme.extendedColors.brandStrong))
                                    isCompleted -> Brush.verticalGradient(listOf(BrandContainer, BrandContainer))
                                    else        -> Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background))
                                }
                            )
                            .padding(horizontal = 7.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = letter,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isToday     -> MaterialTheme.colorScheme.onPrimary
                                isCompleted -> BrandDeep
                                else        -> TextMuted
                            }
                        )
                        Spacer(Modifier.height(4.dp))
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = BrandDeep,
                                modifier = Modifier.size(13.dp)
                            )
                        } else {
                            Text(
                                text = label,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isToday) MaterialTheme.colorScheme.onPrimary else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "On track! Complete today to hit 75% of your weekly target.",
                    fontSize = 11.5.sp,
                    color = BrandDeep,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ── Sub-components ───────────────────────────────────────────────────────

@Composable
private fun HeroTag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.5.sp,
            color = TextMuted,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MomentumStatCard(
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardSurface,
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
            }
        }
    }
}
