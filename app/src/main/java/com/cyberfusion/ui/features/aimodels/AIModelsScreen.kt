package com.cyberfusion.ui.features.aimodels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.CyberBlue
import com.cyberfusion.ui.theme.CyberRed

@Composable
fun AIModelsScreen(navController: NavController) {
    val providers = listOf(
        ModelProviderCard("Local", "Falcon-H1-Tiny-90M", "Tool Calling", "~47 MB", true, "Offline", Icons.Default.Computer, true),
        ModelProviderCard("OpenRouter", "mistralai/mistral-7b-instruct", "Cloud API", "Online", false, "Cloud", Icons.Default.Cloud, false)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Models", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { com.cyberfusion.ui.components.CyberFusionBottomBar(navController) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text("Active AI Provider", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            items(providers) { provider ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(provider.icon, contentDescription = null, tint = if (provider.isLocal) CyberBlue else CyberRed, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(provider.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(provider.model, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (provider.isActive) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = CyberBlue)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("Type: ${provider.type}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Size: ${provider.size}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Status: ${provider.status}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { /* Enable provider */ }, colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)) {
                                Text(if (provider.isActive) "Active" else "Enable")
                            }
                            OutlinedButton(onClick = { /* Configure provider */ }) {
                                Text("Configure")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ModelProviderCard(
    val name: String,
    val model: String,
    val type: String,
    val size: String,
    val isLocal: Boolean,
    val status: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isActive: Boolean
)
 
