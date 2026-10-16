package com.fit10.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainModelTest {

    @Test
    fun fitnessGoal_containsExpectedValues() {
        val expected = setOf("LOSE_WEIGHT", "BUILD_STRENGTH", "IMPROVE_FITNESS", "FLEXIBILITY", "STAY_ACTIVE")
        val actual = FitnessGoal.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun fitnessLevel_containsExpectedValues() {
        val expected = setOf("BEGINNER", "INTERMEDIATE", "ADVANCED")
        val actual = FitnessLevel.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun equipment_containsExpectedValues() {
        val expected = setOf("NONE", "MAT", "DUMBBELLS", "RESISTANCE_BANDS")
        val actual = Equipment.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun intensityPreference_containsExpectedValues() {
        val expected = setOf("EASY", "MODERATE", "CHALLENGING")
        val actual = IntensityPreference.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun focusArea_containsExpectedValues() {
        val expected = setOf("FULL_BODY", "CORE", "UPPER_BODY", "LOWER_BODY", "MOBILITY")
        val actual = FocusArea.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun exerciseType_containsExpectedValues() {
        val expected = setOf("STRENGTH", "CARDIO", "MOBILITY", "CORE", "WARMUP", "COOLDOWN")
        val actual = ExerciseType.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun impactLevel_containsExpectedValues() {
        val expected = setOf("LOW", "MODERATE", "HIGH")
        val actual = ImpactLevel.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun workoutStatus_containsExpectedValues() {
        val expected = setOf("NOT_STARTED", "IN_PROGRESS", "PAUSED", "COMPLETED", "ABANDONED")
        val actual = WorkoutStatus.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun workoutFeedback_containsExpectedValues() {
        val expected = setOf("TOO_EASY", "JUST_RIGHT", "TOO_HARD")
        val actual = WorkoutFeedback.entries.map { it.name }.toSet()
        assertEquals(expected, actual)
    }

    @Test
    fun userProfile_defaultValuesAndImmutability() {
        val profile = UserProfile(
            id = "u1",
            name = "Alex",
            fitnessGoal = FitnessGoal.BUILD_STRENGTH,
            fitnessLevel = FitnessLevel.INTERMEDIATE
        )

        assertEquals(10, profile.targetWorkoutDurationMinutes)
        assertEquals(4, profile.workoutsPerWeek)
        assertEquals(setOf(Equipment.NONE), profile.availableEquipment)
        assertEquals(IntensityPreference.MODERATE, profile.intensityPreference)
        assertFalse(profile.isReminderEnabled)

        val updatedProfile = profile.copy(workoutsPerWeek = 5, isReminderEnabled = true)
        assertNotSame(profile, updatedProfile)
        assertEquals(4, profile.workoutsPerWeek)
        assertEquals(5, updatedProfile.workoutsPerWeek)
        assertTrue(updatedProfile.isReminderEnabled)
    }

    @Test
    fun workoutHierarchy_compositionIntegrity() {
        val pushUp = Exercise(
            id = "ex_pushup",
            name = "Push-ups",
            description = "Upper body push exercise",
            type = ExerciseType.STRENGTH,
            focusArea = FocusArea.UPPER_BODY,
            equipmentRequired = Equipment.NONE,
            impactLevel = ImpactLevel.LOW,
            defaultDurationSeconds = 40,
            defaultReps = 12
        )

        val workoutExercise = WorkoutExercise(
            exercise = pushUp,
            orderIndex = 0,
            durationSeconds = 45,
            restDurationSeconds = 15
        )

        val workout = Workout(
            id = "w1",
            title = "10-Min Upper Body",
            description = "Quick upper body routine",
            fitnessLevel = FitnessLevel.BEGINNER,
            focusArea = FocusArea.UPPER_BODY,
            intensity = IntensityPreference.MODERATE,
            exercises = listOf(workoutExercise)
        )

        val day = ProgramDay(
            dayNumber = 1,
            title = "Day 1 - Push Start",
            workout = workout
        )

        val program = Program(
            id = "prog_1",
            title = "Fit10 Kickstart",
            description = "14-day kickstart program",
            goal = FitnessGoal.IMPROVE_FITNESS,
            fitnessLevel = FitnessLevel.BEGINNER,
            totalDays = 14,
            daysPerWeek = 5,
            estimatedWeeks = 2,
            days = listOf(day)
        )

        assertEquals(1, program.days.size)
        assertEquals("Push-ups", program.days[0].workout?.exercises?.get(0)?.exercise?.name)
        assertFalse(program.days[0].isRestDay)
        assertFalse(program.days[0].isCompleted)
    }

    @Test
    fun workoutSession_stateTransitions() {
        val session = WorkoutSession(
            id = "s1",
            workoutId = "w1",
            workoutTitle = "Core Burn",
            status = WorkoutStatus.NOT_STARTED
        )

        val inProgressSession = session.copy(
            status = WorkoutStatus.IN_PROGRESS,
            startedAtEpochMs = 1000L,
            currentExerciseIndex = 1
        )

        val completedSession = inProgressSession.copy(
            status = WorkoutStatus.COMPLETED,
            completedAtEpochMs = 1600L,
            elapsedSeconds = 600
        )

        assertEquals(WorkoutStatus.NOT_STARTED, session.status)
        assertEquals(WorkoutStatus.IN_PROGRESS, inProgressSession.status)
        assertEquals(WorkoutStatus.COMPLETED, completedSession.status)
        assertEquals(600, completedSession.elapsedSeconds)
    }

    @Test
    fun workoutResult_and_performance_creation() {
        val result = WorkoutResult(
            sessionId = "s1",
            workoutId = "w1",
            workoutTitle = "Core Burn",
            status = WorkoutStatus.COMPLETED,
            durationSeconds = 600,
            exercisesCompletedCount = 8,
            totalExercisesCount = 8,
            caloriesBurned = 75,
            feedback = WorkoutFeedback.JUST_RIGHT
        )

        val performance = WorkoutPerformance(
            sessionId = "s1",
            workoutId = "w1",
            completionRate = 1.0f,
            actualDurationSeconds = 600,
            targetDurationSeconds = 600,
            perceivedExertion = 7,
            exercisePerformances = listOf(
                ExercisePerformance(
                    exerciseId = "ex1",
                    exerciseName = "Plank",
                    completedSeconds = 45,
                    isSkipped = false
                )
            )
        )

        assertEquals(WorkoutFeedback.JUST_RIGHT, result.feedback)
        assertEquals(1.0f, performance.completionRate)
        assertEquals(1, performance.exercisePerformances.size)
    }

    @Test
    fun adaptationResult_and_reminder_creation() {
        val adaptation = AdaptationResult(
            previousLevel = FitnessLevel.BEGINNER,
            recommendedLevel = FitnessLevel.INTERMEDIATE,
            intensityAdjustment = IntensityPreference.CHALLENGING,
            adaptationReason = "Consistently rated workouts as too easy with 100% completion"
        )

        assertEquals(FitnessLevel.BEGINNER, adaptation.previousLevel)
        assertEquals(FitnessLevel.INTERMEDIATE, adaptation.recommendedLevel)
        assertEquals(10, adaptation.recommendedDurationMinutes)

        val reminder = Reminder(
            id = "rem_1",
            userId = "u1",
            hour = 8,
            minute = 30
        )

        assertTrue(reminder.isEnabled)
        assertEquals(8, reminder.hour)
        assertEquals(30, reminder.minute)
        assertTrue(reminder.daysOfWeek.contains(1))
    }
}
