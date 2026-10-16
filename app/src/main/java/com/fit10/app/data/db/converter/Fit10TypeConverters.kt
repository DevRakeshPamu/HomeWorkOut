package com.fit10.app.data.db.converter

import androidx.room.TypeConverter
import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.ExerciseType
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.ImpactLevel
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Fit10TypeConverters {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    // FitnessGoal
    @TypeConverter
    fun fromFitnessGoal(value: FitnessGoal?): String? = value?.name

    @TypeConverter
    fun toFitnessGoal(value: String?): FitnessGoal? =
        value?.let { runCatching { FitnessGoal.valueOf(it) }.getOrNull() }

    // FitnessLevel
    @TypeConverter
    fun fromFitnessLevel(value: FitnessLevel?): String? = value?.name

    @TypeConverter
    fun toFitnessLevel(value: String?): FitnessLevel? =
        value?.let { runCatching { FitnessLevel.valueOf(it) }.getOrNull() }

    // IntensityPreference
    @TypeConverter
    fun fromIntensityPreference(value: IntensityPreference?): String? = value?.name

    @TypeConverter
    fun toIntensityPreference(value: String?): IntensityPreference? =
        value?.let { runCatching { IntensityPreference.valueOf(it) }.getOrNull() }

    // FocusArea
    @TypeConverter
    fun fromFocusArea(value: FocusArea?): String? = value?.name

    @TypeConverter
    fun toFocusArea(value: String?): FocusArea? =
        value?.let { runCatching { FocusArea.valueOf(it) }.getOrNull() }

    // ExerciseType
    @TypeConverter
    fun fromExerciseType(value: ExerciseType?): String? = value?.name

    @TypeConverter
    fun toExerciseType(value: String?): ExerciseType? =
        value?.let { runCatching { ExerciseType.valueOf(it) }.getOrNull() }

    // ImpactLevel
    @TypeConverter
    fun fromImpactLevel(value: ImpactLevel?): String? = value?.name

    @TypeConverter
    fun toImpactLevel(value: String?): ImpactLevel? =
        value?.let { runCatching { ImpactLevel.valueOf(it) }.getOrNull() }

    // Equipment
    @TypeConverter
    fun fromEquipment(value: Equipment?): String? = value?.name

    @TypeConverter
    fun toEquipment(value: String?): Equipment? =
        value?.let { runCatching { Equipment.valueOf(it) }.getOrNull() }

    // WorkoutStatus
    @TypeConverter
    fun fromWorkoutStatus(value: WorkoutStatus?): String? = value?.name

    @TypeConverter
    fun toWorkoutStatus(value: String?): WorkoutStatus? =
        value?.let { runCatching { WorkoutStatus.valueOf(it) }.getOrNull() }

    // WorkoutFeedback
    @TypeConverter
    fun fromWorkoutFeedback(value: WorkoutFeedback?): String? = value?.name

    @TypeConverter
    fun toWorkoutFeedback(value: String?): WorkoutFeedback? =
        value?.let { runCatching { WorkoutFeedback.valueOf(it) }.getOrNull() }

    // String List (e.g. instructions, tips, targetMuscles)
    @TypeConverter
    fun fromStringList(value: List<String>?): String =
        json.encodeToString(value ?: emptyList())

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        if (value.isNullOrBlank()) emptyList() else runCatching { json.decodeFromString<List<String>>(value) }.getOrDefault(emptyList())

    // Equipment Set
    @TypeConverter
    fun fromEquipmentSet(value: Set<Equipment>?): String =
        json.encodeToString((value ?: emptySet()).map { it.name })

    @TypeConverter
    fun toEquipmentSet(value: String?): Set<Equipment> =
        if (value.isNullOrBlank()) emptySet()
        else runCatching {
            json.decodeFromString<List<String>>(value).mapNotNull { name ->
                runCatching { Equipment.valueOf(name) }.getOrNull()
            }.toSet()
        }.getOrDefault(emptySet())

    // FocusArea Set
    @TypeConverter
    fun fromFocusAreaSet(value: Set<FocusArea>?): String =
        json.encodeToString((value ?: emptySet()).map { it.name })

    @TypeConverter
    fun toFocusAreaSet(value: String?): Set<FocusArea> =
        if (value.isNullOrBlank()) emptySet()
        else runCatching {
            json.decodeFromString<List<String>>(value).mapNotNull { name ->
                runCatching { FocusArea.valueOf(name) }.getOrNull()
            }.toSet()
        }.getOrDefault(emptySet())

    // Int Set (e.g. daysOfWeek)
    @TypeConverter
    fun fromIntSet(value: Set<Int>?): String =
        json.encodeToString((value ?: emptySet()).toList())

    @TypeConverter
    fun toIntSet(value: String?): Set<Int> =
        if (value.isNullOrBlank()) emptySet()
        else runCatching { json.decodeFromString<List<Int>>(value).toSet() }.getOrDefault(emptySet())
}
