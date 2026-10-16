package com.fit10.app.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Brand and accent colors that Material 3's [androidx.compose.material3.ColorScheme] has no slot for.
 * Every value has a light and a dark variant so screens never need hardcoded hex colors.
 */
@Immutable
data class Fit10ExtendedColors(
    val brandStrong: Color,        // Deeper end of brand gradients
    val brandBright: Color,        // Lighter end of brand gradients and progress highlights
    val streak: Color,
    val streakContainer: Color,
    val timer: Color,
    val timerContainer: Color,
    val calm: Color,
    val calmContainer: Color,
    val success: Color,
    val successContainer: Color,
    val mediaScrim: Color,         // Overlay on photos and video, always dark in both modes
    val onMediaScrim: Color,
    val celebration: List<Color>,  // Confetti
)

internal val LightExtendedColors = Fit10ExtendedColors(
    brandStrong      = Color(0xFF632B58),
    brandBright      = Color(0xFFB5609F),
    streak           = Color(0xFFC2410C),
    streakContainer  = Color(0xFFFFEDD5),
    timer            = Color(0xFF0E7490),
    timerContainer   = Color(0xFFCFFAFE),
    calm             = Color(0xFF6D28D9),
    calmContainer    = Color(0xFFEDE9FE),
    success          = Color(0xFF15803D),
    successContainer = Color(0xFFDCFCE7),
    mediaScrim       = Color(0xCC1A1019),
    onMediaScrim     = Color(0xFFFFFFFF),
    celebration      = listOf(
        Color(0xFF7E3A70), Color(0xFFE6A8D8), Color(0xFFF0997B),
        Color(0xFFEF9F27), Color(0xFF9FE1CB), Color(0xFFB5609F),
    ),
)

internal val DarkExtendedColors = Fit10ExtendedColors(
    brandStrong      = Color(0xFFC77FB6),
    brandBright      = Color(0xFFF5C9EA),
    streak           = Color(0xFFFDBA74),
    streakContainer  = Color(0xFF431407),
    timer            = Color(0xFF67E8F9),
    timerContainer   = Color(0xFF083344),
    calm             = Color(0xFFC4B5FD),
    calmContainer    = Color(0xFF2E1065),
    success          = Color(0xFF86EFAC),
    successContainer = Color(0xFF052E16),
    mediaScrim       = Color(0xCC000000),
    onMediaScrim     = Color(0xFFFFFFFF),
    celebration      = listOf(
        Color(0xFFE6A8D8), Color(0xFFF5C9EA), Color(0xFFF5B49C),
        Color(0xFFFAC775), Color(0xFF9FE1CB), Color(0xFFC77FB6),
    ),
)

internal val LocalFit10ExtendedColors = staticCompositionLocalOf { LightExtendedColors }
