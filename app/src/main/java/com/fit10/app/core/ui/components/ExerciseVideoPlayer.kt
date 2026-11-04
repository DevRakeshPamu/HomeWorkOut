package com.fit10.app.core.ui.components

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import kotlinx.coroutines.delay
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.fit10.app.R

fun exerciseVideoRes(exerciseId: String): Int? = when (exerciseId) {
    "ex_squats" -> R.raw.video_squats
    "ex_push_ups" -> R.raw.video_pushups
    "ex_lunges" -> R.raw.video_lunges
    "ex_mountain_climbers" -> R.raw.video_mountain_climber
    "ex_high_knees" -> R.raw.video_high_knees
    "ex_shoulder_taps" -> R.raw.video_shoulder_taps
    "ex_bird_dog" -> R.raw.video_bird_dog
    "ex_bicycle_crunches" -> R.raw.video_bicycle_crunch
    "ex_jumping_jacks" -> R.raw.video_jumping_jacks
    "ex_glute_bridges" -> R.raw.video_glute_bridge
    "ex_plank" -> R.raw.video_plank
    "ex_superman" -> R.raw.video_superman
    else -> null
}

private const val REAL_SPEED = 2.0f
private const val SLOW_SPEED = 1.0f

@Composable
fun ExerciseVideo(res: Int, modifier: Modifier = Modifier, slowFirstMs: Long = 8000) {
    var slow by remember(res) { mutableStateOf(true) }
    var player by remember(res) { mutableStateOf<MediaPlayer?>(null) }
    val haptics = rememberFit10Haptics()

    LaunchedEffect(res) {
        delay(slowFirstMs)
        slow = false
    }
    LaunchedEffect(player, slow) {
        player?.let { mp ->
            runCatching { mp.playbackParams = mp.playbackParams.setSpeed(if (slow) SLOW_SPEED else REAL_SPEED) }
        }
    }

    Box(
        modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { haptics.perform(HapticKind.Tick); slow = !slow }
    ) {
        AndroidView(
            factory = { ctx ->
                FrameLayout(ctx).apply {
                    clipChildren = true
                    addView(VideoView(ctx), FrameLayout.LayoutParams(1, 1, Gravity.CENTER))
                }
            },
            update = { container ->
                val video = container.getChildAt(0) as VideoView
                if (video.tag != res) {
                    video.tag = res
                    video.setVideoURI(Uri.parse("android.resource://${container.context.packageName}/$res"))
                    video.setOnPreparedListener { mp ->
                        mp.isLooping = true
                        mp.setVolume(0f, 0f)
                        container.post {
                            val vw = mp.videoWidth.coerceAtLeast(1).toFloat()
                            val vh = mp.videoHeight.coerceAtLeast(1).toFloat()
                            val scale = maxOf(container.width / vw, container.height / vh)
                            video.layoutParams = FrameLayout.LayoutParams((vw * scale).toInt(), (vh * scale).toInt(), Gravity.CENTER)
                        }
                        mp.start()
                        player = mp
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        AnimatedContent(
            targetState = slow,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            label = "speed"
        ) { isSlow ->
            Row(
                Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.55f)).padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isSlow) Icons.Rounded.SlowMotionVideo else Icons.Rounded.Speed, null,
                    tint = Color.White, modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(if (isSlow) "Slow motion · learn the form" else "Real speed", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
        }
    }
}
