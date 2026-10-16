package com.fit10.app.data.db.mapper

import com.fit10.app.data.db.entity.ExerciseEntity
import com.fit10.app.domain.model.Exercise

fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    description = description,
    type = type,
    focusArea = focusArea,
    equipmentRequired = equipmentRequired,
    impactLevel = impactLevel,
    defaultDurationSeconds = defaultDurationSeconds,
    defaultReps = defaultReps,
    instructions = instructions,
    tips = tips,
    targetMuscles = targetMuscles,
    animationUrl = animationUrl,
    thumbnailUrl = thumbnailUrl
)

fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(
    id = id,
    name = name,
    description = description,
    type = type,
    focusArea = focusArea,
    equipmentRequired = equipmentRequired,
    impactLevel = impactLevel,
    defaultDurationSeconds = defaultDurationSeconds,
    defaultReps = defaultReps,
    instructions = instructions,
    tips = tips,
    targetMuscles = targetMuscles,
    animationUrl = animationUrl,
    thumbnailUrl = thumbnailUrl
)
