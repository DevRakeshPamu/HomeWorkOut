package com.fit10.app.feature.exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R
import com.fit10.app.core.ui.components.AnimatedExerciseDemo
import com.fit10.app.core.ui.components.ExerciseMotionGuide
import com.fit10.app.core.ui.components.ExerciseVideoPlayer
import com.fit10.app.core.ui.components.ExerciseVideoPlayerSheet
import com.fit10.app.data.seed.ExerciseCatalog
import com.fit10.app.domain.model.Exercise

private val PrimaryGreen = Color(0xFF10B981)
private val VoltGreen = Color(0xFF00F59B)
private val DarkGreen = Color(0xFF047857)
private val LightGreenSurface = Color(0xFFECFDF5)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextMain = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: String = "push_ups",
    onBack: () -> Unit = {},
    onReplace: () -> Unit = {},
    onGotIt: () -> Unit = {}
) {
    var isBookmarked by remember { mutableStateOf(false) }
    var showMotionGuide by remember { mutableStateOf(false) }
    var showVideoPlayerSheet by remember { mutableStateOf(false) }

    // Resolve dynamic exercise model from catalog
    val exercise = remember(exerciseId) {
        val clean = exerciseId.lowercase()
        ExerciseCatalog.ALL.find { ex ->
            ex.id.equals(clean, ignoreCase = true) ||
            ex.id.removePrefix("ex_").equals(clean.removePrefix("ex_"), ignoreCase = true) ||
            clean.contains(ex.name.lowercase()) ||
            ex.name.lowercase().contains(clean)
        } ?: ExerciseCatalog.PUSH_UPS
    }

    val coachImageRes = remember(exercise.id) {
        when {
            "push" in exercise.id -> R.drawable.coach_pushup
            "squat" in exercise.id -> R.drawable.coach_squat
            else -> R.drawable.coach_maya
        }
    }

    // Modal Video Player Sheet
    if (showVideoPlayerSheet) {
        ExerciseVideoPlayerSheet(
            exercise = exercise,
            onDismiss = { showVideoPlayerSheet = false }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAF9),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8FAF9)),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextMain
                        )
                    }
                },
                title = {
                    // Small header badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(LightGreenSurface)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Form Masterclass",
                            color = DarkGreen,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isBookmarked = !isBookmarked }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) PrimaryGreen else TextMain
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = TextMain
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onReplace,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, CardBorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = TextMain
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Replace",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = onGotIt,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Got It",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            // Mode Selector: Real Model Motion vs Kinetic Skeleton
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE2E8F0))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Real Model Video Tab (Default)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(if (!showMotionGuide) PrimaryGreen else Color.Transparent)
                        .clickable { showMotionGuide = false }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Videocam,
                            contentDescription = null,
                            tint = if (!showMotionGuide) Color.White else TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "🎬 Real Video Demo",
                            color = if (!showMotionGuide) Color.White else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Kinetic Skeleton Guide Tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(if (showMotionGuide) PrimaryGreen else Color.Transparent)
                        .clickable { showMotionGuide = true }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Animation,
                            contentDescription = null,
                            tint = if (showMotionGuide) Color.White else TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "⚡ Kinetic Skeleton",
                            color = if (showMotionGuide) Color.White else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // 1. Hero Demonstration Card (Real Model Video or Skeleton Guide)
            if (showMotionGuide) {
                // Live Biomechanical Motion Guide
                ExerciseMotionGuide(
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    showControls = true,
                    onOpenVideoModal = { showVideoPlayerSheet = true }
                )
            } else {
                // Real Model HD Video Player
                ExerciseVideoPlayer(
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    coachCue = exercise.tips.firstOrNull() ?: "Maintain strict posture and controlled tempo.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(235.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // 2. 4-Grid Spec Cards (2x2)
            val primaryTarget = exercise.targetMuscles.firstOrNull() ?: exercise.focusArea.name.replace("_", " ")
            val secondaryTarget = exercise.targetMuscles.drop(1).take(2).joinToString(", ").ifEmpty { "Core Stability" }
            val difficultyLevel = exercise.impactLevel.name.lowercase().replaceFirstChar { it.uppercase() }
            val equipmentName = exercise.equipmentRequired.name.lowercase().replaceFirstChar { it.uppercase() }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SpecCard(
                    icon = Icons.Rounded.FitnessCenter,
                    label = "Primary Target",
                    value = primaryTarget,
                    subValue = secondaryTarget,
                    modifier = Modifier.weight(1f)
                )
                SpecCard(
                    icon = Icons.Rounded.Speed,
                    label = "Difficulty",
                    value = difficultyLevel,
                    subValue = "Scale as needed",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SpecCard(
                    icon = Icons.Rounded.Security,
                    label = "Equipment",
                    value = equipmentName,
                    subValue = "Mat Optional",
                    modifier = Modifier.weight(1f)
                )
                SpecCard(
                    icon = Icons.Rounded.Timer,
                    label = "Interval Goal",
                    value = "${exercise.defaultDurationSeconds} sec",
                    subValue = "High Intensity",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // 3. Joint Alignment Coach Alert Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = LightGreenSurface,
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.25f)),
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
                            .background(PrimaryGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Security,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Joint Alignment Coach",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                        Text(
                            text = exercise.tips.firstOrNull() ?: "Maintain neutral posture to protect spine and joints",
                            fontSize = 12.sp,
                            color = DarkGreen.copy(alpha = 0.85f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 4. How to do it Section (Dynamic Steps)
            val instructions = exercise.instructions.ifEmpty {
                listOf(
                    "Set up in proper alignment with core engaged.",
                    "Initiate movement with smooth tempo and controlled breathing.",
                    "Drive through the target muscles to return to start."
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "How to do it",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${instructions.size} STEPS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    instructions.forEachIndexed { index, step ->
                        StepItem(
                            number = index + 1,
                            title = "Step ${index + 1}",
                            instruction = step
                        )
                        if (index < instructions.size - 1) {
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 5. Form Tips & Mistakes Section
            Text(
                text = "Form Tips & Mistakes",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )

            Spacer(Modifier.height(12.dp))

            // Optimal Cue Card (Green)
            val optimalTip = exercise.tips.firstOrNull() ?: "Keep core engaged and breathe smoothly throughout."
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(LightGreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "OPTIMAL CUE",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Peak Muscle Engagement",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = optimalTip,
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Common Flaw Card (Red)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFFCA5A5).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "COMMON FLAW",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Loss of Core & Postural Tension",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Rushing repetitions or letting joints compromise alignment dumps strain onto ligaments. Controlled tempo delivers 2x greater muscular adaptation.",
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SpecCard(
    icon: ImageVector,
    label: String,
    value: String,
    subValue: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        border = BorderStroke(1.dp, CardBorderColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Text(
                text = subValue,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun StepItem(
    number: Int,
    title: String,
    instruction: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(LightGreenSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = instruction,
                fontSize = 12.5.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )
        }
    }
}
