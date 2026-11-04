package com.fit10.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.ui.components.GroupedList
import com.fit10.app.core.ui.components.LargeTitle
import com.fit10.app.core.ui.components.ListRow
import com.fit10.app.core.ui.components.SectionLabel
import com.fit10.app.core.ui.components.UserAvatar
import com.fit10.app.core.ui.theme.ThemeMode

@Composable
fun ProfileSettingsScreen(
    isPro: Boolean = false,
    userName: String = "",
    streakDays: Int = 0,
    sessions: Int = 0,
    minutes: Int = 0,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    hapticsEnabled: Boolean = true,
    onHapticsEnabledChange: (Boolean) -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val haptics = rememberFit10Haptics()
    var showTheme by remember { mutableStateOf(false) }
    var showSafety by remember { mutableStateOf(false) }

    if (showTheme) {
        AlertDialog(
            onDismissRequest = { showTheme = false },
            title = { Text("Appearance") },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                .clickable { haptics.perform(HapticKind.Tick); onThemeModeChange(mode); showTheme = false }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = mode == themeMode, onClick = null)
                            Spacer(Modifier.width(10.dp))
                            Text(mode.label(), fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showTheme = false }) { Text("Done") } }
        )
    }
    if (showSafety) {
        AlertDialog(
            onDismissRequest = { showSafety = false },
            title = { Text("Safety first") },
            text = {
                Text(
                    "Fit10 Mom is a fitness app, not medical advice. Start only when your doctor says it's okay. " +
                        "Stop if you feel pain, pressure in your pelvis, leaking or bleeding, and talk to your doctor or a women's health physiotherapist."
                )
            },
            confirmButton = { TextButton(onClick = { showSafety = false }) { Text("Got it") } }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        LargeTitle("Profile")
        GroupedList {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(Modifier.size(60.dp), userName)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(userName.ifBlank { "You" }, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "$sessions sessions · $minutes min · $streakDays-day streak",
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        SectionLabel("Plan")
        GroupedList {
            ListRow(Icons.Rounded.Star, "Fit10 Pro", if (isPro) "Active" else "Free", showDivider = false, onClick = onNavigateToSubscription)
        }

        SectionLabel("Preferences")
        GroupedList {
            ListRow(Icons.Rounded.Notifications, "Reminders", onClick = onNavigateToReminders)
            ListRow(Icons.Rounded.DarkMode, "Appearance", themeMode.label(), onClick = { showTheme = true })
            ListRow(
                Icons.Rounded.Vibration, "Haptics", showDivider = false,
                trailing = {
                    Switch(checked = hapticsEnabled, onCheckedChange = {
                        onHapticsEnabledChange(it)
                        if (it) haptics.perform(HapticKind.Tick)
                    })
                }
            )
        }

        SectionLabel("About")
        GroupedList {
            ListRow(Icons.Rounded.Lock, "Privacy and your data", onClick = onNavigateToPrivacy)
            ListRow(Icons.Rounded.HealthAndSafety, "Safety information", showDivider = false, onClick = { showSafety = true })
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "Your data stays on this phone.",
            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

private fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> "Same as phone"
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
}
