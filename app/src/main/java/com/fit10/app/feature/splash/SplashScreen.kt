package com.fit10.app.feature.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var targetProgress by remember { mutableFloatStateOf(0.08f) }
    var badgeVisible by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
        label = "SplashProgressBar"
    )

    val badgeScale by animateFloatAsState(
        targetValue = if (badgeVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "BadgeScale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "ContentAlpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        badgeVisible = true
        delay(300)
        contentVisible = true
        targetProgress = 1.0f
        delay(2200)
        onSplashFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashInfinite")

    val spinnerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpinnerRotation"
    )

    // Ambient pulse ring on badge
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Slowly drifting particle angle
    val particleAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ParticleAngle"
    )

    val statusText = when {
        animatedProgress < 0.45f -> "CALIBRATING SESSION..."
        animatedProgress < 0.85f -> "OPTIMIZING 10-MIN ROUTINE..."
        else -> "READY TO IGNITE..."
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F0D))
    ) {
        // Background fitness photo
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Multi-layer cinematic vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xDD0E1C19),
                            0.3f to Color(0x88132521),
                            0.6f to Color(0xCC0C1714),
                            1.0f to Color(0xFA08110F)
                        )
                    )
                )
        )

        // Floating particle streaks via Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width * 0.5f
            val cy = size.height * 0.4f
            val particles = listOf(
                Triple(180f, 0.52f, 60f),
                Triple(270f, 0.38f, 40f),
                Triple(45f,  0.65f, 80f),
                Triple(135f, 0.44f, 55f),
                Triple(315f, 0.59f, 70f),
                Triple(220f, 0.70f, 45f),
            )
            particles.forEachIndexed { i, (baseAngle, radiusFrac, length) ->
                val angle = Math.toRadians((baseAngle + particleAngle * (if (i % 2 == 0) 0.3f else -0.2f)).toDouble())
                val radius = size.minDimension * radiusFrac
                val startX = cx + (radius * cos(angle)).toFloat()
                val startY = cy + (radius * sin(angle)).toFloat()
                val endX = startX + (length * cos(angle + Math.PI / 2)).toFloat()
                val endY = startY + (length * sin(angle + Math.PI / 2)).toFloat()
                drawLine(
                    color = Color(0x1534D399),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // 1. Top Pill: ADAPTIVE FITNESS AI
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x301C2B27))
                    .border(
                        width = 1.dp,
                        color = Color(0x4034D399),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Animated green dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .scale(pulseScale)
                            .background(Color(0xFF34D399), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADAPTIVE FITNESS AI",
                        color = Color(0xFFB8D4CC),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 2. Center App Badge — glowing with pulse ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(130.dp)
            ) {
                // Outer pulse ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale * 0.85f + 0.15f)
                        .clip(CircleShape)
                        .background(Color(0x1000E676))
                )
                // Mid ring
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(Color(0x1800E676))
                )
                // Icon container
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .scale(badgeScale)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF005228), Color(0xFF00391A))
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF34D399), Color(0xFF059669))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .drawBehind {
                            drawCircle(
                                color = Color(0x4400E676),
                                radius = size.minDimension * 0.55f,
                                blendMode = androidx.compose.ui.graphics.BlendMode.Screen
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // F10 wordmark
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)) {
                                append("F")
                            }
                            withStyle(SpanStyle(color = Color(0xFF34D399), fontWeight = FontWeight.Black, fontSize = 28.sp)) {
                                append("10")
                            }
                        },
                        letterSpacing = (-1).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title: Fit10 — large impactful wordmark
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Black)) {
                        append("Fit")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF34D399), fontWeight = FontWeight.Black)) {
                        append("10")
                    }
                },
                fontSize = 48.sp,
                letterSpacing = (-1.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Personalized Home Workout",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = 0.2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline
            Text(
                text = "Your workout. Your level.\nYour healthier you.",
                color = Color(0xFF7FA99C),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.weight(1.2f))

            // 3. Bottom: Glowing Progress Bar & Calibration Text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Glowing progress bar
                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x22FFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF059669),
                                        Color(0xFF10B981),
                                        Color(0xFF34D399)
                                    )
                                )
                            )
                            .drawBehind {
                                drawRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0x0034D399),
                                            Color(0x6634D399)
                                        )
                                    ),
                                    topLeft = Offset(0f, -4f),
                                    size = this.size.copy(height = this.size.height + 8f)
                                )
                            }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Spinner + status text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(spinnerRotation)
                    ) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(Color.Transparent, Color(0xFF34D399))
                            ),
                            startAngle = 0f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusText,
                        color = Color(0xFF7FA99C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.6.sp
                    )
                }
            }
        }
    }
}
