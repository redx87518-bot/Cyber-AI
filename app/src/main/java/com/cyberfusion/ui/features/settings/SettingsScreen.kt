package com.cyberfusion.ui.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.Gold

@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Text("AI Providers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gold)
            }
            item {
                ProviderCard("OpenRouter", "Configured", "primary")
            }
            item {
                ProviderCard("Groq", "Not configured", "fallback")
            }
            item {
                Text("Cybersecurity APIs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gold, modifier = Modifier.padding(top = 16.dp))
            }
            item {
                ApiCard("AbuseIPDB", "Connected")
            }
            item {
                ApiCard("ThreatFox", "Connected")
            }
            item {
                ApiCard("MalwareBazaar", "Not configured")
            }
        }
    }
}

@Composable
fun ProviderCard(name: String, status: String, role: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Medium)
                Text(status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(color = if (status == "Configured" || status == "Connected") Gold else MaterialTheme.colorScheme.outline, shape = MaterialTheme.shapes.small) {
                Text(role, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun ApiCard(name: String, status: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(name, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text(status, color = if (status == "Connected") Gold else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}