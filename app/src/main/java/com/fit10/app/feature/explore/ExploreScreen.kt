package com.fit10.app.feature.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.R
import com.fit10.app.core.ui.components.Fit10Chip
import com.fit10.app.core.ui.components.Fit10TopBar
import com.fit10.app.core.ui.theme.Fit10Colors
import com.fit10.app.core.ui.theme.Fit10Shapes
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.Workout

private val PrimaryGreen      = Color(0xFF10B981)
private val DarkGreen         = Color(0xFF047857)
private val LightGreenSurface = Color(0xFFECFDF5)
private val BorderInactive    = Color(0xFFE2E8F0)
private val TextMain          = Color(0xFF0F172A)
private val TextMuted         = Color(0xFF64748B)
private val CardWhite         = Color(0xFFFFFFFF)

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onSelectWorkout: (workoutId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFFF8FAF9),
        topBar = {
            Fit10TopBar(title = "Explore Workouts")
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── Featured Banner ──────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.splash_bg),
                        contentDescription = "Featured",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xCC0F172A)),
                                    startY = 60f
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(PrimaryGreen)
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Rounded.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(text = "FEATURED", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(text = "Full Body Kickstart", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = "10 min • Beginner • No equipment", color = Color.White.copy(0.8f), fontSize = 12.sp)
                    }
                    // Play button overlay
                    Surface(
                        onClick = { state.filteredWorkouts.firstOrNull()?.let { onSelectWorkout(it.id) } },
                        shape = CircleShape,
                        color = PrimaryGreen,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }

            // ── Filter chips ─────────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Fit10Chip(text = "All", selected = state.selectedFilter == null, onClick = { viewModel.setFilter(null) })
                        FocusArea.entries.forEach { area ->
                            Fit10Chip(
                                text = area.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                selected = state.selectedFilter == area,
                                onClick = { viewModel.setFilter(area) }
                            )
                        }
                    }
                }
            }

            // ── Count label ──────────────────────────────────────────────────
            item {
                Text(
                    text = "${state.filteredWorkouts.size} routines crafted for 10-minute maximum impact",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // ── Workout list ─────────────────────────────────────────────────
            items(state.filteredWorkouts, key = { it.id }) { workout ->
                WorkoutListItemCard(
                    workout = workout,
                    onClick = { onSelectWorkout(workout.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun WorkoutListItemCard(
    workout: Workout,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Thumbnail image strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coach_squat),
                    contentDescription = workout.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient fade
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x880F172A)),
                                startY = 40f
                            )
                        )
                )
                // Focus area badge
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF059669), PrimaryGreen))
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = workout.focusArea.name.replace("_", " "),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Content area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workout.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextMain
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = workout.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 2,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Metadata row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetaPill(icon = Icons.Rounded.Schedule, text = "${workout.estimatedDurationMinutes}m", color = Fit10Colors.TimerCyan)
                        workout.caloriesBurnEstimate?.let { cal ->
                            MetaPill(icon = Icons.Rounded.LocalFireDepartment, text = "$cal kcal", color = Fit10Colors.CalorieCoral)
                        }
                        MetaPill(icon = Icons.Rounded.FitnessCenter, text = "${workout.exercises.size} moves", color = PrimaryGreen)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Play button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF059669), PrimaryGreen))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Start Workout",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
