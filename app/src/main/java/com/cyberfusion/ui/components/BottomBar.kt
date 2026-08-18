package com.cyberfusion.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun CyberFusionBottomBar(navController: NavController) {
    val items = listOf(
        ScreenItem("Home", Icons.Default.Home) { navController.navigate("dashboard") },
        ScreenItem("AI", Icons.Default.Psychology) { navController.navigate("ai") },
        ScreenItem("Intel", Icons.Default.Search) { navController.navigate("threat_intel") },
        ScreenItem("Labs", Icons.Default.Science) { navController.navigate("labs") },
        ScreenItem("More", Icons.Default.Menu) { navController.navigate("more") }
    )
    
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = false,
                onClick = item.onClick
            )
        }
    }
}

data class ScreenItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val onClick: () -> Unit)
