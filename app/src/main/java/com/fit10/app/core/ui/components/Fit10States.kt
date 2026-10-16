package com.fit10.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit10.app.core.ui.theme.Fit10Theme

// ── Loading ───────────────────────────────────────────────────────────────────

/**
 * Full-area centered loading indicator.
 *
 * Use while async data is being fetched. Optionally show a descriptive [message].
 */
@Composable
fun Fit10LoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading…",
) {
    Box(
        modifier        = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(
                modifier  = Modifier.size(48.dp),
                color     = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text  = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Empty ─────────────────────────────────────────────────────────────────────

/**
 * Empty-state placeholder — shown when a list or section has no content.
 *
 * @param title       Short headline (e.g. "No workouts yet").
 * @param description Helpful sub-text guiding the user on next steps.
 * @param icon        Illustrative icon. Defaults to a neutral face.
 * @param actionText  Optional CTA label (e.g. "Browse workouts").
 * @param onActionClick CTA callback — required when [actionText] is non-null.
 */
@Composable
fun Fit10EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.SentimentNeutral,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            modifier           = Modifier.size(72.dp),
            tint               = MaterialTheme.colorScheme.outlineVariant,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text      = title,
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text      = description,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actionText != null && onActionClick != null) {
            Spacer(Modifier.height(28.dp))
            Fit10PrimaryButton(
                text    = actionText,
                onClick = onActionClick,
                modifier = Modifier.fillMaxWidth(0.75f),
            )
        }
    }
}

// ── Error ─────────────────────────────────────────────────────────────────────

/**
 * Error-state component — shown when a network or data failure occurs.
 *
 * Always provides a "Try again" action so the user has a clear recovery path.
 */
@Composable
fun Fit10ErrorState(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector        = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            modifier           = Modifier.size(72.dp),
            tint               = MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text      = title,
            style     = MaterialTheme.typography.titleMedium,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        Fit10PrimaryButton(
            text     = "Try again",
            onClick  = onRetry,
            modifier = Modifier.fillMaxWidth(0.75f),
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF5FCF7, widthDp = 360, heightDp = 400)
@Composable
private fun Fit10LoadingStatePreview() {
    Fit10Theme { Fit10LoadingState() }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5FCF7, widthDp = 360, heightDp = 500)
@Composable
private fun Fit10EmptyStatePreview() {
    Fit10Theme {
        Fit10EmptyState(
            title       = "No workouts yet",
            description = "Complete your first workout to start tracking your fitness journey.",
            icon        = Icons.Outlined.FitnessCenter,
            actionText  = "Browse workouts",
            onActionClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5FCF7, widthDp = 360, heightDp = 500)
@Composable
private fun Fit10ErrorStatePreview() {
    Fit10Theme {
        Fit10ErrorState(
            title   = "Something went wrong",
            message = "We couldn't load your workouts. Please check your connection and try again.",
            onRetry = {},
        )
    }
}
