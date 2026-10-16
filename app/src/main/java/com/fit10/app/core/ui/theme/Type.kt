package com.fit10.app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.fit10.app.R

// ── Google Font Provider ─────────────────────────────────────────────────────
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

// ── Outfit — clean modern athletic sans ─────────────────────────────────────
private val OutfitFont = GoogleFont("Outfit")

private val OutfitFontFamily = FontFamily(
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.ExtraBold),
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.Black),
)

/**
 * Fit10 athletic typography scale — Outfit font, strong hierarchy,
 * energetic headlines, crisp data readability.
 */
val Fit10Typography = Typography(

    // ── Display (Timers, big hero metrics) ──────────────────────────────────
    displayLarge = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Black,
        fontSize      = 56.sp,
        lineHeight    = 62.sp,
        letterSpacing = (-1.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.ExtraBold,
        fontSize      = 44.sp,
        lineHeight    = 50.sp,
        letterSpacing = (-1.0).sp,
    ),
    displaySmall = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 36.sp,
        lineHeight    = 42.sp,
        letterSpacing = (-0.5).sp,
    ),

    // ── Headlines (Screen titles, workout names) ─────────────────────────────
    headlineLarge = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 30.sp,
        lineHeight    = 38.sp,
        letterSpacing = (-0.75).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 24.sp,
        lineHeight    = 32.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 20.sp,
        lineHeight    = 28.sp,
        letterSpacing = (-0.25).sp,
    ),

    // ── Titles (Card headers, section labels) ────────────────────────────────
    titleLarge = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 20.sp,
        lineHeight    = 26.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleMedium = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 16.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp,
    ),

    // ── Body (Descriptions, instructions, metadata) ──────────────────────────
    bodyLarge = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 15.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.2.sp,
    ),
    bodySmall = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.25.sp,
    ),

    // ── Labels (Buttons, chips, metric badges, captions) ─────────────────────
    labelLarge = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 15.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 13.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontFamily    = OutfitFontFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 11.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
