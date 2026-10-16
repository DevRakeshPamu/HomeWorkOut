package com.fit10.app.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Modern Fit10 shape scale wired into [MaterialTheme.shapes].
 */
val Fit10MaterialShapes = Shapes(
    extraSmall  = RoundedCornerShape(6.dp),
    small       = RoundedCornerShape(10.dp),
    medium      = RoundedCornerShape(16.dp),
    large       = RoundedCornerShape(20.dp),
    extraLarge  = RoundedCornerShape(28.dp),
)

/**
 * Extended shape tokens for pixel-perfect athletic components.
 */
object Fit10Shapes {
    /** Standard content card — 20dp rounded with smooth contours */
    val card        = RoundedCornerShape(20.dp)

    /** Large hero/feature cards — 24dp rounded */
    val cardLarge   = RoundedCornerShape(24.dp)

    /** Stat tiles & mini badges — 14dp */
    val statCard    = RoundedCornerShape(14.dp)

    /** Pill CTA buttons */
    val button      = RoundedCornerShape(50.dp)

    /** Filter chips & tag pills */
    val chip        = RoundedCornerShape(12.dp)

    /** Alert / confirmation dialogs */
    val dialog      = RoundedCornerShape(28.dp)

    /** Bottom sheets (top corners only) */
    val bottomSheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    /** Square icon containers / avatars */
    val icon        = RoundedCornerShape(14.dp)
}
