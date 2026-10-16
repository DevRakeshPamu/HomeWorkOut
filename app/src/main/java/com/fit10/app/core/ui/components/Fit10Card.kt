package com.fit10.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fit10.app.core.ui.theme.Fit10Shapes
import com.fit10.app.core.ui.theme.Fit10Theme

// ── Base Modern Athletic Card ───────────────────────────────────────────────

/**
 * Modern athletic card with 20dp rounded corners, subtle hairline border,
 * and high-contrast surface styling.
 */
@Composable
fun Fit10Card(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = Fit10Shapes.card,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
    contentPadding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Surface(
            onClick         = onClick,
            modifier        = modifier.fillMaxWidth(),
            shape           = shape,
            color           = containerColor,
            border          = border,
            tonalElevation  = 1.dp,
            shadowElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content  = content,
            )
        }
    } else {
        Surface(
            modifier        = modifier.fillMaxWidth(),
            shape           = shape,
            color           = containerColor,
            border          = border,
            tonalElevation  = 1.dp,
            shadowElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content  = content,
            )
        }
    }
}

// ── Hero Card with Subtle Gradient / Athletic Highlight ─────────────────────

/**
 * Premium Hero Card for flagship content (e.g. Today's Workout).
 */
@Composable
fun Fit10HeroCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = Fit10Shapes.cardLarge,
    border: BorderStroke? = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
    contentPadding: Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        onClick         = onClick ?: {},
        enabled         = onClick != null,
        modifier        = modifier.fillMaxWidth(),
        shape           = shape,
        color           = MaterialTheme.colorScheme.surface,
        border          = border,
        tonalElevation  = 2.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content  = content,
        )
    }
}

// ── Selection Card ──────────────────────────────────────────────────────────

/**
 * Modern selectable card with animated border and check icon.
 */
@Composable
fun Fit10SelectionCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = Fit10Shapes.card,
    content: @Composable ColumnScope.() -> Unit,
) {
    val borderColor by animateColorAsState(
        targetValue    = if (selected) MaterialTheme.colorScheme.primary
                         else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
        animationSpec  = tween(200),
        label          = "selectionBorderColor",
    )
    val containerColor by animateColorAsState(
        targetValue   = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label         = "selectionContainerColor",
    )
    val checkColor by animateColorAsState(
        targetValue   = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(200),
        label         = "checkColor",
    )

    Surface(
        onClick         = onClick,
        modifier        = modifier.fillMaxWidth(),
        enabled         = enabled,
        shape           = shape,
        color           = containerColor,
        border          = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = borderColor,
        ),
        tonalElevation  = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Box {
            Column(
                modifier = Modifier.padding(18.dp),
                content  = content,
            )
            Icon(
                imageVector        = Icons.Rounded.CheckCircle,
                contentDescription = if (selected) "Selected" else null,
                tint               = checkColor,
                modifier           = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .size(22.dp),
            )
        }
    }
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAF9, widthDp = 360)
@Composable
private fun Fit10CardPreview() {
    Fit10Theme {
        Fit10Card {
            Text("Upper Body Blast", style = MaterialTheme.typography.titleMedium)
            Text(
                "10 min · 10 exercises · Intermediate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
