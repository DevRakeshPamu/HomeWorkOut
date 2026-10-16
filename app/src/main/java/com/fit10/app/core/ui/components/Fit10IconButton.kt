package com.fit10.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fit10.app.core.ui.theme.Fit10Theme

/**
 * Standard athletic icon button.
 */
@Composable
fun Fit10IconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
) {
    IconButton(
        onClick  = onClick,
        modifier = modifier.size(size),
        enabled  = enabled,
        colors   = IconButtonDefaults.iconButtonColors(
            contentColor         = tint,
            disabledContentColor = tint.copy(alpha = 0.38f),
        ),
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = contentDescription,
            modifier           = Modifier.size(iconSize),
        )
    }
}

/**
 * Filled tonal athletic icon button.
 */
@Composable
fun Fit10FilledIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
) {
    Surface(
        onClick  = onClick,
        modifier = modifier.size(size),
        enabled  = enabled,
        shape    = CircleShape,
        color    = containerColor,
        border   = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector        = icon,
                contentDescription = contentDescription,
                tint               = contentColor,
                modifier           = Modifier.size(iconSize),
            )
        }
    }
}

/**
 * Hero large circular action icon button with shadow, perfect for workout player controls.
 */
@Composable
fun Fit10HeroIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    size: Dp = 72.dp,
    iconSize: Dp = 36.dp,
) {
    Surface(
        onClick         = onClick,
        modifier        = modifier
            .size(size)
            .shadow(8.dp, CircleShape),
        enabled         = enabled,
        shape           = CircleShape,
        color           = containerColor,
        tonalElevation  = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector        = icon,
                contentDescription = contentDescription,
                tint               = contentColor,
                modifier           = Modifier.size(iconSize),
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAF9)
@Composable
private fun Fit10IconButtonPreview() {
    Fit10Theme {
        Row(modifier = Modifier.padding(16.dp)) {
            Fit10HeroIconButton(
                icon = Icons.Rounded.PlayArrow,
                contentDescription = "Play",
                onClick = {},
            )
        }
    }
}
