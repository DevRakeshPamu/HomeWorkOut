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
import com.fit10.app.domain.plan.PlanLevel
import com.fit10.app.domain.plan.PlanState
import com.fit10.app.domain.plan.ReviewOutcome
import com.fit10.app.domain.plan.WeekPlan
import com.fit10.app.domain.plan.WeeklyPlanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
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
    val plan: WeekPlan? = null,
    val reviewNote: String? = null,
    val isLoading: Boolean = false,
)

class HomeViewModel(
    userRepository: UserRepository,
    workoutRepository: WorkoutRepository,
    private val historyRepository: HistoryRepository,
    private val preferences: UserPreferencesDataSource,
) : ViewModel() {

    private val reviewNote = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch { ensurePlan() }
    }

    private suspend fun ensurePlan() {
        val profile = preferences.postpartumProfile.first() ?: return
        val todayDay = LocalDate.now().toEpochDay()
        val state = preferences.planState.first()
        if (state == null) {
            preferences.setPlanState(PlanState(WeeklyPlanner.startLevel(profile), todayDay, todayDay))
            return
        }
        if (todayDay - state.blockStartDay < 14) return
        val zone = ZoneId.systemDefault()
        val from = LocalDate.ofEpochDay(state.blockStartDay).atStartOfDay(zone).toInstant().toEpochMilli()
        val window = historyRepository.getRecentResults(60).first().filter { it.completedAtEpochMs >= from }
        val (level, outcome) = WeeklyPlanner.review(profile, state.level, window)
        preferences.setPlanState(state.copy(level = level, blockStartDay = todayDay))
        reviewNote.value = when (outcome) {
            ReviewOutcome.UP -> "Nice work. You've moved up to ${PlanLevel.entries[level - 1].title}."
            ReviewOutcome.DOWN -> "We've eased your plan back a little. Slow and steady."
            ReviewOutcome.STAY -> "New 2-week block. Same level, a little fresher."
        }
    }

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
        preferences.planState,
        reviewNote,
    ) { state, postpartum, checkIn, planState, note ->
        val sleep = checkIn?.takeIf { it.first == today }?.let { (_, q) -> SleepQuality.entries.firstOrNull { it.name == q } }
        val plan = if (postpartum != null && planState != null) {
            WeeklyPlanner.buildWeek(postpartum, planState, state.history.recentResults, gentleToday = sleep == SleepQuality.BADLY)
        } else null
        state.copy(
            postpartum = postpartum,
            sleepToday = sleep,
            plan = plan,
            recommendedWorkout = plan?.today ?: state.recommendedWorkout,
            reviewNote = note,
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
