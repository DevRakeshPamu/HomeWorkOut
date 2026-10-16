package com.fit10.app.domain.adaptation

import com.fit10.app.data.seed.WorkoutCatalog
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutHistory
import com.fit10.app.domain.model.WorkoutResult
import com.fit10.app.domain.model.WorkoutStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonalizationEngineTest {

    private val baseProfile = UserProfile(
        id = "u1",
        name = "Alex",
        fitnessGoal = FitnessGoal.BUILD_STRENGTH,
        fitnessLevel = FitnessLevel.BEGINNER,
        preferredFocusAreas = setOf(FocusArea.FULL_BODY, FocusArea.UPPER_BODY),
        intensityPreference = IntensityPreference.MODERATE
    )

    @Test
    fun recommendDailyWorkout_avoidsConsecutiveIdenticalFocusAreas() {
        val historyWithYesterdayLowerBody = WorkoutHistory(
            recentResults = listOf(
                WorkoutResult(
                    sessionId = "s1",
                    workoutId = WorkoutCatalog.LOWER_BODY_BURN.id,
                    workoutTitle = WorkoutCatalog.LOWER_BODY_BURN.title,
                    status = WorkoutStatus.COMPLETED,
                    durationSeconds = 600,
                    exercisesCompletedCount = 10,
                    totalExercisesCount = 10,
                    caloriesBurned = 90,
                    completedAtEpochMs = System.currentTimeMillis() - 86400000L
                )
            )
        )

        val recommendation = PersonalizationEngine.recommendDailyWorkout(
            userProfile = baseProfile,
            availableWorkouts = WorkoutCatalog.ALL,
            history = historyWithYesterdayLowerBody
        )

        // Must NOT recommend Lower Body again today
        assertNotEquals(FocusArea.LOWER_BODY, recommendation.focusArea)
    }

    @Test
    fun evaluateAdaptation_tooEasyFeedback_progressesLevel() {
        val results = listOf(
            createResult("s1", WorkoutFeedback.TOO_EASY),
            createResult("s2", WorkoutFeedback.TOO_EASY),
            createResult("s3", WorkoutFeedback.TOO_EASY)
        )

        val adaptation = PersonalizationEngine.evaluateAdaptation(baseProfile, results)

        assertEquals(FitnessLevel.BEGINNER, adaptation.previousLevel)
        assertEquals(FitnessLevel.INTERMEDIATE, adaptation.recommendedLevel)
        assertEquals(IntensityPreference.CHALLENGING, adaptation.intensityAdjustment)
    }

    @Test
    fun evaluateAdaptation_tooHardFeedback_easesLevelAndSuggestsReplacements() {
        val intermediateProfile = baseProfile.copy(fitnessLevel = FitnessLevel.INTERMEDIATE)
        val results = listOf(
            createResult("s1", WorkoutFeedback.TOO_HARD),
            createResult("s2", WorkoutFeedback.TOO_HARD),
            createResult("s3", WorkoutFeedback.TOO_HARD)
        )

        val adaptation = PersonalizationEngine.evaluateAdaptation(intermediateProfile, results)

        assertEquals(FitnessLevel.INTERMEDIATE, adaptation.previousLevel)
        assertEquals(FitnessLevel.BEGINNER, adaptation.recommendedLevel)
        assertEquals(IntensityPreference.EASY, adaptation.intensityAdjustment)
        assertTrue(adaptation.suggestedExerciseReplacements.isNotEmpty())
    }

    @Test
    fun evaluateAdaptation_justRightFeedback_maintainsCurrentLevel() {
        val results = listOf(
            createResult("s1", WorkoutFeedback.JUST_RIGHT),
            createResult("s2", WorkoutFeedback.JUST_RIGHT)
        )

        val adaptation = PersonalizationEngine.evaluateAdaptation(baseProfile, results)

        assertEquals(FitnessLevel.BEGINNER, adaptation.previousLevel)
        assertEquals(FitnessLevel.BEGINNER, adaptation.recommendedLevel)
    }

    private fun createResult(id: String, feedback: WorkoutFeedback): WorkoutResult {
        return WorkoutResult(
            sessionId = id,
            workoutId = "w1",
            workoutTitle = "Test",
            status = WorkoutStatus.COMPLETED,
            durationSeconds = 600,
            exercisesCompletedCount = 10,
            totalExercisesCount = 10,
            caloriesBurned = 75,
            feedback = feedback,
            completedAtEpochMs = System.currentTimeMillis()
        )
    }
}
