package com.fit10.app.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.ui.theme.Fit10Shapes
import com.fit10.app.core.ui.theme.Fit10Theme

// ── Primary Action Button ───────────────────────────────────────────────────

/**
 * Full-width athletic pill-shaped primary action button.
 */
@Composable
fun Fit10PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    Button(
        onClick         = onClick,
        modifier        = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled         = enabled,
        shape           = Fit10Shapes.button,
        contentPadding  = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        colors          = ButtonDefaults.buttonColors(
            containerColor         = MaterialTheme.colorScheme.primary,
            contentColor           = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor   = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        ),
        elevation       = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    imageVector        = leadingIcon,
                    contentDescription = null,
                    modifier           = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text          = text,
                style         = MaterialTheme.typography.labelLarge.copy(
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 0.3.sp,
                ),
            )
            if (trailingIcon != null) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector        = trailingIcon,
                    contentDescription = null,
                    modifier           = Modifier.size(22.dp),
                )
            }
        }
    }
}

// ── Secondary Action Button ─────────────────────────────────────────────────

/**
 * Full-width athletic tonal button with subtle border.
 */
@Composable
fun Fit10SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    FilledTonalButton(
        onClick         = onClick,
        modifier        = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled         = enabled,
        shape           = Fit10Shapes.button,
        contentPadding  = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        colors          = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
            contentColor   = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        border          = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                modifier           = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text  = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

// ── Outlined Button ─────────────────────────────────────────────────────────

/**
 * Full-width outlined button with refined pill geometry.
 */
@Composable
fun Fit10OutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    OutlinedButton(
        onClick        = onClick,
        modifier       = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled        = enabled,
        shape          = Fit10Shapes.button,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        colors         = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        border         = BorderStroke(
            1.5.dp,
            if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector        = leadingIcon,
                contentDescription = null,
                modifier           = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text  = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF8FAF9, widthDp = 360)
@Composable
private fun Fit10ButtonsPreview() {
    Fit10Theme {
        Fit10PrimaryButton(
            text        = "Start Workout",
            onClick     = {},
            leadingIcon = Icons.Rounded.PlayArrow,
        )
    }
}
