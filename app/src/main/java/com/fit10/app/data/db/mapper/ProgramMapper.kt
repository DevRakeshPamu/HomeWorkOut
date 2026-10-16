package com.fit10.app.data.db.mapper

import com.fit10.app.data.db.entity.ProgramDayEntity
import com.fit10.app.data.db.entity.ProgramEntity
import com.fit10.app.data.db.entity.ProgramWithDays
import com.fit10.app.data.db.entity.UserPlanEntity
import com.fit10.app.domain.model.Program
import com.fit10.app.domain.model.ProgramDay
import com.fit10.app.domain.model.UserPlan
import com.fit10.app.domain.model.Workout

fun ProgramWithDays.toDomain(): Program {
    val sortedDays = daysWithWorkouts
        .sortedBy { it.day.dayNumber }
        .map { dayWithWorkout ->
            ProgramDay(
                dayNumber = dayWithWorkout.day.dayNumber,
                title = dayWithWorkout.day.title,
                isRestDay = dayWithWorkout.day.isRestDay,
                workout = dayWithWorkout.workout?.let { wEntity ->
                    Workout(
                        id = wEntity.id,
                        title = wEntity.title,
                        description = wEntity.description,
                        estimatedDurationMinutes = wEntity.estimatedDurationMinutes,
                        fitnessLevel = wEntity.fitnessLevel,
                        focusArea = wEntity.focusArea,
                        intensity = wEntity.intensity,
                        isCustom = wEntity.isCustom,
                        caloriesBurnEstimate = wEntity.caloriesBurnEstimate
                    )
                },
                isCompleted = dayWithWorkout.day.isCompleted
            )
        }

    return Program(
        id = program.id,
        title = program.title,
        description = program.description,
        goal = program.goal,
        fitnessLevel = program.fitnessLevel,
        totalDays = program.totalDays,
        daysPerWeek = program.daysPerWeek,
        estimatedWeeks = program.estimatedWeeks,
        days = sortedDays
    )
}

fun Program.toEntity(): ProgramEntity = ProgramEntity(
    id = id,
    title = title,
    description = description,
    goal = goal,
    fitnessLevel = fitnessLevel,
    totalDays = totalDays,
    daysPerWeek = daysPerWeek,
    estimatedWeeks = estimatedWeeks
)

fun Program.toDayEntities(): List<ProgramDayEntity> =
    days.map { day ->
        ProgramDayEntity(
            programId = id,
            dayNumber = day.dayNumber,
            title = day.title,
            isRestDay = day.isRestDay,
            workoutId = day.workout?.id,
            isCompleted = day.isCompleted
        )
    }

fun UserPlanEntity.toDomain(scheduledDays: List<ProgramDay> = emptyList()): UserPlan = UserPlan(
    id = id,
    userId = userId,
    programId = programId,
    currentDayNumber = currentDayNumber,
    totalDays = totalDays,
    startDateEpochMs = startDateEpochMs,
    targetCompletionDateEpochMs = targetCompletionDateEpochMs,
    scheduledDays = scheduledDays,
    isCompleted = isCompleted
)

fun UserPlan.toEntity(): UserPlanEntity = UserPlanEntity(
    id = id,
    userId = userId,
    programId = programId,
    currentDayNumber = currentDayNumber,
    totalDays = totalDays,
    startDateEpochMs = startDateEpochMs,
    targetCompletionDateEpochMs = targetCompletionDateEpochMs,
    isCompleted = isCompleted
)
