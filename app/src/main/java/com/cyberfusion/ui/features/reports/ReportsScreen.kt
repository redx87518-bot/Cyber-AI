package com.cyberfusion.ui.features.reports

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
fun ReportsScreen(navController: NavController) {
    val reports = listOf(
        ReportItem("Weekly Threat Summary", "PDF", "2 hours ago"),
        ReportItem("Incident Response Report", "PDF", "1 day ago")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(reports) { report ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(report.title, fontWeight = FontWeight.Bold)
                        Text("${report.format} | ${report.date}")
                    }
                }
            }
        }
    }
}

data class ReportItem(val title: String, val format: String, val date: String)