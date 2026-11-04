package com.fit10.app.core.ui.figure

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

internal enum class Joint { PELVIS, SHOULDER, ELBOW_N, HAND_N, ELBOW_F, HAND_F, KNEE_N, ANKLE_N, KNEE_F, ANKLE_F }

internal class FigFrame(val ms: Int, val hold: Int, val a: FloatArray, val anchor: Joint, val ax: Float, val ay: Float)

internal class FigMove(val front: Boolean, val wall: Boolean, val frames: List<FigFrame>)

fun hasExerciseFigure(exerciseId: String): Boolean = exerciseId in FigureMoves.ALL

private const val SLOW_FACTOR = 2.2f
private const val VIEW_W = 1.6f
private const val VIEW_TOP = -0.14f
private const val VIEW_BOTTOM = 1.06f

private class Skeleton(val p: FloatArray) {
    fun x(j: Int) = p[j * 2]
    fun y(j: Int) = p[j * 2 + 1]
}

private const val SHN = 10
private const val SHF = 11
private const val HPN = 12
private const val HPF = 13
private const val HEAD = 14

private fun dirX(deg: Float, len: Float) = cos(Math.toRadians(deg.toDouble())).toFloat() * len
private fun dirY(deg: Float, len: Float) = sin(Math.toRadians(deg.toDouble())).toFloat() * len

private fun forward(a: FloatArray, front: Boolean): FloatArray {
    val p = FloatArray(30)
    fun set(j: Int, x: Float, y: Float) { p[j * 2] = x; p[j * 2 + 1] = y }
    val sx = dirX(a[0], 0.30f); val sy = dirY(a[0], 0.30f)
    set(Joint.PELVIS.ordinal, 0f, 0f)
    set(Joint.SHOULDER.ordinal, sx, sy)
    val so = if (front) 0.10f else 0f
    val ho = if (front) 0.06f else 0f
    set(SHN, sx + so, sy); set(SHF, sx - so, sy)
    set(HPN, ho, 0f); set(HPF, -ho, 0f)
    set(HEAD, sx + dirX(a[2], 0.105f), sy + dirY(a[2], 0.105f))
    fun chain(from: Int, mid: Joint, end: Joint, a1: Float, a2: Float, l: Float) {
        val mx = p[from * 2] + dirX(a1, l); val my = p[from * 2 + 1] + dirY(a1, l)
        set(mid.ordinal, mx, my)
        set(end.ordinal, mx + dirX(a2, l), my + dirY(a2, l))
    }
    chain(SHN, Joint.ELBOW_N, Joint.HAND_N, a[3], a[4], 0.15f)
    chain(SHF, Joint.ELBOW_F, Joint.HAND_F, a[5], a[6], 0.15f)
    chain(HPN, Joint.KNEE_N, Joint.ANKLE_N, a[7], a[8], 0.24f)
    chain(HPF, Joint.KNEE_F, Joint.ANKLE_F, a[9], a[10], 0.24f)
    return p
}

private fun pose(move: FigMove, timeMs: Long, slow: Boolean): Pair<Skeleton, Float> {
    val k = if (slow) SLOW_FACTOR else 1f
    val frames = move.frames
    val total = frames.sumOf { ((it.ms + it.hold) * k).toInt() }.coerceAtLeast(1)
    var t = (timeMs % total).toFloat()
    var i = 0
    while (i < frames.size) {
        val span = (frames[i].ms + frames[i].hold) * k
        if (t < span) break
        t -= span
        i++
    }
    i = i.coerceAtMost(frames.lastIndex)
    val to = frames[i]
    val from = frames[(i - 1 + frames.size) % frames.size]
    val raw = (t / (to.ms * k)).coerceIn(0f, 1f)
    val e = FastOutSlowInEasing.transform(raw)
    val a = FloatArray(11) { from.a[it] + (to.a[it] - from.a[it]) * e }
    val p = forward(a, move.front)
    val offA = Offset(from.ax - p[from.anchor.ordinal * 2], from.ay - p[from.anchor.ordinal * 2 + 1])
    val offB = Offset(to.ax - p[to.anchor.ordinal * 2], to.ay - p[to.anchor.ordinal * 2 + 1])
    val ox = offA.x + (offB.x - offA.x) * e
    val oy = offA.y + (offB.y - offA.y) * e
    for (j in 0 until 15) { p[j * 2] += ox; p[j * 2 + 1] += oy }
    return Skeleton(p) to a[1]
}

@Composable
fun ExerciseFigure(exerciseId: String, modifier: Modifier = Modifier, slow: Boolean = false) {
    val move = FigureMoves.ALL[exerciseId] ?: return
    var time by remember(exerciseId) { mutableLongStateOf(0L) }
    LaunchedEffect(exerciseId) {
        val start = withFrameMillis { it }
        while (true) withFrameMillis { time = it - start }
    }
    val near = MaterialTheme.colorScheme.primary
    val far = lerp(near, MaterialTheme.colorScheme.surface, 0.5f)
    val ground = MaterialTheme.colorScheme.outlineVariant
    val shadow = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Canvas(modifier) {
        val (sk, bend) = pose(move, time, slow)
        drawFigure(move, sk, bend, near, far, ground, shadow)
    }
}

