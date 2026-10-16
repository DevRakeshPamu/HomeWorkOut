package com.fit10.app.data.repository

import com.fit10.app.data.db.dao.ProgramDao
import com.fit10.app.data.db.mapper.toDomain
import com.fit10.app.data.db.mapper.toEntity
import com.fit10.app.domain.model.Program
import com.fit10.app.domain.model.UserPlan
import com.fit10.app.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProgramRepositoryImpl(
    private val programDao: ProgramDao
) : ProgramRepository {

    override fun getAllPrograms(): Flow<List<Program>> =
        programDao.getAllProgramsWithDays().map { list -> list.map { it.toDomain() } }

    override fun getProgramById(programId: String): Flow<Program?> =
        programDao.getProgramWithDaysById(programId).map { it?.toDomain() }

    override fun getActiveUserPlan(userId: String): Flow<UserPlan?> =
        programDao.getActiveUserPlan(userId).map { it?.toDomain() }

    override suspend fun saveUserPlan(plan: UserPlan) {
        programDao.insertOrUpdateUserPlan(plan.toEntity())
    }

    override suspend fun updateProgramDayProgress(
        programId: String,
        dayNumber: Int,
        isCompleted: Boolean
    ) {
        programDao.updateProgramDayCompletion(programId, dayNumber, isCompleted)
    }
}
