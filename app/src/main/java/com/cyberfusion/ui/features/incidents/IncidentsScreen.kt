package com.cyberfusion.ui.features.incidents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Gold
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun IncidentsScreen(navController: NavController) {
    val viewModel: IncidentsViewModel = viewModel(factory = LocalViewModelFactory.current)
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidents", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.incidents) { inc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(inc.title, fontWeight = FontWeight.Bold)
                        Text("${inc.status} | ${inc.severity}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (uiState.incidents.isEmpty()) {
                item {
                    Text("No incidents recorded.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
