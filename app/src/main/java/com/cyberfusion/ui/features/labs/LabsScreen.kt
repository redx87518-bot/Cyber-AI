package com.cyberfusion.ui.features.labs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.theme.Gold

@Composable
fun LabsScreen(navController: NavController, viewModel: LabsViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cybersecurity Labs", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { CyberFusionBottomBar(navController) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.labs.size) { index ->
                val lab = uiState.labs[index]
                Card(modifier = Modifier.fillMaxWidth(), onClick = { navController.navigate("lab_detail/${lab.id}") }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(lab.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(lab.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = {}, label = { Text(lab.difficulty) }, colors = AssistChipDefaults.assistChipColors(containerColor = Gold.copy(alpha = 0.2f)))
                            lab.progress?.let { progress ->
                                if (progress.completed) {
                                    AssistChip(onClick = {}, label = { Text("Score: ${progress.score}%") })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class LabItem(val title: String, val description: String, val difficulty: String)