package com.cyberfusion.ui.features.grc

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
fun GRCScreen(navController: NavController) {
    val risks = listOf(
        RiskItem("Unpatched VPN", "High", "12", "Open"),
        RiskItem("Weak Password Policy", "Medium", "8", "In Progress")
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GRC", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(risks) { risk ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(risk.title, fontWeight = FontWeight.Bold)
                        Text("Score: ${risk.score} | Status: ${risk.status}")
                    }
                }
            }
        }
    }
}

data class RiskItem(val title: String, val severity: String, val score: String, val status: String)