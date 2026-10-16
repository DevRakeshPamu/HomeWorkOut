package com.fit10.app.feature.plan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R
import com.fit10.app.domain.model.UserProfile

private val PrimaryGreen = Color(0xFF10B981)
private val DarkGreen = Color(0xFF047857)
private val LightGreenSurface = Color(0xFFECFDF5)
private val BorderInactive = Color(0xFFE2E8F0)
private val TextMain = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

@Composable
fun PlanSummaryScreen(
    userProfile: UserProfile?,
    onStartWorkout: () -> Unit,
    onEditPlan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color(0xFFF8FAF9),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAF9))
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onStartWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen,
                        contentColor = Color.White
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Start Today's Workout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                TextButton(onClick = onEditPlan) {
                    Text(
                        text = "Edit Plan & Schedule",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
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
            Spacer(Modifier.height(16.dp))

            // 1. Top Badge: ✦ 100% PERSONALIZED • ADAPTIVE AI ENGINE ✦
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(50))
                    .background(LightGreenSurface)
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.3f), RoundedCornerShape(50))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✦ 100% PERSONALIZED • ADAPTIVE AI ENGINE ✦",
                        color = DarkGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 2. Headline & Subtitle
            Text(
                text = "Your personalized plan is ready",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Crafted specifically for your goals, schedule, and lifestyle.",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // 3. Protocol Card: Fit10 Daily Habit Protocol
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Fit10 Daily Habit Protocol",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextMain
                            )
                            Text(
                                text = "Tailored Baseline v1.0",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(LightGreenSurface)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "Active",
                                    color = DarkGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // 6 Grid Stats
                    val goalLabel = when (userProfile?.fitnessGoal?.name) {
                        "LOSE_WEIGHT" -> "Lose weight"
                        "BUILD_STRENGTH" -> "Build strength"
                        "FLEXIBILITY" -> "Flexibility"
                        "STAY_ACTIVE" -> "Stay active"
                        else -> "Improve fitness"
                    }
                    val levelLabel = when (userProfile?.fitnessLevel?.name) {
                        "ADVANCED" -> "Advanced"
                        "INTERMEDIATE" -> "Intermediate"
                        else -> "Beginner"
                    }
                    val durationLabel = "${userProfile?.targetWorkoutDurationMinutes ?: 10} minutes"
                    val daysTarget = "${userProfile?.workoutsPerWeek ?: 4}x / week"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProtocolStatBox("Goal", goalLabel, Modifier.weight(1f))
                        ProtocolStatBox("Level", levelLabel, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProtocolStatBox("Duration", durationLabel, Modifier.weight(1f))
                        ProtocolStatBox("Equipment", "Bodyweight", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProtocolStatBox("Avoid", "None", Modifier.weight(1f))
                        ProtocolStatBox("Target", daysTarget, Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // 4. Weekly Rhythm Section (Week 1)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Rhythm",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextMain
                        )
                        Text(
                            text = "Week 1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // 7 Days
                    val days = listOf(
                        Pair("Mon", "10m"),
                        Pair("Tue", "Rest"),
                        Pair("Wed", "10m"),
                        Pair("Thu", "Rest"),
                        Pair("Fri", "10m"),
                        Pair("Sat", "10m"),
                        Pair("Sun", "Rest")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEachIndexed { idx, day ->
                            val isWorkout = day.second != "Rest"
                            val isToday = idx == 0
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isToday) PrimaryGreen else if (isWorkout) LightGreenSurface else Color(0xFFF1F5F9))
                                    .padding(horizontal = 7.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = day.first,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) Color.White else if (isWorkout) DarkGreen else TextMuted
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = day.second,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isToday) Color.White else if (isWorkout) DarkGreen else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Today's Focus: Full Body Conditioning",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMain
                            )
                        }
                        Text(
                            text = "Day 1/28",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Commitment progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Weekly Commitment",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "40 mins total",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.25f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(PrimaryGreen)
                        )
                    }

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Just 40 total minutes per week to transform consistency.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // 5. HABIT SCIENCE Callout
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = LightGreenSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "HABIT SCIENCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "\"You're 3.8x more likely to stick with 10-minute micro-habits than exhausting 1-hour sessions.\"",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkGreen,
                            lineHeight = 17.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Source: Behavioral Habit Mechanics Lab",
                            fontSize = 10.5.sp,
                            color = DarkGreen.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 6. Coach Maya Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coach_maya),
                        contentDescription = "Coach Maya",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, PrimaryGreen, CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Coach Maya",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Text(
                            text = "\"Ready to jump in for day one? I'm with you!\"",
                            fontSize = 12.5.sp,
                            color = TextMuted
                        )
                    }
                    Icon(
                        imageVector = Icons.Rounded.ChatBubbleOutline,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProtocolStatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF8FAF9),
        border = BorderStroke(1.dp, BorderInactive),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
        }
    }
}
