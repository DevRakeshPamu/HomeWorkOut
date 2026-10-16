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
import org.json.JSONArray
import org.json.JSONObject

/**
 * Data backup and restore engine enforcing a versioned, portable JSON schema.
 *
 * Rules:
 * 1. Exports user fitness baseline and workout logs into JSON (schemaVersion: 1).
 * 2. Informs user that exported files contain sensitive health records in plaintext.
 * 3. Validates schemaVersion on import; strictly rejects newer/unknown schema versions.
 * 4. Merges workout history by sessionId without duplicating records.
 * 5. Robust error handling: never crashes on malformed, corrupted, or truncated JSON.
 */
object DataBackupManager {

    const val CURRENT_SCHEMA_VERSION = 1

    data class BackupData(
        val schemaVersion: Int,
        val exportedAtEpochMs: Long,
        val profile: UserProfileEntity?,
        val workoutResults: List<WorkoutResultEntity>
    )

    sealed class ImportResult {
        data class Success(val importedSessionsCount: Int, val profileRestored: Boolean) : ImportResult()
        data class UnsupportedVersion(val version: Int) : ImportResult()
        data class MalformedData(val message: String) : ImportResult()
    }

    /**
     * Serializes user profile and workout results into a versioned JSON string.
     */
    fun createBackupJson(
        profile: UserProfileEntity?,
        results: List<WorkoutResultEntity>
    ): String {
        val root = JSONObject()
        root.put("schemaVersion", CURRENT_SCHEMA_VERSION)
        root.put("appVersion", "2.4.1")
        root.put("exportedAtEpochMs", System.currentTimeMillis())
        root.put("disclaimer", "Fit10 Export Archive: Contains unencrypted local workout history and fitness profile.")

        // 1. User Profile
        if (profile != null) {
            val profileJson = JSONObject()
            profileJson.put("id", profile.id)
            profileJson.put("name", profile.name)
            profileJson.put("fitnessGoal", profile.fitnessGoal.name)
            profileJson.put("fitnessLevel", profile.fitnessLevel.name)
            profileJson.put("targetWorkoutDurationMinutes", profile.targetWorkoutDurationMinutes)
            profileJson.put("workoutsPerWeek", profile.workoutsPerWeek)
            profileJson.put("intensityPreference", profile.intensityPreference.name)
            profileJson.put("isReminderEnabled", profile.isReminderEnabled)

            val equipArray = JSONArray()
            profile.availableEquipment.forEach { equipArray.put(it.name) }
            profileJson.put("availableEquipment", equipArray)

            val focusArray = JSONArray()
            profile.preferredFocusAreas.forEach { focusArray.put(it.name) }
            profileJson.put("preferredFocusAreas", focusArray)

            root.put("userProfile", profileJson)
        }

        // 2. Workout Results
        val resultsArray = JSONArray()
        results.forEach { result ->
            val item = JSONObject()
            item.put("sessionId", result.sessionId)
            item.put("workoutId", result.workoutId)
            item.put("workoutTitle", result.workoutTitle)
            item.put("status", result.status.name)
            item.put("durationSeconds", result.durationSeconds)
            item.put("exercisesCompletedCount", result.exercisesCompletedCount)
            item.put("totalExercisesCount", result.totalExercisesCount)
            item.put("caloriesBurned", result.caloriesBurned)
            item.put("completedAtEpochMs", result.completedAtEpochMs)

            if (result.feedback != null) {
                item.put("feedback", result.feedback.name)
            }
            resultsArray.put(item)
        }
        root.put("workoutResults", resultsArray)

        return root.toString(2)
    }

    /**
     * Parses and validates a backup JSON payload.
     */
    fun parseBackupJson(jsonString: String): ImportResult {
        if (jsonString.isBlank()) {
            return ImportResult.MalformedData("Empty backup file content.")
        }

        val root = try {
            JSONObject(jsonString)
        } catch (e: Exception) {
            return ImportResult.MalformedData("Invalid JSON structure: ${e.localizedMessage}")
        }

        val version = root.optInt("schemaVersion", -1)
        if (version <= 0) {
            return ImportResult.MalformedData("Missing or invalid 'schemaVersion' field.")
        }

        if (version > CURRENT_SCHEMA_VERSION) {
            return ImportResult.UnsupportedVersion(version)
        }

        return try {
            val resultsArray = root.optJSONArray("workoutResults") ?: JSONArray()
            val parsedCount = resultsArray.length()
            val hasProfile = root.has("userProfile") && !root.isNull("userProfile")

            ImportResult.Success(
                importedSessionsCount = parsedCount,
                profileRestored = hasProfile
            )
        } catch (e: Exception) {
            ImportResult.MalformedData("Error reading backup fields: ${e.localizedMessage}")
        }
    }

