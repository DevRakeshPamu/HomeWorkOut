package com.fit10.app.feature.profile

import androidx.compose.foundation.BorderStroke
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.ui.theme.ThemeMode
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.material3.RadioButton
import androidx.compose.material3.AlertDialog
import com.fit10.app.core.ui.theme.Fit10Theme
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R

private val BrandPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
private val BrandDeep: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
private val BrandContainer: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
private val BackgroundSurface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
private val BorderInactive: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant
private val TextMain: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
private val TextMuted: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val CardSurface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

@Composable
fun ProfileSettingsScreen(
    isPro: Boolean = false,
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
    var showThemeDialog by remember { mutableStateOf(false) }
    if (showThemeDialog) {
        ThemeModeDialog(
            current = themeMode,
            onSelect = { mode ->
                haptics.perform(HapticKind.Tick)
                onThemeModeChange(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
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

            // 1. Top Header
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
                            .background(BrandContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FitnessCenter,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Fit10",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandDeep
                    )
                }

                Text(
                    text = "PROFILE & SETTINGS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = BrandPrimary
                )

                Image(
                    painter = painterResource(id = R.drawable.coach_maya),
                    contentDescription = "Coach Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, BrandPrimary, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Profile Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.coach_maya),
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .border(2.dp, BrandPrimary, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rakesh Sharma",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextMain
                        )
                        Text(
                            text = "rakesh.sharma@example.com",
                            fontSize = 12.5.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Fit10 Pro Member",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandDeep
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit Profile",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Stats Row (14 Days Streak • 128 Sessions • 1,280 Minutes)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatColumn(value = "14 Days", label = "Streak")
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(BorderInactive)
                    )
                    StatColumn(value = "128", label = "Sessions")
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(BorderInactive)
                    )
                    StatColumn(value = "1,280", label = "Minutes")
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 4. Section: PREFERENCES
            SectionTitle(title = "PREFERENCES")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Rounded.Notifications,
                        title = "Notifications",
                        subtitle = "Daily reminders, workout alerts",
                        onClick = onNavigateToReminders
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Rounded.DarkMode,
                        title = "Appearance",
                        subtitle = themeMode.label(),
                        onClick = { showThemeDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    HapticsRow(
                        enabled = hapticsEnabled,
                        onToggle = { enabled ->
                            onHapticsEnabledChange(enabled)
                            // Confirm with a tick only when turning haptics on
                            if (enabled) haptics.perform(HapticKind.Tick)
                        }
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Rounded.Straighten,
                        title = "Units & Measurements",
                        subtitle = "Metric (kg, cm)",
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 5. Section: ACCOUNT & MEMBERSHIP
            SectionTitle(title = "ACCOUNT & MEMBERSHIP")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Rounded.Person,
                        title = "Account Details",
                        subtitle = "Manage email, password & devices",
                        onClick = {}
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Rounded.Verified,
                        title = "Subscription & Billing",
                        subtitle = if (isPro) "Fit10 Pro • Active Member" else "Fit10 Free Plan • Upgrade Available",
                        badge = if (isPro) "Active" else "Free",
                        onClick = onNavigateToSubscription
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Rounded.Security,
                        title = "Data & Privacy",
                        subtitle = "Permissions, data export, connected apps",
                        onClick = onNavigateToPrivacy
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Rounded.HelpOutline,
                        title = "Help & Support",
                        subtitle = "FAQs, contact coach, feedback",
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 6. Section: ABOUT & SAFETY
            SectionTitle(title = "ABOUT & SAFETY")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Rounded.StarRate,
                        title = "Rate Fit10 on Google Play",
                        subtitle = "Support our 10-minute movement mission",
                        isExternal = true,
                        onClick = {}
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Rounded.Lock,
                        title = "About Fit10",
                        subtitle = "Version 2.4.1 (Build 84)",
                        onClick = {}
                    )
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                    SettingsRow(
                        icon = Icons.Rounded.Logout,
                        title = "Sign Out",
                        subtitle = "Securely terminate current session",
                        isDestructive = true,
                        onClick = onSignOut
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 7. Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌱 Consistency over intensity.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandDeep
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Crafted for 10-minute micro-habits",
                    fontSize = 11.5.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextMuted,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
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

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    isExternal: Boolean = false,
    isDestructive: Boolean = false,
    iconBg: Color = MaterialTheme.colorScheme.surfaceVariant,
    iconTint: Color = BrandPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isDestructive) MaterialTheme.colorScheme.errorContainer else iconBg
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error else iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else TextMain
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BrandContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandDeep
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Icon(
            imageVector = if (isExternal) Icons.Rounded.OpenInNew else Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> "Same as phone"
    ThemeMode.LIGHT  -> "Light"
    ThemeMode.DARK   -> "Dark"
}

@Composable
private fun ThemeModeDialog(
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Appearance") },
        text = {
            Column {
                ThemeMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(mode) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = mode == current, onClick = { onSelect(mode) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = mode.label(), fontSize = 15.sp, color = TextMain)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
private fun HapticsRow(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!enabled) }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Vibration,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Haptics", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextMain)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Gentle vibrations on taps and timers", fontSize = 12.sp, color = TextMuted)
        }
        Switch(checked = enabled, onCheckedChange = onToggle)
    }
}
