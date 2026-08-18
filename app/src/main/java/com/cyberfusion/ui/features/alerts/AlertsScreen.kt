package com.cyberfusion.ui.features.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.Gold

@Composable
fun AlertsScreen(navController: NavController) {
    val alerts = listOf(
        AlertItem("Brute Force Attempt", "Critical", "Auth Logs", "2 min ago"),
        AlertItem("Suspicious Login", "High", "EDR", "15 min ago"),
        AlertItem("Malware Detection", "Medium", "AV", "1 hour ago")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alerts", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(alerts) { alert ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text(alert.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Surface(color = when(alert.severity) { "Critical" -> Color.Red; "High" -> Color(0xFFFF9800); else -> Color.Yellow }, shape = MaterialTheme.shapes.small) {
                                Text(alert.severity, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Text(alert.source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(alert.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

data class AlertItem(val title: String, val severity: String, val source: String, val time: String)