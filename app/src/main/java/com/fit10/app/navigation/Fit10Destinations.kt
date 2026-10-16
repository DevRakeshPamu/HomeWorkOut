package com.fit10.app.navigation

sealed class Fit10Destination(val route: String) {
    object Splash : Fit10Destination("splash")
    object Onboarding : Fit10Destination("onboarding")
    object PlanSummary : Fit10Destination("plan_summary")
    object Home : Fit10Destination("home")
    object Explore : Fit10Destination("explore")
    object History : Fit10Destination("history")

    object WorkoutPreview : Fit10Destination("preview/{workoutId}") {
        fun createRoute(workoutId: String) = "preview/$workoutId"
    }

    object Player : Fit10Destination("player/{workoutId}") {
        fun createRoute(workoutId: String) = "player/$workoutId"
    }

    object Summary : Fit10Destination("summary/{sessionId}/{workoutId}") {
        fun createRoute(sessionId: String, workoutId: String) = "summary/$sessionId/$workoutId"
    }

    object ExerciseDetail : Fit10Destination("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    }

    object ManageSubscription : Fit10Destination("subscription")
    object Reminders : Fit10Destination("reminders")
    object Paywall : Fit10Destination("paywall")

    object Profile : Fit10Destination("profile")
    object PrivacyData : Fit10Destination("privacy_data")
    object ExerciseReplacement : Fit10Destination("exercise_replacement/{exerciseId}") {
        fun createRoute(exerciseId: String) = "exercise_replacement/$exerciseId"
    }
}
