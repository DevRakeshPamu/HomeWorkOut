package com.fit10.app.data.backup

import com.fit10.app.data.db.entity.UserProfileEntity
import com.fit10.app.data.db.entity.WorkoutResultEntity
import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataBackupManagerTest {

    private val sampleProfile = UserProfileEntity(
        id = "user_test_123",
        name = "Athlete",
        fitnessGoal = FitnessGoal.BUILD_STRENGTH,
        fitnessLevel = FitnessLevel.INTERMEDIATE,
        availableEquipment = setOf(Equipment.NONE, Equipment.CHAIR),
        targetWorkoutDurationMinutes = 10,
        workoutsPerWeek = 4,
        preferredFocusAreas = setOf(FocusArea.FULL_BODY, FocusArea.CORE),
        intensityPreference = IntensityPreference.MODERATE,
        isReminderEnabled = true,
        createdAtEpochMs = 1700000000000L,
        updatedAtEpochMs = 1700000000000L
    )

    private val sampleResults = listOf(
        WorkoutResultEntity(
            sessionId = "session_001",
            workoutId = "full_body_ignition",
            workoutTitle = "Full Body Ignition",
            status = WorkoutStatus.COMPLETED,
            durationSeconds = 600,
            exercisesCompletedCount = 5,
            totalExercisesCount = 5,
            caloriesBurned = 55,
            feedback = WorkoutFeedback.JUST_RIGHT,
            completedAtEpochMs = 1700000000000L
        ),
        WorkoutResultEntity(
            sessionId = "session_002",
            workoutId = "core_shred",
            workoutTitle = "Core Shred",
            status = WorkoutStatus.COMPLETED,
            durationSeconds = 610,
            exercisesCompletedCount = 6,
            totalExercisesCount = 6,
            caloriesBurned = 62,
            feedback = WorkoutFeedback.TOO_HARD,
            completedAtEpochMs = 1700100000000L
        )
    )

    @Test
    fun roundTrip_exportAndParse_succeeds() {
        val json = DataBackupManager.createBackupJson(sampleProfile, sampleResults)
        val parseResult = DataBackupManager.parseBackupJson(json)

        assertTrue(parseResult is DataBackupManager.ImportResult.Success)
        val success = parseResult as DataBackupManager.ImportResult.Success
        assertEquals(2, success.importedSessionsCount)
        assertTrue(success.profileRestored)

        val extracted = DataBackupManager.extractEntities(json)
        assertNotNull(extracted)
        assertEquals(DataBackupManager.CURRENT_SCHEMA_VERSION, extracted!!.schemaVersion)
        assertEquals(sampleProfile.id, extracted.profile?.id)
        assertEquals(2, extracted.workoutResults.size)
        assertEquals("session_001", extracted.workoutResults[0].sessionId)
        assertEquals(WorkoutFeedback.JUST_RIGHT, extracted.workoutResults[0].feedback)
    }

    @Test
    fun newerSchemaVersion_rejectedWithUnsupportedVersion() {
        val futureJson = """
            {
                "schemaVersion": 2,
                "workoutResults": []
            }
        """.trimIndent()

        val parseResult = DataBackupManager.parseBackupJson(futureJson)
        assertTrue(parseResult is DataBackupManager.ImportResult.UnsupportedVersion)
        val unsupported = parseResult as DataBackupManager.ImportResult.UnsupportedVersion
        assertEquals(2, unsupported.version)
    }

    @Test
    fun malformedJson_handledGracefullyWithoutCrashing() {
        val corruptJson = "{ this is not valid json content [ { ..."
        val parseResult = DataBackupManager.parseBackupJson(corruptJson)
        assertTrue(parseResult is DataBackupManager.ImportResult.MalformedData)
    }

    @Test
    fun emptyJson_handledGracefully() {
        val parseResult = DataBackupManager.parseBackupJson("")
        assertTrue(parseResult is DataBackupManager.ImportResult.MalformedData)
    }

    @Test
    fun duplicateMerge_mergesBySessionId() {
        val originalList = sampleResults
        val importedDuplicate = sampleResults.first().copy(caloriesBurned = 99)

        // Simulating Room OnConflictStrategy.REPLACE or map merge
        val mergedMap = (originalList + importedDuplicate).associateBy { it.sessionId }
        assertEquals(2, mergedMap.size)
        assertEquals(99, mergedMap["session_001"]?.caloriesBurned)
    }
}
