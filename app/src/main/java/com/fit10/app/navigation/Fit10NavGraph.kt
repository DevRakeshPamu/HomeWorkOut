package com.fit10.app.navigation

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.first
import com.fit10.app.core.ui.theme.ThemeMode
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.fit10.app.core.di.AppContainer
import com.fit10.app.feature.explore.ExploreScreen
import com.fit10.app.feature.explore.ExploreViewModel
import com.fit10.app.feature.history.HistoryScreen
import com.fit10.app.feature.history.HistoryViewModel
import com.fit10.app.feature.home.HomeScreen
import com.fit10.app.feature.home.HomeViewModel
import com.fit10.app.feature.onboarding.OnboardingScreen
import com.fit10.app.feature.breathing.BreathingSessionScreen
import com.fit10.app.feature.onboarding.OnboardingViewModel
import com.fit10.app.feature.player.WorkoutPlayerScreen
import com.fit10.app.feature.player.WorkoutPlayerViewModel
import com.fit10.app.feature.splash.SplashScreen
import com.fit10.app.feature.exercise.ExerciseDetailScreen
import com.fit10.app.feature.exercise.ExerciseReplacementScreen
import com.fit10.app.feature.paywall.PaywallScreen
import com.fit10.app.feature.privacy.PrivacyDataScreen
import com.fit10.app.feature.profile.ProfileSettingsScreen
import com.fit10.app.feature.reminders.ReminderSettingsScreen
import com.fit10.app.feature.subscription.ManageSubscriptionScreen
import com.fit10.app.feature.summary.WorkoutSummaryScreen
import com.fit10.app.feature.summary.WorkoutSummaryViewModel
import com.fit10.app.feature.workout.WorkoutPreviewScreen

