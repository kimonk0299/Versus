package com.versus.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.versus.app.R

// ── Google Fonts Provider ──
// Downloads fonts at runtime from Google Fonts. Falls back to system font if offline.
private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// ── Fredoka ──
// Bubbly, chunky, playful — used for big headings and the app title.
// Think party game logos and fun scoreboards.
private val fredokaFont = GoogleFont("Fredoka")
val FredokaFamily = FontFamily(
    Font(googleFont = fredokaFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = fredokaFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = fredokaFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = fredokaFont, fontProvider = fontProvider, weight = FontWeight.Bold),
)

// ── Nunito ──
// Rounded, friendly, clean — used for body text, buttons, and labels.
// Readable but still fun (not boring like a default sans-serif).
private val nunitoFont = GoogleFont("Nunito")
val NunitoFamily = FontFamily(
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.Bold),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.ExtraBold),
    Font(googleFont = nunitoFont, fontProvider = fontProvider, weight = FontWeight.Black),
)

// ── Typography ──
// Fredoka for big display/headline text (bubbly and fun).
// Nunito for everything else (rounded and friendly).
val VsTypography = Typography(
    // App title "VERSUS" and big labels
    displayLarge = TextStyle(
        fontFamily = FredokaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        letterSpacing = 1.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FredokaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FredokaFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    // Screen titles ("Tournament", "Head to Head")
    headlineLarge = TextStyle(
        fontFamily = FredokaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    // Section headers
    headlineMedium = TextStyle(
        fontFamily = FredokaFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    // Movie titles on cards
    titleLarge = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    // Subtitles and button text
    titleMedium = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    // Body text
    bodyLarge = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    // Secondary body text
    bodyMedium = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Labels and captions
    labelLarge = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp
    )
)
