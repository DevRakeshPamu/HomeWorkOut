package com.fit10.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fit10.app.core.ui.theme.ThemeMode
import com.fit10.app.domain.model.PostpartumProfile
import kotlinx.serialization.json.Json
import com.fit10.app.domain.plan.PlanState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fit10_preferences")

class UserPreferencesDataSource(private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val ACTIVE_USER_ID = stringPreferencesKey("active_user_id")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val POSTPARTUM_PROFILE = stringPreferencesKey("postpartum_profile")
        val SLEEP_CHECK_IN = stringPreferencesKey("sleep_check_in")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val PLAN_STATE = stringPreferencesKey("plan_state")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    val activeUserId: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ACTIVE_USER_ID]
    }

    suspend fun setActiveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ACTIVE_USER_ID] = userId
        }
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE]
            ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
            ?: ThemeMode.SYSTEM
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    val hapticsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    val postpartumProfile: Flow<PostpartumProfile?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.POSTPARTUM_PROFILE]?.let { runCatching { json.decodeFromString<PostpartumProfile>(it) }.getOrNull() }
    }

    suspend fun setPostpartumProfile(profile: PostpartumProfile) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.POSTPARTUM_PROFILE] = json.encodeToString(PostpartumProfile.serializer(), profile)
        }
    }

    val sleepCheckIn: Flow<Pair<String, String>?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SLEEP_CHECK_IN]?.split("|")?.takeIf { it.size == 2 }?.let { it[0] to it[1] }
    }

    suspend fun setSleepCheckIn(date: String, quality: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SLEEP_CHECK_IN] = "$date|$quality"
        }
    }

    val reminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.REMINDER_ENABLED] ?: false }

    val reminderTime: Flow<Pair<Int, Int>> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REMINDER_TIME]?.split(":")?.mapNotNull { it.toIntOrNull() }
            ?.takeIf { it.size == 2 }?.let { it[0] to it[1] } ?: (20 to 30)
    }

    suspend fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDER_ENABLED] = enabled
            preferences[PreferencesKeys.REMINDER_TIME] = "$hour:$minute"
        }
    }

    val planState: Flow<PlanState?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PLAN_STATE]?.split("|")?.takeIf { it.size == 3 }?.let {
            PlanState(it[0].toInt(), it[1].toLong(), it[2].toLong())
        }
    }

    suspend fun setPlanState(state: PlanState) {
        context.dataStore.edit { it[PreferencesKeys.PLAN_STATE] = "${state.level}|${state.blockStartDay}|${state.planStartDay}" }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
