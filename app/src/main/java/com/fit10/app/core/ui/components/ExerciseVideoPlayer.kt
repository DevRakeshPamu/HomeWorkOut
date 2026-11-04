package com.fit10.app.core.ui.components

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.key
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

private val alternatingSides = setOf(R.raw.video_bird_dog, R.raw.video_lunges, R.raw.video_shoulder_taps)

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

    var mirrored by remember(res) { mutableStateOf(false) }
    if (res in alternatingSides) {
        LaunchedEffect(player) {
            val mp = player ?: return@LaunchedEffect
            var last = 0
            while (true) {
                delay(80)
                val pos = runCatching { mp.currentPosition }.getOrNull() ?: break
                if (pos < last - 300) mirrored = !mirrored
                last = pos
            }
        }
    }

    Box(
        modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { haptics.perform(HapticKind.Tick); slow = !slow }
    ) {
        key(res) {
            var videoSize by remember { mutableStateOf(1 to 1) }
            AndroidView(
                factory = { ctx ->
                    TextureView(ctx).apply {
                        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                            override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
                                val mp = MediaPlayer()
                                runCatching {
                                    mp.setDataSource(ctx, Uri.parse("android.resource://${ctx.packageName}/$res"))
                                    mp.setSurface(Surface(st))
                                    mp.isLooping = true
                                    mp.setVolume(0f, 0f)
                                    mp.setOnPreparedListener {
                                        videoSize = it.videoWidth.coerceAtLeast(1) to it.videoHeight.coerceAtLeast(1)
                                        it.start()
                                        player = it
                                    }
                                    mp.prepareAsync()
                                }
                                tag = mp
                            }
                            override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {}
                            override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                                (tag as? MediaPlayer)?.release()
                                player = null
                                return true
                            }
                            override fun onSurfaceTextureUpdated(st: SurfaceTexture) {}
                        }
                    }
                },
                update = { view ->
                    val (vw, vh) = videoSize
                    val w = view.width.toFloat().coerceAtLeast(1f)
                    val h = view.height.toFloat().coerceAtLeast(1f)
                    val k = maxOf(w / vw, h / vh)
                    val sx = vw * k / w
                    val sy = vh * k / h
                    view.setTransform(Matrix().apply { setScale(if (mirrored) -sx else sx, sy, w / 2f, h / 2f) })
                },
                modifier = Modifier.fillMaxSize()
            )
        }
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
