package com.fit10.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit10.app.data.seed.WorkoutCatalog
import com.fit10.app.domain.adaptation.PersonalizationEngine
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.Program
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutHistory
import com.fit10.app.domain.repository.HistoryRepository
import com.fit10.app.domain.repository.ProgramRepository
import com.fit10.app.domain.repository.UserRepository
import com.fit10.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val userProfile: UserProfile? = null,
    val recommendedWorkout: Workout = WorkoutCatalog.FULL_BODY_IGNITION,
    val history: WorkoutHistory = WorkoutHistory(),
    val program: Program? = null,
    val currentProgramDay: Int = 1,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val historyRepository: HistoryRepository,
    private val programRepository: ProgramRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        userRepository.getUserProfile(),
        workoutRepository.getAllWorkouts(),
        historyRepository.getWorkoutHistory(),
        programRepository.getAllPrograms()
    ) { profile, workouts, history, programs ->
        val availableWorkouts = workouts.ifEmpty { WorkoutCatalog.ALL }
        val recommended = profile?.let {
            PersonalizationEngine.recommendDailyWorkout(it, availableWorkouts, history)
        } ?: availableWorkouts.first()

        val activeProgram = programs.firstOrNull()

        // FIX PC-04: The original `(totalWorkoutsCompleted % 14) + 1` expression looped
        // back to Day 1 silently after every 14 completions, with no program-complete state.
        // New logic: advance day up to the program's totalDays, then stay at totalDays so
        // the UI can detect completion. Falls back to day 1 when no program is active.
        val programDay = activeProgram?.let { program ->
            val advancedDay = history.totalWorkoutsCompleted + 1
            advancedDay.coerceIn(1, program.totalDays)
        } ?: 1

        HomeUiState(
            userProfile = profile,
            recommendedWorkout = recommended,
            history = history,
            program = activeProgram,
            currentProgramDay = programDay,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            workoutRepository: WorkoutRepository,
            historyRepository: HistoryRepository,
            programRepository: ProgramRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    userRepository,
                    workoutRepository,
                    historyRepository,
                    programRepository
                ) as T
            }
        }
    }
}
