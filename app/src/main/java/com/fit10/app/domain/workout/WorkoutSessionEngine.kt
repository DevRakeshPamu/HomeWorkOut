package com.fit10.app.domain.workout

import com.fit10.app.core.feedback.WorkoutFeedbackCoordinator
import com.fit10.app.domain.model.ExercisePerformance
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutExercise
import com.fit10.app.domain.model.WorkoutPerformance
import com.fit10.app.domain.model.WorkoutResult
import com.fit10.app.domain.model.WorkoutStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Real-time interval execution engine for Fit10 workouts.
 * Handles countdown, active work intervals, rest intervals, pause/resume, and performance metrics.
 */
class WorkoutSessionEngine(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val feedbackCoordinator: WorkoutFeedbackCoordinator? = null
) {

    private val _state = MutableStateFlow<WorkoutSessionState>(WorkoutSessionState.Idle)
    val state: StateFlow<WorkoutSessionState> = _state.asStateFlow()

    private val _cueEvents = MutableSharedFlow<WorkoutCueEvent>(extraBufferCapacity = 16)
    val cueEvents: SharedFlow<WorkoutCueEvent> = _cueEvents.asSharedFlow()

    private var activeWorkout: Workout? = null
    private var tickerJob: Job? = null

    // Track exercise-level performance
    private val exercisePerformanceMap = mutableMapOf<Int, MutableExerciseRecord>()
    private var sessionStartTimeMs: Long = 0L

    data class MutableExerciseRecord(
        val exercise: WorkoutExercise,
        var secondsCompleted: Int = 0,
        var isSkipped: Boolean = false,
        var repsCompleted: Int? = null
    )

    fun start(workout: Workout, prepCountdownSeconds: Int = 5) {
        tickerJob?.cancel()
        activeWorkout = workout
        sessionStartTimeMs = System.currentTimeMillis()
        exercisePerformanceMap.clear()

        workout.exercises.forEachIndexed { index, exercise ->
            exercisePerformanceMap[index] = MutableExerciseRecord(exercise)
        }

        if (prepCountdownSeconds > 0) {
            _state.value = WorkoutSessionState.Countdown(
                workout = workout,
                secondsRemaining = prepCountdownSeconds,
                totalCountdownSeconds = prepCountdownSeconds
            )
            feedbackCoordinator?.onCountdownTick()
            _cueEvents.tryEmit(WorkoutCueEvent.CountdownTick(prepCountdownSeconds))
            startTicker()
        } else {
            startExercise(0, 0)
        }
    }

    fun pause() {
        val current = _state.value
        if (current is WorkoutSessionState.ActiveExercise || current is WorkoutSessionState.RestInterval || current is WorkoutSessionState.Countdown) {
            tickerJob?.cancel()
            val elapsed = when (current) {
                is WorkoutSessionState.ActiveExercise -> current.elapsedTotalSeconds
                is WorkoutSessionState.RestInterval -> current.elapsedTotalSeconds
                is WorkoutSessionState.Countdown -> 0
            }
            _state.value = WorkoutSessionState.Paused(frozenState = current, elapsedTotalSeconds = elapsed)
        }
    }

    fun resume() {
        val current = _state.value
        if (current is WorkoutSessionState.Paused) {
            _state.value = current.frozenState
            startTicker()
        }
    }

    fun skipToNext() {
        when (val current = _state.value) {
            is WorkoutSessionState.Countdown -> {
                startExercise(0, 0)
            }
            is WorkoutSessionState.ActiveExercise -> {
                exercisePerformanceMap[current.exerciseIndex]?.isSkipped = true
                moveToRestOrNext(current.exerciseIndex, current.elapsedTotalSeconds)
            }
            is WorkoutSessionState.RestInterval -> {
                val nextIndex = current.exerciseIndex + 1
                val workout = current.workout
                if (nextIndex < workout.exercises.size) {
                    startExercise(nextIndex, current.elapsedTotalSeconds)
                } else {
                    finishWorkout(workout, current.elapsedTotalSeconds)
                }
            }
            is WorkoutSessionState.Paused -> {
                // Resume into next
                _state.value = current.frozenState
                skipToNext()
            }
            else -> Unit
        }
    }

    fun previous() {
        val current = _state.value
        val (currentIndex, elapsed) = when (current) {
            is WorkoutSessionState.ActiveExercise -> Pair(current.exerciseIndex, current.elapsedTotalSeconds)
            is WorkoutSessionState.RestInterval -> Pair(current.exerciseIndex, current.elapsedTotalSeconds)
            is WorkoutSessionState.Paused -> {
                when (val frozen = current.frozenState) {
                    is WorkoutSessionState.ActiveExercise -> Pair(frozen.exerciseIndex, current.elapsedTotalSeconds)
                    is WorkoutSessionState.RestInterval -> Pair(frozen.exerciseIndex, current.elapsedTotalSeconds)
                    else -> Pair(0, 0)
                }
            }
            else -> return
        }

        if (currentIndex > 0) {
            startExercise(currentIndex - 1, elapsed)
        }
    }

    fun abandon() {
        tickerJob?.cancel()
        val workout = activeWorkout ?: return
        val current = _state.value
        val elapsed = when (current) {
            is WorkoutSessionState.ActiveExercise -> current.elapsedTotalSeconds
            is WorkoutSessionState.RestInterval -> current.elapsedTotalSeconds
            is WorkoutSessionState.Paused -> current.elapsedTotalSeconds
            else -> 0
        }
        val completedCount = exercisePerformanceMap.values.count { !it.isSkipped && it.secondsCompleted > 0 }
        _state.value = WorkoutSessionState.Abandoned(
            workout = workout,
            elapsedTotalSeconds = elapsed,
            exercisesCompleted = completedCount
        )
    }

    fun tickOneSecond() {
        when (val current = _state.value) {
            is WorkoutSessionState.Countdown -> {
                val newRemaining = current.secondsRemaining - 1
                if (newRemaining > 0) {
                    _state.value = current.copy(secondsRemaining = newRemaining)
                    if (newRemaining <= 3) {
                        feedbackCoordinator?.onCountdownTick()
                    }
                    _cueEvents.tryEmit(WorkoutCueEvent.CountdownTick(newRemaining))
                } else {
                    startExercise(0, 0)
                }
            }
            is WorkoutSessionState.ActiveExercise -> {
                val newRemaining = current.secondsRemaining - 1
                val newElapsed = current.elapsedTotalSeconds + 1
                exercisePerformanceMap[current.exerciseIndex]?.let {
                    it.secondsCompleted += 1
                }

                if (newRemaining <= 3 && newRemaining > 0) {
                    feedbackCoordinator?.onCountdownTick()
                    _cueEvents.tryEmit(WorkoutCueEvent.CountdownTick(newRemaining))
                } else if (newRemaining == current.totalExerciseSeconds / 2) {
                    feedbackCoordinator?.onHalfway()
                    _cueEvents.tryEmit(WorkoutCueEvent.HalfwayPoint)
                }

                if (newRemaining <= 0) {
                    moveToRestOrNext(current.exerciseIndex, newElapsed)
                } else {
                    _state.value = current.copy(
                        secondsRemaining = newRemaining,
                        elapsedTotalSeconds = newElapsed
                    )
                }
            }
            is WorkoutSessionState.RestInterval -> {
                val newRemaining = current.secondsRemaining - 1
                val newElapsed = current.elapsedTotalSeconds + 1

                if (newRemaining <= 3 && newRemaining > 0) {
                    feedbackCoordinator?.onCountdownTick()
                    _cueEvents.tryEmit(WorkoutCueEvent.CountdownTick(newRemaining))
                }

                if (newRemaining <= 0) {
                    val nextIndex = current.exerciseIndex + 1
                    if (nextIndex < current.workout.exercises.size) {
                        startExercise(nextIndex, newElapsed)
                    } else {
                        finishWorkout(current.workout, newElapsed)
                    }
                } else {
                    _state.value = current.copy(
                        secondsRemaining = newRemaining,
                        elapsedTotalSeconds = newElapsed
                    )
                }
            }
            else -> Unit
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                tickOneSecond()
            }
        }
    }

    private fun startExercise(index: Int, elapsedSeconds: Int) {
        val workout = activeWorkout ?: return
        if (index >= workout.exercises.size) {
            finishWorkout(workout, elapsedSeconds)
            return
        }

        val exercise = workout.exercises[index]
        _state.value = WorkoutSessionState.ActiveExercise(
            workout = workout,
            exerciseIndex = index,
            currentExercise = exercise,
            secondsRemaining = exercise.durationSeconds,
            totalExerciseSeconds = exercise.durationSeconds,
            elapsedTotalSeconds = elapsedSeconds
        )

        feedbackCoordinator?.onExerciseStart()
        _cueEvents.tryEmit(WorkoutCueEvent.ExerciseStarted(exercise, index))
        startTicker()
    }

    private fun moveToRestOrNext(exerciseIndex: Int, elapsedSeconds: Int) {
        val workout = activeWorkout ?: return
        val currentExercise = workout.exercises[exerciseIndex]
        val nextIndex = exerciseIndex + 1

        if (nextIndex < workout.exercises.size) {
            val restSeconds = currentExercise.restDurationSeconds
            if (restSeconds > 0) {
                val nextExercise = workout.exercises[nextIndex]
                _state.value = WorkoutSessionState.RestInterval(
                    workout = workout,
                    exerciseIndex = exerciseIndex,
                    nextExercise = nextExercise,
                    secondsRemaining = restSeconds,
                    totalRestSeconds = restSeconds,
                    elapsedTotalSeconds = elapsedSeconds
                )
                feedbackCoordinator?.onRestStart()
                _cueEvents.tryEmit(WorkoutCueEvent.RestStarted(nextExercise, restSeconds))
            } else {
                startExercise(nextIndex, elapsedSeconds)
            }
        } else {
            finishWorkout(workout, elapsedSeconds)
        }
    }

    private fun finishWorkout(workout: Workout, elapsedTotalSeconds: Int) {
        tickerJob?.cancel()
        feedbackCoordinator?.onWorkoutCompleted()
        _cueEvents.tryEmit(WorkoutCueEvent.WorkoutFinished)

        val targetSeconds = workout.exercises.sumOf { it.durationSeconds + it.restDurationSeconds }
        val completedExercises = exercisePerformanceMap.values.filter { !it.isSkipped && it.secondsCompleted > 0 }
        val totalExercises = workout.exercises.size

        val completionRate = if (totalExercises > 0) {
            completedExercises.size.toFloat() / totalExercises.toFloat()
        } else 1.0f

        val performanceList = exercisePerformanceMap.entries.sortedBy { it.key }.map { (idx, record) ->
            ExercisePerformance(
                exerciseId = record.exercise.exercise.id,
                exerciseName = record.exercise.exercise.name,
                completedReps = record.repsCompleted ?: record.exercise.reps,
                completedSeconds = record.secondsCompleted,
                isSkipped = record.isSkipped
            )
        }

        val performance = WorkoutPerformance(
            sessionId = UUID.randomUUID().toString(),
            workoutId = workout.id,
            completionRate = completionRate,
            actualDurationSeconds = elapsedTotalSeconds,
            targetDurationSeconds = targetSeconds,
            perceivedExertion = null,
            exercisePerformances = performanceList
        )

        val result = WorkoutResult(
            sessionId = performance.sessionId,
            workoutId = workout.id,
            workoutTitle = workout.title,
            status = WorkoutStatus.COMPLETED,
            durationSeconds = elapsedTotalSeconds,
            exercisesCompletedCount = completedExercises.size,
            totalExercisesCount = totalExercises,
            caloriesBurned = workout.caloriesBurnEstimate ?: (elapsedTotalSeconds * 70 / 600),
            feedback = null,
            completedAtEpochMs = System.currentTimeMillis()
        )

        _state.value = WorkoutSessionState.Completed(
            workout = workout,
            performance = performance,
            result = result
        )
    }

    fun stop() {
        tickerJob?.cancel()
        _state.value = WorkoutSessionState.Idle
    }
}
