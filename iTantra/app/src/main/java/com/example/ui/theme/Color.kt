package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Warm Modern Editorial Palette (No Blue, No Black, No Green, No Glowing)
// Warm Linen, Soft Sandstone, Terracotta Rust, Warm Amber Ochre, Deep Espresso
// ==========================================
val WarmCanvas = Color(0xFFFAF7F2)          // Soft warm linen canvas
val WarmCard = Color(0xFFFFFFFF)            // Pure warm white elevated card
val WarmCardSubtle = Color(0xFFF4ECE4)      // Soft clay/sand inset
val WarmCardSelected = Color(0xFFEFE6DC)    // Warm stone selected state

// Primary Brand Accent: Rich Warm Terracotta (Non-blue, non-green, non-black)
val WarmTerracotta = Color(0xFFC7512E)      // Distinctive terracotta copper
val WarmTerracottaDark = Color(0xFFA83F20)  // Deep terracotta
val WarmTerracottaLight = Color(0xFFFCEEE8) // Warm blush/terracotta wash
val WarmTerracottaSubtle = Color(0xFFFFF6F2)

// Warm Ochre / Amber Accent (Replaces Green for Connected & Delivered status)
val WarmAmber = Color(0xFFB45309)           // Warm golden ochre (Connected/Delivered)
val WarmAmberLight = Color(0xFFFEF3C7)      // Soft warm amber tint
val WarmGold = Color(0xFFD97706)            // Secondary amber gold
val WarmGoldLight = Color(0xFFFFFBEB)

// Alert / SOS / Emergency: Deep Wine Crimson
val WarmCrimson = Color(0xFFB91C1C)         // Clean rich crimson
val WarmCrimsonLight = Color(0xFFFEE2E2)    // Soft rose crimson wash

// High-Contrast Warm Typography (Crisp Espresso Roast - not cold harsh black)
val TextWarmPrimary = Color(0xFF26211E)     // Deep roasted espresso
val TextWarmSecondary = Color(0xFF6B625B)   // Warm balanced taupe
val TextWarmTertiary = Color(0xFF9E948A)    // Soft muted sandstone
val BorderWarm = Color(0xFFE8E0D5)          // Crisp soft stone border
val BorderWarmActive = Color(0xFFC7512E)    // Terracotta focus border

// ------------------------------------------
// Mapped Platform Aliases
// ------------------------------------------
val IosCanvas = WarmCanvas
val IosCard = WarmCard
val IosCardSubtle = WarmCardSubtle
val IosCardSelected = WarmCardSelected

val IosPrimary = WarmTerracotta
val IosPrimaryDark = WarmTerracottaDark
val IosPrimaryLight = WarmTerracottaLight
val IosPrimarySubtle = WarmTerracottaSubtle

val IosSuccess = WarmAmber                  // Warm amber (Non-green)
val IosSuccessLight = WarmAmberLight
val IosWarning = WarmGold
val IosWarningLight = WarmGoldLight
val IosDestructive = WarmCrimson
val IosDestructiveLight = WarmCrimsonLight
val IosDestructiveBorder = WarmCrimson

val IosTextPrimary = TextWarmPrimary
val IosTextSecondary = TextWarmSecondary
val IosTextTertiary = TextWarmTertiary
val IosBorder = BorderWarm
val IosBorderLight = BorderWarm

// Backward-compatible tactical aliases mapped to Warm Palette
val DarkCanvas = WarmCanvas
val DarkCard = WarmCard
val DarkCardSubtle = WarmCardSubtle
val DarkCardSelected = WarmCardSelected

val DarkAccentCyan = WarmTerracotta
val DarkAccentBlue = WarmTerracottaDark
val DarkAccentGlow = Color(0x14C7512E)

val StatusGreen = WarmAmber
val StatusGreenGlow = Color(0x14B45309)
val StatusAmber = WarmGold
val StatusRed = WarmCrimson
val StatusRedGlow = Color(0x14B91C1C)

val TextLightPrimary = TextWarmPrimary
val TextLightSecondary = TextWarmSecondary
val TextLightTertiary = TextWarmTertiary
val BorderMuted = BorderWarm
val BorderActive = BorderWarmActive

val NavyVoid = WarmCanvas
val NavyDark = WarmCard
val NavyCard = WarmCard
val NavySurface = WarmCardSubtle
val NavyBorder = BorderWarm
val NavyBorderBright = BorderWarmActive

val ElectricBlue = WarmTerracotta
val ElectricBlueGlow = Color(0x14C7512E)
val TealAccent = WarmAmber
val TealDim = WarmAmber

val StatusOnline = WarmAmber
val StatusOffline = TextWarmTertiary
val WarningAmber = WarmGold
val EmergencyRed = WarmCrimson
val EmergencyDark = WarmCrimsonLight
val EmergencyGlow = Color(0x14B91C1C)

val TextPrimary = TextWarmPrimary
val TextSecondary = TextWarmSecondary
val TextMuted = TextWarmTertiary
val TextInverse = Color(0xFFFAF7F2)



