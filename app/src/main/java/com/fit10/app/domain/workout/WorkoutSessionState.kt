package com.fit10.app.domain.workout

import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutExercise
import com.fit10.app.domain.model.WorkoutPerformance
import com.fit10.app.domain.model.WorkoutResult

sealed interface WorkoutSessionState {

    object Idle : WorkoutSessionState

    data class Countdown(
        val workout: Workout,
        val secondsRemaining: Int,
        val totalCountdownSeconds: Int = 5
    ) : WorkoutSessionState

    data class ActiveExercise(
        val workout: Workout,
        val exerciseIndex: Int,
        val currentExercise: WorkoutExercise,
        val secondsRemaining: Int,
        val totalExerciseSeconds: Int,
        val elapsedTotalSeconds: Int
    ) : WorkoutSessionState

    data class RestInterval(
        val workout: Workout,
        val exerciseIndex: Int,
        val nextExercise: WorkoutExercise,
        val secondsRemaining: Int,
        val totalRestSeconds: Int,
        val elapsedTotalSeconds: Int
    ) : WorkoutSessionState

    data class Paused(
        val frozenState: WorkoutSessionState,
        val elapsedTotalSeconds: Int
    ) : WorkoutSessionState

    data class Completed(
        val workout: Workout,
        val performance: WorkoutPerformance,
        val result: WorkoutResult
    ) : WorkoutSessionState

    data class Abandoned(
        val workout: Workout,
        val elapsedTotalSeconds: Int,
        val exercisesCompleted: Int
    ) : WorkoutSessionState
}

sealed interface WorkoutCueEvent {
    data class CountdownTick(val second: Int) : WorkoutCueEvent
    data class ExerciseStarted(val exercise: WorkoutExercise, val index: Int) : WorkoutCueEvent
    object HalfwayPoint : WorkoutCueEvent
    data class RestStarted(val nextExercise: WorkoutExercise, val restSeconds: Int) : WorkoutCueEvent
    object WorkoutFinished : WorkoutCueEvent
}
