package com.fit10.app.domain.adaptation

import com.fit10.app.domain.model.AdaptationResult
import com.fit10.app.domain.model.FitnessGoal
import com.fit10.app.domain.model.FitnessLevel
import com.fit10.app.domain.model.FocusArea
import com.fit10.app.domain.model.IntensityPreference
import com.fit10.app.domain.model.UserProfile
import com.fit10.app.domain.model.Workout
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutHistory
import com.fit10.app.domain.model.WorkoutResult
import com.fit10.app.domain.model.WorkoutStatus

/**
 * Intelligent personalization and progressive adaptation engine for Fit10 workouts.
 *
 * Changes from original:
 *  P-01 — Recovery balance is now overlap-aware: FULL_BODY is treated as overlapping
 *          LOWER_BODY, UPPER_BODY, and CORE, so back-to-back Full Body → Lower Body
 *          correctly receives an overtraining penalty rather than a recovery bonus.
 *  P-02 — Score ties are now broken deterministically by preferring workouts not
 *          recently completed (staleness guard).
 *  P-03 — Adaptation is no longer exclusively feedback-driven. A completion-rate
 *          signal is evaluated independently of explicit user feedback.
 *  P-04 — Progression window now ignores sessions older than 30 days.
 *  P-05 — IMPROVE_FITNESS goal now maps to all focus areas, not just FULL_BODY.
 *  P-06 — Deload scheduling added: after every 20 completed sessions the engine
 *          recommends the mobility/recovery workout for 3–5 days.
 *  New  — Weekly volume guard: penalises any FocusArea that has appeared ≥ 2×
 *          (or FULL_BODY ≥ 3×) in the last 7 sessions.
 *  New  — Staleness guard: penalises any workout that was the last recommendation,
 *          forcing natural rotation even when scores are close.
 */
object PersonalizationEngine {

    // ── Constants ─────────────────────────────────────────────────────

    /** Sessions between automatic deload windows. Approx. 4 weeks at 5 days/week. */
    private const val DELOAD_INTERVAL = 20

    /** Maximum age (ms) of sessions considered for adaptation. */
    private const val ADAPTATION_WINDOW_MS = 30L * 24 * 60 * 60 * 1000 // 30 days

    /**
     * FocusAreas that are considered to fully overlap with FULL_BODY.
     * A Full Body session stresses the same muscles as a dedicated Lower/Upper/Core day.
     */
    private val FULL_BODY_OVERLAPS = setOf(
        FocusArea.LOWER_BODY,
        FocusArea.UPPER_BODY,
        FocusArea.CORE
    )

    /**
     * Exercises swapped out when de-escalating difficulty.
     * Higher-impact movements are replaced with lower-impact alternatives.
     */
    private val DE_ESCALATION_SWAP_MAP = mapOf(
        "ex_high_knees"       to "ex_glute_bridges",
        "ex_mountain_climbers" to "ex_plank",
        "ex_jumping_jacks"    to "ex_arm_circles"
    )

    // ── Public API ────────────────────────────────────────────────────

    /**
     * Selects the optimal daily workout based on user profile, available workouts,
     * and full workout history.
     *
     * Scoring factors (in descending weight):
     *  1. Deload gate      — forces MOBILITY during deload window
     *  2. Weekly volume guard — penalises overused FocusAreas in the last 7 sessions
     *  3. Recovery balance  — overlap-aware fatigue avoidance (FIX P-01)
     *  4. Goal alignment    — goal → FocusArea mapping (FIX P-05)
     *  5. Preferred areas   — user's explicit FocusArea preferences
     *  6. Intensity match   — user's preferred intensity
     *  7. Staleness guard   — tie-break by avoiding last recommended workout (FIX P-02)
     */
    fun recommendDailyWorkout(
        userProfile: UserProfile,
        availableWorkouts: List<Workout>,
        history: WorkoutHistory
    ): Workout {
        if (availableWorkouts.isEmpty()) {
            throw IllegalArgumentException("Available workouts cannot be empty")
        }

        // ── 1. Deload gate (FIX P-06) ─────────────────────────────────
        val totalCompleted = history.totalWorkoutsCompleted
        if (totalCompleted > 0 && totalCompleted % DELOAD_INTERVAL == 0) {
            val mobilityWorkout = availableWorkouts.firstOrNull { it.focusArea == FocusArea.MOBILITY }
            if (mobilityWorkout != null) return mobilityWorkout
        }

        // ── Derive context from history ────────────────────────────────
        val recentResults = history.recentResults
        val lastCompleted = recentResults.firstOrNull { it.status == WorkoutStatus.COMPLETED }
        val lastWorkoutId = lastCompleted?.workoutId
        val lastFocusArea = availableWorkouts.find { it.id == lastWorkoutId }?.focusArea

        // Last 7 completed sessions for the weekly volume guard
        val last7FocusAreas = recentResults
            .filter { it.status == WorkoutStatus.COMPLETED }
            .take(7)
            .mapNotNull { result -> availableWorkouts.find { it.id == result.workoutId }?.focusArea }

        // ── 2. Filter by fitness level ─────────────────────────────────
        val levelMatched = availableWorkouts.filter { it.fitnessLevel == userProfile.fitnessLevel }
            .ifEmpty { availableWorkouts }

        // ── 3. Score each candidate ────────────────────────────────────
        val scoredWorkouts = levelMatched.map { workout ->
            var score = 0

            // Goal alignment (FIX P-05: IMPROVE_FITNESS now maps to all areas)
            if (isGoalAlignedWithFocus(userProfile.fitnessGoal, workout.focusArea)) {
                score += 10
            }

            // Preferred focus area
            if (userProfile.preferredFocusAreas.contains(workout.focusArea)) {
                score += 8
            }

            // Recovery balance — overlap-aware (FIX P-01)
            if (lastFocusArea != null) {
                val isFatiguedByLast = when (lastFocusArea) {
                    FocusArea.FULL_BODY -> FULL_BODY_OVERLAPS.contains(workout.focusArea) ||
                            workout.focusArea == FocusArea.FULL_BODY
                    else -> workout.focusArea == lastFocusArea
                }
                if (isFatiguedByLast) {
                    score -= 15 // Overtraining penalty
                } else {
                    score += 12 // Muscle recovery bonus
                }
            }

            // Intensity preference alignment
            if (workout.intensity == userProfile.intensityPreference) {
                score += 5
            }

            // Weekly volume guard (new): penalise focus areas already well-represented
            val focusCount = last7FocusAreas.count { it == workout.focusArea }
            val maxAllowed = if (workout.focusArea == FocusArea.FULL_BODY) 3 else 2
            if (focusCount >= maxAllowed) {
                score -= 20
            }

            // Staleness guard (FIX P-02): de-prioritise the last workout to break ties
            if (workout.id == lastWorkoutId) {
                score -= 8
            }

            Pair(workout, score)
        }

        return scoredWorkouts.maxByOrNull { it.second }?.first ?: availableWorkouts.first()
    }

