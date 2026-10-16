package com.fit10.app.core.analytics

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsTrackerTest {

    @Test
    fun defaultConsent_isFalseAndDropsEvents() {
        val tracker = LocalAnalyticsTracker()
        assertFalse(tracker.isConsentGiven.value)

        tracker.trackEvent("app_open", mapOf("source" to "launcher"))
        assertEquals(0, tracker.getQueuedEvents().size)
    }

    @Test
    fun consentEnabled_queuesValidEvents() = runBlocking {
        val tracker = LocalAnalyticsTracker()
        tracker.setConsent(true)
        assertTrue(tracker.isConsentGiven.value)

        tracker.trackEvent("workout_started", mapOf("workout_id" to "full_body_ignition"))
        tracker.trackEvent("workout_completed", mapOf("workout_id" to "full_body_ignition", "duration_bucket" to "10m"))

        val queued = tracker.getQueuedEvents()
        assertEquals(2, queued.size)
        assertEquals("workout_started", queued[0].eventName)
        assertEquals("workout_completed", queued[1].eventName)
        assertTrue(queued[0].properties.containsKey("install_id"))
    }

    @Test
    fun revokingConsent_immediatelyClearsQueueAndHaltsTracking() = runBlocking {
        val tracker = LocalAnalyticsTracker()
        tracker.setConsent(true)
        tracker.trackEvent("app_open")
        assertEquals(1, tracker.getQueuedEvents().size)

        // User revokes consent in settings
        tracker.setConsent(false)
        assertFalse(tracker.isConsentGiven.value)
        assertEquals("Queue must be wiped when consent is revoked", 0, tracker.getQueuedEvents().size)

        tracker.trackEvent("workout_started")
        assertEquals(0, tracker.getQueuedEvents().size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun allowlist_blocksForbiddenHealthKey_calories() = runBlocking {
        val tracker = LocalAnalyticsTracker()
        tracker.setConsent(true)
        tracker.trackEvent("workout_completed", mapOf("calories" to 120))
    }

    @Test(expected = IllegalArgumentException::class)
    fun allowlist_blocksForbiddenHealthKey_weight() = runBlocking {
        val tracker = LocalAnalyticsTracker()
        tracker.setConsent(true)
        tracker.trackEvent("onboarding_completed", mapOf("weight" to 75))
    }

    @Test(expected = IllegalArgumentException::class)
    fun allowlist_blocksForbiddenKey_advertisingId() = runBlocking {
        val tracker = LocalAnalyticsTracker()
        tracker.setConsent(true)
        tracker.trackEvent("app_open", mapOf("advertising_id" to "abc-123"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun allowlist_blocksUnknownEventName() = runBlocking {
        val tracker = LocalAnalyticsTracker()
        tracker.setConsent(true)
        tracker.trackEvent("user_clicked_secret_button")
    }

    @Test
    fun installId_canBeReset() {
        val tracker = LocalAnalyticsTracker()
        val id1 = tracker.getOrCreateInstallId()
        val id2 = tracker.getOrCreateInstallId()
        assertEquals(id1, id2)

        val id3 = tracker.resetInstallId()
        assertNotEquals(id1, id3)
    }
}
