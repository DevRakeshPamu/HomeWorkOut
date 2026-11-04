package com.fit10.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DeliveryType { VAGINAL, C_SECTION, PREFER_NOT_TO_SAY }

@Serializable
enum class ClearanceStatus { YES, NOT_YET, NOT_SURE }

@Serializable
enum class BodySignal { PAIN, BLEEDING, LEAKING, HEAVINESS, DOMING }

@Serializable
enum class PriorActivity { INACTIVE, SOMEWHAT_ACTIVE, VERY_ACTIVE }

@Serializable
enum class MomGoal { ENERGY, CORE_STRENGTH, BACK_RELIEF, OVERALL_FITNESS }

enum class StartingPoint { SEE_DOCTOR, GENTLE_BASICS, PHASE_ONE }

@Serializable
data class PostpartumProfile(
    val name: String = "",
    val babyAgeWeeks: Int = 12,
    val delivery: DeliveryType = DeliveryType.VAGINAL,
    val clearance: ClearanceStatus = ClearanceStatus.YES,
    val signals: Set<BodySignal> = emptySet(),
    val priorActivity: PriorActivity = PriorActivity.SOMEWHAT_ACTIVE,
    val goal: MomGoal = MomGoal.ENERGY,
    val daysPerWeek: Int = 5,
) {
    val hasRedFlag: Boolean get() = BodySignal.PAIN in signals || BodySignal.BLEEDING in signals

    val minWeeksBeforeJourney: Int get() = if (delivery == DeliveryType.VAGINAL) 6 else 8

    val startingPoint: StartingPoint
        get() = when {
            hasRedFlag -> StartingPoint.SEE_DOCTOR
            clearance != ClearanceStatus.YES || babyAgeWeeks < minWeeksBeforeJourney -> StartingPoint.GENTLE_BASICS
            else -> StartingPoint.PHASE_ONE
        }

    val avoidImpact: Boolean get() = BodySignal.LEAKING in signals || BodySignal.HEAVINESS in signals

    val protectCore: Boolean get() = BodySignal.DOMING in signals || delivery != DeliveryType.VAGINAL
}
