package com.cyberfusion.ui.features.alerts
import androidx.compose.runtime.getValue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Gold
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AlertsScreen(navController: NavController) {
    val viewModel: AlertsViewModel = viewModel(factory = LocalViewModelFactory.current)
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alerts", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.alerts) { alert ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text(alert.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Surface(color = when(alert.severity) { "Critical" -> Color.Red; "High" -> Color(0xFFFF9800); "Medium" -> Color.Yellow; else -> Color.Green }, shape = MaterialTheme.shapes.small) {
                                Text(alert.severity, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Text(alert.source, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(alert.description ?: alert.status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (uiState.alerts.isEmpty()) {
                item {
                    Text("No alerts found. Configure threat intelligence sources in Settings.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
