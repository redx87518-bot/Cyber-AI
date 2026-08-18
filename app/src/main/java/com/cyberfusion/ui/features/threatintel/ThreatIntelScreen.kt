package com.cyberfusion.ui.features.threatintel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.theme.Gold

@Composable
fun ThreatIntelScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<ThreatIntelResult>()) }
    
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
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("IP, domain, hash, CVE...") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { results = listOf(ThreatIntelResult("8.8.8.8", "IP", "Clean", 0, "Google DNS")) }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                    Text("Search")
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results) { result ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(result.ioc, fontWeight = FontWeight.Bold)
                            Text("Type: ${result.type} | Reputation: ${result.reputation} | Confidence: ${result.confidence}%")
                        }
                    }
                }
            }
        }
    }
}

data class ThreatIntelResult(val ioc: String, val type: String, val reputation: String, val confidence: Int, val source: String)