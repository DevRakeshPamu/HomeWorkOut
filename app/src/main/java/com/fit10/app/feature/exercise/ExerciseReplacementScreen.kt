package com.fit10.app.feature.exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R
import com.fit10.app.core.ui.components.ContextualUpsellSheet
import com.fit10.app.domain.billing.ProFeature
import androidx.compose.material.icons.rounded.Lock

private val PrimaryGreen = Color(0xFF10B981)
private val DarkGreen = Color(0xFF047857)
private val LightGreenSurface = Color(0xFFECFDF5)
private val BackgroundSurface = Color(0xFFF8FAF9)
private val BorderInactive = Color(0xFFE2E8F0)
private val TextMain = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

data class ExerciseVariation(
    val id: String,
    val name: String,
    val focusArea: String,
    val description: String,
    val difficultyModifier: String, // e.g. "+ Easier", "+ Harder"
    val isHarder: Boolean = false,
    val isProOnly: Boolean = false
)

@Composable
fun ExerciseReplacementScreen(
    exerciseId: String = "push_ups",
    isPro: Boolean = false,
    onBack: () -> Unit = {},
    onSelectVariation: (String) -> Unit = {},
    onKeepOriginal: () -> Unit = {},
    onNavigateToPaywall: () -> Unit = {}
) {
    var showUpsellSheet by remember { mutableStateOf(false) }

    val variations = remember {
        listOf(
            ExerciseVariation(
                id = "incline_push_up",
                name = "Incline Push-up",
                focusArea = "Upper chest & arms",
                description = "Hands elevated on a couch or sturdy surface to reduce load on wrists and shoulders.",
                difficultyModifier = "+ Easier",
                isProOnly = false
            ),
            ExerciseVariation(
                id = "knee_push_up",
                name = "Knee Push-up",
                focusArea = "Chest & core",
                description = "Pivot from knees to build pressing strength while maintaining proper spine alignment.",
                difficultyModifier = "+ Easier",
                isProOnly = false
            ),
            ExerciseVariation(
                id = "wall_push_up",
                name = "Wall Push-up",
                focusArea = "Shoulders & chest",
                description = "Standing upright against a wall. Ideal for joint relief and zero floor strain.",
                difficultyModifier = "+ Easier",
                isProOnly = false
            ),
            ExerciseVariation(
                id = "decline_push_up",
                name = "Decline Push-up",
                focusArea = "Upper chest & delts",
                description = "Feet elevated on a bench or chair for maximum resistance and upper body tension.",
                difficultyModifier = "+ Harder",
                isHarder = true,
                isProOnly = true
            )
        )
    }

    var selectedVariationId by remember { mutableStateOf("knee_push_up") }
    val selectedVariation = variations.find { it.id == selectedVariationId } ?: variations[1]

    if (showUpsellSheet) {
        ContextualUpsellSheet(
            feature = ProFeature.FULL_EXERCISE_REGRESSION,
            onDismiss = { showUpsellSheet = false },
            onNavigateToPaywall = {
                showUpsellSheet = false
                onNavigateToPaywall()
            }
        )
    }

    Scaffold(
        containerColor = BackgroundSurface,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, BorderInactive)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { onSelectVariation(selectedVariationId) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Use ${selectedVariation.name}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButton(onClick = onKeepOriginal) {
                        Text(
                            text = "Keep Original",
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // 1. Top Header with Back, Adaptive Progression Pill, and Maya Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = TextMain,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = LightGreenSurface,
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Adaptive Progression",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGreen
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.coach_maya),
                    contentDescription = "Coach Maya",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, PrimaryGreen, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Title & Subtitle
            Text(
                text = "Too difficult? Try an alternative.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Swap with a variation tailored to your joints and strength baseline.",
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. CURRENT EXERCISE section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CURRENT EXERCISE",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "Default",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coach_pushup),
                        contentDescription = "Standard Push-up",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Push-up (Standard)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextMain
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Chest & Triceps",
                            fontSize = 12.5.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Baseline • Priority",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. RECOMMENDED VARIATIONS section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommended Variations",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "4 Available",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Variations Cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                variations.forEach { variation ->
                    val isLocked = variation.isProOnly && !isPro
                    val isSelected = variation.id == selectedVariationId
                    VariationCard(
                        variation = variation,
                        isSelected = isSelected,
                        isLocked = isLocked,
                        onClick = {
                            if (isLocked) {
                                showUpsellSheet = true
                            } else {
                                selectedVariationId = variation.id
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You can revert or swap back to standard push-up at any set interval during your routine.",
                fontSize = 11.5.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VariationCard(
    variation: ExerciseVariation,
    isSelected: Boolean,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryGreen else BorderInactive
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.coach_pushup),
                contentDescription = variation.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = variation.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = TextMain
                    )

                    if (isLocked) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Pro required",
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    text = "PRO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (variation.isHarder) Color(0xFFF1F5F9) else LightGreenSurface,
                            border = BorderStroke(
                                1.dp,
                                if (variation.isHarder) BorderInactive else PrimaryGreen.copy(alpha = 0.3f)
                            )
                        ) {
                            Text(
                                text = variation.difficultyModifier,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (variation.isHarder) Color(0xFF475569) else DarkGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = variation.focusArea,
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = variation.description,
                    fontSize = 11.5.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
