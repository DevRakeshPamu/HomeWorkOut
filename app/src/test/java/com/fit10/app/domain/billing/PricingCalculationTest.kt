package com.fit10.app.domain.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PricingCalculationTest {

    @Test
    fun computeMonthlyEquivalent_annualRupees_computesAccurateFraction() {
        val annualMicros = 499_000_000L // ₹499
        val monthlyFormatted = SubscriptionPlanDetails.computeMonthlyEquivalent(annualMicros, "INR")

        // 499 / 12 = 41.5833... -> must format to 41.58, NEVER 41.50
        assertTrue(
            "Monthly equivalent for ₹499 must contain '41.58', actual: $monthlyFormatted",
            monthlyFormatted.contains("41.58")
        )
    }

    @Test
    fun computeMonthlyEquivalent_usdAndEur_computesAccurately() {
        // $47.99 / year -> $4.00 / month
        val usdFormatted = SubscriptionPlanDetails.computeMonthlyEquivalent(47_990_000L, "USD")
        assertTrue("USD monthly equivalent should round to 4.00: $usdFormatted", usdFormatted.contains("4.00"))

        // €59.99 / year -> €5.00 / month
        val eurFormatted = SubscriptionPlanDetails.computeMonthlyEquivalent(59_990_000L, "EUR")
        assertTrue("EUR monthly equivalent should round to 5.00: $eurFormatted", eurFormatted.contains("5.00"))
    }

    @Test
    fun computeSavingsPercentage_calculatesRealPercentageFromMicros() {
        val annualMicros = 499_000_000L    // ₹499/yr
        val monthlyMicros = 149_000_000L   // ₹149/mo -> 12 * 149 = ₹1788/yr
        // (1788 - 499) / 1788 = 1289 / 1788 = 72.09% -> 72%
        val savings = SubscriptionPlanDetails.computeSavingsPercentage(annualMicros, monthlyMicros)
        assertEquals(72, savings)
    }

    @Test
    fun computeSavingsPercentage_alternativePrices() {
        // $48/yr vs $6/mo ($72/yr) -> 24 / 72 = 33%
        val savings33 = SubscriptionPlanDetails.computeSavingsPercentage(48_000_000L, 6_000_000L)
        assertEquals(33, savings33)

        // Equal cost -> 0%
        val savings0 = SubscriptionPlanDetails.computeSavingsPercentage(120_000_000L, 10_000_000L)
        assertEquals(0, savings0)
    }
}
