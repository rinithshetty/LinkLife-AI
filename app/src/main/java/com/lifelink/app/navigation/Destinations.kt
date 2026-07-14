package com.lifelink.app.navigation

/**
 * Single source of truth for every route in the app. Kept as plain string-route objects
 * (rather than typed nav-args classes) deliberately for v1 simplicity — see ADR-003 for
 * why type-safe Navigation Compose args were deferred to a later milestone.
 */
sealed class Destination(val route: String) {
    data object Onboarding : Destination("onboarding")
    data object Auth : Destination("auth")
    data object Home : Destination("home")
    data object Sos : Destination("sos")
    data object Contacts : Destination("contacts")
    data object LocationSharing : Destination("location_sharing")
    data object Vault : Destination("vault")
    data object Reminders : Destination("reminders")
    data object Guides : Destination("guides")
    data object GuideDetail : Destination("guide_detail/{guideId}") {
        fun createRoute(guideId: String) = "guide_detail/$guideId"
    }
    data object Assistant : Destination("assistant")
    data object HospitalLocator : Destination("hospital_locator")
    data object OcrScanner : Destination("ocr_scanner")
    data object Alerts : Destination("alerts")
    data object FamilyCheckIn : Destination("family_checkin")
    data object Settings : Destination("settings")
}
