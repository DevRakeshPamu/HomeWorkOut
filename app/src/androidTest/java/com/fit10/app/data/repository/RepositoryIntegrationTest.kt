package com.fit10.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fit10.app.data.db.Fit10Database
import com.fit10.app.data.preferences.UserPreferencesDataSource
import com.fit10.app.data.seed.DatabaseSeeder
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryIntegrationTest {

    private lateinit var db: Fit10Database
    private lateinit var seeder: DatabaseSeeder
    private lateinit var workoutRepo: WorkoutRepositoryImpl
    private lateinit var programRepo: ProgramRepositoryImpl
    private lateinit var userRepo: UserRepositoryImpl
    private lateinit var userPrefs: UserPreferencesDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, Fit10Database::class.java)
            .allowMainThreadQueries()
            .build()
        seeder = DatabaseSeeder(db)
        workoutRepo = WorkoutRepositoryImpl(db.workoutDao(), db.exerciseDao(), seeder)
        programRepo = ProgramRepositoryImpl(db.programDao())
        userPrefs = UserPreferencesDataSource(context)
        userRepo = UserRepositoryImpl(db.userProfileDao(), userPrefs)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun databaseSeeder_populatesCatalogAndIsIdempotent() = runBlocking {
        // Initial state is empty
        val initialWorkouts = workoutRepo.getAllWorkouts().first()
        assertTrue(initialWorkouts.isEmpty())

        // Seed
        workoutRepo.ensureSeedDataLoaded()

        val workoutsAfterSeed = workoutRepo.getAllWorkouts().first()
        assertEquals(5, workoutsAfterSeed.size)

        val coreWorkouts = workoutRepo.getWorkoutsByFocusArea(FocusArea.CORE).first()
        assertEquals(1, coreWorkouts.size)
        assertEquals("10-Min Core Shred", coreWorkouts[0].title)

        val programs = programRepo.getAllPrograms().first()
        assertEquals(1, programs.size)
        assertEquals(14, programs[0].totalDays)

        // Calling ensureSeedDataLoaded again should be a no-op
        workoutRepo.ensureSeedDataLoaded()
        val workoutsAfterSecondCall = workoutRepo.getAllWorkouts().first()
        assertEquals(5, workoutsAfterSecondCall.size)
    }

    @Test
    fun userRepo_savesProfileAndUpdatesPreferences() = runBlocking {
        assertFalse(userRepo.isOnboardingCompleted().first())

        val profile = UserProfile(
            id = "user_test_1",
            name = "Jordan",
            fitnessGoal = FitnessGoal.STAY_ACTIVE,
            fitnessLevel = FitnessLevel.BEGINNER
        )

        userRepo.saveUserProfile(profile)
        userRepo.setOnboardingCompleted(true)

        val savedProfile = userRepo.getUserProfile().first()
        assertNotNull(savedProfile)
        assertEquals("Jordan", savedProfile?.name)
        assertTrue(userRepo.isOnboardingCompleted().first())
    }
}
