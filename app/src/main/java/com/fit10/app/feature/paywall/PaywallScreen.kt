package com.fit10.app.feature.paywall

import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.feedback.HapticKind
import androidx.compose.material3.MaterialTheme
import com.fit10.app.core.ui.theme.Fit10Theme
import androidx.compose.runtime.ReadOnlyComposable
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R
import com.fit10.app.domain.billing.BillingConstants
import com.fit10.app.domain.billing.SubscriptionPlanDetails

private val BrandPrimary: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
private val OnBrand: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary
private val BrandDeep: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
private val BrandContainer: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
private val ScreenBackground: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
private val BorderInactive: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant
private val TextMain: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
private val TextMuted: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val CardSurface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    plans: List<SubscriptionPlanDetails> = listOf(
        SubscriptionPlanDetails.FALLBACK_ANNUAL,
        SubscriptionPlanDetails.FALLBACK_MONTHLY
    ),
    isLoading: Boolean = false,
    hasError: Boolean = false,
    onRetry: () -> Unit = {},
    onClose: () -> Unit = {},
    onStartTrial: (isAnnual: Boolean) -> Unit = {},
    onSelectPlan: (SubscriptionPlanDetails) -> Unit = { onStartTrial(it.productId == BillingConstants.PRODUCT_ID_ANNUAL) },
    onRestorePurchases: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptics = rememberFit10Haptics()
    var selectedPlanIndex by remember { mutableIntStateOf(0) } // 0: Annual, 1: Monthly

    val activePlans = plans.ifEmpty {
        listOf(SubscriptionPlanDetails.FALLBACK_ANNUAL, SubscriptionPlanDetails.FALLBACK_MONTHLY)
    }

    val annualPlan = activePlans.find { it.productId == BillingConstants.PRODUCT_ID_ANNUAL } ?: activePlans.first()
    val monthlyPlan = activePlans.find { it.productId == BillingConstants.PRODUCT_ID_MONTHLY } ?: activePlans.getOrElse(1) { activePlans.first() }

    val selectedPlan = if (selectedPlanIndex == 0) annualPlan else monthlyPlan

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ScreenBackground),
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = TextMain
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Official Pass",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                    }
                },
                actions = {
                    Text(
                        text = "Restore Purchases",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        modifier = Modifier
                            .clickable { haptics.perform(HapticKind.Tick); onRestorePurchases() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.coach_maya),
                        contentDescription = "Coach Maya",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, BrandPrimary, CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            Surface(
                color = CardSurface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { haptics.perform(HapticKind.Confirm); onSelectPlan(selectedPlan) },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandPrimary,
                            contentColor = OnBrand
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (selectedPlan.hasFreeTrial) {
                                    "Start ${selectedPlan.freeTrialDays}-Day Free Trial, then ${selectedPlan.formattedPrice}/yr"
                                } else {
                                    "Continue with ${selectedPlan.formattedPrice}/mo"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "No thanks",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        modifier = Modifier
                            .clickable { haptics.perform(HapticKind.Tick); onClose() }
                            .padding(vertical = 4.dp)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Terms of Service • Privacy Policy • Google Play Subscription Terms",
                        fontSize = 10.5.sp,
                        color = TextMuted.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            // 1. Pro Access Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(BrandContainer)
                    .border(1.dp, BrandPrimary.copy(alpha = 0.3f), RoundedCornerShape(50))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "✦  FIT10 PRO ACCESS  ✦",
                    color = BrandDeep,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            // 2. Title & Subtitle
            Text(
                text = "Go Premium",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Unlock your full potential with adaptive micro-workouts tailored to your ongoing progress.",
                fontSize = 13.5.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(20.dp))

            // 3. What's included in Pro Card (Copy-accurate, no ungrounded claims)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "What's included in Pro",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMain
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(BrandContainer)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "All Unlocked",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandDeep
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    ProFeatureItem(
                        title = "All workout programs",
                        subtitle = "Full access to guided multi-week tracks beyond the 3-day preview"
                    )
                    Spacer(Modifier.height(12.dp))
                    ProFeatureItem(
                        title = "Adaptive difficulty between sessions",
                        subtitle = "Adapts workout intensity and volume between sessions based on your completion and feedback"
                    )
                    Spacer(Modifier.height(12.dp))
                    ProFeatureItem(
                        title = "Full exercise regression engine",
                        subtitle = "Joint-friendly exercise swaps and regressions for any limitation"
                    )
                    Spacer(Modifier.height(12.dp))
                    ProFeatureItem(
                        title = "Detailed progress insights",
                        subtitle = "Muscle volume balance breakdowns, RPE fatigue tracking, and streak freeze"
                    )
                    Spacer(Modifier.height(12.dp))
                    ProFeatureItem(
                        title = "No ads, no tracking of your workouts",
                        subtitle = "Zero third-party ads and complete offline data privacy"
                    )
                    Spacer(Modifier.height(12.dp))
                    ProFeatureItem(
                        title = "Curated workout variants",
                        subtitle = "Targeted 5 & 15-minute variants and equipment routines"
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 4. Loading or Error Retry States
            if (isLoading) {
                CircularProgressIndicator(
                    color = BrandPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (hasError) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Could not load real-time prices from Google Play.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = onRetry) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Retry Connection", fontSize = 12.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // 5. Subscription Options (Dynamic real prices)
            // Option 1: Annual
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selectedPlanIndex == 0) BrandContainer.copy(alpha = 0.35f) else CardSurface,
                border = BorderStroke(
                    width = if (selectedPlanIndex == 0) 2.dp else 1.dp,
                    color = if (selectedPlanIndex == 0) BrandPrimary else BorderInactive
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { haptics.perform(HapticKind.Tick); selectedPlanIndex = 0 }
            ) {
                Column {
                    // Badge: Computed savings from real prices
                    val savingsBadge = annualPlan.savingsPercentage?.let { "BEST VALUE • SAVE $it%" } ?: "BEST VALUE"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandPrimary)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = savingsBadge,
                            color = OnBrand,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPlanIndex == 0,
                            onClick = { haptics.perform(HapticKind.Tick); selectedPlanIndex = 0 },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = BrandPrimary,
                                unselectedColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = annualPlan.formattedPrice,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain
                                )
                                Text(
                                    text = " / year",
                                    fontSize = 13.sp,
                                    color = TextMuted
                                )
                                if (annualPlan.hasFreeTrial) {
                                    Spacer(Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(BrandPrimary)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${annualPlan.freeTrialDays}-Day Free",
                                            color = OnBrand,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Just ${annualPlan.formattedMonthlyEquivalent} / month, billed annually",
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
                            if (annualPlan.isFallbackSkeleton) {
                                Text(
                                    text = "Offline estimated price. Charged price verified by Google Play.",
                                    fontSize = 10.sp,
                                    color = TextMuted.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Option 2: Monthly
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (selectedPlanIndex == 1) BrandContainer.copy(alpha = 0.35f) else CardSurface,
                border = BorderStroke(
                    width = if (selectedPlanIndex == 1) 2.dp else 1.dp,
                    color = if (selectedPlanIndex == 1) BrandPrimary else BorderInactive
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { haptics.perform(HapticKind.Tick); selectedPlanIndex = 1 }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPlanIndex == 1,
                        onClick = { haptics.perform(HapticKind.Tick); selectedPlanIndex = 1 },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = BrandPrimary,
                            unselectedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = monthlyPlan.formattedPrice,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                            Text(
                                text = " / month",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Flexible monthly plan, cancel anytime",
                            fontSize = 11.5.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 6. Explicit Subscription Terms (Play Store Requirement)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardSurface,
                border = BorderStroke(1.dp, BorderInactive),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Subscription Terms & Renewal Policy",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "• After the 7-day free trial, your Google Play account will be charged ${annualPlan.formattedPrice}/year (or ${monthlyPlan.formattedPrice}/month if selected) unless canceled at least 24 hours before the trial ends.\n" +
                                "• Subscriptions automatically renew until canceled in your Google Play account settings.\n" +
                                "• You can cancel anytime directly via Google Play Subscriptions.\n" +
                                "• Refund policy: governed exclusively by Google Play refund rules.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Manage in Google Play Store ↗",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary,
                        modifier = Modifier.clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/account/subscriptions")
                            )
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ProFeatureItem(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(BrandContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = BrandDeep,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 16.sp
            )
        }
    }
}
