package com.fit10.app.data.seed

import com.fit10.app.data.db.Fit10Database
import com.fit10.app.data.db.mapper.toDayEntities
import com.fit10.app.data.db.mapper.toEntity
import com.fit10.app.data.db.mapper.toExerciseEntities
import kotlinx.coroutines.flow.first

class DatabaseSeeder(private val database: Fit10Database) {

    suspend fun seedIfNeeded() {
        val existingExercises = database.exerciseDao().getAllExercises().first()
        if (existingExercises.isEmpty()) {
            seed()
        }
    }

    suspend fun seed() {
        // 1. Seed Exercises
        val exerciseEntities = ExerciseCatalog.ALL.map { it.toEntity() }
        database.exerciseDao().insertExercises(exerciseEntities)

        // 2. Seed Workouts & WorkoutExercises
        WorkoutCatalog.ALL.forEach { workout ->
            val workoutEntity = workout.toEntity()
            val workoutExerciseEntities = workout.toExerciseEntities()
            database.workoutDao().insertWorkoutWithExercises(workoutEntity, workoutExerciseEntities)
        }

        // 3. Seed Programs & ProgramDays
        ProgramCatalog.ALL.forEach { program ->
            val programEntity = program.toEntity()
            val programDayEntities = program.toDayEntities()
            database.programDao().insertProgramWithDays(programEntity, programDayEntities)
        }
    }
}
