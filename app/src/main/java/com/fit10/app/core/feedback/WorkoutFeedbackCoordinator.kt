package com.fit10.app.core.feedback

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Coordinates audio chimes and haptic vibrations during workout transitions and timers.
 */
class WorkoutFeedbackCoordinator(context: Context? = null) {

    /** Mirrors the Haptics toggle in Settings; tones still play when off. */
    @Volatile
    var hapticsEnabled: Boolean = true

    private val vibrator: Vibrator? = context?.let { ctx ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 75)
    } catch (e: Exception) {
        null
    }

    fun onCountdownTick() {
        playTone(ToneGenerator.TONE_PROP_BEEP, 120)
        vibrate(20)  // Light tick for 3-2-1
    }

    fun onExerciseStart() {
        playTone(ToneGenerator.TONE_PROP_ACK, 250)
        vibratePattern(longArrayOf(0, 60, 50, 90))  // Confident "go"
    }

    fun onRestStart() {
        playTone(ToneGenerator.TONE_PROP_PROMPT, 200)
        vibrate(50)  // Soft settle into rest
    }

    fun onHalfway() {
        playTone(ToneGenerator.TONE_PROP_BEEP2, 180)
        vibratePattern(longArrayOf(0, 30, 70, 30))  // Double pulse at halfway
    }

    fun onWorkoutCompleted() {
        playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500)
        vibratePattern(longArrayOf(0, 40, 60, 40, 60, 90, 80, 160))  // Warm celebration
    }

    private fun playTone(toneType: Int, durationMs: Int) {
        try {
            toneGenerator?.startTone(toneType, durationMs)
        } catch (ignored: Exception) {
        }
    }

    private fun vibrate(durationMs: Long) {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (ignored: Exception) {
        }
    }

    private fun vibratePattern(timings: LongArray) {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (ignored: Exception) {
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (ignored: Exception) {
        }
    }
}
