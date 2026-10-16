package com.fit10.app.feature.subscription

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.R
import com.fit10.app.domain.billing.BillingConstants
import com.fit10.app.domain.billing.EntitlementInfo
import com.fit10.app.domain.billing.SubscriptionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PrimaryGreen = Color(0xFF00C070)
private val DarkGreen = Color(0xFF065F46)
private val LightGreenSurface = Color(0xFFE8F7F0)
private val CardBorderColor = Color(0xFFE2E8F0)
private val TextMain = Color(0xFF0F172A)
private val TextMuted = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSubscriptionScreen(
    entitlement: EntitlementInfo = EntitlementInfo(
        subscriptionState = SubscriptionState.ACTIVE,
        planId = BillingConstants.PRODUCT_ID_ANNUAL,
        planName = "Fit10 Pro (Annual Plan)",
        isAutoRenewing = true
    ),
    onBack: () -> Unit = {},
    onManageGooglePlay: () -> Unit = {},
    onRestorePurchases: () -> Unit = {},
    onViewReceipts: () -> Unit = {},
    onCancelSubscription: () -> Unit = {}
) {
    val context = LocalContext.current

    val sku = entitlement.planId ?: BillingConstants.PRODUCT_ID_ANNUAL
    val deepLinkUrl = "https://play.google.com/store/account/subscriptions?package=com.fit10.app&sku=$sku"

    val (badgeText, badgeBg, badgeTextColor) = when (entitlement.subscriptionState) {
        SubscriptionState.ACTIVE -> Triple("Active", LightGreenSurface, DarkGreen)
        SubscriptionState.IN_TRIAL -> Triple("Free Trial", Color(0xFFFEF3C7), Color(0xFF92400E))
        SubscriptionState.GRACE_PERIOD -> Triple("Grace Period", Color(0xFFFEF3C7), Color(0xFFB45309))
        SubscriptionState.ON_HOLD -> Triple("Account On Hold", Color(0xFFFEE2E2), Color(0xFFB91C1C))
        SubscriptionState.CANCELED_ACTIVE -> Triple("Canceled (Active)", Color(0xFFEFF6FF), Color(0xFF1D4ED8))
        SubscriptionState.EXPIRED, SubscriptionState.FREE -> Triple("Free Tier", Color(0xFFF1F5F9), TextMuted)
    }

    val expiryFormatted = entitlement.expiryEpochMs?.let {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Managed via Google Play"

    Scaffold(
        containerColor = Color(0xFFF8FAF9),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8FAF9)),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextMain
                        )
                    }
                },
                title = {
                    Text(
                        text = "Subscription",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.coach_maya),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, PrimaryGreen, CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(10.dp))

            // 1. Pro Member Header Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (entitlement.isPro()) PrimaryGreen else TextMuted)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (entitlement.isPro()) "FIT10 PRO MEMBER" else "FIT10 FREE",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (entitlement.isPro()) DarkGreen else TextMuted,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeBg)
                        .border(1.dp, badgeTextColor.copy(alpha = 0.3f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeTextColor,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 2. Plan Title and Price
            Text(
                text = entitlement.planName ?: if (entitlement.isPro()) "Fit10 Pro" else "Fit10 Free Plan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (sku == BillingConstants.PRODUCT_ID_ANNUAL) "₹499 / year" else if (sku == BillingConstants.PRODUCT_ID_MONTHLY) "₹149 / month" else "Free Forever",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (entitlement.isPro()) PrimaryGreen else TextMuted
            )

            Spacer(Modifier.height(14.dp))

            // Next Renewal / Expiry Info Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (entitlement.isAutoRenewing) "Next Renewal:" else "Expires:",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = expiryFormatted,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Rounded.Security,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 3. Plan Details Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Plan Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Text(
                    text = "Google Play Billing",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            Spacer(Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(
                        icon = Icons.Rounded.CreditCard,
                        label = "Billing Provider",
                        value = "Google Play Subscriptions"
                    )
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(Modifier.height(14.dp))
                    DetailRow(
                        icon = Icons.Rounded.CalendarToday,
                        label = "Auto-Renewal",
                        value = if (entitlement.isAutoRenewing) "Enabled (renews automatically)" else "Disabled / One-time"
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 4. Primary Action: Deep-link directly to Google Play Subscription management
            Button(
                onClick = {
                    onManageGooglePlay()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUrl))
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Manage on Google Play",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 5. Restore Purchases Button
            OutlinedButton(
                onClick = onRestorePurchases,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CardBorderColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMain),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextMuted
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Restore Past Purchases",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // 6. Security & Policy Information
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = LightGreenSurface.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "How to Cancel or Switch Plans",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Subscriptions can be modified or canceled anytime through Google Play Subscriptions. Canceling prevents future renewal charges while maintaining your Pro benefits until your current billing period ends.",
                        fontSize = 11.5.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextMuted
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMain
        )
    }
}
