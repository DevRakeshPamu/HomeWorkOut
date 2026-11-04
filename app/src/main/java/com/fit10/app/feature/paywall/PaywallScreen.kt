package com.fit10.app.feature.paywall

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.feedback.HapticKind
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.domain.billing.BillingConstants
import com.fit10.app.domain.billing.SubscriptionPlanDetails

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
    val haptics = rememberFit10Haptics()
    val annual = plans.find { it.productId == BillingConstants.PRODUCT_ID_ANNUAL } ?: SubscriptionPlanDetails.FALLBACK_ANNUAL
    val monthly = plans.find { it.productId == BillingConstants.PRODUCT_ID_MONTHLY } ?: SubscriptionPlanDetails.FALLBACK_MONTHLY
    val lifetime = plans.find { it.productId == BillingConstants.PRODUCT_ID_LIFETIME } ?: SubscriptionPlanDetails.FALLBACK_LIFETIME
    val options = listOf(annual, monthly, lifetime)
    var selected by remember { mutableIntStateOf(0) }
    val plan = options[selected]

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(onClick = { haptics.perform(HapticKind.Tick); onClose() }) { Icon(Icons.Rounded.Close, contentDescription = "Close") }
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)
        ) {
            Text("Your full recovery journey", fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(20.dp))
            listOf(
                "All 4 phases of the 12-week plan",
                "Sessions that adapt to your sleep and energy",
                "Back and neck relief for feeding and carrying",
                "New gentle sessions every week",
            ).forEach { Benefit(it) }

            Spacer(Modifier.height(24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PlanOption(
                    title = "Yearly",
                    price = "${annual.formattedPrice} / year",
                    note = if (annual.hasFreeTrial) "${annual.freeTrialDays} days free, then ${annual.formattedMonthlyEquivalent}/month" else "${annual.formattedMonthlyEquivalent}/month",
                    badge = "Best value",
                    selected = selected == 0
                ) { haptics.perform(HapticKind.Tick); selected = 0 }
                PlanOption("Monthly", "${monthly.formattedPrice} / month", "Cancel anytime", null, selected == 1) {
                    haptics.perform(HapticKind.Tick); selected = 1
                }
                PlanOption("Lifetime", "${lifetime.formattedPrice} once", "Pay once, yours forever", null, selected == 2) {
                    haptics.perform(HapticKind.Tick); selected = 2
                }
            }
            if (hasError) {
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Couldn't load prices.", fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onRetry) { Text("Try again") }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                when (plan.productId) {
                    BillingConstants.PRODUCT_ID_LIFETIME -> "One-time purchase. No renewals."
                    BillingConstants.PRODUCT_ID_ANNUAL -> "Renews yearly unless canceled at least 24 hours before the trial or period ends. Manage or cancel in Google Play."
                    else -> "Renews monthly. Cancel anytime in Google Play."
                },
                fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { haptics.perform(HapticKind.Confirm); onSelectPlan(plan) },
                enabled = !isLoading,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        when {
                            plan.productId == BillingConstants.PRODUCT_ID_LIFETIME -> "Get lifetime access"
                            plan.hasFreeTrial -> "Start ${plan.freeTrialDays}-day free trial"
                            else -> "Continue"
                        },
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
            TextButton(onClick = { haptics.perform(HapticKind.Tick); onRestorePurchases() }) {
                Text("Restore purchases", textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun Benefit(text: String) {
    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Check, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun PlanOption(title: String, price: String, note: String, badge: String?, selected: Boolean, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    val border by animateColorAsState(if (selected) scheme.primary else scheme.outlineVariant, label = "planBorder")
    val width by animateDpAsState(if (selected) 2.dp else 1.dp, label = "planWidth")
    val scale by animateFloatAsState(if (selected) 1f else 0.98f, spring(dampingRatio = 0.6f), label = "planScale")
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) scheme.primaryContainer else scheme.surface,
        border = BorderStroke(width, border),
        modifier = Modifier.fillMaxWidth().scale(scale).clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface)
                    if (badge != null) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            badge, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = scheme.onPrimary,
                            modifier = Modifier.clip(CircleShape).background(scheme.primary).padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Text(note, fontSize = 13.sp, color = scheme.onSurfaceVariant)
            }
            Text(price, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface)
        }
    }
}
