package com.fit10.app.domain.plan

import com.fit10.app.data.seed.ExerciseCatalog
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.MomGoal
import com.fit10.app.domain.model.PostpartumProfile
import com.fit10.app.domain.model.PriorActivity
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutExercise
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutResult
import com.fit10.app.domain.model.WorkoutStatus
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

data class PlanState(val level: Int, val blockStartDay: Long, val planStartDay: Long)

enum class PlanFocus(val label: String) {
    CORE("Core & breath"), LOWER("Legs & glutes"), UPPER("Arms & posture"), BACK("Back care"), FULL("Full body")
}

enum class PlanLevel(val title: String, val blurb: String) {
    RECONNECT("Reconnect", "Breath, posture and gentle core"),
    REBUILD("Rebuild", "Legs, glutes and back strength"),
    STRENGTHEN("Strengthen", "Longer holds, upper body"),
    ENERGIZE("Energize", "Light cardio, full body"),
}

data class PlanDay(val date: LocalDate, val focus: PlanFocus?, val done: Boolean, val isToday: Boolean)

data class WeekPlan(
    val level: PlanLevel,
    val week: Int,
    val days: List<PlanDay>,
    val today: Workout,
    val todayFocus: PlanFocus,
    val isRestDay: Boolean,
    val doneToday: Boolean,
    val reason: String,
    val daysToReview: Int,
)

enum class ReviewOutcome { UP, STAY, DOWN }

object WeeklyPlanner {

    private class Move(val id: String, val minLevel: Int, val focuses: Set<PlanFocus>, val coreLoad: Boolean = false, val impact: Boolean = false)

    private val warmUp = listOf("ex_arm_circles", "ex_cat_cow")
    private const val COOL_DOWN = "ex_childs_pose"

    private val moves = listOf(
        Move("ex_bird_dog", 1, setOf(PlanFocus.CORE, PlanFocus.BACK, PlanFocus.FULL)),
        Move("ex_glute_bridges", 1, setOf(PlanFocus.LOWER, PlanFocus.CORE, PlanFocus.FULL)),
        Move("ex_cat_cow", 1, setOf(PlanFocus.BACK, PlanFocus.CORE)),
        Move("ex_arm_circles", 1, setOf(PlanFocus.UPPER)),
        Move("ex_glute_bridge_march", 2, setOf(PlanFocus.LOWER, PlanFocus.CORE)),
        Move("ex_wall_sit", 2, setOf(PlanFocus.LOWER)),
        Move("ex_squats", 2, setOf(PlanFocus.LOWER, PlanFocus.FULL)),
        Move("ex_reverse_lunges", 2, setOf(PlanFocus.LOWER, PlanFocus.FULL)),
        Move("ex_prone_ytw", 2, setOf(PlanFocus.UPPER, PlanFocus.BACK)),
        Move("ex_superman", 2, setOf(PlanFocus.BACK, PlanFocus.UPPER)),
        Move("ex_lunges", 3, setOf(PlanFocus.LOWER, PlanFocus.FULL)),
        Move("ex_wide_push_ups", 3, setOf(PlanFocus.UPPER, PlanFocus.FULL), coreLoad = true),
        Move("ex_push_ups", 3, setOf(PlanFocus.UPPER), coreLoad = true),
        Move("ex_plank", 3, setOf(PlanFocus.CORE), coreLoad = true),
        Move("ex_shoulder_taps", 3, setOf(PlanFocus.UPPER, PlanFocus.CORE), coreLoad = true),
        Move("ex_diamond_push_ups", 4, setOf(PlanFocus.UPPER), coreLoad = true),
        Move("ex_bicycle_crunches", 4, setOf(PlanFocus.CORE), coreLoad = true),
        Move("ex_jumping_jacks", 4, setOf(PlanFocus.FULL), impact = true),
        Move("ex_high_knees", 4, setOf(PlanFocus.FULL, PlanFocus.LOWER), impact = true),
        Move("ex_mountain_climbers", 4, setOf(PlanFocus.FULL, PlanFocus.CORE), impact = true, coreLoad = true),
    )

