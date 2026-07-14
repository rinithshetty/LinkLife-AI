package com.lifelink.core.ui.theme

import androidx.compose.ui.graphics.Color

// Primary: calm, trustworthy teal — deliberately NOT red, so red stays reserved
// exclusively for SOS/urgent UI and keeps its signaling power.
val TealPrimary = Color(0xFF00695C)
val TealPrimaryDark = Color(0xFF4DB6AC)
val TealContainer = Color(0xFFB2DFDB)

// Reserved urgency color — used ONLY for SOS button, active-emergency states, and
// critical alerts. Never used decoratively elsewhere in the app.
val EmergencyRed = Color(0xFFD32F2F)
val EmergencyRedContainer = Color(0xFFFFCDD2)

val SafetyGreen = Color(0xFF2E7D32) // "safe check-in" / resolved states
val WarningAmber = Color(0xFFF9A825) // disaster alerts, medium priority

val Neutral10 = Color(0xFF1A1C1C)
val Neutral95 = Color(0xFFF3F4F4)
val NeutralOutline = Color(0xFF6F7979)

val SurfaceLight = Color(0xFFFAFDFC)
val SurfaceDark = Color(0xFF0F1414)
