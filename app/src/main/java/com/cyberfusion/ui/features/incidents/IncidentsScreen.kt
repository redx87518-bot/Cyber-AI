package com.cyberfusion.ui.features.incidents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.EmptyState
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.ScreenScaffold
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.severityColor
import com.cyberfusion.ui.theme.statusColor

@Composable
fun IncidentsScreen(navController: NavController) {
    val viewModel: IncidentsViewModel = viewModel(factory = LocalViewModelFactory.current)
    val uiState by viewModel.uiState.collectAsState()

    ScreenScaffold(
        title = "Incidents",
        subtitle = "Incident response tracking",
        navController = navController
    ) {
        if (uiState.incidents.isEmpty() && !uiState.isLoading) {
            EmptyState(
                icon = Icons.Default.Warning,
                title = "No incidents recorded",
                hint = "Active incidents raised by your SOC workflow will appear here.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.incidents.size) { index ->
                    val incident = uiState.incidents[index]
                    Panel(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                incident.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            StatusChip(incident.severity, severityColor(incident.severity))
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusChip(incident.status, statusColor(incident.status))
                        }
                        if (incident.description.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                incident.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextLo,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
