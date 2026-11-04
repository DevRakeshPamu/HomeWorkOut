package com.fit10.app.feature.player

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import com.fit10.app.domain.model.WorkoutResult
import com.fit10.app.domain.repository.HistoryRepository
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit10.app.core.feedback.WorkoutFeedbackCoordinator
import com.fit10.app.data.seed.WorkoutCatalog
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.repository.WorkoutRepository
import com.fit10.app.domain.workout.WorkoutSessionEngine
import com.fit10.app.domain.workout.WorkoutSessionState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkoutPlayerViewModel(
    private val workoutId: String,
    private val workoutRepository: WorkoutRepository,
    private val historyRepository: HistoryRepository,
    feedbackCoordinator: WorkoutFeedbackCoordinator
) : ViewModel() {

    private val _savedResult = MutableStateFlow<WorkoutResult?>(null)
    val savedResult: StateFlow<WorkoutResult?> = _savedResult.asStateFlow()

    private val engine = WorkoutSessionEngine(
        scope = viewModelScope,
        feedbackCoordinator = feedbackCoordinator
    )

    val state: StateFlow<WorkoutSessionState> = engine.state

    init {
        loadAndStartWorkout()
        viewModelScope.launch {
            val completed = engine.state.filterIsInstance<WorkoutSessionState.Completed>().first()
            historyRepository.saveWorkoutResult(completed.result)
            _savedResult.value = completed.result
        }
    }

    private fun loadAndStartWorkout() {
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutById(workoutId).first()
                ?: com.fit10.app.domain.plan.WeeklyPlanner.workoutFor(workoutId)
                ?: WorkoutCatalog.ALL.find { it.id == workoutId }
                ?: WorkoutCatalog.FULL_BODY_IGNITION

            engine.start(workout, prepCountdownSeconds = 5)
        }
    }

    fun pause() = engine.pause()
    fun resume() = engine.resume()
    fun skip() = engine.skipToNext()
    fun previous() = engine.previous()
    fun abandon() = engine.abandon()

    override fun onCleared() {
        super.onCleared()
        engine.stop()
    }

    companion object {
        fun provideFactory(
            workoutId: String,
            workoutRepository: WorkoutRepository,
            historyRepository: HistoryRepository,
            feedbackCoordinator: WorkoutFeedbackCoordinator
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WorkoutPlayerViewModel(
                    workoutId,
                    workoutRepository,
                    historyRepository,
                    feedbackCoordinator
                ) as T
            }
        }
    }
}
