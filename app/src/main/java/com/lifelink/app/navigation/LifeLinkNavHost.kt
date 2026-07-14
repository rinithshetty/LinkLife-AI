package com.lifelink.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.lifelink.app.ui.HomeDashboardScreen
import com.lifelink.app.ui.SettingsScreen
import com.lifelink.feature.assistant.AssistantScreen
import com.lifelink.feature.guides.GuideDetailScreen
import com.lifelink.feature.guides.GuidesScreen
import com.lifelink.feature.guides.AlertsScreen
import com.lifelink.feature.hospital.HospitalLocatorScreen
import com.lifelink.feature.medical.ReminderScreen
import com.lifelink.feature.medical.OcrScannerScreen
import com.lifelink.feature.sos.FamilyCheckInScreen
import com.lifelink.feature.medical.VaultScreen
import com.lifelink.feature.onboarding.AuthScreen
import com.lifelink.feature.onboarding.OnboardingScreen
import com.lifelink.feature.sos.ContactsScreen
import com.lifelink.feature.sos.LocationSharingScreen
import com.lifelink.feature.sos.SosScreen

/**
 * :app owns only the top-level NavHost wiring — every screen composable lives in its
 * feature module. This is the module-boundary rule from the M1 architecture doc made
 * concrete: :app never contains a ViewModel or a business-logic screen of its own.
 */
@Composable
fun LifeLinkNavHost(
    onToggleLargeText: (Boolean) -> Unit,
    onToggleDarkTheme: (Boolean?) -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Destination.Onboarding.route) {

        composable(Destination.Onboarding.route) {
            OnboardingScreen(onFinished = { navController.navigate(Destination.Auth.route) })
        }

        composable(Destination.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Destination.Home.route) {
                        popUpTo(Destination.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Destination.Home.route) {
            HomeDashboardScreen(
                onNavigate = { destination -> navController.navigate(destination.route) },
            )
        }

        composable(Destination.Sos.route) {
            SosScreen(onManageContacts = { navController.navigate(Destination.Contacts.route) })
        }

        composable(Destination.Contacts.route) {
            ContactsScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.LocationSharing.route) {
            LocationSharingScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.Vault.route) {
            VaultScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.Reminders.route) {
            ReminderScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.Guides.route) {
            GuidesScreen(
                onBack = { navController.popBackStack() },
                onOpenGuide = { guideId -> navController.navigate(Destination.GuideDetail.createRoute(guideId)) },
            )
        }

        composable(
            route = Destination.GuideDetail.route,
            arguments = listOf(navArgument("guideId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val guideId = backStackEntry.arguments?.getString("guideId").orEmpty()
            GuideDetailScreen(guideId = guideId, onBack = { navController.popBackStack() })
        }

        composable(Destination.Assistant.route) {
            AssistantScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.HospitalLocator.route) {
            HospitalLocatorScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.OcrScanner.route) {
            OcrScannerScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.Alerts.route) {
            AlertsScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.FamilyCheckIn.route) {
            FamilyCheckInScreen(onBack = { navController.popBackStack() })
        }

        composable(Destination.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onToggleLargeText = onToggleLargeText,
                onToggleDarkTheme = onToggleDarkTheme,
            )
        }
    }
}
