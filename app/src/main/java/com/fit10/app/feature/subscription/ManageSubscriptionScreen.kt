package com.fit10.app.feature.subscription

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit10.app.core.ui.components.BackBar
import com.fit10.app.core.ui.components.GroupedList
import com.fit10.app.core.ui.components.LargeTitle
import com.fit10.app.core.ui.components.ListRow
import com.fit10.app.core.ui.components.PrimaryCta
import com.fit10.app.domain.billing.BillingConstants
import com.fit10.app.domain.billing.EntitlementInfo
import com.fit10.app.domain.billing.SubscriptionState

@Composable
fun ManageSubscriptionScreen(
    entitlement: EntitlementInfo = EntitlementInfo(),
    onBack: () -> Unit = {},
    onManageGooglePlay: () -> Unit = {},
    onRestorePurchases: () -> Unit = {},
    onViewReceipts: () -> Unit = {},
    onCancelSubscription: () -> Unit = {},
    onUpgrade: () -> Unit = {}
) {
    val context = LocalContext.current
    val isPro = entitlement.subscriptionState.isEntitled
    val isLifetime = entitlement.planId == BillingConstants.PRODUCT_ID_LIFETIME
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding().navigationBarsPadding()) {
        BackBar(onBack)
        Column(Modifier.padding(horizontal = 20.dp).weight(1f)) {
            LargeTitle("Fit10 Pro")
            GroupedList {
                Column(Modifier.padding(18.dp)) {
                    Text(if (isPro) entitlement.planName ?: "Fit10 Pro" else "Free plan", fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        when {
                            !isPro -> "Onboarding, safety check, first week and daily breathing are free."
                            isLifetime -> "Yours forever. Thank you for supporting Fit10 Mom."
                            entitlement.subscriptionState == SubscriptionState.CANCELED_ACTIVE -> "Canceled. Access continues until the end of this period."
                            entitlement.subscriptionState == SubscriptionState.GRACE_PERIOD -> "There's a payment issue. Please update it in Google Play."
                            else -> "Renews automatically. Manage or cancel anytime in Google Play."
                        },
                        fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            GroupedList {
                if (isPro && !isLifetime) {
                    ListRow(Icons.Rounded.OpenInNew, "Manage in Google Play", onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions?package=${context.packageName}"))
                        )
                        onManageGooglePlay()
                    })
                }
                ListRow(Icons.Rounded.Refresh, "Restore purchases", showDivider = false, onClick = onRestorePurchases)
            }
        }
        if (!isPro) {
            PrimaryCta("See Pro plans", Modifier.padding(horizontal = 20.dp, vertical = 12.dp), onClick = onUpgrade)
        }
    }
}
