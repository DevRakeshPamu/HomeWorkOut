package com.fit10.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fit10.app.data.db.entity.WorkoutResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutResultDao {

    @Query("SELECT * FROM workout_results ORDER BY completedAtEpochMs DESC")
    fun getAllResults(): Flow<List<WorkoutResultEntity>>

    @Query("SELECT * FROM workout_results ORDER BY completedAtEpochMs DESC LIMIT :limit")
    fun getRecentResults(limit: Int = 20): Flow<List<WorkoutResultEntity>>

    @Query("SELECT * FROM workout_results WHERE sessionId = :sessionId")
    suspend fun getResultBySessionId(sessionId: String): WorkoutResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: WorkoutResultEntity)

    @Query("SELECT COUNT(*) FROM workout_results WHERE status = 'COMPLETED'")
    fun getTotalWorkoutsCompleted(): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM workout_results WHERE status = 'COMPLETED'")
    fun getTotalSecondsWorkedOut(): Flow<Int?>

    @Query("SELECT SUM(caloriesBurned) FROM workout_results WHERE status = 'COMPLETED'")
    fun getTotalCaloriesBurned(): Flow<Int?>

    @Query("DELETE FROM workout_results")
    suspend fun deleteAll()
}
