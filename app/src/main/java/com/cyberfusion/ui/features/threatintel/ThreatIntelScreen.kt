package com.cyberfusion.ui.features.threatintel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.theme.Gold

@Composable
fun ThreatIntelScreen(navController: NavController, viewModel: ThreatIntelViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Threat Intelligence", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { CyberFusionBottomBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.updateQuery(it)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("IP, domain, hash, CVE...") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.search() },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    enabled = !uiState.isLoading
                ) {
                    Text(if (uiState.isLoading) "..." else "Search")
                }
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.results) { result ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(result.ioc, fontWeight = FontWeight.Bold)
                            Text("Type: ${result.type} | Reputation: ${result.reputation} | Confidence: ${result.confidence}% | Source: ${result.source}")
                            result.details?.let { details ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
