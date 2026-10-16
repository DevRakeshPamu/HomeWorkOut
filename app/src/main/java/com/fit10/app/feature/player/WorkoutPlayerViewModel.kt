package com.fit10.app.feature.player

import androidx.lifecycle.ViewModel
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
    feedbackCoordinator: WorkoutFeedbackCoordinator
) : ViewModel() {

    private val engine = WorkoutSessionEngine(
        scope = viewModelScope,
        feedbackCoordinator = feedbackCoordinator
    )

    val state: StateFlow<WorkoutSessionState> = engine.state

    init {
        loadAndStartWorkout()
    }

    private fun loadAndStartWorkout() {
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutById(workoutId).first()
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
            feedbackCoordinator: WorkoutFeedbackCoordinator
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WorkoutPlayerViewModel(
                    workoutId,
                    workoutRepository,
                    feedbackCoordinator
                ) as T
            }
        }
    }
}
