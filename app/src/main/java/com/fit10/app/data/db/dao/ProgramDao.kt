package com.fit10.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fit10.app.data.db.entity.ProgramDayEntity
import com.fit10.app.data.db.entity.ProgramEntity
import com.fit10.app.data.db.entity.ProgramWithDays
import com.fit10.app.data.db.entity.UserPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {

    @Transaction
    @Query("SELECT * FROM programs ORDER BY title ASC")
    fun getAllProgramsWithDays(): Flow<List<ProgramWithDays>>

    @Transaction
    @Query("SELECT * FROM programs WHERE id = :programId")
    fun getProgramWithDaysById(programId: String): Flow<ProgramWithDays?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: ProgramEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgramDays(days: List<ProgramDayEntity>)

    @Query("DELETE FROM program_days WHERE programId = :programId")
    suspend fun deleteProgramDays(programId: String)

    @Transaction
    suspend fun insertProgramWithDays(
        program: ProgramEntity,
        days: List<ProgramDayEntity>
    ) {
        insertProgram(program)
        deleteProgramDays(program.id)
        insertProgramDays(days)
    }

    // UserPlan
    @Query("SELECT * FROM user_plans WHERE userId = :userId AND isCompleted = 0 LIMIT 1")
    fun getActiveUserPlan(userId: String): Flow<UserPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserPlan(plan: UserPlanEntity)

    @Query("UPDATE user_plans SET currentDayNumber = :currentDay, isCompleted = :isCompleted WHERE id = :planId")
    suspend fun updateUserPlanProgress(planId: String, currentDay: Int, isCompleted: Boolean)

    @Query("UPDATE program_days SET isCompleted = :isCompleted WHERE programId = :programId AND dayNumber = :dayNumber")
    suspend fun updateProgramDayCompletion(programId: String, dayNumber: Int, isCompleted: Boolean)
}
