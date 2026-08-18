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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.CyberBlue
import com.cyberfusion.ui.theme.CyberRed
import com.cyberfusion.ui.compose.LocalViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun AIModelsScreen(navController: NavController, viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = LocalViewModelFactory.current)) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    
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
                Text("AI Providers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            items(uiState.providers) { provider ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (provider.id == "openrouter") Icons.Default.Cloud else Icons.Default.Computer,
                                contentDescription = null,
                                tint = if (provider.status == "Connected") CyberBlue else CyberRed,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(provider.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text(provider.model, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (provider.isEnabled) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Enabled", tint = CyberBlue)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.saveProviderSettings(provider.copy(isEnabled = !provider.isEnabled))
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (provider.isEnabled) CyberRed else CyberBlue)
                            ) {
                                Text(if (provider.isEnabled) "Disable" else "Enable")
                            }
                            OutlinedButton(onClick = {
                                scope.launch {
                                    viewModel.testProviderConnection(provider.id)
                                }
                            }) {
                                Text("Test")
                            }
                        }
                    }
                }
            }
        }
    }
}
