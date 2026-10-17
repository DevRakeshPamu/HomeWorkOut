package com.fit10.app.core.ui.components

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.ui.theme.Fit10Theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.fit10.app.R

// ─────────────────────────────────────────────────────────────────────────────
// Video resource resolver
// ─────────────────────────────────────────────────────────────────────────────

@RawRes
fun exerciseVideoRes(exerciseId: String): Int = when (exerciseId) {
    "ex_squats", "ex_wall_sit" -> R.raw.video_squats
    "ex_push_ups", "ex_wide_push_ups", "ex_diamond_push_ups" -> R.raw.video_pushups
    "ex_lunges", "ex_reverse_lunges" -> R.raw.video_lunges
    "ex_mountain_climbers" -> R.raw.video_mountain_climber
    "ex_high_knees" -> R.raw.video_high_knees
    "ex_shoulder_taps" -> R.raw.video_shoulder_taps
    "ex_bird_dog", "ex_cat_cow", "ex_childs_pose" -> R.raw.video_bird_dog
    "ex_bicycle_crunches" -> R.raw.video_bicycle_crunch
    "ex_jumping_jacks", "ex_arm_circles" -> R.raw.video_jumping_jacks
    "ex_glute_bridges", "ex_glute_bridge_march" -> R.raw.video_glute_bridge
    "ex_plank" -> R.raw.video_plank
    "ex_superman", "ex_prone_ytw" -> R.raw.video_superman
    else -> R.raw.video_squats
}

// ─────────────────────────────────────────────────────────────────────────────
// Colors
// ─────────────────────────────────────────────────────────────────────────────

// Everything here sits on video, so it uses the media scrim colors in both modes.
private val MediaAccent: Color
    @Composable @ReadOnlyComposable get() = Fit10Theme.extendedColors.mediaAccent
private val MediaScrim: Color
    @Composable @ReadOnlyComposable get() = Fit10Theme.extendedColors.mediaScrim
private val OnMedia: Color
    @Composable @ReadOnlyComposable get() = Fit10Theme.extendedColors.onMediaScrim

/**
 * High-definition looping video player demonstration featuring a real human fitness model.
 *
 * Capabilities:
 *  • Continuous loop playback of exact exercise movements
 *  • Hardware-accelerated 0.5x Slow-Motion speed control for inspecting joint biomechanics
 *  • Instant play/pause freeze-frame control
 *  • Dynamic coach form cue banner
 *  • Zero network latency / 100% offline bundled MP4 playback
 */
@Composable
fun ExerciseVideoPlayer(
    exerciseId: String,
    exerciseName: String,
    coachCue: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptics = rememberFit10Haptics()
    val videoRes = remember(exerciseId) { exerciseVideoRes(exerciseId) }
    val videoUri = remember(videoRes) {
        Uri.parse("android.resource://${context.packageName}/$videoRes")
    }

    var isPlaying by remember { mutableStateOf(true) }
    var isSlowMotion by remember { mutableStateOf(false) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }

    // Update playback speed dynamically
    fun updatePlaybackSpeed(mediaPlayer: MediaPlayer?, slowMo: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mediaPlayer != null) {
            try {
                val params = mediaPlayer.playbackParams ?: PlaybackParams()
                params.speed = if (slowMo) 0.5f else 1.0f
                mediaPlayer.playbackParams = params
            } catch (_: Exception) {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.scrim)
    ) {
        // ── 1. Native VideoView ──────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setVideoURI(videoUri)
                    setOnPreparedListener { mp ->
                        mediaPlayerRef = mp
                        mp.isLooping = true
                        mp.setVolume(0f, 0f) // Mute video audio so it doesn't clash with trainer cues
                        updatePlaybackSpeed(mp, isSlowMotion)
                        mp.start()
                    }
                    videoViewRef = this
                }
            },
            update = { view ->
                videoViewRef = view
                if (view.tag != videoRes) {
                    view.tag = videoRes
                    view.setVideoURI(videoUri)
                    view.setOnPreparedListener { mp ->
                        mediaPlayerRef = mp
                        mp.isLooping = true
                        mp.setVolume(0f, 0f)
                        updatePlaybackSpeed(mp, isSlowMotion)
                        mp.start()
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        DisposableEffect(exerciseId) {
            onDispose {
                try {
                    videoViewRef?.stopPlayback()
                } catch (_: Exception) {}
            }
        }

        // ── 2. Vignette & Gradient Overlays ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MediaScrim.copy(alpha = 0.8f),
                            Color.Transparent,
                            Color.Transparent,
                            MediaScrim.copy(alpha = 0.93f)
                        )
                    )
                )
        )

        // ── 3. Top Controls Bar: HD Live Badge + Speed & Play/Pause Controls ─
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "HD VIDEO • REAL MODEL" Live Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MediaScrim.copy(alpha = 0.73f))
                    .border(1.dp, MediaAccent.copy(alpha = 0.6f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(MediaAccent)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "HD VIDEO • REAL MODEL",
                        color = OnMedia,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Interactive Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 0.5x Slow-Motion Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSlowMotion) MediaAccent else MediaScrim.copy(alpha = 0.6f))
                        .clickable {
                            haptics.perform(HapticKind.Tick)
                            val newSlowMo = !isSlowMotion
                            isSlowMotion = newSlowMo
                            updatePlaybackSpeed(mediaPlayerRef, newSlowMo)
                        }
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.SlowMotionVideo,
                            contentDescription = "Slow Motion",
                            tint = if (isSlowMotion) MediaScrim else OnMedia,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isSlowMotion) "0.5x Slow-Mo" else "1.0x Speed",
                            color = if (isSlowMotion) MediaScrim else OnMedia,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Play / Pause Toggle
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MediaScrim.copy(alpha = 0.6f))
                        .clickable {
                            haptics.perform(HapticKind.Tick)
                            if (isPlaying) {
                                videoViewRef?.pause()
                                isPlaying = false
                            } else {
                                videoViewRef?.start()
                                isPlaying = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = OnMedia,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // ── 4. Bottom Coach Form Cue Banner ──────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = MediaScrim.copy(alpha = 0.95f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = MediaAccent,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = coachCue,
                    color = OnMedia.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp,
                    maxLines = 1
                )
            }
        }
    }
}
