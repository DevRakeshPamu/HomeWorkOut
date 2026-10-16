package com.fit10.app.data.db.mapper

import com.fit10.app.data.db.entity.WorkoutEntity
import com.fit10.app.data.db.entity.WorkoutExerciseEntity
import com.fit10.app.data.db.entity.WorkoutWithExercises
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutExercise

fun WorkoutWithExercises.toDomain(): Workout {
    val exerciseMap = exercises.associateBy { it.id }
    val sortedExercises = workoutExercises
        .sortedBy { it.orderIndex }
        .mapNotNull { we ->
            exerciseMap[we.exerciseId]?.let { exEntity ->
                WorkoutExercise(
                    exercise = exEntity.toDomain(),
                    orderIndex = we.orderIndex,
                    durationSeconds = we.durationSeconds,
                    reps = we.reps,
                    restDurationSeconds = we.restDurationSeconds
                )
            }
        }

    return Workout(
        id = workout.id,
        title = workout.title,
        description = workout.description,
        estimatedDurationMinutes = workout.estimatedDurationMinutes,
        fitnessLevel = workout.fitnessLevel,
        focusArea = workout.focusArea,
        intensity = workout.intensity,
        exercises = sortedExercises,
        isCustom = workout.isCustom,
        caloriesBurnEstimate = workout.caloriesBurnEstimate
    )
}

fun Workout.toEntity(): WorkoutEntity = WorkoutEntity(
    id = id,
    title = title,
    description = description,
    estimatedDurationMinutes = estimatedDurationMinutes,
    fitnessLevel = fitnessLevel,
    focusArea = focusArea,
    intensity = intensity,
    isCustom = isCustom,
    caloriesBurnEstimate = caloriesBurnEstimate
)

fun Workout.toExerciseEntities(): List<WorkoutExerciseEntity> =
    exercises.map { we ->
        WorkoutExerciseEntity(
            workoutId = id,
            exerciseId = we.exercise.id,
            orderIndex = we.orderIndex,
            durationSeconds = we.durationSeconds,
            reps = we.reps,
            restDurationSeconds = we.restDurationSeconds
        )
    }
