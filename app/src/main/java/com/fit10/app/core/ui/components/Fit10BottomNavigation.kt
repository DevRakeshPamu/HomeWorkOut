package com.fit10.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import com.fit10.app.core.feedback.rememberFit10Haptics
import com.fit10.app.core.feedback.HapticKind
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fit10.app.core.ui.theme.Fit10Theme

/**
 * Describes a single destination in [Fit10BottomNavigation].
 */
data class Fit10NavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val contentDescription: String = label,
)

/**
 * Modern athletic bottom navigation dock with animated active indicator.
 */
@Composable
fun Fit10BottomNavigation(
    items: List<Fit10NavItem>,
    selectedIndex: Int,
    onItemSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberFit10Haptics()
    Surface(
        modifier        = modifier.fillMaxWidth(),
        color           = MaterialTheme.colorScheme.surface,
        tonalElevation  = 3.dp,
        shadowElevation = 12.dp,
    ) {
        androidx.compose.foundation.layout.Column {
            HorizontalDivider(
                thickness = 1.dp,
                color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            )
            NavigationBar(
                modifier         = Modifier.fillMaxWidth(),
                containerColor   = MaterialTheme.colorScheme.surface,
                tonalElevation   = 0.dp,
                windowInsets     = WindowInsets.navigationBars,
            ) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex

                    // Scale spring animation on tap
                    val iconScale by animateFloatAsState(
                        targetValue = if (selected) 1.18f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness    = Spring.StiffnessMedium
                        ),
                        label = "NavIconScale_$index"
                    )

                    val iconColor by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(200, easing = FastOutSlowInEasing),
                        label = "NavIconColor_$index"
                    )

                    NavigationBarItem(
                        selected = selected,
                        onClick  = { haptics.perform(HapticKind.Tick); onItemSelected(index) },
                        icon = {
                            Box(contentAlignment = Alignment.Center) {
                                // Active glow dot indicator above icon
                                if (selected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(bottom = 28.dp)
                                            .size(4.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                                Icon(
                                    imageVector        = if (selected) item.selectedIcon else item.icon,
                                    contentDescription = item.contentDescription,
                                    modifier           = Modifier
                                        .size(if (selected) 26.dp else 22.dp)
                                        .scale(iconScale),
                                    tint               = iconColor
                                )
                            }
                        },
                        label = {
                            Text(
                                text  = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color      = iconColor
                                ),
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor   = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor      = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewNavItems = listOf(
    Fit10NavItem("Home",     Icons.Rounded.Home,         contentDescription = "Home"),
    Fit10NavItem("Workouts", Icons.Rounded.FitnessCenter, contentDescription = "Workouts"),
    Fit10NavItem("History",  Icons.Rounded.BarChart,      contentDescription = "History"),
    Fit10NavItem("Profile",  Icons.Rounded.Person,        contentDescription = "Profile"),
)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, widthDp = 360)
@Composable
private fun Fit10BottomNavigationPreview() {
    Fit10Theme {
        var selected by remember { mutableIntStateOf(0) }
        Fit10BottomNavigation(
            items           = previewNavItems,
            selectedIndex   = selected,
            onItemSelected  = { selected = it },
        )
    }
}
