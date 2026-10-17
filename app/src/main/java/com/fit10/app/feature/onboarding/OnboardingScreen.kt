package com.fit10.app.feature.onboarding

import com.fit10.app.core.ui.theme.Fit10Theme
import androidx.compose.runtime.ReadOnlyComposable
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference

private val BrandPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
private val BrandStrong: Color
    @Composable @ReadOnlyComposable get() = Fit10Theme.extendedColors.brandStrong
private val OnBrand: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary
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
private val IconWell: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant
private val ScreenBackground: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingFinished: () -> Unit
) {
    val haptics = rememberFit10Haptics()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onOnboardingFinished()
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBackground),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (state.step) {
                                1 -> "Fit10"
                                2 -> "Activity Level"
                                3 -> "Fitness Goal"
                                4 -> "Daily Time Commitment"
                                5 -> "Home Equipment"
                                else -> "Personalize Workouts"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (state.step > 1) {
                        IconButton(onClick = viewModel::previousStep) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = TextMain
                            )
                        }
                    }
                },
                actions = {
                    TextButton(onClick = viewModel::skipOnboarding) {
                        Text(
                            text = "Skip",
                            color = BrandPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        },
        bottomBar = {
            androidx.compose.material3.Surface(
                color = CardSurface,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderInactive)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (!state.isLoading)
                                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        listOf(BrandStrong, BrandPrimary)
                                    )
                                else
                                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        listOf(TextMuted.copy(alpha = 0.38f), TextMuted.copy(alpha = 0.38f))
                                    )
                            )
                            .clickable(enabled = !state.isLoading, onClick = { haptics.perform(HapticKind.Confirm); viewModel.nextStep() }),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = OnBrand,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (state.step) {
                                        1 -> "Build My Workout"
                                        6 -> "Continue"
                                        else -> "Next Step"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnBrand
                                )
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = OnBrand
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = when (state.step) {
                            1 -> "Already have an account? Sign in"
                            2 -> "2 of 6 completed • Almost ready to move!"
                            3 -> "3 of 6 completed • Calibrating plan"
                            4 -> "You can adjust your time in your profile settings"
                            5 -> "Step 5 of 6 • You can update equipment anytime in settings"
                            else -> "Step 6 of 6 • Almost ready to generate your plan"
                        },
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Step Progress Indicator (Steps 2..6) — gradient filled
            if (state.step > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 1..6) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (i <= state.step)
                                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                                            listOf(BrandStrong, BrandPrimary)
                                        )
                                    else
                                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                                            listOf(BorderInactive, BorderInactive)
                                        )
                                )
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = state.step,
                transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
                label = "OnboardingStepAnimation"
            ) { targetStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))

                    when (targetStep) {
                        1 -> WelcomeStep(onStart = viewModel::nextStep)
                        2 -> FitnessLevelStep(
                            selected = state.selectedLevel,
                            onSelect = viewModel::selectLevel
                        )
                        3 -> GoalStep(
                            selected = state.selectedGoal,
                            onSelect = viewModel::selectGoal
                        )
                        4 -> DurationStep(
                            selectedMinutes = state.targetDurationMinutes,
                            onSelect = viewModel::selectDuration
                        )
                        5 -> EquipmentStep(
                            selectedEquipment = state.selectedEquipment,
                            onToggleEquipment = viewModel::toggleEquipment,
                            avoidList = state.exercisesToAvoid,
                            onToggleAvoid = viewModel::toggleExerciseToAvoid
                        )
                        6 -> PersonalizeStep(
                            intensity = state.intensityPreference,
                            onSelectIntensity = viewModel::selectIntensity,
                            preferredDays = state.preferredDays,
                            onToggleDay = viewModel::toggleDay,
                            recovery = state.recoveryPreference,
                            onSelectRecovery = viewModel::selectRecovery,
                            focusAreas = state.preferredFocusAreas,
                            onToggleFocusArea = viewModel::toggleFocusArea
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN 1: Welcome & Benefits
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WelcomeStep(onStart: () -> Unit) {
    Column {
        // Tag badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(BrandContainer)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "⚡ 10-min Workout Routine",
                color = BrandDeep,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Get fitter in 10 minutes",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Personalized home workouts that adapt to you.",
            fontSize = 15.sp,
            color = TextMuted
        )

        Spacer(Modifier.height(18.dp))

        // Daily Spark Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardSurface,
            border = BorderStroke(1.dp, BorderInactive),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BrandContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Spark",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextMain
                    )
                    Text(
                        text = "Calibrated for your morning",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BrandContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "10:00",
                        color = BrandDeep,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 4 Value Proposition Cards
        BenefitRow(
            icon = Icons.Rounded.Shield,
            title = "No equipment required",
            subtitle = "Work out anywhere, anytime with zero gear"
        )
        BenefitRow(
            icon = Icons.Rounded.Speed,
            title = "Workouts for your level",
            subtitle = "Paced to your current fitness, never overwhelming"
        )
        BenefitRow(
            icon = Icons.AutoMirrored.Rounded.TrendingUp,
            title = "Adapts as you improve",
            subtitle = "Smart progression that evolves with your stamina"
        )
        BenefitRow(
            icon = Icons.Rounded.CalendarToday,
            title = "Build healthy habits",
            subtitle = "Short daily sessions that seamlessly fit your schedule"
        )

        Spacer(Modifier.height(14.dp))

        // Social Proof Stack
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                Fit10Theme.extendedColors.celebration.take(3).forEach { col ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(col)
                            .border(1.5.dp, CardSurface, CircleShape)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Joined by 14,000+ habit builders",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun BenefitRow(icon: ImageVector, title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardSurface,
        border = BorderStroke(1.dp, BorderInactive),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(IconWell),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = TextMain
                )
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    color = TextMuted
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN 2: Fitness Level
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FitnessLevelStep(
    selected: FitnessLevel,
    onSelect: (FitnessLevel) -> Unit
) {
    Column {
        Text(
            text = "What's your fitness level?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "This helps us personalize your workouts to build sustainable, lifelong momentum.",
            fontSize = 14.sp,
            color = TextMuted,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(18.dp))

        LevelCard(
            title = "Beginner",
            subtitle = "Gentle introductory movements to safely wake up muscles and build baseline endurance.",
            paceLabel = "Low Pace",
            barsFilled = 1,
            isSelected = selected == FitnessLevel.BEGINNER,
            onClick = { onSelect(FitnessLevel.BEGINNER) }
        )

        LevelCard(
            title = "Intermediate",
            badge = "🔥 Most Popular",
            subtitle = "Balanced mix of strength, cardio intervals, and dynamic movement to challenge you.",
            paceLabel = "Moderate Pace",
            barsFilled = 2,
            isSelected = selected == FitnessLevel.INTERMEDIATE,
            onClick = { onSelect(FitnessLevel.INTERMEDIATE) }
        )

        LevelCard(
            title = "Advanced",
            subtitle = "High-intensity circuits and targeted progressions to maximize performance and power.",
            paceLabel = "High Intensity",
            barsFilled = 3,
            isSelected = selected == FitnessLevel.ADVANCED,
            onClick = { onSelect(FitnessLevel.ADVANCED) }
        )

        Spacer(Modifier.height(12.dp))

        // Tip Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BrandContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Not sure? You can safely adjust your difficulty benchmark or swap out exercises anytime in settings.",
                    fontSize = 12.5.sp,
                    color = BrandDeep,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun LevelCard(
    title: String,
    subtitle: String,
    paceLabel: String,
    barsFilled: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    val haptics = rememberFit10Haptics()
    Surface(
        onClick = { haptics.perform(HapticKind.Tick); onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) BrandContainer else CardSurface,
        border = BorderStroke(
            width = if (isSelected) 1.8.dp else 1.dp,
            color = if (isSelected) BrandPrimary else BorderInactive
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextMain
                )
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(BrandPrimary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badge,
                            color = OnBrand,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Pace meter bars
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..3) {
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (i <= barsFilled) BrandPrimary else BorderInactive
                                )
                        )
                    }
                }

                Text(
                    text = paceLabel,
                    fontSize = 12.sp,
                    color = if (isSelected) BrandDeep else TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN 3: Fitness Goal
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun GoalStep(
    selected: FitnessGoal,
    onSelect: (FitnessGoal) -> Unit
) {
    Column {
        Text(
            text = "What's your goal?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Choose one to personalize your daily micro-habits.",
            fontSize = 14.sp,
            color = TextMuted
        )

        Spacer(Modifier.height(18.dp))

        GoalRow(
            goal = FitnessGoal.LOSE_WEIGHT,
            title = "Lose weight",
            subtitle = "Burn calories and shed fat with high-efficiency intervals.",
            icon = Icons.Rounded.Speed,
            isSelected = selected == FitnessGoal.LOSE_WEIGHT,
            onClick = { onSelect(FitnessGoal.LOSE_WEIGHT) }
        )

        GoalRow(
            goal = FitnessGoal.BUILD_STRENGTH,
            title = "Build strength",
            badge = "Recommended",
            subtitle = "Tone muscle and improve core full-body functional power.",
            icon = Icons.Rounded.FitnessCenter,
            isSelected = selected == FitnessGoal.BUILD_STRENGTH,
            onClick = { onSelect(FitnessGoal.BUILD_STRENGTH) }
        )

        GoalRow(
            goal = FitnessGoal.IMPROVE_FITNESS,
            title = "Improve fitness",
            subtitle = "Boost daily stamina, heart health, and energy levels.",
            icon = Icons.Rounded.Bolt,
            isSelected = selected == FitnessGoal.IMPROVE_FITNESS,
            onClick = { onSelect(FitnessGoal.IMPROVE_FITNESS) }
        )

        GoalRow(
            goal = FitnessGoal.FLEXIBILITY,
            title = "Improve flexibility",
            subtitle = "Release tension, improve posture, and feel agile.",
            icon = Icons.Rounded.SelfImprovement,
            isSelected = selected == FitnessGoal.FLEXIBILITY,
            onClick = { onSelect(FitnessGoal.FLEXIBILITY) }
        )

        GoalRow(
            goal = FitnessGoal.STAY_ACTIVE,
            title = "Stay active",
            subtitle = "Keep moving consistently with enjoyable daily momentum.",
            icon = Icons.AutoMirrored.Rounded.DirectionsRun,
            isSelected = selected == FitnessGoal.STAY_ACTIVE,
            onClick = { onSelect(FitnessGoal.STAY_ACTIVE) }
        )

        Spacer(Modifier.height(12.dp))

        // Fit10 Adaptive Engine card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BrandContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Fit10 Adaptive Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandDeep
                    )
                    Text(
                        text = "Workouts calibrate dynamically to your choices.",
                        fontSize = 12.sp,
                        color = BrandDeep
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalRow(
    goal: FitnessGoal,
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    val haptics = rememberFit10Haptics()
    Surface(
        onClick = { haptics.perform(HapticKind.Tick); onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) BrandContainer else CardSurface,
        border = BorderStroke(
            width = if (isSelected) 1.8.dp else 1.dp,
            color = if (isSelected) BrandPrimary else BorderInactive
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) BrandPrimary else IconWell),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) OnBrand else BrandPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.5.sp,
                        color = TextMain
                    )
                    if (badge != null) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(BrandPrimary)
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                color = OnBrand,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    color = TextMuted,
                    lineHeight = 17.sp
                )
            }

            if (isSelected) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = BrandPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN 4: Time Commitment
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DurationStep(
    selectedMinutes: Int,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(
            text = "How much time do you have?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Even 5 minutes can make a noticeable difference.",
            fontSize = 14.sp,
            color = TextMuted
        )

        Spacer(Modifier.height(18.dp))

        DurationCard(
            minutes = 5,
            tag = "Quick Energizer",
            subtitle = "Fast reset between tasks or a crisp morning kickoff.",
            isSelected = selectedMinutes == 5,
            onClick = { onSelect(5) }
        )

        DurationCard(
            minutes = 10,
            tag = null,
            badge = "FIT10 SIGNATURE",
            subtitle = "The scientifically proven sweet spot for daily consistency and sustained metabolic boost.",
            socialStat = "Chosen by 81% of new members",
            isSelected = selectedMinutes == 10,
            onClick = { onSelect(10) }
        )

        DurationCard(
            minutes = 15,
            tag = "Balanced Burn",
            subtitle = "Deeper strength building, steady calorie burn, and muscle conditioning.",
            isSelected = selectedMinutes == 15,
            onClick = { onSelect(15) }
        )

        DurationCard(
            minutes = 20,
            tag = "Full Sweat",
            subtitle = "Comprehensive full-body burn complete with dynamic warmup and cool-down.",
            isSelected = selectedMinutes == 20,
            onClick = { onSelect(20) }
        )

        DurationCard(
            minutes = 30,
            tag = "Max Performance",
            subtitle = "High-intensity exercise targets for maximum cardiovascular endurance.",
            isSelected = selectedMinutes == 30,
            onClick = { onSelect(30) }
        )

        Spacer(Modifier.height(12.dp))

        // Habit Science insight card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BrandContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "HABIT SCIENCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Fit10 members who commit to 10 minutes daily are 3.4x more likely to maintain their streak after 90 days.",
                    fontSize = 12.5.sp,
                    color = BrandDeep,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun DurationCard(
    minutes: Int,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String? = null,
    badge: String? = null,
    socialStat: String? = null
) {
    val haptics = rememberFit10Haptics()
    Surface(
        onClick = { haptics.perform(HapticKind.Tick); onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) BrandContainer else CardSurface,
        border = BorderStroke(
            width = if (isSelected) 1.8.dp else 1.dp,
            color = if (isSelected) BrandPrimary else BorderInactive
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSelected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isSelected) BrandPrimary else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "$minutes minutes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextMain
                    )
                    if (tag != null) {
                        Text(
                            text = " • $tag",
                            fontSize = 13.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(BrandPrimary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badge,
                            color = OnBrand,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            if (socialStat != null) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = socialStat,
                        color = BrandDeep,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN 5: Equipment & Exercises to Avoid
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EquipmentStep(
    selectedEquipment: Set<Equipment>,
    onToggleEquipment: (Equipment) -> Unit,
    avoidList: Set<String>,
    onToggleAvoid: (String) -> Unit
) {
    val haptics = rememberFit10Haptics()
    Column {
        Text(
            text = "What do you have at home?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Select all the equipment you have available for your micro-sessions.",
            fontSize = 14.sp,
            color = TextMuted
        )

        Spacer(Modifier.height(16.dp))

        EquipmentCheckRow(
            title = "No equipment",
            subtitle = "Bodyweight only — perfect for anywhere",
            icon = Icons.AutoMirrored.Rounded.DirectionsRun,
            isSelected = selectedEquipment.contains(Equipment.NONE),
            onClick = { onToggleEquipment(Equipment.NONE) }
        )

        EquipmentCheckRow(
            title = "Yoga mat",
            subtitle = "Comfort for floor exercises & mobility",
            icon = Icons.Rounded.SelfImprovement,
            isSelected = selectedEquipment.contains(Equipment.MAT),
            onClick = { onToggleEquipment(Equipment.MAT) }
        )

        EquipmentCheckRow(
            title = "Dumbbells",
            subtitle = "Light to medium hand weights",
            icon = Icons.Rounded.FitnessCenter,
            isSelected = selectedEquipment.contains(Equipment.DUMBBELLS),
            onClick = { onToggleEquipment(Equipment.DUMBBELLS) }
        )

        EquipmentCheckRow(
            title = "Resistance bands",
            subtitle = "Versatile loop or tube bands",
            icon = Icons.Rounded.Speed,
            isSelected = selectedEquipment.contains(Equipment.RESISTANCE_BANDS),
            onClick = { onToggleEquipment(Equipment.RESISTANCE_BANDS) }
        )

        Spacer(Modifier.height(10.dp))

        // 10-Min Session Highlight
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BrandContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = OnBrand,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Every session is 10 minutes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BrandDeep
                    )
                    Text(
                        text = "We adapt rest intervals and intensity to your exact gear.",
                        fontSize = 12.sp,
                        color = BrandDeep
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Exercises to avoid section
        Text(
            text = "Exercises to avoid",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextMain
        )
        Text(
            text = "We'll tailor your routines to protect joints and prevent pain.",
            fontSize = 13.sp,
            color = TextMuted
        )

        Spacer(Modifier.height(10.dp))

        val avoidOptions = listOf("None", "Jumping", "Knee-intensive", "High-impact", "Back-intensive")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            avoidOptions.forEach { option ->
                val isSelected = avoidList.contains(option)
                Surface(
                    onClick = { haptics.perform(HapticKind.Tick); onToggleAvoid(option) },
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) BrandContainer else CardSurface,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) BrandPrimary else BorderInactive
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Rounded.Check else Icons.Rounded.Close,
                            contentDescription = null,
                            tint = if (isSelected) BrandPrimary else TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = option,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandDeep else TextMain
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentCheckRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptics = rememberFit10Haptics()
    Surface(
        onClick = { haptics.perform(HapticKind.Tick); onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) BrandContainer else CardSurface,
        border = BorderStroke(
            width = if (isSelected) 1.8.dp else 1.dp,
            color = if (isSelected) BrandPrimary else BorderInactive
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(IconWell),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.5.sp,
                    color = TextMain
                )
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    color = TextMuted
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected",
                        tint = OnBrand,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SCREEN 6: Personalization
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PersonalizeStep(
    intensity: IntensityPreference,
    onSelectIntensity: (IntensityPreference) -> Unit,
    preferredDays: Set<Int>,
    onToggleDay: (Int) -> Unit,
    recovery: String,
    onSelectRecovery: (String) -> Unit,
    focusAreas: Set<FocusArea>,
    onToggleFocusArea: (FocusArea) -> Unit
) {
    val haptics = rememberFit10Haptics()
    Column {
        Text(
            text = "Let's personalize your workouts",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Fine-tune your training style to match your daily rhythm and body needs.",
            fontSize = 14.sp,
            color = TextMuted
        )

        Spacer(Modifier.height(20.dp))

        // 1. Workout intensity preference
        Text(
            text = "Workout intensity preference",
            fontWeight = FontWeight.Bold,
            fontSize = 15.5.sp,
            color = TextMain
        )
        Text(
            text = "Paced that aligns with your energy bandwidth",
            fontSize = 12.5.sp,
            color = TextMuted
        )
        Spacer(Modifier.height(10.dp))

        IntensityChoiceCard(
            title = "Gentle",
            subtitle = "Low-impact, mindful pacing",
            isSelected = intensity == IntensityPreference.EASY,
            onClick = { onSelectIntensity(IntensityPreference.EASY) }
        )
        IntensityChoiceCard(
            title = "Moderate",
            subtitle = "Steady calorie burn & toning",
            isSelected = intensity == IntensityPreference.MODERATE,
            onClick = { onSelectIntensity(IntensityPreference.MODERATE) }
        )
        IntensityChoiceCard(
            title = "High Energy",
            subtitle = "Quick peak heart-rate intervals",
            isSelected = intensity == IntensityPreference.CHALLENGING,
            onClick = { onSelectIntensity(IntensityPreference.CHALLENGING) }
        )

        Spacer(Modifier.height(20.dp))

        // 2. Preferred workout days
        Text(
            text = "Preferred workout days",
            fontWeight = FontWeight.Bold,
            fontSize = 15.5.sp,
            color = TextMain
        )
        Text(
            text = "Select the days you are ready for a 10-minute micro-session",
            fontSize = 12.5.sp,
            color = TextMuted
        )
        Spacer(Modifier.height(12.dp))

        val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEachIndexed { index, label ->
                val dayNum = index + 1
                val isSelected = preferredDays.contains(dayNum)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) BrandPrimary else CardSurface)
                        .border(1.dp, if (isSelected) BrandPrimary else BorderInactive, CircleShape)
                        .clickable { haptics.perform(HapticKind.Tick); onToggleDay(dayNum) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) OnBrand else TextMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${preferredDays.size} days selected • Optimal for habit formation",
            fontSize = 12.sp,
            color = BrandDeep,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(20.dp))

        // 3. Recovery preference
        Text(
            text = "Recovery preference",
            fontWeight = FontWeight.Bold,
            fontSize = 15.5.sp,
            color = TextMain
        )
        Text(
            text = "How you rest on off-days",
            fontSize = 12.5.sp,
            color = TextMuted
        )
        Spacer(Modifier.height(10.dp))

        RecoveryCard(
            title = "Active Recovery",
            subtitle = "Light mobility & guided breathwork on rest days",
            isSelected = recovery == "ACTIVE_RECOVERY",
            onClick = { onSelectRecovery("ACTIVE_RECOVERY") }
        )
        RecoveryCard(
            title = "Complete Rest",
            subtitle = "No scheduled movement or reminders",
            isSelected = recovery == "COMPLETE_REST",
            onClick = { onSelectRecovery("COMPLETE_REST") }
        )

        Spacer(Modifier.height(20.dp))

        // 4. Focus areas
        Text(
            text = "Focus areas",
            fontWeight = FontWeight.Bold,
            fontSize = 15.5.sp,
            color = TextMain
        )
        Text(
            text = "Choose where you want to feel the strongest",
            fontSize = 12.5.sp,
            color = TextMuted
        )
        Spacer(Modifier.height(10.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FocusArea.entries.forEach { area ->
                val isSelected = focusAreas.contains(area)
                val label = when (area) {
                    FocusArea.FULL_BODY -> "Full Body"
                    FocusArea.CORE -> "Core"
                    FocusArea.UPPER_BODY -> "Upper Body"
                    FocusArea.LOWER_BODY -> "Lower Body"
                    FocusArea.MOBILITY -> "Mobility"
                }
                Surface(
                    onClick = { haptics.perform(HapticKind.Tick); onToggleFocusArea(area) },
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) BrandContainer else CardSurface,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) BrandPrimary else BorderInactive
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BrandDeep else TextMain
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // 5. Adaptive Micro-Cycles Info Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BrandContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = OnBrand,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Adaptive Micro-Cycles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BrandDeep
                    )
                    Text(
                        text = "Fit10 modulates workout intensity automatically if your sleep or recovery signals low readiness.",
                        fontSize = 12.sp,
                        color = BrandDeep,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun IntensityChoiceCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptics = rememberFit10Haptics()
    Surface(
        onClick = { haptics.perform(HapticKind.Tick); onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) BrandContainer else CardSurface,
        border = BorderStroke(
            width = if (isSelected) 1.8.dp else 1.dp,
            color = if (isSelected) BrandPrimary else BorderInactive
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) BrandPrimary else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextMain
                )
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun RecoveryCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptics = rememberFit10Haptics()
    Surface(
        onClick = { haptics.perform(HapticKind.Tick); onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) BrandContainer else CardSurface,
        border = BorderStroke(
            width = if (isSelected) 1.8.dp else 1.dp,
            color = if (isSelected) BrandPrimary else BorderInactive
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) BrandPrimary else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextMain
                )
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    color = TextMuted
                )
            }
        }
    }
}
