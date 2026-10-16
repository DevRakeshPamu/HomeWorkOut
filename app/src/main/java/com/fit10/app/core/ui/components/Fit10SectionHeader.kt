package com.fit10.app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit10.app.core.ui.theme.Fit10Theme

/**
 * Modern section title row with an optional "See all" action.
 */
@Composable
fun Fit10SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier            = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment   = Alignment.CenterVertically,
    ) {
        Text(
            text       = title,
            style      = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color      = MaterialTheme.colorScheme.onBackground,
        )
        if (actionText != null && onActionClick != null) {
            Text(
                text     = actionText,
                style    = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color    = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onActionClick)
                    .padding(vertical = 4.dp, horizontal = 4.dp),
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAF9, widthDp = 360)
@Composable
private fun Fit10SectionHeaderPreview() {
    Fit10Theme {
        Fit10SectionHeader(
            title         = "Explore Workouts",
            actionText    = "See All",
            onActionClick = {},
        )
    }
}
