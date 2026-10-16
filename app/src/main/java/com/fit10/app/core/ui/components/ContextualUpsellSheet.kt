package com.fit10.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.domain.billing.ProFeature

private val PrimaryGreen = Color(0xFF00C070)
private val DarkGreen = Color(0xFF065F46)
private val LightGreenSurface = Color(0xFFE8F7F0)
private val TextMain = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContextualUpsellSheet(
    feature: ProFeature,
    onDismiss: () -> Unit,
    onNavigateToPaywall: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val (title, description) = when (feature) {
        ProFeature.FULL_PROGRAMS -> Pair(
            "Unlock Full Program Tracks",
            "You've completed the free 3-day preview! Fit10 Pro gives you full access to all multi-week guided progressive tracks."
        )
        ProFeature.FULL_EXERCISE_REGRESSION -> Pair(
            "Joint-Friendly Regression Engine",
            "Customize and swap any exercise across your routines with targeted low-impact joint regressions designed for your body."
        )
        ProFeature.ADAPTIVE_DIFFICULTY -> Pair(
            "Adaptive Difficulty Between Sessions",
            "Automatically calibrate rep pacing and exercise duration between sessions based on your consistency and feedback."
        )
        ProFeature.ADVANCED_ANALYTICS -> Pair(
            "Muscle Balance & Volume Insights",
            "Access comprehensive breakdown charts tracking weekly muscle volume distribution, recovery balance, and exertion trends."
        )
        ProFeature.STREAK_REPAIR -> Pair(
            "Streak Freeze & Repair",
            "Never lose your hard-earned progress. Fit10 Pro preserves and restores your consistency streaks."
        )
        ProFeature.WORKOUT_VARIANTS -> Pair(
            "5 & 15-Minute Workout Variants",
            "Short on time or ready for more? Fit10 Pro adds 5-minute micro bursts and 15-minute extended challenge routines."
        )
        else -> Pair(
            "Unlock Fit10 Pro",
            "Take your daily 10-minute fitness journey to the next level with full personalization and unlimited access."
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(LightGreenSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Locked Feature",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(LightGreenSurface)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "FIT10 PRO FEATURE",
                    color = DarkGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onDismiss()
                    onNavigateToPaywall()
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Upgrade to Fit10 Pro",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text(
                    text = "Maybe Later",
                    color = TextMuted,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
