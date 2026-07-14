package com.lifelink.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Base type scale is intentionally ~1sp larger than stock Material3 defaults across the
 * board. This gives headroom for the Accessibility "Large Text" multiplier (applied in
 * LifeLinkTheme) to scale cleanly without body text ever dropping below a comfortable
 * reading size for emergency-context reading (stress reduces reading ease).
 */
val LifeLinkTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 58.sp, lineHeight = 64.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 29.sp, lineHeight = 36.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 23.sp, lineHeight = 29.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 25.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 17.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 21.sp),
)