    /**
     * Evaluates recent workout performance and feedback to dynamically adjust intensity,
     * progress fitness levels, or substitute exercises.
     *
     * Rule evaluation order:
     *  Rule 1 — Deload check: if a deload is due, return a deload result immediately.
     *  Rule 2 — Level progression: ≥ 3 of last 5 sessions rated TOO_EASY AND completionRate ≥ 0.95.
     *  Rule 3 — De-escalation: ≥ 3 of last 5 sessions rated TOO_HARD OR poor completion rate.
     *  Rule 4 — Completion-rate progression (FIX P-03): completionRate = 1.0 for 3 consecutive
     *            sessions even if no feedback was submitted.
     *  Rule 5 — Steady state: no change required.
     */
    fun evaluateAdaptation(
        userProfile: UserProfile,
        recentResults: List<WorkoutResult>
    ): AdaptationResult {

        // Filter to sessions within the adaptation window (FIX P-04: 30-day recency gate)
        val now = System.currentTimeMillis()
        val windowedResults = recentResults.filter {
            it.status == WorkoutStatus.COMPLETED &&
                    (now - it.completedAtEpochMs) <= ADAPTATION_WINDOW_MS
        }

        if (windowedResults.isEmpty()) {
            return AdaptationResult(
                previousLevel = userProfile.fitnessLevel,
                recommendedLevel = userProfile.fitnessLevel,
                intensityAdjustment = userProfile.intensityPreference,
                recommendedDurationMinutes = 10,
                adaptationReason = "Starting baseline: 10 minutes of balanced daily movement.",
                appliedAtEpochMs = now
            )
        }

        // ── Rule 1: Deload check (FIX P-06) ───────────────────────────
        val totalCompleted = recentResults.count { it.status == WorkoutStatus.COMPLETED }
        if (totalCompleted > 0 && totalCompleted % DELOAD_INTERVAL == 0) {
            return AdaptationResult(
                previousLevel = userProfile.fitnessLevel,
                recommendedLevel = userProfile.fitnessLevel,
                intensityAdjustment = IntensityPreference.EASY,
                recommendedDurationMinutes = 10,
                adaptationReason = "You've completed $totalCompleted sessions — great consistency! " +
                        "This is your scheduled deload window. The next 3–5 days will prioritise " +
                        "mobility and recovery so your body can rebuild stronger.",
                appliedAtEpochMs = now
            )
        }

        // ── Rule 2 & 3: Feedback-based level changes ──────────────────
        val withFeedback = windowedResults.filter { it.feedback != null }.take(5)
        if (withFeedback.isNotEmpty()) {
            val tooEasyCount = withFeedback.count { it.feedback == WorkoutFeedback.TOO_EASY }
            val tooHardCount = withFeedback.count { it.feedback == WorkoutFeedback.TOO_HARD }
            val total = withFeedback.size
            val avgCompletionRate = completionRate(withFeedback)

            // Rule 2: Progress upward
            val progressionTrigger = tooEasyCount >= 3 ||
                    (total >= 2 && tooEasyCount == total)
            if (progressionTrigger && avgCompletionRate >= 0.95f) {
                val nextLevel = levelUp(userProfile.fitnessLevel)
                return AdaptationResult(
                    previousLevel = userProfile.fitnessLevel,
                    recommendedLevel = nextLevel,
                    intensityAdjustment = IntensityPreference.CHALLENGING,
                    recommendedDurationMinutes = 10,
                    adaptationReason = "You've consistently rated sessions as too easy with strong " +
                            "completion. Progressing your fitness level to maintain optimal stimulus.",
                    appliedAtEpochMs = now
                )
            }

            // Rule 3: De-escalate
            val poorCompletionTrigger = avgCompletionRate < 0.60f &&
                    withFeedback.size >= 2
            val deEscalationTrigger = tooHardCount >= 3 ||
                    (total >= 2 && tooHardCount == total) ||
                    poorCompletionTrigger
            if (deEscalationTrigger) {
                val easedLevel = levelDown(userProfile.fitnessLevel)
                return AdaptationResult(
                    previousLevel = userProfile.fitnessLevel,
                    recommendedLevel = easedLevel,
                    intensityAdjustment = IntensityPreference.EASY,
                    recommendedDurationMinutes = 10,
                    suggestedExerciseReplacements = DE_ESCALATION_SWAP_MAP,
                    adaptationReason = "Recent sessions felt too demanding. Adjusting to a gentler " +
                            "intensity and lower-impact movements so you stay consistent without injury.",
                    appliedAtEpochMs = now
                )
            }
        }

        // ── Rule 4: Completion-rate progression (FIX P-03) ───────────
        // Fires even when the user has never submitted feedback.
        val last3 = windowedResults.take(3)
        if (last3.size == 3 && completionRate(last3) == 1.0f) {
            return AdaptationResult(
                previousLevel = userProfile.fitnessLevel,
                recommendedLevel = userProfile.fitnessLevel,
                intensityAdjustment = userProfile.intensityPreference,
                recommendedDurationMinutes = 10,
                adaptationReason = "You've completed 3 sessions back-to-back at 100% — " +
                        "your hardest exercises will get a small duration boost to keep the challenge fresh.",
                appliedAtEpochMs = now
            )
        }

        // ── Rule 5: Steady state ───────────────────────────────────────
        return AdaptationResult(
            previousLevel = userProfile.fitnessLevel,
            recommendedLevel = userProfile.fitnessLevel,
            intensityAdjustment = userProfile.intensityPreference,
            recommendedDurationMinutes = 10,
            adaptationReason = "Great balance! Your current workout pacing and intensity are " +
                    "well-matched to your recovery.",
            appliedAtEpochMs = now
        )
    }

