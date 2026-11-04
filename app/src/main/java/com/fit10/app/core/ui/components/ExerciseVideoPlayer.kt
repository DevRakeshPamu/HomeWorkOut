package com.fit10.app.core.ui.components

import android.net.Uri
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

@Composable
fun ExerciseVideo(res: Int, modifier: Modifier = Modifier) {
    Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
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
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
