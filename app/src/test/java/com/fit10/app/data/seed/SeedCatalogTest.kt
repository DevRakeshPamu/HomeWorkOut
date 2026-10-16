package com.fit10.app.data.seed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedCatalogTest {

    @Test
    fun exerciseCatalog_hasValidUniqueExercises() {
        val exercises = ExerciseCatalog.ALL
        assertTrue(exercises.isNotEmpty())

        val ids = exercises.map { it.id }
        assertEquals("Exercise IDs must be unique", ids.distinct().size, ids.size)

        exercises.forEach { exercise ->
            assertTrue("Exercise name cannot be blank", exercise.name.isNotBlank())
            assertTrue("Exercise description cannot be blank", exercise.description.isNotBlank())
            assertTrue("Exercise instructions cannot be empty", exercise.instructions.isNotEmpty())
            assertTrue("Exercise target muscles cannot be empty", exercise.targetMuscles.isNotEmpty())
            assertTrue("Exercise default duration must be > 0", exercise.defaultDurationSeconds > 0)
        }
    }

    @Test
    fun workoutCatalog_workoutsFollowTenMinuteStructure() {
        val workouts = WorkoutCatalog.ALL
        assertTrue(workouts.isNotEmpty())

        val ids = workouts.map { it.id }
        assertEquals("Workout IDs must be unique", ids.distinct().size, ids.size)

        workouts.forEach { workout ->
            assertEquals(10, workout.estimatedDurationMinutes)
            assertNotNull(workout.caloriesBurnEstimate)
            assertTrue(workout.caloriesBurnEstimate!! > 0)
            assertEquals("Each workout should contain 10 interval exercises", 10, workout.exercises.size)

            workout.exercises.forEachIndexed { index, we ->
                assertEquals("Exercise order index must match position", index, we.orderIndex)
                assertTrue("Exercise duration must be > 0", we.durationSeconds > 0)
            }
        }
    }

    @Test
    fun programCatalog_kickstartFollowsFourteenDayStructure() {
        val program = ProgramCatalog.KICKSTART_14_DAY
        assertEquals(14, program.totalDays)
        assertEquals(14, program.days.size)

        program.days.forEachIndexed { index, day ->
            assertEquals(index + 1, day.dayNumber)
            if (!day.isRestDay) {
                assertNotNull("Active days must have an assigned workout", day.workout)
            }
        }
    }
}
