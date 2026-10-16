package com.fit10.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel

@Entity(tableName = "programs")
data class ProgramEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val goal: FitnessGoal,
    val fitnessLevel: FitnessLevel,
    val totalDays: Int,
    val daysPerWeek: Int,
    val estimatedWeeks: Int
)
