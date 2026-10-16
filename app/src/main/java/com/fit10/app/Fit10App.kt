package com.fit10.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fit10.app.core.ui.components.Fit10BottomNavigation
import com.fit10.app.core.ui.components.Fit10LoadingState
import com.fit10.app.core.ui.components.Fit10NavItem
import com.fit10.app.navigation.Fit10Destination
import com.fit10.app.navigation.Fit10NavGraph

@Composable
fun Fit10App() {
    val context = LocalContext.current
    val app = context.applicationContext as Fit10Application
    val container = app.container

    val isOnboardingCompleted by container.userRepository
        .isOnboardingCompleted()
        .collectAsStateWithLifecycle(initialValue = null)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItems = remember {
        listOf(
            Fit10NavItem(label = "Home", icon = Icons.Rounded.Home, contentDescription = "Home"),
            Fit10NavItem(label = "Programs", icon = Icons.Rounded.FitnessCenter, contentDescription = "Programs"),
            Fit10NavItem(label = "Progress", icon = Icons.Rounded.BarChart, contentDescription = "Progress"),
            Fit10NavItem(label = "Profile", icon = Icons.Rounded.Person, contentDescription = "Profile")
        )
    }

    val showBottomBar = currentRoute in listOf(
        Fit10Destination.Home.route,
        Fit10Destination.Explore.route,
        Fit10Destination.History.route,
        Fit10Destination.Profile.route
    )

    val selectedIndex = when (currentRoute) {
        Fit10Destination.Explore.route -> 1
        Fit10Destination.History.route -> 2
        Fit10Destination.Profile.route -> 3
        else -> 0
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                Fit10BottomNavigation(
                    items = navItems,
                    selectedIndex = selectedIndex,
                    onItemSelected = { index ->
                        val target = when (index) {
                            1 -> Fit10Destination.Explore.route
                            2 -> Fit10Destination.History.route
                            3 -> Fit10Destination.Profile.route
                            else -> Fit10Destination.Home.route
                        }
                        if (currentRoute != target) {
                            navController.navigate(target) {
                                popUpTo(Fit10Destination.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Fit10NavGraph(
            navController = navController,
            container = container,
            isOnboardingCompleted = isOnboardingCompleted,
            startDestination = Fit10Destination.Splash.route,
            modifier = if (currentRoute == Fit10Destination.Splash.route) Modifier else Modifier.padding(paddingValues)
        )
    }
}