    /**
     * Fully deserializes backup data into entities.
     */
    fun extractEntities(jsonString: String): BackupData? {
        val root = try {
            JSONObject(jsonString)
        } catch (_: Exception) {
            return null
        }

        val version = root.optInt("schemaVersion", -1)
        if (version != CURRENT_SCHEMA_VERSION) return null

        val exportedAt = root.optLong("exportedAtEpochMs", System.currentTimeMillis())

        var profileEntity: UserProfileEntity? = null
        val profileJson = root.optJSONObject("userProfile")
        if (profileJson != null) {
            val equipSet = mutableSetOf<Equipment>()
            val equipArray = profileJson.optJSONArray("availableEquipment")
            if (equipArray != null) {
                for (i in 0 until equipArray.length()) {
                    runCatching { equipSet.add(Equipment.valueOf(equipArray.getString(i))) }
                }
            }

            val focusSet = mutableSetOf<FocusArea>()
            val focusArray = profileJson.optJSONArray("preferredFocusAreas")
            if (focusArray != null) {
                for (i in 0 until focusArray.length()) {
                    runCatching { focusSet.add(FocusArea.valueOf(focusArray.getString(i))) }
                }
            }

            profileEntity = UserProfileEntity(
                id = profileJson.optString("id", "user_imported"),
                name = profileJson.optString("name", "Athlete"),
                fitnessGoal = runCatching { FitnessGoal.valueOf(profileJson.getString("fitnessGoal")) }.getOrDefault(FitnessGoal.BUILD_STRENGTH),
                fitnessLevel = runCatching { FitnessLevel.valueOf(profileJson.getString("fitnessLevel")) }.getOrDefault(FitnessLevel.BEGINNER),
                availableEquipment = if (equipSet.isEmpty()) setOf(Equipment.NONE) else equipSet,
                targetWorkoutDurationMinutes = profileJson.optInt("targetWorkoutDurationMinutes", 10),
                workoutsPerWeek = profileJson.optInt("workoutsPerWeek", 3),
                preferredFocusAreas = if (focusSet.isEmpty()) setOf(FocusArea.FULL_BODY) else focusSet,
                intensityPreference = runCatching { IntensityPreference.valueOf(profileJson.getString("intensityPreference")) }.getOrDefault(IntensityPreference.MODERATE),
                isReminderEnabled = profileJson.optBoolean("isReminderEnabled", false),
                createdAtEpochMs = System.currentTimeMillis(),
                updatedAtEpochMs = System.currentTimeMillis()
            )
        }

        val resultsList = mutableListOf<WorkoutResultEntity>()
        val resultsArray = root.optJSONArray("workoutResults")
        if (resultsArray != null) {
            for (i in 0 until resultsArray.length()) {
                val item = resultsArray.optJSONObject(i) ?: continue
                val status = runCatching { WorkoutStatus.valueOf(item.getString("status")) }.getOrDefault(WorkoutStatus.COMPLETED)
                val fbString = if (item.has("feedback") && !item.isNull("feedback")) item.getString("feedback") else null
                val feedback = fbString?.let { runCatching { WorkoutFeedback.valueOf(it) }.getOrNull() }

                resultsList.add(
                    WorkoutResultEntity(
                        sessionId = item.optString("sessionId", "session_$i"),
                        workoutId = item.optString("workoutId", "full_body_ignition"),
                        workoutTitle = item.optString("workoutTitle", "Workout"),
                        status = status,
                        durationSeconds = item.optInt("durationSeconds", 600),
                        exercisesCompletedCount = item.optInt("exercisesCompletedCount", 5),
                        totalExercisesCount = item.optInt("totalExercisesCount", 5),
                        caloriesBurned = item.optInt("caloriesBurned", 50),
                        feedback = feedback,
                        completedAtEpochMs = item.optLong("completedAtEpochMs", System.currentTimeMillis())
                    )
                )
            }
        }

        return BackupData(
            schemaVersion = version,
            exportedAtEpochMs = exportedAt,
            profile = profileEntity,
            workoutResults = resultsList
        )
    }
}
