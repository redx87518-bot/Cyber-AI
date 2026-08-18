package com.cyberfusion.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun CyberFusionBottomBar(navController: NavController) {
    val items = listOf(
        ScreenItem("Home", "dashboard", Icons.Default.Home),
        ScreenItem("AI", "ai", Icons.Default.Psychology),
        ScreenItem("Intel", "threat_intel", Icons.Default.Search),
        ScreenItem("Labs", "labs", Icons.Default.Science),
        ScreenItem("More", "more", Icons.Default.Menu)
    )
    
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            val selected = currentRoute == item.route || (item.route == "dashboard" && currentRoute in listOf("alerts", "investigations", "incidents", "grc", "reports", "tools", "ai_models"))
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selected,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

data class ScreenItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
