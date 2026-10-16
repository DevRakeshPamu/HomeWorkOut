package com.fit10.app.feature.reminders

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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R

private val PrimaryGreen = Color(0xFF00C070)
private val DarkGreen = Color(0xFF065F46)
private val LightGreenSurface = Color(0xFFE8F7F0)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextMain = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onCancel: () -> Unit = onBack,
    onTestNotification: () -> Unit = {}
) {
    var remindersEnabled by remember { mutableStateOf(true) }
    var selectedHour by remember { mutableIntStateOf(19) } // 7:00 PM
    var selectedMinute by remember { mutableIntStateOf(0) }
    var selectedDays by remember { mutableStateOf(setOf(1, 3, 5, 6)) } // Mon, Wed, Fri, Sat
    var selectedMessageIndex by remember { mutableIntStateOf(0) }

    val reminderMessages = listOf(
        "\"10 minutes for yourself?\"",
        "\"Your daily streak is waiting!\"",
        "\"Quick micro-burst before dinner.\""
    )

    val daysOfWeek = listOf(
        1 to "M",
        2 to "T",
        3 to "W",
        4 to "T",
        5 to "F",
        6 to "S",
        7 to "S"
    )

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
                    Text(
                        text = "Daily Reminder",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.coach_maya),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, PrimaryGreen, CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onSave,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Save reminders",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 13.sp,
                            color = TextMuted,
                            modifier = Modifier
                                .clickable(onClick = onCancel)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        Text(text = "•", color = TextMuted, fontSize = 12.sp)
                        Text(
                            text = "Test notification",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryGreen,
                            modifier = Modifier
                                .clickable(onClick = onTestNotification)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
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
            Spacer(Modifier.height(10.dp))

            // 1. Micro-Habit Engine Header Banner
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = LightGreenSurface,
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = null,
                            tint = DarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "MICRO-HABIT ENGINE",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "Small daily prompt, huge compounding fitness.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Never miss your 10 minutes. Build unstoppable consistency with gentle reminders.",
                            fontSize = 12.sp,
                            color = DarkGreen.copy(alpha = 0.85f),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 2. Android Permission Status Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
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
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Android precise reminders",
                            fontSize = 12.5.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(LightGreenSurface)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Allowed ✓",
                            color = DarkGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 3. Enable Reminders Toggle Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = null,
                                tint = TextMain,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Enable reminders",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Receive a nudge on scheduled training days",
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Switch(
                        checked = remindersEnabled,
                        onCheckedChange = { remindersEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // 4. Reminder Time Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Notifications,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "REMINDER TIME",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Text(
                            text = "Evening Wind-Down",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "7:00 PM",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )

                        OutlinedButton(
                            onClick = {
                                // Toggle demo time
                                if (selectedHour == 19) selectedHour = 8 else selectedHour = 19
                            },
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, CardBorderColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = TextMuted
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Change",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // 5. Repeat on Workout Days Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REPEAT ON WORKOUT DAYS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.8.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${selectedDays.size} active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysOfWeek.forEach { (dayInt, label) ->
                            val isSelected = selectedDays.contains(dayInt)
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) PrimaryGreen else Color(0xFFF1F5F9))
                                    .clickable {
                                        selectedDays = if (isSelected) {
                                            selectedDays - dayInt
                                        } else {
                                            selectedDays + dayInt
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Matches your current 4x / week plan (Mon, Wed, Fri, Sat)",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // 6. Reminder Message Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REMINDER MESSAGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.8.sp
                        )
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    reminderMessages.forEachIndexed { index, message ->
                        val isSelected = selectedMessageIndex == index
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) LightGreenSurface.copy(alpha = 0.5f) else Color.Transparent)
                                .clickable { selectedMessageIndex = index }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedMessageIndex = index },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = PrimaryGreen,
                                    unselectedColor = Color(0xFFCBD5E1)
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = message,
                                fontSize = 13.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextMain else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}
