package com.fit10.app.core.analytics

/**
 * Representation of an ethical, consent-governed analytics event.
 */
data class AnalyticsEvent(
    val eventName: String,
    val properties: Map<String, Any>,
    val timestampEpochMs: Long = System.currentTimeMillis()
)

/**
 * Strict allowlist validator for Fit10 analytics.
 * Enforces zero-health-data, zero-fingerprinting, and zero-PII principles.
 */
object AnalyticsEventAllowlist {

    val ALLOWED_EVENTS = setOf(
        "onboarding_completed",
        "workout_started",
        "workout_completed",
        "workout_abandoned",
        "paywall_viewed",
        "trial_started",
        "subscription_started",
        "reminder_enabled",
        "difficulty_rating_submitted",
        "app_open"
    )

    // Strictly forbidden property keys under DPDP / Privacy by Design
    val FORBIDDEN_KEYS = setOf(
        "weight",
        "height",
        "injury",
        "injuries",
        "calorie",
        "calories",
        "heart_rate",
        "pulse",
        "exercise_name",
        "advertising_id",
        "device_id",
        "imei",
        "mac_address",
        "email",
        "phone",
        "name",
        "free_text",
        "notes"
    )

    /**
     * Validates whether an event name and its properties comply with the privacy allowlist.
     * Throws IllegalArgumentException if any forbidden key or invalid event is detected.
     */
    fun validateEvent(eventName: String, properties: Map<String, Any>) {
        require(eventName in ALLOWED_EVENTS) {
            "Event '$eventName' is not in the approved analytics event allowlist."
        }

        for (key in properties.keys) {
            val normalizedKey = key.lowercase().trim()
            require(normalizedKey !in FORBIDDEN_KEYS) {
                "Property '$key' is strictly forbidden in analytics under Fit10 privacy policy."
            }

            // Ensure no free-text or unbounded string values that could leak personal info
            val value = properties[key]
            if (value is String) {
                require(value.length <= 100) {
                    "Property value for '$key' exceeds maximum allowed length of 100 characters."
                }
            }
        }
    }
}
