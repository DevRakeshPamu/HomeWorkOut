package com.fit10.app.core.di

import android.content.Context
import com.fit10.app.core.feedback.WorkoutFeedbackCoordinator
import com.fit10.app.data.db.Fit10Database
import com.fit10.app.data.preferences.UserPreferencesDataSource
import com.fit10.app.data.repository.HistoryRepositoryImpl
import com.fit10.app.data.repository.ProgramRepositoryImpl
import com.fit10.app.data.repository.ReminderRepositoryImpl
import com.fit10.app.data.repository.UserRepositoryImpl
import com.fit10.app.data.repository.WorkoutRepositoryImpl
import com.fit10.app.data.seed.DatabaseSeeder
import com.fit10.app.domain.repository.HistoryRepository
import com.fit10.app.domain.repository.ProgramRepository
import com.fit10.app.domain.repository.ReminderRepository
import com.fit10.app.domain.repository.UserRepository
import com.fit10.app.domain.repository.WorkoutRepository

/**
 * Dependency container providing singletons for local storage, repositories, and hardware coordinators.
 */
class AppContainer(private val context: Context) {

    val database: Fit10Database by lazy {
        Fit10Database.getInstance(context)
    }

    val preferencesDataSource: UserPreferencesDataSource by lazy {
        UserPreferencesDataSource(context)
    }

    val databaseSeeder: DatabaseSeeder by lazy {
        DatabaseSeeder(database)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(database.userProfileDao(), preferencesDataSource)
    }

    val workoutRepository: WorkoutRepository by lazy {
        WorkoutRepositoryImpl(database.workoutDao(), database.exerciseDao(), databaseSeeder)
    }

    val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(database.workoutResultDao())
    }

    val programRepository: ProgramRepository by lazy {
        ProgramRepositoryImpl(database.programDao())
    }

    val reminderRepository: ReminderRepository by lazy {
        ReminderRepositoryImpl(database.reminderDao())
    }

    val feedbackCoordinator: WorkoutFeedbackCoordinator by lazy {
        WorkoutFeedbackCoordinator(context)
    }

    val billingRepository: com.fit10.app.domain.repository.BillingRepository by lazy {
        com.fit10.app.data.billing.BillingRepositoryImpl(context)
    }

    val analyticsTracker: com.fit10.app.core.analytics.AnalyticsTracker by lazy {
        com.fit10.app.core.analytics.LocalAnalyticsTracker(context)
    }
}

