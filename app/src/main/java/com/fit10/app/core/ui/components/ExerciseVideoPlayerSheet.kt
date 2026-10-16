package com.fit10.app.core.ui.components

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
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.domain.model.Exercise

private val SheetBackground = Color(0xFF0F172A)
private val PrimaryGreen = Color(0xFF10B981)
private val VoltGreen = Color(0xFF00F59B)
private val DarkCardSurface = Color(0xFF1E293B)
private val TextMuted = Color(0xFF94A3B8)
private val DangerRed = Color(0xFFEF4444)

/**
 * Fullscreen / Bottom sheet modal providing interactive HD workout video playback,
 * 0.5x slow-motion form breakdown, step-by-step cue guides, and common mistakes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseVideoPlayerSheet(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FORM MASTERCLASS & VIDEO",
                        color = VoltGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = exercise.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkCardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Embedded Video Player Section
            EmbeddedExerciseVideoPlayer(exercise = exercise)

            Spacer(Modifier.height(18.dp))

            // Tab Selector: Form Steps vs Mistakes & Tips
            var selectedTab by remember { mutableIntStateOf(0) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TabPill(
                    title = "Form Breakdown",
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabPill(
                    title = "Common Mistakes",
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Tab Content
            if (selectedTab == 0) {
                FormBreakdownContent(exercise = exercise)
            } else {
                CommonMistakesContent(exercise = exercise)
            }

            Spacer(Modifier.height(24.dp))

            // "Got it! Continue Workout" Action Button
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Got It • Continue Workout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Embedded media demonstration player with play/pause, scrub slider, and 0.5x slow-mo.
 */
@Composable
private fun EmbeddedExerciseVideoPlayer(exercise: Exercise) {
    var showBiomechanicalOverlay by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            if (showBiomechanicalOverlay) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Color(0xFF0F172A))
                ) {
                    ExerciseMotionGuide(
                        exerciseId = exercise.id,
                        exerciseName = exercise.name,
                        showControls = false,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                ExerciseVideoPlayer(
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    coachCue = exercise.tips.firstOrNull() ?: "Maintain strict tempo & full range of motion.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Toggle Biomechanical Kinetic Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (showBiomechanicalOverlay) VoltGreen else Color(0x33FFFFFF))
                    .clickable { showBiomechanicalOverlay = !showBiomechanicalOverlay }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (showBiomechanicalOverlay) "⚡ Kinetic Skeleton: ON" else "⚡ Kinetic Skeleton: OFF",
                    color = if (showBiomechanicalOverlay) Color.Black else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "HD Motion • Real Model",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Step-by-step coaching breakdown tab content.
 */
@Composable
private fun FormBreakdownContent(exercise: Exercise) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val steps = exercise.instructions.ifEmpty {
            listOf(
                "Stand tall with core braced and shoulders engaged.",
                "Initiate movement with controlled tempo, keeping joints aligned.",
                "Drive through the working muscles back to starting position."
            )
        }

        steps.forEachIndexed { index, step ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkCardSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = VoltGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = step,
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Breathing Rhythm Guidance
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F233B),
            border = BorderStroke(1.dp, Color(0xFF1E3A8A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Air,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "BREATHING RHYTHM",
                        color = Color(0xFF38BDF8),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Inhale on descent (eccentric) • Exhale forcefully on exertion (concentric)",
                        color = Color(0xFFE0F2FE),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * Common mistakes tab content.
 */
@Composable
private fun CommonMistakesContent(exercise: Exercise) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val tips = exercise.tips.ifEmpty {
            listOf("Maintain steady cadence", "Avoid hyperextending joints")
        }

        tips.forEach { tip ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkCardSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = VoltGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "PRO COACH TIP",
                            color = VoltGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = tip,
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Common Mistake Callout
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF2D1515),
            border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AVOID THIS MISTAKE",
                        color = DangerRed,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rushing repetitions or letting hips sag. Quality of movement always beats quantity for joint health and muscle stimulation.",
                        color = Color(0xFFFECACA),
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) PrimaryGreen else DarkCardSurface)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else TextMuted,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
