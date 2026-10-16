package com.fit10.app.data.repository

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class HistoryRepositoryTest {

    private val oneDayMs = TimeUnit.DAYS.toMillis(1)
    private val nowMs = System.currentTimeMillis()

    @Test
    fun calculateStreaks_emptyHistory_returnsZeroes() {
        val (current, best) = HistoryRepositoryImpl.calculateStreaks(emptyList(), nowMs)
        assertEquals(0, current)
        assertEquals(0, best)
    }

    @Test
    fun calculateStreaks_workoutToday_hasStreakOfOne() {
        val timestamps = listOf(nowMs)
        val (current, best) = HistoryRepositoryImpl.calculateStreaks(timestamps, nowMs)
        assertEquals(1, current)
        assertEquals(1, best)
    }

    @Test
    fun calculateStreaks_workoutYesterday_hasStreakOfOne() {
        val timestamps = listOf(nowMs - oneDayMs)
        val (current, best) = HistoryRepositoryImpl.calculateStreaks(timestamps, nowMs)
        assertEquals(1, current)
        assertEquals(1, best)
    }

    @Test
    fun calculateStreaks_workoutTwoDaysAgo_streakBroken() {
        val timestamps = listOf(nowMs - (2 * oneDayMs))
        val (current, best) = HistoryRepositoryImpl.calculateStreaks(timestamps, nowMs)
        assertEquals(0, current)
        assertEquals(1, best)
    }

    @Test
    fun calculateStreaks_consecutiveThreeDays_hasStreakOfThree() {
        val timestamps = listOf(
            nowMs,
            nowMs - oneDayMs,
            nowMs - (2 * oneDayMs)
        )
        val (current, best) = HistoryRepositoryImpl.calculateStreaks(timestamps, nowMs)
        assertEquals(3, current)
        assertEquals(3, best)
    }

    @Test
    fun calculateStreaks_multipleWorkoutsOnSameDay_countedOnce() {
        val timestamps = listOf(
            nowMs,
            nowMs - 3600000L, // 1 hour earlier today
            nowMs - oneDayMs
        )
        val (current, best) = HistoryRepositoryImpl.calculateStreaks(timestamps, nowMs)
        assertEquals(2, current)
        assertEquals(2, best)
    }

    @Test
    fun calculateStreaks_pastStreakHigherThanCurrent_preservesBestStreak() {
        // Past streak: days -10, -9, -8, -7, -6 (5 days)
        // Current streak: today, yesterday (2 days)
        val timestamps = listOf(
            nowMs,
            nowMs - oneDayMs,
            nowMs - (6 * oneDayMs),
            nowMs - (7 * oneDayMs),
            nowMs - (8 * oneDayMs),
            nowMs - (9 * oneDayMs),
            nowMs - (10 * oneDayMs)
        )
        val (current, best) = HistoryRepositoryImpl.calculateStreaks(timestamps, nowMs)
        assertEquals(2, current)
        assertEquals(5, best)
    }
}
