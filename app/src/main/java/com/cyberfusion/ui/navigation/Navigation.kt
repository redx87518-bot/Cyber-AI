package com.cyberfusion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.features.ai.ChatScreen
import com.cyberfusion.ui.features.dashboard.DashboardScreen
import com.cyberfusion.ui.features.labs.LabDetailScreen
import com.cyberfusion.ui.features.labs.LabsScreen
import com.cyberfusion.ui.features.more.MoreScreen
import com.cyberfusion.ui.features.settings.SettingsScreen
import com.cyberfusion.ui.features.threatintel.ThreatIntelScreen

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object AI : Screen("ai", "AI Analyst")
    object ThreatIntel : Screen("threat_intel", "Threat Intel")
    object Labs : Screen("labs", "Labs")
    object More : Screen("more", "More")
    object LabDetail : Screen("lab_detail/{labId}", "Lab Detail")
    object Settings : Screen("settings", "Settings")
}

@Composable
fun CyberFusionNavHost(navController: NavHostController = androidx.navigation.compose.rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.AI.route) { ChatScreen(navController) }
        composable(Screen.ThreatIntel.route) { ThreatIntelScreen(navController) }
        composable(Screen.Labs.route) { LabsScreen(navController) }
        composable(Screen.More.route) { MoreScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.LabDetail.route) { backStackEntry ->
            val labId = backStackEntry.arguments?.getString("labId")?.toLongOrNull() ?: 0L
            LabDetailScreen(navController, labId)
        }
    }
}
