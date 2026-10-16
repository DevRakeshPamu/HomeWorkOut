package com.fit10.app.domain.usecase

import android.content.Context
import com.fit10.app.core.analytics.AnalyticsTracker
import com.fit10.app.core.security.DatabaseKeyManager
import com.fit10.app.data.db.Fit10Database
import com.fit10.app.data.preferences.UserPreferencesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Executes a complete, permanent wipe of all user data stored across the device.
 *
 * Stores wiped:
 * 1. Room SQLite Database (user_profiles, workout_results, reminders, user_plans)
 * 2. DataStore Preferences (onboarding status, active user id)
 * 3. Analytics event queue and install ID
 * 4. Hardware keystore-wrapped database passphrase and local billing cache
 */
class DeleteAllDataUseCase(
    private val context: Context,
    private val database: Fit10Database,
    private val preferencesDataSource: UserPreferencesDataSource,
    private val analyticsTracker: AnalyticsTracker
) {

    suspend fun execute(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Wipe Room tables
            database.clearAllTables()

            // 2. Clear DataStore preferences
            preferencesDataSource.clearAll()

            // 3. Purge analytics queue and reset install ID
            analyticsTracker.clearQueue()
            analyticsTracker.resetInstallId()
            analyticsTracker.setConsent(false)

            // 4. Wipe Keystore passphrase and billing cache
            DatabaseKeyManager.clearPassphrase(context)
            val billingPrefs = context.getSharedPreferences("fit10_billing_cache", Context.MODE_PRIVATE)
            billingPrefs.edit().clear().apply()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
