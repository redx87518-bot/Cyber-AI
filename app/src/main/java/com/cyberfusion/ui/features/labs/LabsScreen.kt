package com.cyberfusion.ui.features.labs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.theme.Gold

@Composable
fun LabsScreen(navController: NavController) {
    val labs = listOf(
        LabItem("Lab 01", "Phishing Investigation", "Beginner"),
        LabItem("Lab 02", "IOC Analysis", "Beginner"),
        LabItem("Lab 03", "Malware Investigation", "Intermediate"),
        LabItem("Lab 04", "Incident Response", "Intermediate"),
        LabItem("Lab 05", "Threat Intelligence", "Beginner"),
        LabItem("Lab 06", "Log Analysis", "Intermediate"),
        LabItem("Lab 07", "GRC Risk Assessment", "Advanced"),
        LabItem("Lab 08", "SOC Investigation", "Advanced")
    )
    
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
            items(labs) { lab ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(lab.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(lab.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        AssistChip(onClick = {}, label = { Text(lab.difficulty) }, colors = AssistChipDefaults.assistChipColors(containerColor = Gold.copy(alpha = 0.2f)))
                    }
                }
            }
        }
    }
}

data class LabItem(val title: String, val description: String, val difficulty: String)