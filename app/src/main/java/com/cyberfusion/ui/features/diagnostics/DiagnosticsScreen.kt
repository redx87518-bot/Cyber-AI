package com.cyberfusion.ui.features.diagnostics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.Gold
import com.cyberfusion.core.logging.CyberFusionLogger

@Composable
fun DiagnosticsScreen(navController: NavController) {
    val logs = CyberFusionLogger.getLogs()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnostics", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                actions = {
                    IconButton(onClick = { CyberFusionLogger.clearLogs() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear logs", tint = Gold)
                    }
                }
            )
        },
        bottomBar = { com.cyberfusion.ui.components.CyberFusionBottomBar(navController) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Text("System Logs (${logs.size} entries)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gold)
            }
            items(logs.takeLast(100)) { log ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Text(text = log, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (logs.isEmpty()) {
                item {
                    Text("No logs recorded yet.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
