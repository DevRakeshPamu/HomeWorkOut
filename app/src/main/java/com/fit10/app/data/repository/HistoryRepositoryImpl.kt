package com.fit10.app.data.repository

import com.fit10.app.data.db.dao.WorkoutResultDao
import com.fit10.app.data.db.mapper.toDomain
import com.fit10.app.data.db.mapper.toEntity
import com.fit10.app.domain.model.WorkoutFeedback
import com.fit10.app.domain.model.WorkoutHistory
import com.fit10.app.domain.model.WorkoutResult
import com.fit10.app.domain.model.WorkoutStatus
import com.fit10.app.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class HistoryRepositoryImpl(
    private val workoutResultDao: WorkoutResultDao
) : HistoryRepository {

    override fun getWorkoutHistory(): Flow<WorkoutHistory> =
        workoutResultDao.getAllResults().map { allEntities ->
            val completed = allEntities.filter { it.status == WorkoutStatus.COMPLETED }
            val totalCount = completed.size
            val totalSeconds = completed.sumOf { it.durationSeconds }
            val totalCalories = completed.sumOf { it.caloriesBurned }

            val completedTimestamps = completed.map { it.completedAtEpochMs }
            val (currentStreak, bestStreak) = calculateStreaks(completedTimestamps)

            val recent = allEntities.take(20).map { it.toDomain() }

            WorkoutHistory(
                totalWorkoutsCompleted = totalCount,
                totalMinutesWorkedOut = totalSeconds / 60,
                totalCaloriesBurned = totalCalories,
                currentStreakDays = currentStreak,
                bestStreakDays = bestStreak,
                recentResults = recent
            )
        }

    override suspend fun saveWorkoutResult(result: WorkoutResult) {
        workoutResultDao.insertResult(result.toEntity())
    }

    override fun getRecentResults(limit: Int): Flow<List<WorkoutResult>> =
        workoutResultDao.getRecentResults(limit).map { list -> list.map { it.toDomain() } }

    companion object {
        /**
         * Computes [currentStreak, bestStreak] based on days of completed workouts.
         */
        fun calculateStreaks(
            completedTimestamps: List<Long>,
            currentEpochMs: Long = System.currentTimeMillis()
        ): Pair<Int, Int> {
            if (completedTimestamps.isEmpty()) return Pair(0, 0)

            // Convert epoch ms to UTC epoch days (days since Jan 1 1970)
            val daysSet = completedTimestamps
                .map { TimeUnit.MILLISECONDS.toDays(it) }
                .toSortedSet(Comparator.reverseOrder())

            if (daysSet.isEmpty()) return Pair(0, 0)

            val todayDay = TimeUnit.MILLISECONDS.toDays(currentEpochMs)
            val sortedDays = daysSet.toList()

            // Current streak calculation
            val mostRecentWorkoutDay = sortedDays.first()
            val isCurrentActive = mostRecentWorkoutDay == todayDay || mostRecentWorkoutDay == todayDay - 1

            var currentStreak = 0
            if (isCurrentActive) {
                var expectedDay = mostRecentWorkoutDay
                for (day in sortedDays) {
                    if (day == expectedDay) {
                        currentStreak++
                        expectedDay--
                    } else if (day < expectedDay) {
                        break
                    }
                }
            }

            // Best streak calculation across all time
            var bestStreak = 0
            var tempStreak = 0
            var prevDay: Long? = null

            for (day in sortedDays.reversed()) { // Ascending
                if (prevDay == null || day == prevDay + 1) {
                    tempStreak++
                } else if (day > prevDay + 1) {
                    tempStreak = 1
                }
                if (tempStreak > bestStreak) {
                    bestStreak = tempStreak
                }
                prevDay = day
            }

            return Pair(currentStreak, maxOf(currentStreak, bestStreak))
        }
    }
}
