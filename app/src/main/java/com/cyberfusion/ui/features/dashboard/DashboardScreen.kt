package com.cyberfusion.ui.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.Gold
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardScreen(navController: NavController) {
    val viewModel: DashboardViewModel = viewModel(factory = LocalViewModelFactory.current)
    val state by viewModel.state.collectAsState()
    
    val cards = listOf(
        DashboardCard("Critical Alerts", state.criticalAlerts.toString(), Icons.Default.Security),
        DashboardCard("Investigations", state.investigationsCount.toString(), Icons.Default.Security),
        DashboardCard("Active Incidents", state.activeIncidents.toString(), Icons.Default.Security),
        DashboardCard("Lab Progress", "${state.labsCount} labs", Icons.Default.Security)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CyberFusion", fontWeight = FontWeight.Bold, color = Gold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { CyberFusionBottomBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Good evening, Analyst", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("CyberFusion Dashboard", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(cards.size) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(cards[index].icon, contentDescription = null, tint = Gold, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cards[index].title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(cards[index].value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Gold)
                        }
                    }
                }
            }
        }
    }
}

data class DashboardCard(val title: String, val value: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
