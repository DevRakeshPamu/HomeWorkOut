package com.fit10.app.data.repository

import com.fit10.app.data.db.dao.ExerciseDao
import com.fit10.app.data.db.dao.WorkoutDao
import com.fit10.app.data.db.mapper.toDomain
import com.fit10.app.data.seed.DatabaseSeeder
import com.fit10.app.domain.model.Exercise
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val seeder: DatabaseSeeder
) : WorkoutRepository {

    override fun getAllWorkouts(): Flow<List<Workout>> =
        workoutDao.getAllWorkoutsWithExercises().map { list -> list.map { it.toDomain() } }

    override fun getWorkoutById(workoutId: String): Flow<Workout?> =
        workoutDao.getWorkoutWithExercises(workoutId).map { it?.toDomain() }

    override fun getWorkoutsByFocusArea(focusArea: FocusArea): Flow<List<Workout>> =
        workoutDao.getWorkoutsByFocusArea(focusArea).map { list -> list.map { it.toDomain() } }

    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAllExercises().map { list -> list.map { it.toDomain() } }

    override suspend fun getExerciseById(exerciseId: String): Exercise? =
        exerciseDao.getExerciseById(exerciseId)?.toDomain()

    override suspend fun ensureSeedDataLoaded() {
        seeder.seedIfNeeded()
    }
}
