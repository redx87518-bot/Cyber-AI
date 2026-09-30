package com.cyberfusion.ui.features.labs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cyberfusion.ui.components.CyberBackground
import com.cyberfusion.ui.components.CyberFusionBottomBar
import com.cyberfusion.ui.components.EmptyState
import com.cyberfusion.ui.components.Panel
import com.cyberfusion.ui.components.SectionHeader
import com.cyberfusion.ui.components.StatusChip
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.navigation.Screen
import com.cyberfusion.ui.theme.Cyan
import com.cyberfusion.ui.theme.Mint
import com.cyberfusion.ui.theme.TextLo
import com.cyberfusion.ui.theme.severityColor

@Composable
fun LabsScreen(
    navController: NavController,
    viewModel: LabsViewModel = viewModel(factory = LocalViewModelFactory.current)
) {
    val uiState by viewModel.uiState.collectAsState()

    CyberBackground {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text("TRAINING GROUNDS", style = MaterialTheme.typography.labelSmall, color = Cyan)
                Text(
                    "Cyber Labs",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.completed > 0) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { uiState.progressFraction },
                            modifier = Modifier.weight(1f),
                            color = Mint,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "${uiState.completed}/${uiState.total}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Mint
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.labs.isEmpty() && !uiState.isLoading) {
                    item {
                        EmptyState(
                            icon = Icons.Filled.Science,
                            title = "Loading labs…",
                            hint = "Preparing interactive SOC, GRC and red-team scenarios.",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                items(uiState.labs.size) { index ->
                    val lab = uiState.labs[index]
                    Panel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.labDetail(lab.id)) },
                        borderColor = if (lab.progress?.completed == true)
                            Mint.copy(alpha = 0.4f) else Cyan.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    lab.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    lab.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextLo,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    StatusChip(lab.difficulty, severityColor(lab.difficulty))
                                    StatusChip(lab.category, TextLo)
                                    lab.progress?.completed?.let { done ->
                                        if (done) StatusChip("score ${lab.progress!!.score}%", Mint)
                                    }
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Icon(
                                Icons.Filled.Science,
                                contentDescription = null,
                                tint = if (lab.progress?.completed == true) Mint else Cyan.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            CyberFusionBottomBar(navController)
        }
    }
}
