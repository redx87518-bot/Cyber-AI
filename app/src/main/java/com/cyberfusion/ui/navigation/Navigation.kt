package com.cyberfusion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cyberfusion.ui.features.ai.ChatScreen
import com.cyberfusion.ui.features.dashboard.DashboardScreen
import com.cyberfusion.ui.features.labs.LabsScreen
import com.cyberfusion.ui.features.more.MoreScreen
import com.cyberfusion.ui.features.threatintel.ThreatIntelScreen

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object AI : Screen("ai", "AI Analyst")
    object ThreatIntel : Screen("threat_intel", "Threat Intel")
    object Labs : Screen("labs", "Labs")
    object More : Screen("more", "More")
}

@Composable
fun CyberFusionNavHost(navController: NavHostController = androidx.navigation.compose.rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.AI.route) { ChatScreen(navController) }
        composable(Screen.ThreatIntel.route) { ThreatIntelScreen(navController) }
        composable(Screen.Labs.route) { LabsScreen(navController) }
        composable(Screen.More.route) { MoreScreen(navController) }
    }
}
