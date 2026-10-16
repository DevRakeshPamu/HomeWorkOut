package com.fit10.app.core.feedback

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView

/** Haptic moments used across the UI. Workout timer patterns live in [WorkoutFeedbackCoordinator]. */
enum class HapticKind {
    Tick,     // Choosing an option, toggling, selecting a chip
    Confirm,  // Primary actions such as Start
    Soft,     // Pause and resume
    Reject,   // Safety stop, errors
}

/** Whether haptics are enabled, from the user's Settings. Provided at the app root. */
val LocalHapticsEnabled = staticCompositionLocalOf { true }

/**
 * Light UI haptics through the view system, so they follow the phone's own
 * "touch feedback" setting as well as the in-app Haptics toggle.
 */
class Fit10Haptics internal constructor(
    private val view: View,
    private val enabled: Boolean,
) {
    fun perform(kind: HapticKind) {
        if (!enabled) return
        val constant = when (kind) {
            HapticKind.Tick -> HapticFeedbackConstants.CLOCK_TICK
            HapticKind.Confirm ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.CONFIRM
                else HapticFeedbackConstants.VIRTUAL_KEY
            HapticKind.Soft ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) HapticFeedbackConstants.KEYBOARD_RELEASE
                else HapticFeedbackConstants.KEYBOARD_TAP
            HapticKind.Reject ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) HapticFeedbackConstants.REJECT
                else HapticFeedbackConstants.LONG_PRESS
        }
        view.performHapticFeedback(constant)
    }
}

@Composable
fun rememberFit10Haptics(): Fit10Haptics {
    val view = LocalView.current
    val enabled = LocalHapticsEnabled.current
    return remember(view, enabled) { Fit10Haptics(view, enabled) }
}
