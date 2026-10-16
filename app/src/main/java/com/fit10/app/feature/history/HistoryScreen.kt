package com.fit10.app.feature.history

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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fit10.app.R

private val PrimaryGreen      = Color(0xFF10B981)
private val VoltGreen         = Color(0xFF00E676)
private val DarkGreen         = Color(0xFF047857)
private val DeepForestGreen   = Color(0xFF064E3B)
private val LightGreenSurface = Color(0xFFECFDF5)
private val BackgroundSurface = Color(0xFFF8FAF9)
private val BorderInactive    = Color(0xFFE2E8F0)
private val TextMain          = Color(0xFF0F172A)
private val TextMuted         = Color(0xFF64748B)
private val CardWhite         = Color(0xFFFFFFFF)

private val BarGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF00E676), Color(0xFF10B981))
)

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onExploreWorkouts: () -> Unit = {},
    onPreviewWorkout: (String) -> Unit = {}
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = BackgroundSurface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // 1. Top Header with Logo, Notification Bell, and Maya Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightGreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FitnessCenter,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Fit10",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, BorderInactive, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = "Notifications",
                            tint = TextMain,
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Image(
                        painter = painterResource(id = R.drawable.coach_maya),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, PrimaryGreen, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Hero Header
            Text(
                text = "Your Consistency Pays Off",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You're compounding 10 minutes every day into lifelong fitness. Keep the momentum high.",
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Top 4 Stats Grid (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Streak
                StatTile(
                    icon = Icons.Rounded.LocalFireDepartment,
                    iconTint = Color(0xFFF97316),
                    iconBg = Color(0xFFFFF7ED),
                    badge = "+2 d",
                    badgeColor = LightGreenSurface,
                    badgeText = DarkGreen,
                    value = "7 Days",
                    label = "Current Streak",
                    modifier = Modifier.weight(1f)
                )

                // Card 2: Total Workouts
                StatTile(
                    icon = Icons.Rounded.FitnessCenter,
                    iconTint = PrimaryGreen,
                    iconBg = LightGreenSurface,
                    badge = "Jan '25",
                    badgeColor = Color(0xFFF1F5F9),
                    badgeText = TextMuted,
                    value = "28",
                    label = "Total Workouts",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 3: Active Duration
                StatTile(
                    icon = Icons.Rounded.Schedule,
                    iconTint = Color(0xFF0284C7),
                    iconBg = Color(0xFFF0F9FF),
                    badge = "4.6 hrs",
                    badgeColor = Color(0xFFF1F5F9),
                    badgeText = TextMuted,
                    value = "280 min",
                    label = "Active Duration",
                    modifier = Modifier.weight(1f)
                )

                // Card 4: Calories Burned
                StatTile(
                    icon = Icons.Rounded.Bolt,
                    iconTint = PrimaryGreen,
                    iconBg = LightGreenSurface,
                    badge = "Top 8%",
                    badgeColor = LightGreenSurface,
                    badgeText = DarkGreen,
                    value = "2,240",
                    label = "Calories Burned",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Workouts This Week Card with Bar Chart
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CardWhite,
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Workouts This Week",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextMain
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Daily commitment to micro-sets",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LightGreenSurface,
                            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "✓ 4 of 4 Met",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 7-day Bar Chart
                    val dayData = listOf(
                        Triple("M", 75, true),
                        Triple("T", 14, false), // Rest
                        Triple("W", 100, true),
                        Triple("T", 65, true),
                        Triple("F", 85, true), // Today with 10m tag
                        Triple("S", 14, false),
                        Triple("S", 14, false)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dayData.forEachIndexed { index, (day, heightPercent, isActive) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (index == 4) { // Friday with 10m tag
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = LightGreenSurface,
                                        border = BorderStroke(0.5.dp, PrimaryGreen.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = "10m",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkGreen,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height((heightPercent * 0.9).dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isActive) BarGradient
                                            else Brush.verticalGradient(listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0)))
                                        )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = day,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isActive) TextMain else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

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
                                    .background(PrimaryGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daily Target: 10 min",
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
                        }

                        Text(
                            text = "Details ›",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGreen,
                            modifier = Modifier.clickable { onExploreWorkouts() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Habit Stability Score Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Donut circular progress chart
                    Box(
                        modifier = Modifier.size(68.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { 0.94f },
                            modifier = Modifier.fillMaxSize(),
                            color = PrimaryGreen,
                            trackColor = Color(0xFFE2E8F0),
                            strokeWidth = 6.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "94%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextMain
                            )
                            Text(
                                text = "SCORE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "HABIT STABILITY",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = DarkGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Outstanding Performance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp,
                            color = TextMain
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "You have finished 12 micro-workouts in the last 14 days without friction.",
                            fontSize = 11.5.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 6. Recent Milestones Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Milestones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextMain
                    )
                    Text(
                        text = "Badges earned through consistency",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                }

                Text(
                    text = "See All (9)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Milestone 1: 7-Day Streak
                    MilestoneRow(
                        icon = Icons.Rounded.Shield,
                        iconTint = PrimaryGreen,
                        iconBg = LightGreenSurface,
                        title = "7-Day Streak",
                        badge = "New",
                        subtitle = "Completed 7 consecutive days of mo...",
                        date = "Today"
                    )

                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Milestone 2: 25 Workouts Club
                    MilestoneRow(
                        icon = Icons.Rounded.EmojiEvents,
                        iconTint = Color(0xFFF59E0B),
                        iconBg = Color(0xFFFEF3C7),
                        title = "25 Workouts Club",
                        subtitle = "Quarter-century milestone achieved",
                        date = "Jan 10"
                    )

                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Milestone 3: 5 Hours Completed
                    MilestoneRow(
                        icon = Icons.Rounded.Schedule,
                        iconTint = Color(0xFF0284C7),
                        iconBg = Color(0xFFF0F9FF),
                        title = "5 Hours Completed",
                        subtitle = "Accumulated 300+ active minutes",
                        date = "Jan 8"
                    )

                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                    // Milestone 4: 30-Day Habit Master with Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "30-Day Habit Master",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp,
                                    color = TextMain
                                )
                                Text(
                                    text = "24/30 days",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { 24f / 30f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PrimaryGreen,
                                trackColor = Color(0xFFE2E8F0),
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Daily Protocol Banner (Dark Forest Green)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepForestGreen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DAILY PROTOCOL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Ready for Saturday's 10m?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Upper Body Flush scheduled for tomorrow morning.",
                            fontSize = 11.5.sp,
                            color = Color(0xFFA7F3D0),
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        modifier = Modifier.clickable { onPreviewWorkout("full_body_ignition") }
                    ) {
                        Text(
                            text = "Preview →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepForestGreen,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatTile(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    badge: String,
    badgeColor: Color,
    badgeText: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderInactive),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextMain
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 11.5.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun MilestoneRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    date: String,
    badge: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = TextMain
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = LightGreenSurface,
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = TextMuted
            )
        }

        Text(
            text = date,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted
        )
    }
}
