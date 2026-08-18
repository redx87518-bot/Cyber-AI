package com.cyberfusion.ui.features.incidents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.Gold

@Composable
fun IncidentsScreen(navController: NavController) {
    val incidents = listOf(
        IncidentItem("Ransomware Outbreak", "Active", "Critical"),
        IncidentItem("Data Exfiltration", "Contained", "High")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidents", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(incidents) { inc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(inc.title, fontWeight = FontWeight.Bold)
                        Text("${inc.status} | ${inc.severity}")
                    }
                }
            }
        }
    }
}

data class IncidentItem(val title: String, val status: String, val severity: String)