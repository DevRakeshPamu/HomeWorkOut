package com.fit10.app.data.db.mapper

import com.fit10.app.data.db.entity.ExerciseEntity
import com.fit10.app.data.db.entity.ProgramDayEntity
import com.fit10.app.data.db.entity.ProgramDayWithWorkout
import com.fit10.app.data.db.entity.ProgramEntity
import com.fit10.app.data.db.entity.ProgramWithDays
import com.fit10.app.data.db.entity.ReminderEntity
import com.fit10.app.data.db.entity.UserProfileEntity
import com.fit10.app.data.db.entity.WorkoutEntity
import com.fit10.app.data.db.entity.WorkoutExerciseEntity
import com.fit10.app.data.db.entity.WorkoutResultEntity
import com.fit10.app.data.db.entity.WorkoutWithExercises
import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.Exercise
import com.fit10.app.domain.model.ExerciseType
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.ImpactLevel
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.Program
import com.fit10.app.domain.model.ProgramDay
import com.fit10.app.domain.model.Reminder
import com.fit10.app.domain.model.UserPlan
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutExercise
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutResult
import com.fit10.app.domain.model.WorkoutStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Fit10MappersTest {

    @Test
    fun userProfileMapper_roundTrip() {
        val domain = UserProfile(
            id = "u1",
            name = "Rakesh",
            fitnessGoal = FitnessGoal.BUILD_STRENGTH,
            fitnessLevel = FitnessLevel.INTERMEDIATE,
            availableEquipment = setOf(Equipment.NONE, Equipment.MAT),
            targetWorkoutDurationMinutes = 10,
            workoutsPerWeek = 4,
            preferredFocusAreas = setOf(FocusArea.FULL_BODY, FocusArea.CORE),
            intensityPreference = IntensityPreference.MODERATE,
            isReminderEnabled = true
        )

        val entity = domain.toEntity()
        assertEquals(domain.id, entity.id)
        assertEquals(domain.name, entity.name)
        assertEquals(domain.fitnessGoal, entity.fitnessGoal)
        assertEquals(domain.availableEquipment, entity.availableEquipment)

        val backToDomain = entity.toDomain()
        assertEquals(domain, backToDomain)
    }

    @Test
    fun exerciseMapper_roundTrip() {
        val domain = Exercise(
            id = "ex_squat",
            name = "Air Squats",
            description = "Lower body compound movement",
            type = ExerciseType.STRENGTH,
            focusArea = FocusArea.LOWER_BODY,
            equipmentRequired = Equipment.NONE,
            impactLevel = ImpactLevel.LOW,
            defaultDurationSeconds = 40,
            defaultReps = 15,
            instructions = listOf("Stand feet shoulder-width", "Hips down and back"),
            tips = listOf("Keep chest up"),
            targetMuscles = listOf("Quadriceps", "Glutes")
        )

        val entity = domain.toEntity()
        assertEquals(domain.id, entity.id)
        assertEquals(domain.instructions, entity.instructions)

        val backToDomain = entity.toDomain()
        assertEquals(domain, backToDomain)
    }

    @Test
    fun workoutMapper_roundTrip() {
        val exEntity = ExerciseEntity(
            id = "ex_plank",
            name = "Plank",
            description = "Isometric core hold",
            type = ExerciseType.CORE,
            focusArea = FocusArea.CORE,
            equipmentRequired = Equipment.NONE,
            impactLevel = ImpactLevel.LOW,
            defaultDurationSeconds = 45,
            defaultReps = null,
            instructions = listOf("Forearms on ground"),
            tips = emptyList(),
            targetMuscles = listOf("Abs"),
            animationUrl = null,
            thumbnailUrl = null
        )

        val workoutEntity = WorkoutEntity(
            id = "w_core",
            title = "10-Min Core Burner",
            description = "Quick daily core routine",
            estimatedDurationMinutes = 10,
            fitnessLevel = FitnessLevel.BEGINNER,
            focusArea = FocusArea.CORE,
            intensity = IntensityPreference.MODERATE,
            isCustom = false,
            caloriesBurnEstimate = 65
        )

        val workoutExerciseEntity = WorkoutExerciseEntity(
            workoutId = "w_core",
            exerciseId = "ex_plank",
            orderIndex = 0,
            durationSeconds = 45,
            reps = null,
            restDurationSeconds = 15
        )

        val relation = WorkoutWithExercises(
            workout = workoutEntity,
            workoutExercises = listOf(workoutExerciseEntity),
            exercises = listOf(exEntity)
        )

        val domainWorkout = relation.toDomain()
        assertEquals("w_core", domainWorkout.id)
        assertEquals(1, domainWorkout.exercises.size)
        assertEquals("Plank", domainWorkout.exercises[0].exercise.name)
        assertEquals(45, domainWorkout.exercises[0].durationSeconds)

        val backToEntity = domainWorkout.toEntity()
        assertEquals(workoutEntity, backToEntity)

        val exerciseEntities = domainWorkout.toExerciseEntities()
        assertEquals(1, exerciseEntities.size)
        assertEquals("ex_plank", exerciseEntities[0].exerciseId)
    }

    @Test
    fun workoutResultMapper_roundTrip() {
        val domain = WorkoutResult(
            sessionId = "sess_100",
            workoutId = "w_core",
            workoutTitle = "10-Min Core Burner",
            status = WorkoutStatus.COMPLETED,
            durationSeconds = 600,
            exercisesCompletedCount = 8,
            totalExercisesCount = 8,
            caloriesBurned = 72,
            feedback = WorkoutFeedback.JUST_RIGHT,
            completedAtEpochMs = 1720000000000L
        )

        val entity = domain.toEntity()
        assertEquals(domain.sessionId, entity.sessionId)
        assertEquals(domain.status, entity.status)
        assertEquals(domain.feedback, entity.feedback)

        val backToDomain = entity.toDomain()
        assertEquals(domain, backToDomain)
    }

    @Test
    fun programAndUserPlanMapper_roundTrip() {
        val progEntity = ProgramEntity(
            id = "prog_kickstart",
            title = "Fit10 Kickstart",
            description = "Get started in 14 days",
            goal = FitnessGoal.IMPROVE_FITNESS,
            fitnessLevel = FitnessLevel.BEGINNER,
            totalDays = 14,
            daysPerWeek = 5,
            estimatedWeeks = 2
        )

        val dayEntity = ProgramDayEntity(
            programId = "prog_kickstart",
            dayNumber = 1,
            title = "Day 1: Full Body Intro",
            isRestDay = false,
            workoutId = null,
            isCompleted = true
        )

        val relation = ProgramWithDays(
            program = progEntity,
            daysWithWorkouts = listOf(
                ProgramDayWithWorkout(
                    day = dayEntity,
                    workout = null
                )
            )
        )

        val domainProgram = relation.toDomain()
        assertEquals(1, domainProgram.days.size)
        assertEquals(1, domainProgram.days[0].dayNumber)
        assertTrue(domainProgram.days[0].isCompleted)

        val userPlan = UserPlan(
            id = "plan_1",
            userId = "u1",
            programId = "prog_kickstart",
            currentDayNumber = 2,
            totalDays = 14,
            isCompleted = false
        )

        val userPlanEntity = userPlan.toEntity()
        assertEquals(userPlan.id, userPlanEntity.id)
        assertEquals(2, userPlanEntity.currentDayNumber)

        val backToUserPlan = userPlanEntity.toDomain()
        assertEquals(userPlan.id, backToUserPlan.id)
    }

    @Test
    fun reminderMapper_roundTrip() {
        val domain = Reminder(
            id = "rem_1",
            userId = "u1",
            isEnabled = true,
            hour = 7,
            minute = 15,
            daysOfWeek = setOf(1, 2, 3, 4, 5),
            title = "Morning Fit10",
            message = "Crush your 10 minutes!"
        )

        val entity = domain.toEntity()
        assertEquals(domain.id, entity.id)
        assertEquals(domain.daysOfWeek, entity.daysOfWeek)

        val backToDomain = entity.toDomain()
        assertEquals(domain, backToDomain)
    }
}