    fun maxLevel(p: PostpartumProfile): Int = if (p.protectCore || p.avoidImpact) 3 else 4

    fun startLevel(p: PostpartumProfile): Int = if (p.priorActivity == PriorActivity.INACTIVE) 1 else 2

    fun review(p: PostpartumProfile, level: Int, window: List<WorkoutResult>): Pair<Int, ReviewOutcome> {
        val done = window.count { it.status == WorkoutStatus.COMPLETED }
        val hard = window.count { it.feedback == WorkoutFeedback.TOO_HARD }
        val easy = window.count { it.feedback == WorkoutFeedback.TOO_EASY }
        val target = p.daysPerWeek * 2
        return when {
            hard >= 2 && level > 1 -> level - 1 to ReviewOutcome.DOWN
            hard == 0 && (done * 10 >= target * 6 || easy >= 3) && level < maxLevel(p) -> level + 1 to ReviewOutcome.UP
            else -> level to ReviewOutcome.STAY
        }
    }

    fun trainingDays(daysPerWeek: Int): Set<DayOfWeek> = when (daysPerWeek.coerceIn(1, 7)) {
        1 -> setOf(DayOfWeek.WEDNESDAY)
        2 -> setOf(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY)
        3 -> setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        4 -> setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY)
        5 -> setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
        6 -> DayOfWeek.entries.toSet() - DayOfWeek.SUNDAY
        else -> DayOfWeek.entries.toSet()
    }

    private fun rotation(goal: MomGoal): List<PlanFocus> = when (goal) {
        MomGoal.CORE_STRENGTH -> listOf(PlanFocus.CORE, PlanFocus.LOWER, PlanFocus.CORE, PlanFocus.BACK)
        MomGoal.BACK_RELIEF -> listOf(PlanFocus.BACK, PlanFocus.CORE, PlanFocus.BACK, PlanFocus.LOWER)
        MomGoal.ENERGY -> listOf(PlanFocus.FULL, PlanFocus.LOWER, PlanFocus.UPPER, PlanFocus.FULL)
        MomGoal.OVERALL_FITNESS -> listOf(PlanFocus.FULL, PlanFocus.CORE, PlanFocus.LOWER, PlanFocus.UPPER)
    }

    fun buildWeek(
        p: PostpartumProfile,
        state: PlanState,
        results: List<WorkoutResult>,
        gentleToday: Boolean,
        today: LocalDate = LocalDate.now(),
    ): WeekPlan {
        val zone = ZoneId.systemDefault()
        val doneDates = results.filter { it.status == WorkoutStatus.COMPLETED }
            .map { Instant.ofEpochMilli(it.completedAtEpochMs).atZone(zone).toLocalDate() }.toSet()
        val monday = today.with(DayOfWeek.MONDAY)
        val training = trainingDays(p.daysPerWeek)
        val rot = rotation(p.goal)
        var slot = 0
        val days = (0..6).map { i ->
            val d = monday.plusDays(i.toLong())
            val focus = if (d.dayOfWeek in training) rot[slot++ % rot.size] else null
            PlanDay(d, focus, d in doneDates, d == today)
        }
        val week = ((today.toEpochDay() - state.planStartDay) / 7 + 1).toInt().coerceAtLeast(1)
        val weekInBlock = (((today.toEpochDay() - state.blockStartDay) / 7).toInt()).coerceIn(0, 1)
        val todayDay = days.first { it.isToday }
        val isRest = todayDay.focus == null
        val focus = todayDay.focus ?: days.firstOrNull { it.date > today && it.focus != null }?.focus ?: rot.first()
        var flags = 0
        if (p.protectCore) flags = flags or 1
        if (p.avoidImpact) flags = flags or 2
        if (gentleToday) flags = flags or 4
        val id = "plan-${state.level}-${focus.ordinal}-$flags-$weekInBlock-${today.toEpochDay()}"
        val reason = when {
            gentleToday -> "Rough night, so today is shorter and softer."
            isRest -> "Rest day. Recovery is part of the plan, or do this if you feel up to it."
            p.protectCore -> "${focus.label} day, with core-safe moves for your recovery."
            p.avoidImpact -> "${focus.label} day, no jumping to protect your pelvic floor."
            else -> "${focus.label} day, chosen for your ${goalLabel(p.goal)} goal."
        }
        return WeekPlan(
            level = PlanLevel.entries[state.level - 1],
            week = week,
            days = days,
            today = workoutFor(id)!!,
            todayFocus = focus,
            isRestDay = isRest,
            doneToday = today in doneDates,
            reason = reason,
            daysToReview = (14 - (today.toEpochDay() - state.blockStartDay)).toInt().coerceAtLeast(0),
        )
    }

    private fun goalLabel(g: MomGoal) = when (g) {
        MomGoal.ENERGY -> "energy"
        MomGoal.CORE_STRENGTH -> "core strength"
        MomGoal.BACK_RELIEF -> "back relief"
        MomGoal.OVERALL_FITNESS -> "overall fitness"
    }

    fun workoutFor(id: String): Workout? {
        val parts = id.split("-")
        if (parts.size != 6 || parts[0] != "plan") return null
        val requested = parts[1].toIntOrNull() ?: return null
        val focus = PlanFocus.entries.getOrNull(parts[2].toIntOrNull() ?: return null) ?: return null
        val flags = parts[3].toIntOrNull() ?: 0
        val bump = parts[4].toIntOrNull() ?: 0
        val seed = parts[5].toLongOrNull() ?: 0L
        val protectCore = flags and 1 != 0
        val avoidImpact = flags and 2 != 0
        val gentle = flags and 4 != 0
        val level = (if (gentle) requested - 1 else requested).coerceIn(1, 4)

        val allowed = moves.filter { it.minLevel <= level && !(protectCore && it.coreLoad) && !(avoidImpact && it.impact) && it.id !in warmUp }
        val rnd = Random(seed * 31 + focus.ordinal)
        val primary = allowed.filter { focus in it.focuses }.shuffled(rnd)
        val secondary = (allowed - primary.toSet()).shuffled(rnd)
        val pool = (primary + secondary).ifEmpty { moves.filter { it.minLevel == 1 } }
        val mainCount = if (gentle) 4 else 6
        val main = List(mainCount) { pool[it % pool.size].id }

        val work = listOf(30, 40, 45, 45)[level - 1] + bump * 5 - (if (gentle) 5 else 0)
        val rest = listOf(20, 15, 15, 12)[level - 1]
        val ids = warmUp + main + COOL_DOWN
        val exercises = ids.mapIndexedNotNull { i, exId ->
            val ex = ExerciseCatalog.ALL.find { it.id == exId } ?: return@mapIndexedNotNull null
            val isEdge = i < warmUp.size || i == ids.lastIndex
            WorkoutExercise(ex, i, durationSeconds = if (isEdge) 40 else work, reps = null, restDurationSeconds = if (i == ids.lastIndex) 0 else rest)
        }
        val minutes = (exercises.sumOf { it.durationSeconds + it.restDurationSeconds } + 59) / 60
        return Workout(
            id = id,
            title = if (gentle) "Gentle ${focus.label.lowercase()}" else focus.label,
            description = PlanLevel.entries[level - 1].blurb,
            estimatedDurationMinutes = minutes,
            fitnessLevel = FitnessLevel.BEGINNER,
            focusArea = when (focus) {
                PlanFocus.CORE -> FocusArea.CORE
                PlanFocus.LOWER -> FocusArea.LOWER_BODY
                PlanFocus.UPPER -> FocusArea.UPPER_BODY
                PlanFocus.BACK -> FocusArea.MOBILITY
                PlanFocus.FULL -> FocusArea.FULL_BODY
            },
            intensity = IntensityPreference.EASY,
            exercises = exercises,
        )
    }
}
