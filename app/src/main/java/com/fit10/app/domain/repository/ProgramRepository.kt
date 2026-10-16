package com.fit10.app.domain.repository

import com.fit10.app.domain.model.Program
import com.fit10.app.domain.model.UserPlan
import kotlinx.coroutines.flow.Flow

interface ProgramRepository {
    fun getAllPrograms(): Flow<List<Program>>
    fun getProgramById(programId: String): Flow<Program?>
    fun getActiveUserPlan(userId: String): Flow<UserPlan?>
    suspend fun saveUserPlan(plan: UserPlan)
    suspend fun updateProgramDayProgress(programId: String, dayNumber: Int, isCompleted: Boolean)
}
