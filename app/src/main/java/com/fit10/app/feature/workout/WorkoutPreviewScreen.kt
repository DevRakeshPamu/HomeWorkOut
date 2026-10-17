package com.fit10.app.feature.workout

import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.ui.theme.Fit10Theme
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.domain.model.Workout

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
private val ScreenBackground: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPreviewScreen(
    workout: Workout?,
    onBack: () -> Unit,
    onStartWorkout: () -> Unit,
    onExerciseClick: (exerciseId: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptics = rememberFit10Haptics()
    val title = workout?.title ?: "Full Body Workout"
    val description = workout?.description ?: "Balanced routine designed to activate major muscle groups with zero gear."
    val durationMin = workout?.estimatedDurationMinutes ?: 10
    val exerciseCount = workout?.exercises?.size ?: 6

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBackground),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextMain
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Rounded.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = TextMain
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = CardSurface,
                shadowElevation = 10.dp,
                border = BorderStroke(1.dp, BorderInactive)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    listOf(BrandStrong, BrandPrimary)
                                )
                            )
                            .clickable { haptics.perform(HapticKind.Confirm); onStartWorkout() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = OnBrand,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Start Workout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnBrand
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Routine Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(BrandContainer)
                    .border(1.dp, BrandPrimary.copy(alpha = 0.3f), RoundedCornerShape(50))
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
                        text = "Routine #10 • Quick Activation",
                        color = BrandDeep,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 2. Title & Subtitle
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = TextMuted,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            // 3. Specs Row: ⏱ 10 minutes • 6 exercises • Beginner • 🔥 +1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpecBadge("⏱ $durationMin minutes")
                SpecBadge("$exerciseCount exercises")
                SpecBadge("Beginner")
                SpecBadge("🔥 +1")
            }

            Spacer(Modifier.height(16.dp))

            // 4. Guided Rhythm Included Box
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
                            text = "Guided Rhythm Included",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = BrandDeep
                        )
                        Text(
                            text = "30-sec guided warmup and cooldown included in pacing.",
                            fontSize = 12.sp,
                            color = BrandDeep
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 5. Workout Flow Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Workout Flow",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextMain
                )
                Text(
                    text = "Tap Swap to customize",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(Modifier.height(12.dp))

            // Exercises List
            val exerciseList = workout?.exercises?.ifEmpty { null } ?: listOf()
            if (exerciseList.isNotEmpty()) {
                exerciseList.forEachIndexed { index, item ->
                    val isLast = index == exerciseList.size - 1
                    ExerciseFlowCard(
                        title = item.exercise.name,
                        category = item.exercise.focusArea.name.replace("_", " "),
                        timing = "${item.durationSeconds} sec • ${if (isLast) "Final stretch" else "${item.restDurationSeconds}s rest after"}",
                        icon = getExerciseIcon(item.exercise.name),
                        index = index,
                        onClick = { onExerciseClick(item.exercise.id) }
                    )
                }
            } else {
                // Default exercises
                ExerciseFlowCard("Jumping Jacks", "Cardio Warmup", "30 sec • 15s rest after", Icons.Rounded.Speed, index = 0, onClick = { onExerciseClick("ex_jumping_jacks") })
                ExerciseFlowCard("Squats", "Lower Body", "40 sec • 15s rest after", Icons.Rounded.FitnessCenter, index = 1, onClick = { onExerciseClick("ex_squats") })
                ExerciseFlowCard("Push-ups", "Upper Body", "30 sec • 15s rest after", Icons.Rounded.SelfImprovement, index = 2, onClick = { onExerciseClick("ex_push_ups") })
                ExerciseFlowCard("Lunges", "Legs & Balance", "30 sec • 15s rest after", Icons.Rounded.DirectionsRun, index = 3, onClick = { onExerciseClick("ex_lunges") })
                ExerciseFlowCard("Plank", "Core Stability", "30 sec • 15s rest after", Icons.Rounded.Bolt, index = 4, onClick = { onExerciseClick("ex_plank") })
                ExerciseFlowCard("Mountain Climbers", "Cardio & Core", "30 sec • Final stretch", Icons.Rounded.LocalFireDepartment, index = 5, onClick = { onExerciseClick("ex_mountain_climbers") })
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SpecBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = IconWell,
        border = BorderStroke(1.dp, BorderInactive)
    ) {
        Text(
            text = text,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMain,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ExerciseFlowCard(
    title: String,
    category: String,
    timing: String,
    icon: ImageVector,
    index: Int = 0,
    onClick: () -> Unit = {}
) {
    val haptics = rememberFit10Haptics()
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardSurface,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { haptics.perform(HapticKind.Tick); onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numbered gradient circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(BrandStrong, BrandPrimary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = OnBrand,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = TextMain
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(BrandContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 9.5.sp,
                            color = BrandDeep,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timing,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Fit10Theme.extendedColors.timerContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "▶ Motion Guide",
                            fontSize = 9.sp,
                            color = Fit10Theme.extendedColors.timer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Swap button
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BrandContainer)
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.SwapHoriz,
                    contentDescription = "Replace",
                    tint = BrandPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun getExerciseIcon(name: String): ImageVector {
    val lower = name.lowercase()
    return when {
        lower.contains("squat") || lower.contains("push") -> Icons.Rounded.FitnessCenter
        lower.contains("plank") || lower.contains("core") -> Icons.Rounded.Bolt
        lower.contains("jump") || lower.contains("run") -> Icons.Rounded.Speed
        else -> Icons.Rounded.DirectionsRun
    }
}
