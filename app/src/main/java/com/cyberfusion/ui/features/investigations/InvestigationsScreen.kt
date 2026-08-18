package com.cyberfusion.ui.features.investigations

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
fun InvestigationsScreen(navController: NavController) {
    val investigations = listOf(
        InvestigationItem("APT29 Infrastructure", "In Progress", "High", "2 days ago"),
        InvestigationItem("Phishing Campaign Q3", "Open", "Medium", "1 week ago")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investigations", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(investigations) { inv ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(inv.title, fontWeight = FontWeight.Bold)
                        Text("${inv.status} | ${inv.severity} | ${inv.updated}")
                    }
                }
            }
        }
    }
}

data class InvestigationItem(val title: String, val status: String, val severity: String, val updated: String)