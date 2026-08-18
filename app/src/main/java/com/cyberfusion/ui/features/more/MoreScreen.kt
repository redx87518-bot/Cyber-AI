package com.cyberfusion.ui.features.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.theme.Gold

@Composable
fun MoreScreen(navController: NavController) {
    val items = listOf(
        MoreItem("Alerts", Icons.Default.Notifications, "alerts"),
        MoreItem("Investigations", Icons.Default.Search, "investigations"),
        MoreItem("Incidents", Icons.Default.Warning, "incidents"),
        MoreItem("GRC", Icons.Default.Security, "grc"),
        MoreItem("Reports", Icons.Default.Description, "reports"),
        MoreItem("Settings", Icons.Default.Settings, "settings")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("More", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { CyberFusionBottomBar(navController) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { item ->
                Card(modifier = Modifier.fillMaxWidth(), onClick = { 
                    when (item.route) {
                        "settings" -> navController.navigate("settings")
                        "alerts" -> navController.navigate("dashboard")
                        "investigations" -> navController.navigate("dashboard")
                        "incidents" -> navController.navigate("dashboard")
                        "grc" -> navController.navigate("dashboard")
                        "reports" -> navController.navigate("dashboard")
                    }
                }) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(item.icon, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(item.title, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

data class MoreItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)