package com.fit10.app.core.analytics

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface AnalyticsTracker {
    val isConsentGiven: StateFlow<Boolean>
    suspend fun setConsent(enabled: Boolean)
    fun trackEvent(eventName: String, properties: Map<String, Any> = emptyMap())
    fun getQueuedEvents(): List<AnalyticsEvent>
    fun clearQueue()
    fun getOrCreateInstallId(): String
    fun resetInstallId(): String
}

/**
 * Privacy-first local implementation of AnalyticsTracker.
 *
 * Rules:
 * 1. Default consent is OFF (false).
 * 2. If consent is OFF, trackEvent is a strict no-op.
 * 3. When consent is toggled to OFF, the local queue is immediately wiped.
 * 4. Install ID is a random pseudo-anonymous UUID, completely resettable.
 * 5. All events pass through AnalyticsEventAllowlist validation.
 */
class LocalAnalyticsTracker(
    context: Context? = null,
    private val prefs: SharedPreferences? = context?.getSharedPreferences("fit10_analytics_prefs", Context.MODE_PRIVATE)
) : AnalyticsTracker {

    private val _isConsentGiven = MutableStateFlow(
        prefs?.getBoolean("analytics_consent", false) ?: false
    )
    override val isConsentGiven: StateFlow<Boolean> = _isConsentGiven.asStateFlow()

    private val eventQueue = mutableListOf<AnalyticsEvent>()
    private val lock = Any()

    override suspend fun setConsent(enabled: Boolean) {
        _isConsentGiven.value = enabled
        prefs?.edit()?.putBoolean("analytics_consent", enabled)?.apply()
        if (!enabled) {
            clearQueue()
        }
    }

    override fun trackEvent(eventName: String, properties: Map<String, Any>) {
        if (!_isConsentGiven.value) {
            // Strictly drop when consent is disabled
            return
        }

        AnalyticsEventAllowlist.validateEvent(eventName, properties)

        val enrichedProps = properties.toMutableMap()
        enrichedProps["install_id"] = getOrCreateInstallId()

        synchronized(lock) {
            eventQueue.add(AnalyticsEvent(eventName, enrichedProps))
        }
    }

    override fun getQueuedEvents(): List<AnalyticsEvent> {
        synchronized(lock) {
            return eventQueue.toList()
        }
    }

    override fun clearQueue() {
        synchronized(lock) {
            eventQueue.clear()
        }
    }

    private var inMemoryInstallId: String? = null

    override fun getOrCreateInstallId(): String {
        val existing = prefs?.getString("install_id", null) ?: inMemoryInstallId
        if (!existing.isNullOrBlank()) return existing

        val newId = UUID.randomUUID().toString()
        inMemoryInstallId = newId
        prefs?.edit()?.putString("install_id", newId)?.apply()
        return newId
    }

    override fun resetInstallId(): String {
        val newId = UUID.randomUUID().toString()
        inMemoryInstallId = newId
        prefs?.edit()?.putString("install_id", newId)?.apply()
        return newId
    }
}
