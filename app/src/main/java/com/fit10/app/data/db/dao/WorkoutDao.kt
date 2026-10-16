package com.fit10.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fit10.app.data.db.entity.WorkoutEntity
import com.fit10.app.data.db.entity.WorkoutExerciseEntity
import com.fit10.app.data.db.entity.WorkoutWithExercises
import com.fit10.app.domain.model.FocusArea
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Transaction
    @Query("SELECT * FROM workouts ORDER BY title ASC")
    fun getAllWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkoutWithExercises(workoutId: String): Flow<WorkoutWithExercises?>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutWithExercisesSuspend(workoutId: String): WorkoutWithExercises?

    @Transaction
    @Query("SELECT * FROM workouts WHERE focusArea = :focusArea")
    fun getWorkoutsByFocusArea(focusArea: FocusArea): Flow<List<WorkoutWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercises(exercises: List<WorkoutExerciseEntity>)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteWorkoutExercises(workoutId: String)

    @Transaction
    suspend fun insertWorkoutWithExercises(
        workout: WorkoutEntity,
        exercises: List<WorkoutExerciseEntity>
    ) {
        insertWorkout(workout)
        deleteWorkoutExercises(workout.id)
        insertWorkoutExercises(exercises)
    }

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: String)

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()
}
