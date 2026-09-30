package com.cyberfusion.ui.navigation

/**
 * App destinations. `isTab` entries live in the bottom bar; navigate with
 * [navigateTab] so the back stack stays shallow and state is restored.
 */
sealed class Screen(val route: String, val title: String, val isTab: Boolean = false) {
    data object Dashboard : Screen("dashboard", "Dashboard", isTab = true)
    data object AI : Screen("ai", "AI Analyst", isTab = true)
    data object ThreatIntel : Screen("threat_intel", "Threat Intel", isTab = true)
    data object Labs : Screen("labs", "Labs", isTab = true)
    data object More : Screen("more", "More", isTab = true)

    data object LabDetail : Screen("lab_detail/{labId}", "Lab Detail")
    data object Settings : Screen("settings", "Settings")
    data object Alerts : Screen("alerts", "Alerts")
    data object Incidents : Screen("incidents", "Incidents")
    data object GRC : Screen("grc", "GRC")
    data object Reports : Screen("reports", "Reports")
    data object Investigations : Screen("investigations", "Investigations")
    data object Tools : Screen("tools", "Tools")
    data object AIModels : Screen("ai_models", "AI Models")
    data object Diagnostics : Screen("diagnostics", "Diagnostics")

    companion object {
        val tabs = listOf(Dashboard, AI, ThreatIntel, Labs, More)

        fun labDetail(labId: Long) = "lab_detail/$labId"
    }
}
