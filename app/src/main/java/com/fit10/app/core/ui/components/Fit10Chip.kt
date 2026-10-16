package com.fit10.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.feedback.HapticKind
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit10.app.core.ui.theme.Fit10Shapes
import com.fit10.app.core.ui.theme.Fit10Theme

/**
 * Modern athletic filter chip for workout categories and preferences.
 */
@Composable
fun Fit10Chip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    val haptics = rememberFit10Haptics()
    FilterChip(
        selected  = selected,
        onClick   = { haptics.perform(HapticKind.Tick); onClick() },
        label     = {
            Text(
                text       = text,
                style      = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                ),
            )
        },
        modifier  = modifier,
        enabled   = enabled,
        shape     = Fit10Shapes.chip,
        leadingIcon = {
            if (selected) {
                Icon(
                    imageVector        = Icons.Rounded.Check,
                    contentDescription = "Selected",
                )
            } else if (leadingIcon != null) {
                Icon(
                    imageVector        = leadingIcon,
                    contentDescription = null,
                )
            }
        },
        colors    = FilterChipDefaults.filterChipColors(
            selectedContainerColor     = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor         = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor   = MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor             = MaterialTheme.colorScheme.surface,
            labelColor                 = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        border    = FilterChipDefaults.filterChipBorder(
            enabled             = enabled,
            selected            = selected,
            borderColor         = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            borderWidth         = 1.dp,
            selectedBorderWidth = 1.5.dp,
        ),
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAF9)
@Composable
private fun Fit10ChipPreview() {
    Fit10Theme {
        Row(
            modifier              = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            var selected by remember { mutableStateOf(true) }
            Fit10Chip(
                text     = "Full Body",
                selected = selected,
                onClick  = { selected = !selected },
            )
            Fit10Chip(
                text     = "Core",
                selected = false,
                onClick  = {},
            )
        }
    }
}
