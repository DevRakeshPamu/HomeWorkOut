package com.fit10.app.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fit10.app.data.db.dao.ExerciseDao
import com.fit10.app.data.db.dao.ProgramDao
import com.fit10.app.data.db.dao.ReminderDao
import com.fit10.app.data.db.dao.UserProfileDao
import com.fit10.app.data.db.dao.WorkoutDao
import com.fit10.app.data.db.dao.WorkoutResultDao
import com.fit10.app.data.db.entity.ExerciseEntity
import com.fit10.app.data.db.entity.ProgramDayEntity
import com.fit10.app.data.db.entity.ProgramEntity
import com.fit10.app.data.db.entity.ReminderEntity
import com.fit10.app.data.db.entity.UserProfileEntity
import com.fit10.app.data.db.entity.WorkoutEntity
import com.fit10.app.data.db.entity.WorkoutExerciseEntity
import com.fit10.app.data.db.entity.WorkoutResultEntity
import com.fit10.app.data.db.mapper.toDomain
import com.fit10.app.domain.model.Equipment
import com.fit10.app.domain.model.ExerciseType
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.ImpactLevel
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Fit10DatabaseInstrumentationTest {

    private lateinit var db: Fit10Database
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var workoutDao: WorkoutDao
    private lateinit var workoutResultDao: WorkoutResultDao
    private lateinit var programDao: ProgramDao
    private lateinit var reminderDao: ReminderDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, Fit10Database::class.java)
            .allowMainThreadQueries()
            .build()
        userProfileDao = db.userProfileDao()
        exerciseDao = db.exerciseDao()
        workoutDao = db.workoutDao()
        workoutResultDao = db.workoutResultDao()
        programDao = db.programDao()
        reminderDao = db.reminderDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun userProfileDao_insertAndQuery() = runBlocking {
        val profile = UserProfileEntity(
            id = "u1",
            name = "Rakesh",
            fitnessGoal = FitnessGoal.BUILD_STRENGTH,
            fitnessLevel = FitnessLevel.INTERMEDIATE,
            availableEquipment = setOf(Equipment.NONE, Equipment.DUMBBELLS),
            targetWorkoutDurationMinutes = 10,
            workoutsPerWeek = 4,
            preferredFocusAreas = setOf(FocusArea.FULL_BODY),
            intensityPreference = IntensityPreference.MODERATE,
            isReminderEnabled = true,
            createdAtEpochMs = 1000L,
            updatedAtEpochMs = 1000L
        )

        userProfileDao.insertOrUpdate(profile)
        val fetched = userProfileDao.getUserProfileById("u1")
        assertNotNull(fetched)
        assertEquals("Rakesh", fetched?.name)
        assertEquals(FitnessGoal.BUILD_STRENGTH, fetched?.fitnessGoal)
        assertEquals(setOf(Equipment.NONE, Equipment.DUMBBELLS), fetched?.availableEquipment)

        val flowResult = userProfileDao.getUserProfile().first()
        assertEquals("u1", flowResult?.id)

        userProfileDao.clear()
        assertNull(userProfileDao.getUserProfileById("u1"))
    }

    @Test
    fun exerciseAndWorkoutDao_relationsIntegrity() = runBlocking {
        val exercise1 = ExerciseEntity(
            id = "ex_jumping_jacks",
            name = "Jumping Jacks",
            description = "Cardio warm-up",
            type = ExerciseType.CARDIO,
            focusArea = FocusArea.FULL_BODY,
            equipmentRequired = Equipment.NONE,
            impactLevel = ImpactLevel.MODERATE,
            defaultDurationSeconds = 45,
            defaultReps = null,
            instructions = listOf("Jump feet out and arms up"),
            tips = listOf("Stay light on your feet"),
            targetMuscles = listOf("Calves", "Shoulders"),
            animationUrl = null,
            thumbnailUrl = null
        )

        val exercise2 = ExerciseEntity(
            id = "ex_pushups",
            name = "Push-ups",
            description = "Upper body push",
            type = ExerciseType.STRENGTH,
            focusArea = FocusArea.UPPER_BODY,
            equipmentRequired = Equipment.NONE,
            impactLevel = ImpactLevel.LOW,
            defaultDurationSeconds = 40,
            defaultReps = 12,
            instructions = listOf("Lower chest to ground"),
            tips = listOf("Keep core braced"),
            targetMuscles = listOf("Chest", "Triceps"),
            animationUrl = null,
            thumbnailUrl = null
        )

        exerciseDao.insertExercises(listOf(exercise1, exercise2))

        val workout = WorkoutEntity(
            id = "w_quick_full",
            title = "10-Min Full Body Starter",
            description = "Energizing routine",
            estimatedDurationMinutes = 10,
            fitnessLevel = FitnessLevel.BEGINNER,
            focusArea = FocusArea.FULL_BODY,
            intensity = IntensityPreference.MODERATE,
            isCustom = false,
            caloriesBurnEstimate = 70
        )

        val we1 = WorkoutExerciseEntity(
            workoutId = "w_quick_full",
            exerciseId = "ex_jumping_jacks",
            orderIndex = 0,
            durationSeconds = 45,
            reps = null,
            restDurationSeconds = 15
        )

        val we2 = WorkoutExerciseEntity(
            workoutId = "w_quick_full",
            exerciseId = "ex_pushups",
            orderIndex = 1,
            durationSeconds = 40,
            reps = 12,
            restDurationSeconds = 20
        )

        workoutDao.insertWorkoutWithExercises(workout, listOf(we1, we2))

        val fetchedWithExercises = workoutDao.getWorkoutWithExercisesSuspend("w_quick_full")
        assertNotNull(fetchedWithExercises)
        assertEquals("10-Min Full Body Starter", fetchedWithExercises?.workout?.title)
        assertEquals(2, fetchedWithExercises?.workoutExercises?.size)
        assertEquals(2, fetchedWithExercises?.exercises?.size)

        // Convert to domain and verify mapping integrity
        val domainWorkout = fetchedWithExercises!!.toDomain()
        assertEquals(2, domainWorkout.exercises.size)
        assertEquals("Jumping Jacks", domainWorkout.exercises[0].exercise.name)
        assertEquals("Push-ups", domainWorkout.exercises[1].exercise.name)
        assertEquals(0, domainWorkout.exercises[0].orderIndex)
        assertEquals(1, domainWorkout.exercises[1].orderIndex)
    }

    @Test
    fun workoutResultDao_insertAndAggregates() = runBlocking {
        val result1 = WorkoutResultEntity(
            sessionId = "s1",
            workoutId = "w1",
            workoutTitle = "Quick Core",
            status = WorkoutStatus.COMPLETED,
            durationSeconds = 600,
            exercisesCompletedCount = 6,
            totalExercisesCount = 6,
            caloriesBurned = 60,
            feedback = WorkoutFeedback.JUST_RIGHT,
            completedAtEpochMs = 2000L
        )

        val result2 = WorkoutResultEntity(
            sessionId = "s2",
            workoutId = "w2",
            workoutTitle = "Upper Burn",
            status = WorkoutStatus.COMPLETED,
            durationSeconds = 600,
            exercisesCompletedCount = 6,
            totalExercisesCount = 6,
            caloriesBurned = 75,
            feedback = WorkoutFeedback.TOO_EASY,
            completedAtEpochMs = 3000L
        )

        workoutResultDao.insertResult(result1)
        workoutResultDao.insertResult(result2)

        val count = workoutResultDao.getTotalWorkoutsCompleted().first()
        val totalSeconds = workoutResultDao.getTotalSecondsWorkedOut().first()
        val totalCalories = workoutResultDao.getTotalCaloriesBurned().first()

        assertEquals(2, count)
        assertEquals(1200, totalSeconds)
        assertEquals(135, totalCalories)

        val recent = workoutResultDao.getRecentResults(5).first()
        assertEquals(2, recent.size)
        assertEquals("s2", recent[0].sessionId) // ordered by completedAtEpochMs DESC
    }

    @Test
    fun programDao_insertAndQueryWithDays() = runBlocking {
        val program = ProgramEntity(
            id = "prog_1",
            title = "14-Day Reset",
            description = "Get moving",
            goal = FitnessGoal.STAY_ACTIVE,
            fitnessLevel = FitnessLevel.BEGINNER,
            totalDays = 14,
            daysPerWeek = 5,
            estimatedWeeks = 2
        )

        val day1 = ProgramDayEntity(
            programId = "prog_1",
            dayNumber = 1,
            title = "Day 1",
            isRestDay = false,
            workoutId = null,
            isCompleted = false
        )

        val day2 = ProgramDayEntity(
            programId = "prog_1",
            dayNumber = 2,
            title = "Day 2",
            isRestDay = true,
            workoutId = null,
            isCompleted = false
        )

        programDao.insertProgramWithDays(program, listOf(day1, day2))

        val fetched = programDao.getProgramWithDaysById("prog_1").first()
        assertNotNull(fetched)
        assertEquals("14-Day Reset", fetched?.program?.title)
        assertEquals(2, fetched?.daysWithWorkouts?.size)
    }

    @Test
    fun reminderDao_crud() = runBlocking {
        val reminder = ReminderEntity(
            id = "r1",
            userId = "u1",
            isEnabled = true,
            hour = 8,
            minute = 0,
            daysOfWeek = setOf(1, 2, 3, 4, 5),
            title = "Fit10 Time",
            message = "Your 10 minutes starts now!"
        )

        reminderDao.insertOrUpdate(reminder)
        val fetched = reminderDao.getReminderForUser("u1").first()
        assertNotNull(fetched)
        assertTrue(fetched!!.isEnabled)

        reminderDao.setReminderEnabled("r1", false)
        val updated = reminderDao.getReminderForUser("u1").first()
        assertEquals(false, updated?.isEnabled)
    }
}
