package com.fit10.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit10.app.data.preferences.UserPreferencesDataSource
import com.fit10.app.data.seed.WorkoutCatalog
import com.fit10.app.domain.adaptation.PersonalizationEngine
import com.fit10.app.domain.model.PostpartumProfile
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutHistory
import com.fit10.app.domain.repository.HistoryRepository
import com.fit10.app.domain.repository.UserRepository
import com.fit10.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

enum class SleepQuality(val label: String, val message: String) {
    BADLY("Badly", "Rough night. Today's session is extra gentle."),
    OKAY("Okay", "Steady pace today. You've got this."),
    WELL("Well", "Rested and ready. Let's make it count."),
}

data class HomeUiState(
    val userProfile: UserProfile? = null,
    val postpartum: PostpartumProfile? = null,
    val recommendedWorkout: Workout = WorkoutCatalog.FULL_BODY_IGNITION,
    val history: WorkoutHistory = WorkoutHistory(),
    val sessionsThisWeek: Int = 0,
    val sleepToday: SleepQuality? = null,
    val isLoading: Boolean = false,
)

class HomeViewModel(
    userRepository: UserRepository,
    workoutRepository: WorkoutRepository,
    historyRepository: HistoryRepository,
    private val preferences: UserPreferencesDataSource,
) : ViewModel() {

    private val today get() = LocalDate.now().toString()

    private val base = combine(
        userRepository.getUserProfile(),
        workoutRepository.getAllWorkouts(),
        historyRepository.getWorkoutHistory(),
    ) { profile, workouts, history ->
        val available = workouts.ifEmpty { WorkoutCatalog.ALL }
        val recommended = profile?.let { PersonalizationEngine.recommendDailyWorkout(it, available, history) } ?: available.first()
        val weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        HomeUiState(
            userProfile = profile,
            recommendedWorkout = recommended,
            history = history,
            sessionsThisWeek = history.recentResults.count { it.completedAtEpochMs >= weekStart },
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        base,
        preferences.postpartumProfile,
        preferences.sleepCheckIn,
    ) { state, postpartum, checkIn ->
        state.copy(
            postpartum = postpartum,
            sleepToday = checkIn?.takeIf { it.first == today }?.let { (_, q) -> SleepQuality.entries.firstOrNull { it.name == q } },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState(isLoading = true))

    fun checkIn(quality: SleepQuality) {
        viewModelScope.launch { preferences.setSleepCheckIn(today, quality.name) }
    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            workoutRepository: WorkoutRepository,
            historyRepository: HistoryRepository,
            preferences: UserPreferencesDataSource,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                HomeViewModel(userRepository, workoutRepository, historyRepository, preferences) as T
        }
    }
}
