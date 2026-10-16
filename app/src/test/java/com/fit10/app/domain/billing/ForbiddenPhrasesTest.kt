package com.fit10.app.domain.billing

import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class ForbiddenPhrasesTest {

    private val forbiddenPhrases = listOf(
        "Real-time AI adaptive recalibrations",
        "science-backed",
        "100% ad-free",
        "AI adaptive"
    )

    @Test
    fun paywallSource_doesNotContainForbiddenPhrases() {
        val paywallFile = File("src/main/java/com/fit10/app/feature/paywall/PaywallScreen.kt")
        if (paywallFile.exists()) {
            val content = paywallFile.readText()
            for (phrase in forbiddenPhrases) {
                assertFalse("PaywallScreen must not contain forbidden phrase '$phrase'", content.contains(phrase, ignoreCase = true))
            }
        }
    }

    @Test
    fun stringsXml_doesNotContainForbiddenPhrases() {
        val stringsFile = File("src/main/res/values/strings.xml")
        if (stringsFile.exists()) {
            val content = stringsFile.readText()
            for (phrase in forbiddenPhrases) {
                assertFalse("strings.xml must not contain forbidden phrase '$phrase'", content.contains(phrase, ignoreCase = true))
            }
        }
    }

    @Test
    fun prdHtml_doesNotContainForbiddenPhrases() {
        // Test root or relative path
        val prdFile = File("../../fit10_prd.html").takeIf { it.exists() } ?: File("fit10_prd.html")
        if (prdFile.exists()) {
            val content = prdFile.readText()
            for (phrase in forbiddenPhrases) {
                assertFalse("fit10_prd.html must not contain forbidden phrase '$phrase'", content.contains(phrase, ignoreCase = true))
            }
        }
    }
}
