package com.fit10.app.core.ui.figure

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

private val mirrorEveryLoop = setOf("ex_bird_dog", "ex_lunges", "ex_reverse_lunges", "ex_shoulder_taps")
private const val REAL_SPEED = 2.0f

@SuppressLint("DiscouragedApi")
fun exerciseVideoRes(context: Context, exerciseId: String): Int? =
    context.resources.getIdentifier("video_" + exerciseId.removePrefix("ex_"), "raw", context.packageName).takeIf { it != 0 }

@Composable
internal fun ExerciseVideo(exerciseId: String, res: Int, slow: Boolean, modifier: Modifier = Modifier) {
    var player by remember(res) { mutableStateOf<MediaPlayer?>(null) }
    var mirrored by remember(res) { mutableStateOf(false) }

    LaunchedEffect(player, slow) {
        player?.let { mp -> runCatching { mp.playbackParams = mp.playbackParams.setSpeed(if (slow) 1f else REAL_SPEED) } }
    }
    if (exerciseId in mirrorEveryLoop) {
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
                            player = null
                            (tag as? MediaPlayer)?.release()
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
            modifier = modifier
        )
    }
}
