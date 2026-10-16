package com.fit10.app.domain.workout

import com.fit10.app.data.seed.ExerciseCatalog
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutExercise
import com.fit10.app.domain.model.WorkoutStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WorkoutSessionEngineTest {

    private val testJob = Job()
    private val testScope = CoroutineScope(Dispatchers.Default + testJob)
    private lateinit var engine: WorkoutSessionEngine

    private val testWorkout = Workout(
        id = "w_test",
        title = "Quick Test Workout",
        description = "Test 2-exercise routine",
        estimatedDurationMinutes = 1,
        fitnessLevel = FitnessLevel.BEGINNER,
        focusArea = FocusArea.FULL_BODY,
        intensity = IntensityPreference.MODERATE,
        exercises = listOf(
            WorkoutExercise(
                exercise = ExerciseCatalog.JUMPING_JACKS,
                orderIndex = 0,
                durationSeconds = 5,
                restDurationSeconds = 3
            ),
            WorkoutExercise(
                exercise = ExerciseCatalog.BODYWEIGHT_SQUATS,
                orderIndex = 1,
                durationSeconds = 4,
                restDurationSeconds = 0
            )
        )
    )

    @Before
    fun setUp() {
        engine = WorkoutSessionEngine(scope = testScope, feedbackCoordinator = null)
    }

    @After
    fun tearDown() {
        engine.stop()
        testScope.cancel()
    }

    @Test
    fun start_entersCountdownState() {
        engine.start(testWorkout, prepCountdownSeconds = 3)
        val state = engine.state.value

        assertTrue(state is WorkoutSessionState.Countdown)
        assertEquals(3, (state as WorkoutSessionState.Countdown).secondsRemaining)
    }

    @Test
    fun tickingCountdown_transitionsToActiveExercise() {
        engine.start(testWorkout, prepCountdownSeconds = 2)

        engine.tickOneSecond() // 1s left
        assertTrue(engine.state.value is WorkoutSessionState.Countdown)

        engine.tickOneSecond() // 0s -> ActiveExercise index 0
        val state = engine.state.value
        assertTrue(state is WorkoutSessionState.ActiveExercise)
        val active = state as WorkoutSessionState.ActiveExercise
        assertEquals(0, active.exerciseIndex)
        assertEquals(5, active.secondsRemaining)
        assertEquals("Jumping Jacks", active.currentExercise.exercise.name)
    }

    @Test
    fun activeExercise_completesDuration_transitionsToRest() {
        engine.start(testWorkout, prepCountdownSeconds = 0)
        assertTrue(engine.state.value is WorkoutSessionState.ActiveExercise)

        // Tick through all 5 seconds of exercise 0
        repeat(5) {
            engine.tickOneSecond()
        }

        val state = engine.state.value
        assertTrue("Should transition to RestInterval", state is WorkoutSessionState.RestInterval)
        val rest = state as WorkoutSessionState.RestInterval
        assertEquals(0, rest.exerciseIndex)
        assertEquals(3, rest.secondsRemaining)
        assertEquals("Bodyweight Squats", rest.nextExercise.exercise.name)
    }

    @Test
    fun pauseAndResume_preservesSecondsRemaining() {
        engine.start(testWorkout, prepCountdownSeconds = 0)
        engine.tickOneSecond()
        engine.tickOneSecond()

        val activeBeforePause = engine.state.value as WorkoutSessionState.ActiveExercise
        assertEquals(3, activeBeforePause.secondsRemaining)

        engine.pause()
        val pausedState = engine.state.value
        assertTrue(pausedState is WorkoutSessionState.Paused)
        assertEquals(2, (pausedState as WorkoutSessionState.Paused).elapsedTotalSeconds)

        engine.resume()
        val resumedState = engine.state.value
        assertTrue(resumedState is WorkoutSessionState.ActiveExercise)
        assertEquals(3, (resumedState as WorkoutSessionState.ActiveExercise).secondsRemaining)
    }

    @Test
    fun skipToNext_recordsSkippedAndAdvances() {
        engine.start(testWorkout, prepCountdownSeconds = 0)
        engine.tickOneSecond() // 1 second into jumping jacks

        engine.skipToNext() // Skips rest / jumping jacks to next exercise
        val state = engine.state.value
        assertTrue("Should transition to RestInterval after skipping", state is WorkoutSessionState.RestInterval)

        engine.skipToNext() // Skips rest directly into exercise 1
        val nextActive = engine.state.value
        assertTrue(nextActive is WorkoutSessionState.ActiveExercise)
        assertEquals(1, (nextActive as WorkoutSessionState.ActiveExercise).exerciseIndex)
        assertEquals("Bodyweight Squats", nextActive.currentExercise.exercise.name)
    }

    @Test
    fun fullWorkoutExecution_completesWithPerformanceAndResult() {
        engine.start(testWorkout, prepCountdownSeconds = 0)

        // Exercise 0 (5s)
        repeat(5) { engine.tickOneSecond() }
        // Rest (3s)
        repeat(3) { engine.tickOneSecond() }
        // Exercise 1 (4s)
        repeat(4) { engine.tickOneSecond() }

        val finalState = engine.state.value
        assertTrue("Should reach Completed state", finalState is WorkoutSessionState.Completed)
        val completed = finalState as WorkoutSessionState.Completed

        assertEquals(WorkoutStatus.COMPLETED, completed.result.status)
        assertEquals(12, completed.result.durationSeconds) // 5 + 3 + 4
        assertEquals(2, completed.result.exercisesCompletedCount)
        assertEquals(2, completed.result.totalExercisesCount)
        assertEquals(1.0f, completed.performance.completionRate)
        assertFalse(completed.performance.exercisePerformances[0].isSkipped)
    }

    @Test
    fun abandon_stopsAndCapturesPartialStats() {
        engine.start(testWorkout, prepCountdownSeconds = 0)
        repeat(3) { engine.tickOneSecond() }

        engine.abandon()
        val state = engine.state.value
        assertTrue(state is WorkoutSessionState.Abandoned)
        val abandoned = state as WorkoutSessionState.Abandoned
        assertEquals(3, abandoned.elapsedTotalSeconds)
    }
}
