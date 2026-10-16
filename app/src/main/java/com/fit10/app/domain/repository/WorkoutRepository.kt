package com.fit10.app.domain.repository

import com.fit10.app.domain.model.Exercise
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkouts(): Flow<List<Workout>>
    fun getWorkoutById(workoutId: String): Flow<Workout?>
    fun getWorkoutsByFocusArea(focusArea: FocusArea): Flow<List<Workout>>
    fun getAllExercises(): Flow<List<Exercise>>
    suspend fun getExerciseById(exerciseId: String): Exercise?
    suspend fun ensureSeedDataLoaded()
}
