package com.fit10.app.domain.repository

import com.fit10.app.domain.model.WorkoutHistory
import com.fit10.app.domain.model.WorkoutResult
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getWorkoutHistory(): Flow<WorkoutHistory>
    suspend fun saveWorkoutResult(result: WorkoutResult)
    fun getRecentResults(limit: Int = 20): Flow<List<WorkoutResult>>
}