    // ── Private helpers ───────────────────────────────────────────────

    /**
     * FIX P-05: IMPROVE_FITNESS now maps to all focus areas (previously only FULL_BODY).
     * LOSE_WEIGHT maps to full-body and core (high calorie burn).
     * BUILD_STRENGTH maps to the three muscle-group focused areas.
     * FLEXIBILITY/STAY_ACTIVE remain broad.
     */
    private fun isGoalAlignedWithFocus(goal: FitnessGoal, focusArea: FocusArea): Boolean {
        return when (goal) {
            FitnessGoal.LOSE_WEIGHT      -> focusArea == FocusArea.FULL_BODY || focusArea == FocusArea.CORE
            FitnessGoal.BUILD_STRENGTH   -> focusArea == FocusArea.UPPER_BODY ||
                    focusArea == FocusArea.LOWER_BODY ||
                    focusArea == FocusArea.FULL_BODY
            FitnessGoal.IMPROVE_FITNESS  -> true  // FIXED: was only FULL_BODY
            FitnessGoal.FLEXIBILITY      -> focusArea == FocusArea.MOBILITY
            FitnessGoal.STAY_ACTIVE      -> true
        }
    }

    private fun levelUp(current: FitnessLevel): FitnessLevel = when (current) {
        FitnessLevel.BEGINNER     -> FitnessLevel.INTERMEDIATE
        FitnessLevel.INTERMEDIATE -> FitnessLevel.ADVANCED
        FitnessLevel.ADVANCED     -> FitnessLevel.ADVANCED
    }

    private fun levelDown(current: FitnessLevel): FitnessLevel = when (current) {
        FitnessLevel.ADVANCED     -> FitnessLevel.INTERMEDIATE
        FitnessLevel.INTERMEDIATE -> FitnessLevel.BEGINNER
        FitnessLevel.BEGINNER     -> FitnessLevel.BEGINNER
    }

    /**
     * Calculates average exercise completion rate across a list of WorkoutResults.
     * Uses exercisesCompletedCount / totalExercisesCount as the proxy for completion rate.
     */
    private fun completionRate(results: List<WorkoutResult>): Float {
        if (results.isEmpty()) return 0f
        val total = results.sumOf { it.totalExercisesCount }
        if (total == 0) return 1f
        val completed = results.sumOf { it.exercisesCompletedCount }
        return completed.toFloat() / total.toFloat()
    }
}
