package com.cyberfusion.ui.features.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.Gold
import com.cyberfusion.core.ai.tools.AIToolRegistry

@Composable
fun ToolsScreen(navController: NavController) {
    val tools = AIToolRegistry.tools
    val categories = tools.groupBy { 
        when {
            it.name.contains("alert", true) || it.name.contains("incident", true) -> "SOC"
            it.name.contains("ioc", true) || it.name.contains("threat", true) || it.name.contains("malware", true) || it.name.contains("abuse", true) || it.name.contains("otx", true) || it.name.contains("urlscan", true) || it.name.contains("dns", true) || it.name.contains("rdap", true) || it.name.contains("whois", true) -> "Threat Intelligence"
            it.name.contains("grc", true) || it.name.contains("risk", true) || it.name.contains("iso", true) -> "GRC"
            it.name.contains("cve", true) || it.name.contains("mitre", true) || it.name.contains("vulnerability", true) -> "Vulnerability"
            it.name.contains("report", true) || it.name.contains("pdf", true) -> "Reporting"
            it.name.contains("lab", true) -> "Labs"
            it.name.contains("setting", true) -> "Utilities"
            else -> "General"
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tools", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { com.cyberfusion.ui.components.CyberFusionBottomBar(navController) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            categories.forEach { (category, categoryTools) ->
                item {
                    Text(category, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gold)
                }
                items(categoryTools) { tool ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Icon(Icons.Default.Build, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(tool.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(tool.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (tool.parameters.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Parameters: ${tool.parameters.keys.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
            }
        }
    }
}
 
