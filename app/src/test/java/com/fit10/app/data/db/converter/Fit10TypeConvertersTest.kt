package com.fit10.app.data.db.converter

import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.ExerciseType
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.ImpactLevel
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Fit10TypeConvertersTest {

    private val converters = Fit10TypeConverters()

    @Test
    fun enums_roundTripConversion() {
        // FitnessGoal
        assertEquals(FitnessGoal.BUILD_STRENGTH, converters.toFitnessGoal(converters.fromFitnessGoal(FitnessGoal.BUILD_STRENGTH)))
        assertNull(converters.toFitnessGoal(null))

        // FitnessLevel
        assertEquals(FitnessLevel.ADVANCED, converters.toFitnessLevel(converters.fromFitnessLevel(FitnessLevel.ADVANCED)))

        // IntensityPreference
        assertEquals(IntensityPreference.CHALLENGING, converters.toIntensityPreference(converters.fromIntensityPreference(IntensityPreference.CHALLENGING)))

        // FocusArea
        assertEquals(FocusArea.CORE, converters.toFocusArea(converters.fromFocusArea(FocusArea.CORE)))

        // ExerciseType
        assertEquals(ExerciseType.CARDIO, converters.toExerciseType(converters.fromExerciseType(ExerciseType.CARDIO)))

        // ImpactLevel
        assertEquals(ImpactLevel.LOW, converters.toImpactLevel(converters.fromImpactLevel(ImpactLevel.LOW)))

        // Equipment
        assertEquals(Equipment.DUMBBELLS, converters.toEquipment(converters.fromEquipment(Equipment.DUMBBELLS)))

        // WorkoutStatus
        assertEquals(WorkoutStatus.COMPLETED, converters.toWorkoutStatus(converters.fromWorkoutStatus(WorkoutStatus.COMPLETED)))

        // WorkoutFeedback
        assertEquals(WorkoutFeedback.JUST_RIGHT, converters.toWorkoutFeedback(converters.fromWorkoutFeedback(WorkoutFeedback.JUST_RIGHT)))
    }

    @Test
    fun stringList_roundTripConversion() {
        val original = listOf("Keep back straight", "Engage core", "Breathe steadily")
        val json = converters.fromStringList(original)
        val result = converters.toStringList(json)
        assertEquals(original, result)

        // Empty & null safety
        assertTrue(converters.toStringList(null).isEmpty())
        assertTrue(converters.toStringList("").isEmpty())
    }

    @Test
    fun equipmentSet_roundTripConversion() {
        val original = setOf(Equipment.MAT, Equipment.RESISTANCE_BANDS)
        val json = converters.fromEquipmentSet(original)
        val result = converters.toEquipmentSet(json)
        assertEquals(original, result)

        assertTrue(converters.toEquipmentSet(null).isEmpty())
    }

    @Test
    fun focusAreaSet_roundTripConversion() {
        val original = setOf(FocusArea.CORE, FocusArea.UPPER_BODY)
        val json = converters.fromFocusAreaSet(original)
        val result = converters.toFocusAreaSet(json)
        assertEquals(original, result)

        assertTrue(converters.toFocusAreaSet(null).isEmpty())
    }

    @Test
    fun intSet_roundTripConversion() {
        val original = setOf(1, 3, 5)
        val json = converters.fromIntSet(original)
        val result = converters.toIntSet(json)
        assertEquals(original, result)

        assertTrue(converters.toIntSet(null).isEmpty())
    }
}
