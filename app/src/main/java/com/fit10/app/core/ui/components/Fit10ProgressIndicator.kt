package com.fit10.app.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fit10.app.core.ui.theme.Fit10Theme
import kotlin.math.roundToInt

/**
 * Modern animated athletic progress indicator with rounded pill track.
 */
@Composable
fun Fit10ProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
    showPercentage: Boolean = true,
    barHeight: Dp = 10.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue   = clamped,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label         = "progressAnimation",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null || showPercentage) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                if (label != null) {
                    Text(
                        text       = label,
                        style      = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color      = MaterialTheme.colorScheme.onSurface,
                    )
                }
                if (showPercentage) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text       = "${(animatedProgress * 100).roundToInt()}%",
                            style      = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color      = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(CircleShape)
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedProgress.coerceAtLeast(0.01f))
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAF9, widthDp = 360)
@Composable
private fun Fit10ProgressIndicatorPreview() {
    Fit10Theme {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Fit10ProgressIndicator(progress = 0.72f, label = "Program Progress")
            Fit10ProgressIndicator(progress = 0.4f,  label = "Exercise 4 of 10", showPercentage = false)
            Fit10ProgressIndicator(progress = 1f,    label = "Session Complete")
        }
    }
}
