package com.fit10.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fit10.app.data.db.entity.ExerciseEntity
import com.fit10.app.domain.model.ExerciseType
import com.fit10.app.domain.model.FocusArea
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: String): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE focusArea = :focusArea ORDER BY name ASC")
    fun getExercisesByFocusArea(focusArea: FocusArea): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE type = :type ORDER BY name ASC")
    fun getExercisesByType(type: ExerciseType): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()
}