@Composable
fun Fit10NavGraph(
    navController: NavHostController,
    container: AppContainer,
    isOnboardingCompleted: Boolean?,
    startDestination: String = Fit10Destination.Splash.route,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Fit10Destination.Splash.route) {
            SplashScreen(
                isReady = isOnboardingCompleted != null,
                onSplashFinished = {
                    val nextRoute = if (isOnboardingCompleted == true) {
                        Fit10Destination.Home.route
                    } else {
                        Fit10Destination.Onboarding.route
                    }
                    navController.navigate(nextRoute) {
                        popUpTo(Fit10Destination.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Fit10Destination.Onboarding.route) {
            val viewModel: OnboardingViewModel = viewModel(
                factory = OnboardingViewModel.provideFactory(container.userRepository, container.preferencesDataSource)
            )
            OnboardingScreen(
                viewModel = viewModel,
                onOnboardingFinished = {
                    navController.navigate(Fit10Destination.Breathing.route) {
                        popUpTo(Fit10Destination.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Fit10Destination.Breathing.route) {
            BreathingSessionScreen(
                onFinished = {
                    if (!navController.popBackStack(Fit10Destination.Home.route, inclusive = false)) {
                        navController.navigate(Fit10Destination.Home.route) {
                            popUpTo(Fit10Destination.Breathing.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Fit10Destination.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    container.userRepository,
                    container.workoutRepository,
                    container.historyRepository,
                    container.preferencesDataSource
                )
            )
            HomeScreen(
                viewModel = viewModel,
                onStartWorkout = { workoutId -> navController.navigate(Fit10Destination.Player.createRoute(workoutId)) },
                onStartBreathing = { navController.navigate(Fit10Destination.Breathing.route) },
                onOpenProfile = {
                    navController.navigate(Fit10Destination.Profile.route) {
                        popUpTo(Fit10Destination.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(
            route = Fit10Destination.WorkoutPreview.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            val workout by container.workoutRepository.getWorkoutById(workoutId)
                .collectAsStateWithLifecycle(initialValue = null)
            WorkoutPreviewScreen(
                workout = workout,
                onBack = { navController.popBackStack() },
                onStartWorkout = {
                    navController.navigate(Fit10Destination.Player.createRoute(workoutId))
                },
                onExerciseClick = { exerciseId ->
                    navController.navigate(Fit10Destination.ExerciseDetail.createRoute(exerciseId))
                }
            )
        }

        composable(
            route = Fit10Destination.ExerciseDetail.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: "push_ups"
            ExerciseDetailScreen(
                exerciseId = exerciseId,
                onBack = { navController.popBackStack() },
                onReplace = {
                    navController.navigate(Fit10Destination.ExerciseReplacement.createRoute(exerciseId))
                },
                onGotIt = { navController.popBackStack() }
            )
        }

        composable(
            route = Fit10Destination.ExerciseReplacement.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: "push_ups"
            val entitlement by container.billingRepository.entitlement.collectAsStateWithLifecycle()
            ExerciseReplacementScreen(
                exerciseId = exerciseId,
                isPro = entitlement.isPro(),
                onBack = { navController.popBackStack() },
                onSelectVariation = { variationId ->
                    navController.popBackStack()
                },
                onKeepOriginal = { navController.popBackStack() },
                onNavigateToPaywall = {
                    navController.navigate(Fit10Destination.Paywall.route)
                }
            )
        }

        composable(Fit10Destination.Profile.route) {
            val entitlement by container.billingRepository.entitlement.collectAsStateWithLifecycle()
            val themeMode by container.preferencesDataSource.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val hapticsEnabled by container.preferencesDataSource.hapticsEnabled
                .collectAsStateWithLifecycle(initialValue = true)
            val scope = rememberCoroutineScope()
            val profile by container.userRepository.getUserProfile().collectAsStateWithLifecycle(initialValue = null)
            val history by container.historyRepository.getWorkoutHistory()
                .collectAsStateWithLifecycle(initialValue = com.fit10.app.domain.model.WorkoutHistory())
            ProfileSettingsScreen(
                isPro = entitlement.isPro(),
                userName = profile?.name.orEmpty(),
                streakDays = history.currentStreakDays,
                sessions = history.totalWorkoutsCompleted,
                minutes = history.totalMinutesWorkedOut,
                themeMode = themeMode,
                onThemeModeChange = { mode ->
                    scope.launch { container.preferencesDataSource.setThemeMode(mode) }
                },
                hapticsEnabled = hapticsEnabled,
                onHapticsEnabledChange = { enabled ->
                    scope.launch { container.preferencesDataSource.setHapticsEnabled(enabled) }
                },
                onNavigateToReminders = {
                    navController.navigate(Fit10Destination.Reminders.route)
                },
                onNavigateToSubscription = {
                    navController.navigate(Fit10Destination.ManageSubscription.route)
                },
                onNavigateToPrivacy = {
                    navController.navigate(Fit10Destination.PrivacyData.route)
                },
                onSignOut = {}
            )
        }

        composable(Fit10Destination.PrivacyData.route) {
            val scope = rememberCoroutineScope()
            val context = androidx.compose.ui.platform.LocalContext.current
            PrivacyDataScreen(
                onBack = { navController.popBackStack() },
                onDownloadData = {
                    scope.launch {
                        val json = org.json.JSONObject().apply {
                            put("postpartumProfile", container.preferencesDataSource.postpartumProfile.first()?.toString() ?: "")
                            put("workouts", org.json.JSONArray(container.historyRepository.getRecentResults(1000).first().map {
                                org.json.JSONObject().put("title", it.workoutTitle).put("completedAt", it.completedAtEpochMs)
                                    .put("durationSeconds", it.durationSeconds).put("feedback", it.feedback?.name ?: "")
                            }))
                        }.toString(2)
                        val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "Fit10 Mom data export")
                            putExtra(android.content.Intent.EXTRA_TEXT, json)
                        }
                        context.startActivity(android.content.Intent.createChooser(send, "Export my data"))
                    }
                },
                onDeleteAccount = {
                    scope.launch {
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            container.database.clearAllTables()
                            container.preferencesDataSource.clearAll()
                        }
                        com.fit10.app.core.reminder.ReminderScheduler.cancel(context)
                        navController.navigate(Fit10Destination.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Fit10Destination.ManageSubscription.route) {
            val entitlement by container.billingRepository.entitlement.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()
            ManageSubscriptionScreen(
                entitlement = entitlement,
                onBack = { navController.popBackStack() },
                onUpgrade = { navController.navigate(Fit10Destination.Paywall.route) },
                onRestorePurchases = {
                    coroutineScope.launch {
                        container.billingRepository.restorePurchases()
                    }
                }
            )
        }

        composable(Fit10Destination.Reminders.route) {
            ReminderSettingsScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                onTestNotification = {}
            )
        }

        composable(Fit10Destination.Paywall.route) {
            val plans by container.billingRepository.subscriptionPlans.collectAsStateWithLifecycle()
            val isLoading by container.billingRepository.isLoading.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()
            val context = androidx.compose.ui.platform.LocalContext.current
            val activity = context as? android.app.Activity

            PaywallScreen(
                plans = plans,
                isLoading = isLoading,
                onRetry = {
                    coroutineScope.launch {
                        container.billingRepository.retryBillingConnection()
                    }
                },
                onClose = { navController.popBackStack() },
                onSelectPlan = { plan ->
                    if (activity != null) {
                        coroutineScope.launch {
                            container.billingRepository.launchBillingFlow(activity, plan)
                        }
                    } else {
                        navController.navigate(Fit10Destination.ManageSubscription.route) {
                            popUpTo(Fit10Destination.Paywall.route) { inclusive = true }
                        }
                    }
                },
                onRestorePurchases = {
                    coroutineScope.launch {
                        container.billingRepository.restorePurchases()
                    }
                }
            )
        }

        composable(Fit10Destination.Explore.route) {
            val viewModel: ExploreViewModel = viewModel(
                factory = ExploreViewModel.provideFactory(container.workoutRepository)
            )
            ExploreScreen(
                viewModel = viewModel,
                onSelectWorkout = { workoutId ->
                    navController.navigate(Fit10Destination.Player.createRoute(workoutId))
                }
            )
        }

        composable(Fit10Destination.History.route) {
            val viewModel: HistoryViewModel = viewModel(
                factory = HistoryViewModel.provideFactory(container.historyRepository)
            )
            HistoryScreen(
                viewModel = viewModel,
                onExploreWorkouts = {
                    navController.navigate(Fit10Destination.Explore.route)
                },
                onPreviewWorkout = { workoutId ->
                    navController.navigate(Fit10Destination.WorkoutPreview.createRoute(workoutId))
                }
            )
        }

        composable(
            route = Fit10Destination.Player.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            val viewModel: WorkoutPlayerViewModel = viewModel(
                factory = WorkoutPlayerViewModel.provideFactory(
                    workoutId = workoutId,
                    workoutRepository = container.workoutRepository,
                    historyRepository = container.historyRepository,
                    feedbackCoordinator = container.feedbackCoordinator
                )
            )
            WorkoutPlayerScreen(
                viewModel = viewModel,
                onWorkoutFinished = { sessionId, id ->
                    navController.navigate(Fit10Destination.Summary.createRoute(sessionId, id)) {
                        popUpTo(Fit10Destination.Player.route) { inclusive = true }
                    }
                },
                onExit = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Fit10Destination.Summary.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("workoutId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            val viewModel: WorkoutSummaryViewModel = viewModel(
                factory = WorkoutSummaryViewModel.provideFactory(
                    sessionId = sessionId,
                    workoutId = workoutId,
                    historyRepository = container.historyRepository,
                    userRepository = container.userRepository
                )
            )
            WorkoutSummaryScreen(
                viewModel = viewModel,
                onDone = {
                    navController.navigate(Fit10Destination.Home.route) {
                        popUpTo(Fit10Destination.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