private fun DrawScope.drawFigure(move: FigMove, sk: Skeleton, bend: Float, near: Color, far: Color, ground: Color, shadow: Color) {
    val unit = min(size.width / VIEW_W, size.height / (VIEW_BOTTOM - VIEW_TOP))
    val ox = (size.width - VIEW_W * unit) / 2f
    val oy = (size.height - (VIEW_BOTTOM - VIEW_TOP) * unit) / 2f - VIEW_TOP * unit
    fun pt(j: Int) = Offset(ox + sk.x(j) * unit, oy + sk.y(j) * unit)
    fun pt(x: Float, y: Float) = Offset(ox + x * unit, oy + y * unit)

    drawLine(ground, pt(0.08f, 1.0f), pt(VIEW_W - 0.08f, 1.0f), strokeWidth = 0.012f * unit, cap = StrokeCap.Round)
    if (move.wall) drawLine(ground, pt(0.42f, 0.05f), pt(0.42f, 1.0f), strokeWidth = 0.02f * unit, cap = StrokeCap.Round)
    val minX = (0 until 15).minOf { sk.x(it) }
    val maxX = (0 until 15).maxOf { sk.x(it) }
    drawOval(shadow, topLeft = pt(minX - 0.04f, 0.985f), size = Size((maxX - minX + 0.08f) * unit, 0.035f * unit))

    fun limb(a: Int, b: Int, c: Color, w: Float) = drawLine(c, pt(a), pt(b), strokeWidth = w * unit, cap = StrokeCap.Round)
    val farC = if (move.front) near else far
    limb(SHF, Joint.ELBOW_F.ordinal, farC, 0.055f); limb(Joint.ELBOW_F.ordinal, Joint.HAND_F.ordinal, farC, 0.05f)
    limb(HPF, Joint.KNEE_F.ordinal, farC, 0.07f); limb(Joint.KNEE_F.ordinal, Joint.ANKLE_F.ordinal, farC, 0.06f)

    val pe = pt(Joint.PELVIS.ordinal); val sh = pt(Joint.SHOULDER.ordinal)
    val dx = sh.x - pe.x; val dy = sh.y - pe.y
    val ln = hypot(dx, dy).coerceAtLeast(1f)
    var nx = dy / ln; var ny = -dx / ln
    if (ny > 0) { nx = -nx; ny = -ny }
    val cp = Offset((pe.x + sh.x) / 2 + nx * bend * 0.08f * unit, (pe.y + sh.y) / 2 + ny * bend * 0.08f * unit)
    val torso = Path().apply { moveTo(pe.x, pe.y); quadraticTo(cp.x, cp.y, sh.x, sh.y) }
    drawPath(torso, near, style = Stroke(width = (if (move.front) 0.17f else 0.11f) * unit, cap = StrokeCap.Round))
    drawCircle(near, radius = 0.068f * unit, center = pt(HEAD))

    limb(HPN, Joint.KNEE_N.ordinal, near, 0.07f); limb(Joint.KNEE_N.ordinal, Joint.ANKLE_N.ordinal, near, 0.06f)
    limb(SHN, Joint.ELBOW_N.ordinal, near, 0.055f); limb(Joint.ELBOW_N.ordinal, Joint.HAND_N.ordinal, near, 0.05f)
}

@Composable
fun ExerciseDemo(exerciseId: String, modifier: Modifier = Modifier, showSpeed: Boolean = true, slowFirstMs: Long = 8000) {
    var slow by remember(exerciseId) { mutableStateOf(showSpeed) }
    val haptics = rememberFit10Haptics()
    if (showSpeed) {
        LaunchedEffect(exerciseId) {
            kotlinx.coroutines.delay(slowFirstMs)
            slow = false
        }
    }
    val bg = Brush.verticalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface))
    Box(
        modifier.background(bg)
            .then(if (showSpeed) Modifier.clickable { haptics.perform(HapticKind.Tick); slow = !slow } else Modifier)
    ) {
        val context = LocalContext.current
        val video = remember(exerciseId) { exerciseVideoRes(context, exerciseId) }
        if (video != null) ExerciseVideo(exerciseId, video, slow, Modifier.fillMaxSize())
        else ExerciseFigure(exerciseId, Modifier.fillMaxSize().padding(16.dp), slow)
        if (showSpeed) {
            AnimatedContent(
                targetState = slow,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                label = "speed"
            ) { isSlow ->
                Row(
                    Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isSlow) Icons.Rounded.SlowMotionVideo else Icons.Rounded.Speed, null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (isSlow) "Slow motion · learn the form" else "Real speed",
                        fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
