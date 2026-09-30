package com.cyberfusion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyberfusion.ui.features.ai.ChatScreen
import com.cyberfusion.ui.features.alerts.AlertsScreen
import com.cyberfusion.ui.features.aimodels.AIModelsScreen
import com.cyberfusion.ui.features.dashboard.DashboardScreen
import com.cyberfusion.ui.features.diagnostics.DiagnosticsScreen
import com.cyberfusion.ui.features.grc.GRCScreen
import com.cyberfusion.ui.features.incidents.IncidentsScreen
import com.cyberfusion.ui.features.investigations.InvestigationsScreen
import com.cyberfusion.ui.features.labs.LabDetailScreen
import com.cyberfusion.ui.features.labs.LabsScreen
import com.cyberfusion.ui.features.more.MoreScreen
import com.cyberfusion.ui.features.reports.ReportsScreen
import com.cyberfusion.ui.features.settings.SettingsScreen
import com.cyberfusion.ui.features.threatintel.ThreatIntelScreen
import com.cyberfusion.ui.features.tools.ToolsScreen

@Composable
fun CyberFusionNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.AI.route) { ChatScreen(navController) }
        composable(Screen.ThreatIntel.route) { ThreatIntelScreen(navController) }
        composable(Screen.Labs.route) { LabsScreen(navController) }
        composable(Screen.More.route) { MoreScreen(navController) }

        composable(Screen.LabDetail.route) { entry ->
            val labId = entry.arguments?.getString("labId")?.toLongOrNull() ?: 0L
            LabDetailScreen(navController, labId)
        }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.Alerts.route) { AlertsScreen(navController) }
        composable(Screen.Incidents.route) { IncidentsScreen(navController) }
        composable(Screen.GRC.route) { GRCScreen(navController) }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable(Screen.Investigations.route) { InvestigationsScreen(navController) }
        composable(Screen.Tools.route) { ToolsScreen(navController) }
        composable(Screen.AIModels.route) { AIModelsScreen(navController) }
        composable(Screen.Diagnostics.route) { DiagnosticsScreen(navController) }
    }
}
